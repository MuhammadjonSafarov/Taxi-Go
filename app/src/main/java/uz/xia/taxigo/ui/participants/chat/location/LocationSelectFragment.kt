package uz.xia.taxigo.ui.participants.chat.location

import android.animation.ValueAnimator
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.airbnb.lottie.LottieDrawable
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
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import timber.log.Timber
import uz.xia.taxigo.R
import uz.xia.taxigo.common.EMPTY_STRING
import uz.xia.taxigo.common.TASHKENT_LATITUDE
import uz.xia.taxigo.common.TASHKENT_LONGITUDE
import uz.xia.taxigo.data.IPreference
import uz.xia.taxigo.databinding.FragmentChatLocationBinding
import uz.xia.taxigo.utils.isLocationPermissionGranted
import javax.inject.Inject

private const val TAG = "LocationSelectFragment"

@AndroidEntryPoint
class LocationSelectFragment : DialogFragment(R.layout.fragment_chat_location),
    View.OnTouchListener, ValueAnimator.AnimatorUpdateListener, MapListener {
    private var _binding: FragmentChatLocationBinding? = null
    private val binding get() = _binding!!
    private var map: MapView? = null
    private var mapController: IMapController? = null
    private val animator = ValueAnimator.ofFloat(0.5f, 1f)
    private var address: String = EMPTY_STRING
    private var selectLongitude: Double = TASHKENT_LONGITUDE
    private var selectLatitude: Double = TASHKENT_LATITUDE

    @Inject
    lateinit var preference: IPreference

    override fun getTheme(): Int = R.style.FullScreenDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatLocationBinding.inflate(inflater, container, false)
        val ctx: Context? = context?.applicationContext
        Configuration.getInstance().load(
            ctx, androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext())
        )
        map = binding.mapView
        map?.setTileSource(TileSourceFactory.MAPNIK)
        map?.setBuiltInZoomControls(false)
        map?.setMultiTouchControls(true)
        mapController = map?.controller
        mapController?.setZoom(15)
        val startPoint = GeoPoint(preference.latitude, preference.longitude)
        mapController?.setCenter(startPoint)
        mapController?.animateTo(startPoint)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (requireContext().isLocationPermissionGranted()) {
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
    }

    private fun setUpViews() {
        val mRotationGestureOverlay = RotationGestureOverlay(requireContext(), map)
        mRotationGestureOverlay.isEnabled = true
        map!!.setMultiTouchControls(true)
        map!!.overlays.add(mRotationGestureOverlay)
        map?.setMapListener(DelayedMapListener(this))
        binding.materialToolbar.setNavigationOnClickListener {
            dismiss()
        }
        binding.llSelects.setOnClickListener{
            val bundle = bundleOf(
                Pair("key_longitude",selectLongitude),
                Pair("key_latitude",selectLatitude)
            )
            setFragmentResult("location_data",bundle)
            dismiss()
        }
        map?.setOnTouchListener(this)
    }

    override fun onTouch(item: View?, motionEvent: MotionEvent?): Boolean {
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
                    selectLongitude = center?.longitude ?: TASHKENT_LONGITUDE
                    selectLatitude = center?.latitude ?: TASHKENT_LATITUDE
                }
            }
        }
        return false
    }

    override fun onAnimationUpdate(p0: ValueAnimator) {
        binding.animationView.progress = animator.animatedValue as Float
    }

    override fun onScroll(event: ScrollEvent?): Boolean {
        selectLatitude = map?.mapCenter?.latitude ?: 0.0
        selectLongitude = map?.mapCenter?.longitude ?: 0.0
        return true
    }

    override fun onZoom(event: ZoomEvent?): Boolean {
        selectLatitude = map?.mapCenter?.latitude ?: 0.0
        selectLongitude = map?.mapCenter?.longitude ?: 0.0
        return true
    }

}
