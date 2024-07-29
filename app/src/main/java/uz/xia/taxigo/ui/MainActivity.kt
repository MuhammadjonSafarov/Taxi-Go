package uz.xia.taxigo.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import uz.xia.taxigo.R
import uz.xia.taxigo.data.IPreference
import uz.xia.taxigo.data.remote.enumrition.UserRoleType
import uz.xia.taxigo.databinding.ActivityMainBinding
import uz.xia.taxigo.ui.home.ILocationRequestListener
import uz.xia.taxigo.utils.isCheckLocationPermission
import uz.xia.taxigo.utils.lazyFast
import uz.xia.taxigo.utils.setStatusBarColor
import javax.inject.Inject

private const val TAG = "MainActivity"
private const val DEFAULT_INACTIVITY_DELAY_IN_MILLISECONDS = 200
const val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 10_000
const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2
const val REQUEST_CHECK_SETTINGS_CODE = 2022
const val REQUEST_CODE = 1_001

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnSuccessListener<LocationSettingsResponse>,
    OnFailureListener, ILocationRequestListener, NavController.OnDestinationChangedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val navController by lazyFast { supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment }

    @Inject
    lateinit var preferences: IPreference
    private var locationPermissionGranted = false
    private val fusedLocationProviderClient: FusedLocationProviderClient by lazyFast {
        LocationServices.getFusedLocationProviderClient(this)
    }
    private val settingsClient by lazyFast {
        LocationServices.getSettingsClient(this)
    }
    private var requestingLocationUpdates: Boolean = false
    private var hasEnableLocation: Boolean = false

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            currentLocation = locationResult.lastLocation
            if (currentLocation == null) return

            if (!hasEnableLocation) {
                val fragments = navController.childFragmentManager.fragments
                for (fragment in fragments) {
                    if (fragment is ILocationResultListener) fragment.onChangeCurrentLocation()
                }
                hasEnableLocation = true
            }
        }
    }

    private var locationRequest: LocationRequest? = null
    private val locationSettingsRequest by lazyFast {
        LocationSettingsRequest.Builder().addLocationRequest(locationRequest!!).build()
    }
    private var currentLocation: Location? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor(R.color.colorWhite, R.color.colorBlack, false)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_driver_home,
                R.id.nav_driver,
                R.id.nav_buses,
                R.id.nav_address,
                R.id.parkingListFragment,
                R.id.nav_participants,
                R.id.nav_music,
                R.id.nav_settings
            ), drawerLayout
        )
        val navController = navController.navController

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener(this)

        /* menu */
        val role = preferences.userRole
        val navMenu: Menu = navView.menu
        navMenu.findItem(R.id.nav_driver_home).isVisible = (role == UserRoleType.DRIVER.name)
        navMenu.findItem(R.id.nav_home).isVisible = (role != UserRoleType.DRIVER.name)
        navMenu.findItem(R.id.nav_participants).isVisible = (role != UserRoleType.GUEST.name)

        /* header */
        val headerView: View = navView.getHeaderView(0)
        val avatarCard = headerView.findViewById<CardView>(R.id.cardView2)
        val avatarView = headerView.findViewById<AppCompatImageView>(R.id.avatar_image)
        val fullName = headerView.findViewById<TextView>(R.id.fullName)
        val phone = headerView.findViewById<TextView>(R.id.phone)
        val driverIcon = headerView.findViewById<LinearLayout>(R.id.driver_icon)
        val login = headerView.findViewById<Button>(R.id.login)

        fullName.isVisible = (role != UserRoleType.GUEST.name)
        avatarCard.isVisible = (role != UserRoleType.GUEST.name)
        phone.isVisible = (role != UserRoleType.GUEST.name)
        driverIcon.isVisible = (role == UserRoleType.DRIVER.name)
        login.isVisible = (role == UserRoleType.GUEST.name)

        avatarView.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START, true)
            navController.navigate(R.id.nav_profile)
        }
        login.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START, true)
            navController.navigate(R.id.loginFragment)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!checkNotificationPermission()) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1_000)
            }
        }

        /*  val text = savedInstanceState?.getString(NOTIFICATION)?:""
          if (text.isNotEmpty()){
              Toast.makeText(this, "text", Toast.LENGTH_SHORT).show()
          }
          Timber.d("$TAG Message $text")
          FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
              if (!task.isSuccessful) {
                  Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                  return@OnCompleteListener
              }

              // Get new FCM registration token
              val token = task.result
              Timber.d("TAG token:$token")
              Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
          })*/
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onRequestLocation() {
        hasEnableLocation = false
        onLocation()
    }

    private fun onLocation() {
        if (!isCheckLocationPermission()) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), REQUEST_CODE
            )
            return
        }
        if (locationRequest == null)
            createLocationRequest()
        startLocationUpdates()
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = UPDATE_INTERVAL_IN_MILLISECONDS
            fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private fun startLocationUpdates() {
        settingsClient.checkLocationSettings(locationSettingsRequest)
            .addOnSuccessListener(this, this)
            .addOnFailureListener(this, this)
    }

    @SuppressLint("MissingPermission")
    override fun onSuccess(lsres: LocationSettingsResponse?) {
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest!!, locationCallback, Looper.getMainLooper()
        )
    }

    override fun onFailure(e: Exception) {
        when ((e as? ApiException)?.statusCode) {
            LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                Timber.i("Location settings are not satisfied. Attempting to upgrade location settings ")
                try {
                    val rae = e as ResolvableApiException
                    rae.startResolutionForResult(this, REQUEST_CHECK_SETTINGS_CODE)
                } catch (e: Exception) {
                    Timber.i("PendingIntent unable to execute request: ${e.localizedMessage}")
                }
            }

            LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                Toast.makeText(this, R.string.error_enable_gps_setting, Toast.LENGTH_LONG).show()
                requestingLocationUpdates = false
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Timber.d("$TAG onNewIntent ${intent?.extras?.getString("type") ?: ""}")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val fragments = navController.childFragmentManager.fragments
                    for (fragment in fragments) {
                        if (fragment is ILocationResultListener) fragment.onGPSEnable()
                    }
                    onLocation()
                }

                Activity.RESULT_CANCELED -> {
                    val fragments = navController.childFragmentManager.fragments
                    for (fragment in fragments) {
                        if (fragment is ILocationResultListener) fragment.onGPSDisable()
                    }
                }
            }
        }

    }


    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
            onLocation()
        }
    }

    private fun checkNotificationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onPause() {
        super.onPause()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        binding.appBarMain.appBarLayout.isVisible = (destination.id !in listOf(R.id.loginFragment,R.id.welcomeFragment))
    }
}

interface ILocationResultListener {
    fun onGPSEnable()
    fun onGPSDisable()
    fun onChangeCurrentLocation()
}
