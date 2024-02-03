package uz.xia.taxigo.ui.address.add

import android.Manifest
import android.animation.ValueAnimator
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
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
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import timber.log.Timber
import uz.xia.taxigo.R
import uz.xia.taxigo.common.EMPTY_STRING
import uz.xia.taxigo.data.IPreference
import uz.xia.taxigo.data.remote.model.nomination.NearbyPlace
import uz.xia.taxigo.databinding.FragmentAddressAddBinding
import uz.xia.taxigo.ui.address.add.adapter.LocationAdapter
import uz.xia.taxigo.utils.lazyFast
import javax.inject.Inject


private const val TAG = "AddAddressFragment"

@AndroidEntryPoint
class AddAddressFragment : Fragment(), View.OnTouchListener, MapListener,
    ValueAnimator.AnimatorUpdateListener, View.OnClickListener,
    LocationAdapter.OnPlaceClickListener {
    private var _binding: FragmentAddressAddBinding? = null
    private val binding get() = _binding!!
    private var map: MapView? = null
    private var mapController: IMapController? = null
    private val animator = ValueAnimator.ofFloat(0.5f, 1f)
    private var address: String = EMPTY_STRING
    private val mViewModel: IAddAddressViewModel by viewModels<AddAddressViewModel>()
    private val navController by lazyFast {
        Navigation.findNavController(
            requireActivity(), R.id.nav_host_fragment_content_main
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
    private val placeAdapter by lazyFast { LocationAdapter(this) }

    private val placeListObserver = Observer<List<NearbyPlace>> {
        placeAdapter.submitList(it)
    }

    @Inject
    lateinit var preference: IPreference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddressAddBinding.inflate(inflater, container, false)
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
        val startPoint = GeoPoint(preference.latitude, preference.longitude)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel.geoCode(preference.latitude, preference.longitude, "uz")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setUpObserver()
    }

    private fun setUpViews() {
        setFragmentResultListener("key_data") { requestKey, bundle ->
            val position = bundle.getInt("key_address_type")
            val addressName = bundle.getString("key_address_name") ?: EMPTY_STRING
            val address = bundle.getString("key_address") ?: EMPTY_STRING
            mViewModel.saveAddress(position, addressName, address)
        }
        map?.setOnTouchListener(this)
        map?.setMapListener(DelayedMapListener(this))
        binding.buttonConform.setOnClickListener(this)
        binding.buttonCancel.setOnClickListener(this)
        binding.fabLocation.setOnClickListener(this)
        binding.etAddress.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(text: String?) = false

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.length > 2)
                    mViewModel.searchPlace(newText, preference.language)
                else if (newText.isEmpty()) {
                    placeAdapter.submitList(emptyList())
                }
                return true
            }
        })
        binding.recyclerAdjust.adapter = placeAdapter
        /*    val point1 = GeoPoint(41.279593, 69.155648)
              val point2 = GeoPoint(41.254793, 69.224553)
              setMarker(point1)
              setMarker(point2)
              val centerPoint = getCenterBetweenPoints(point1, point2)
              mapController?.setZoom(getZoomLevel(point1, point2))
              mapController?.setCenter(centerPoint)
              mapController?.animateTo(centerPoint)

              */
    }

    private fun setUpObserver() {
        mViewModel.livePlace.observe(viewLifecycleOwner) {
            shimmer.cancel()
            address = it.name
            binding.addressDescription.text = it.name
        }
        mViewModel.isAddressSaveLiveData.observe(viewLifecycleOwner) {
            if (it) navController.popBackStack()
        }
        mViewModel.livePlaceList.observe(viewLifecycleOwner, placeListObserver)
    }

    // Ikki nuqta orasidagi markazni topish
    private fun getCenterBetweenPoints(point1: GeoPoint, point2: GeoPoint): GeoPoint {
        val latCenter = (point1.latitude + point2.latitude) / 2
        val lngCenter = (point1.longitude + point2.longitude) / 2
        return GeoPoint(latCenter, lngCenter)
    }

    // Ikki nuqta orasidagi zoom levelni topish
    private fun getZoomLevel(point1: GeoPoint, point2: GeoPoint): Double {
        val WORLD_DIMENSION = 256
        val ZOOM_MAX = 21
        val latDiff = Math.abs(point1.latitude - point2.latitude)
        val lngDiff = Math.abs(point1.longitude - point2.longitude)
        val latZoom = Math.floor(Math.log(WORLD_DIMENSION / latDiff) / Math.log(2.0))
        val lngZoom = Math.floor(Math.log(WORLD_DIMENSION / lngDiff) / Math.log(2.0))
        val zoom = Math.min(latZoom, lngZoom)
        return Math.min(Math.max(zoom, 0.0), ZOOM_MAX.toDouble()) + 3
    }

    override fun onClick(item: View?) {
        when (item?.id) {
            R.id.button_conform -> {
                val bundle = bundleOf(Pair("key_address", address))
                navController.navigate(R.id.nav_add_address_popup, bundle)
            }

            R.id.button_cancel -> navController.popBackStack()
            R.id.fabLocation -> {}
        }
    }

    private fun setMarker(point: GeoPoint) {
        val marker = Marker(map)
        marker.position = point
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = "Bu mening joylashuvim"
        val d = ResourcesCompat.getDrawable(
            resources, org.osmdroid.library.R.drawable.marker_default, null
        )
        val bitmap = (d as BitmapDrawable?)!!.bitmap
        val dr: Drawable = BitmapDrawable(
            resources, Bitmap.createScaledBitmap(
                bitmap,
                (48.0f * resources.displayMetrics.density).toInt(),
                (48.0f * resources.displayMetrics.density).toInt(),
                true
            )
        )
        marker.icon = dr
        map?.overlays?.add(marker)
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
                    mViewModel.geoCode(center?.latitude ?: 0.0, center?.longitude ?: 0.0, "uz")
                    shimmer.start(binding.addressDescription)

                    /*      val marker = Marker(map)
                          marker.position = (center?:GeoPoint(41.213383, 69.246071)) as GeoPoint?
                          marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                          marker.title = "Bu mening joylashuvim"
                          val d = ResourcesCompat.getDrawable(resources, org.osmdroid.library.R.drawable.marker_default, null)
                          val bitmap = (d as BitmapDrawable?)!!.bitmap
                          val dr: Drawable = BitmapDrawable(
                              resources,
                              Bitmap.createScaledBitmap(
                                  bitmap,
                                  (48.0f * resources.displayMetrics.density).toInt(),
                                  (48.0f * resources.displayMetrics.density).toInt(),
                                  true
                              )
                          )
                          marker.icon = dr
                          map?.overlays?.add(marker)*/
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
            mViewModel.geoCode(center?.latitude ?: 0.0, center?.longitude ?: 0.0, "uz")
            shimmer.start(binding.addressDescription)
        }
        Timber.d("$TAG onZoom ")
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

    override fun onNearPlace(place: NearbyPlace) {
        placeAdapter.submitList(emptyList())
        val geoPoint = GeoPoint(place.latitude, place.longitude)
        mapController?.animateTo(geoPoint)

        val center = map?.mapCenter
        mViewModel.geoCode(center?.latitude ?: 0.0, center?.longitude ?: 0.0, "uz")
        shimmer.start(binding.addressDescription)
    }
}
