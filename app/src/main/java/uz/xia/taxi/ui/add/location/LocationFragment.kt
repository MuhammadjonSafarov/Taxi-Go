package uz.xia.taxi.ui.add.location

import android.Manifest
import android.animation.ValueAnimator
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.airbnb.lottie.LottieDrawable
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
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import timber.log.Timber
import uz.xia.taxi.R
import uz.xia.taxi.common.EMPTY_STRING
import uz.xia.taxi.data.IPreference
import uz.xia.taxi.databinding.FragmentLocationBinding
import uz.xia.taxi.utils.lazyFast
import javax.inject.Inject

private const val TAG = "LocationFragment"

@AndroidEntryPoint
class LocationFragment : Fragment(), View.OnTouchListener, MapListener,
    ValueAnimator.AnimatorUpdateListener, View.OnClickListener {
    private var _binding: FragmentLocationBinding? = null
    private val binding get() = _binding!!
    private var map: MapView? = null
    private var mapController: IMapController? = null
    private val animator = ValueAnimator.ofFloat(0.5f, 1f)
    private val homeViewModel by viewModels<LocationViewModel>()
    private var address: String = EMPTY_STRING
    private val navController by lazyFast {
        Navigation.findNavController(
            requireActivity(),
            R.id.nav_host_fragment_content_main
        )
    }
    private val shimmer: Shimmer by lazyFast {
        Shimmer()
            .setRepeatCount(ValueAnimator.INFINITE)
            .setDuration(500)
            .setStartDelay(300)
            .setDirection(Shimmer.ANIMATION_DIRECTION_RTL)
            .setAnimatorListener(null)
    }

    @Inject
    lateinit var preference: IPreference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationBinding.inflate(inflater, container, false)
        val ctx: Context? = context?.applicationContext
        Configuration.getInstance().load(
            ctx, androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext())
        )
        map = binding.mapView
        map?.setTileSource(TileSourceFactory.MAPNIK)
        map?.setBuiltInZoomControls(true)
        map?.setMultiTouchControls(true)
        mapController = map?.controller
        mapController?.setZoom(15)
        val startPoint = GeoPoint(41.213383, 69.246071)
        mapController?.setCenter(startPoint)
        mapController?.animateTo(startPoint)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isLocationPermissionGranted()) {
                val mLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), map)
                mLocationOverlay.enableFollowLocation()
                mLocationOverlay.enableMyLocation()
                map?.overlays?.add(mLocationOverlay)
            } else {
                //TODO permission is dined
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setUpObserver()
    }

    private fun setUpViews() {
        map?.setOnTouchListener(this)
        map?.setMapListener(DelayedMapListener(this))
        binding.buttonConform.setOnClickListener(this)
        binding.fabLocation.setOnClickListener(this)
    }

    override fun onClick(item: View?) {
        when (item?.id) {
            R.id.button_conform -> {
                val bundle = bundleOf(
                    Pair("key_latitude", preference.latitude),
                    Pair("key_longitude", preference.longitude),
                )
                setFragmentResult("key_location",bundle)
                navController.popBackStack()
            }

            R.id.button_cancel -> navController.popBackStack()
            R.id.fabLocation -> {}
        }
    }

    private fun setUpObserver() {
        homeViewModel.livePlace.observe(viewLifecycleOwner) {
            shimmer.cancel()
            address = it.name
            binding.addressDescription.text = it.name
        }
    }

    override fun onTouch(p0: View?, motionEvent: MotionEvent?): Boolean {
        val action = motionEvent?.action
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                binding.animationView.repeatMode = LottieDrawable.REVERSE
                binding.animationView.repeatCount = LottieDrawable.INFINITE
                binding.animationView.setMinAndMaxProgress(0f, 0.5f)
                binding.animationView.cancelAnimation()
                binding.animationView.playAnimation()
                Timber.d("$TAG ACTION_DOWN")
            }

            MotionEvent.ACTION_MOVE -> {
                Timber.d("$TAG ACTION_MOVE")
            }

            MotionEvent.ACTION_UP -> {
                if (binding.animationView.isAnimating) {
                    binding.animationView.repeatCount = 0
                    binding.animationView.setMinAndMaxProgress(0.5f, 1f)
                    animator.addUpdateListener(this)
                    animator.start()
                    val center = map?.mapCenter
                    preference.latitude = center?.latitude ?: 0.0
                    preference.longitude = center?.longitude ?: 0.0
                    homeViewModel.geoCode(preference.latitude, preference.longitude, preference.language)
                    shimmer.start(binding.addressDescription)
                }

                Timber.d("$TAG ACTION_UP")
            }
        }
        return false
    }

    override fun onAnimationUpdate(p0: ValueAnimator) {
        binding.animationView.progress = animator.animatedValue as Float
    }

    override fun onScroll(event: ScrollEvent?): Boolean {
        Timber.d("$TAG onScroll ")
        return false
    }

    override fun onZoom(event: ZoomEvent?): Boolean {
        if (binding.animationView.isAnimating) {
            binding.animationView.repeatCount = 0
            binding.animationView.setMinAndMaxProgress(0.5f, 1f)
            animator.addUpdateListener(this)
            animator.start()
            val center = map?.mapCenter
            preference.latitude = center?.latitude ?: 0.0
            preference.longitude = center?.longitude ?: 0.0
            homeViewModel.geoCode(preference.latitude, preference.longitude, preference.language)
            shimmer.start(binding.addressDescription)
        }
        return false
    }

    private fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
