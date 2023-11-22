package uz.xia.taxi.ui.home

import android.animation.ValueAnimator
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.drawable.Animatable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.romainpiel.shimmer.Shimmer
import dagger.hilt.android.AndroidEntryPoint
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.events.DelayedMapListener
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Marker.ANCHOR_BOTTOM
import org.osmdroid.views.overlay.Marker.ANCHOR_CENTER
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import timber.log.Timber
import uz.xia.taxi.R
import uz.xia.taxi.data.IPreference
import uz.xia.taxi.data.local.entity.GeoLocation
import uz.xia.taxi.data.local.entity.ParkingData
import uz.xia.taxi.data.remote.model.nomination.NearbyPlace
import uz.xia.taxi.databinding.FragmentHomeBinding
import uz.xia.taxi.ui.ILocationResultListener
import uz.xia.taxi.ui.home.roads.ParkDialogFragment
import uz.xia.taxi.utils.getBitmapFromVector
import uz.xia.taxi.utils.isCheckLocationPermission
import uz.xia.taxi.utils.lazyFast
import uz.xia.taxi.utils.locationEnabled
import javax.inject.Inject


private const val TAG = "HomeFragment"

@AndroidEntryPoint
class HomeFragment : Fragment(), MapListener, Marker.OnMarkerClickListener,
    ParkDialogFragment.MyDialogCloseListener, ILocationResultListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var oldMarker: Marker? = null
    private var map: MapView? = null
    private var mapController: IMapController? = null
    private val homeViewModel: IHomeViewModel by viewModels<HomeViewModel>()
    private val navController by lazyFast {
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main)
    }
    private var isInitialization: Boolean = false
    private val animator = ValueAnimator.ofFloat(0.5f, 1f)
    private var mListener: ILocationRequestListener? = null

    @Inject
    lateinit var preferences: IPreference
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    private val selectMapAppDialog by lazyFast {
        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_choose_map_app, binding.root, false)
        val llcYandex = view.findViewById<LinearLayoutCompat>(R.id.llc_yandex)
        val llcGoogle = view.findViewById<LinearLayoutCompat>(R.id.llc_google)
        val chOnlyChoosing = view.findViewById<AppCompatCheckBox>(R.id.chb_only_choosing_this)
        val builder = AlertDialog.Builder(requireContext(), R.style.CustomDialog)
            .setTitle(R.string.choose_on_map_app).setView(view)
        val dialog = builder.create()
        llcGoogle.setOnClickListener {
            preferences.isYandexMapRoute = false
            startGoogleMaps()
            dialog.dismiss()
        }
        llcYandex.setOnClickListener {
            preferences.isYandexMapRoute = true
            startYandexNavigator()
            dialog.dismiss()
        }
        chOnlyChoosing.setOnCheckedChangeListener { _, isChecked ->
            preferences.isNotMoreChoosingMap = isChecked
        }

        return@lazyFast dialog
    }
    private val shimmer: Shimmer by lazyFast {
        Shimmer().setRepeatCount(ValueAnimator.INFINITE).setDuration(500).setStartDelay(300)
            .setDirection(Shimmer.ANIMATION_DIRECTION_RTL).setAnimatorListener(null)
    }
    private val placeObserver = Observer<NearbyPlace> {
        shimmer.cancel()
        binding.addressDescription.text = it.name
    }
    private val parkingLotsObserver = Observer<List<GeoLocation>> {
        it.forEach { p ->
            val point = GeoPoint(p.latitude, p.longitude)
            setMarker(point)
        }
    }
    private val parkingObserver = Observer<ParkingData> {
        val parkDialog = ParkDialogFragment.newInstaince(it.id)
        parkDialog.setListener(this)
        parkDialog.show(childFragmentManager, "tag")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ILocationRequestListener) {
            mListener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val ctx: Context? = context?.applicationContext
        Configuration.getInstance().load(
            ctx, androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext())
        )
        map = binding.mapView
        map?.setTileSource(TileSourceFactory.MAPNIK)
        map?.setBuiltInZoomControls(true)
        map?.setMultiTouchControls(true)
        mapController = map?.controller
        mapController?.setZoom(preferences.mapZoomLevel)
        val startPoint = GeoPoint(preferences.latitude, preferences.longitude)
        mapController?.setCenter(startPoint)
        mapController?.animateTo(startPoint)
        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel.geoCode(preferences.latitude, preferences.longitude, "uz")
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task: Task<String> ->
                if (!task.isSuccessful) {
                    Timber.w(TAG+"Fetching FCM registration token failed", task.exception)
                    return@addOnCompleteListener
                }
                val token = task.result
                Timber.d("$TAG :$token")
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setUpObserver()
    }

    private fun setUpViews() {
        if (!isInitialization)
            if (requireContext().isCheckLocationPermission()) {
                if (requireContext().locationEnabled()) {
                    val mLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), map)
                    mLocationOverlay.enableFollowLocation()
                    mLocationOverlay.enableMyLocation()
                    map?.overlays?.add(mLocationOverlay)
                    binding.fabLocation.setImageResource(R.drawable.ic_location_enable)
                    isInitialization=true
                } else {
                    binding.fabLocation.setImageResource(R.drawable.ic_location_disable)
                }
            } else {
                binding.fabLocation.setImageResource(R.drawable.ic_location_disable)
            }
        val mRotationGestureOverlay = RotationGestureOverlay(requireContext(), map)
        mRotationGestureOverlay.isEnabled = true
        map!!.setMultiTouchControls(true)
        map!!.overlays.add(mRotationGestureOverlay)
        map?.setMapListener(DelayedMapListener(this))
        binding.fabLocation.setOnClickListener {
            mListener?.onRequestLocation()
            if (requireContext().isCheckLocationPermission() && requireContext().locationEnabled()) {
                startAnimLocation()
            }

        }
    }

    private fun setUpObserver() {
        homeViewModel.livePlace.observe(viewLifecycleOwner, placeObserver)
        homeViewModel.liveParkingLotsLocations.observe(viewLifecycleOwner, parkingLotsObserver)
        homeViewModel.liveParking.observe(viewLifecycleOwner, parkingObserver)
    }

    private fun setMarker(point: GeoPoint) {
        val marker = Marker(map)
        marker.position = point
        marker.setAnchor(ANCHOR_CENTER, ANCHOR_BOTTOM)
        val bitmap = requireContext().getBitmapFromVector(R.drawable.ic_parking)
        val dr: Drawable = BitmapDrawable(resources, bitmap)
        marker.icon = dr
        marker.setOnMarkerClickListener(this)
        map?.overlays?.add(marker)
    }

    override fun onScroll(event: ScrollEvent?): Boolean {
        preferences.latitude = map?.mapCenter?.latitude ?: 0.0
        preferences.longitude = map?.mapCenter?.longitude ?: 0.0
        preferences.mapZoomLevel = map?.zoomLevelDouble ?: 7.0
        return false
    }

    override fun onZoom(event: ZoomEvent?): Boolean {
        preferences.mapZoomLevel = event?.zoomLevel ?: 7.0
        return false
    }

    override fun onMarkerClick(marker: Marker, mapView: MapView?): Boolean {
        mapView?.overlays?.remove(marker)
        val bitmap = requireContext().getBitmapFromVector(R.drawable.ic_parking_clicked)
        val dr: Drawable = BitmapDrawable(resources, bitmap)
        marker.icon = dr
        mapView?.overlays?.add(marker)
        oldMarker = marker
        mapView?.invalidate()
        val point = marker.position
        mapController?.animateTo(point)
        if (mapView!!.zoomLevelDouble < 17.0) {
            mapController?.setZoom(17.0)
        }
        homeViewModel.loadParking(point.longitude, point.latitude)
        return true
    }

    override fun handleDialogClose(dialog: DialogInterface) {
        if (oldMarker != null) {
            map?.overlays?.remove(oldMarker)
            val bitmap = requireContext().getBitmapFromVector(R.drawable.ic_parking)
            val dr: Drawable = BitmapDrawable(resources, bitmap)
            oldMarker?.icon = dr
            map?.overlays?.add(oldMarker)
        }
    }

    override fun onClickListener(latitude: Double, longitude: Double) {
        this.latitude = latitude
        this.longitude = longitude
        selectMapAppDialog.show()
    }

    override fun onRoadClickListener(id: Long) {
        Timber.d("$TAG onRoadClickListener $id")
        navController.navigate(R.id.nav_cars)
    }

    private fun startGoogleMaps() {
        val gmmIntentUri = Uri.parse("google.navigation:q=$latitude,$longitude")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }

    private fun startYandexNavigator() {
        val uri = Uri.parse("yandexnavi://build_route_on_map?lat_to=$latitude&lon_to=$longitude")
        var intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("ru.yandex.yandexnavi")
        val packageManager = requireContext().packageManager
        val activities: List<ResolveInfo> = packageManager.queryIntentActivities(intent, 0)
        val isIntentSafe = activities.isNotEmpty()
        if (isIntentSafe) startActivity(intent)
        else {
            intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("market://details?id=ru.yandex.yandexnavi")
            startActivity(intent)
        }
    }

    private fun startAnimLocation() {
        _binding?.fabLocation?.setImageResource(R.drawable.ic_location_disable)
        _binding?.fabLocation?.setImageResource(R.drawable.animated_location)
        val drawable = _binding?.fabLocation?.drawable
        if (drawable is Animatable?) {
            drawable?.start()
        }
    }

    private fun stopAnimLocation() {
        val drawable = _binding?.fabLocation?.drawable
        if (drawable is Animatable && drawable.isRunning) {
            drawable.stop()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mListener = null
    }

    override fun onGPSEnable() {
        startAnimLocation()
    }

    override fun onGPSDisable() {
        Toast.makeText(requireContext(), "GPS xizmati yoqilmagan", Toast.LENGTH_SHORT).show()
    }

    override fun onChangeCurrentLocation() {
        stopAnimLocation()
        binding.fabLocation.setImageResource(R.drawable.ic_location_enable)
        val mLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), map)
        mLocationOverlay.enableFollowLocation()
        mLocationOverlay.enableMyLocation()
        map?.overlays?.add(mLocationOverlay)
    }
}

interface ILocationRequestListener {
    fun onRequestLocation()
}
