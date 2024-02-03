package uz.xia.taxigo.ui.add_data.location

import android.animation.ValueAnimator
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.airbnb.lottie.LottieDrawable
import com.romainpiel.shimmer.Shimmer
import dagger.hilt.android.AndroidEntryPoint
import org.osmdroid.api.IMapController
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.bonuspack.routing.RoadManager.buildRoadOverlay
import org.osmdroid.bonuspack.utils.BonusPackHelper
import org.osmdroid.config.Configuration
import org.osmdroid.events.DelayedMapListener
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import timber.log.Timber
import uz.xia.taxigo.R
import uz.xia.taxigo.common.EMPTY_STRING
import uz.xia.taxigo.common.TASHKENT_LATITUDE
import uz.xia.taxigo.common.TASHKENT_LONGITUDE
import uz.xia.taxigo.data.IPreference
import uz.xia.taxigo.data.local.entity.DistrictData
import uz.xia.taxigo.data.local.entity.RegionData
import uz.xia.taxigo.data.remote.model.nomination.NearbyPlace
import uz.xia.taxigo.databinding.FragmentLocationBinding
import uz.xia.taxigo.ui.address.add.adapter.LocationAdapter
import uz.xia.taxigo.utils.isCheckLocationPermission
import uz.xia.taxigo.utils.lazyFast
import javax.inject.Inject


private const val TAG = "LocationFragment"

@AndroidEntryPoint
class LocationFragment : Fragment(), View.OnTouchListener, MapListener,
    ValueAnimator.AnimatorUpdateListener, View.OnClickListener,
    LocationAdapter.OnPlaceClickListener, SearchView.OnQueryTextListener {
    private var _binding: FragmentLocationBinding? = null
    private val binding get() = _binding!!
    private var map: MapView? = null
    private var mapController: IMapController? = null
    private val animator = ValueAnimator.ofFloat(0.5f, 1f)
    private val viewModel by viewModels<LocationViewModel>()
    private var address: String = EMPTY_STRING
    private val navController by lazyFast {
        Navigation.findNavController(
            requireActivity(),
            R.id.nav_host_fragment_content_main
        )
    }
    private var regionName:String=""
    private val roadList = arrayListOf(
        GeoPoint(41.258830, 69.194940),
        GeoPoint(40.030424, 65.969301),
    )
    private var mLongitude: Double = 0.0
    private var mLatitude: Double = 0.0
    private val handlerThread = HandlerThread("")
    private val placeAdapter by lazyFast { LocationAdapter(this) }

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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLongitude = arguments?.getDouble("key_longitude") ?: TASHKENT_LONGITUDE
        mLatitude = arguments?.getDouble("key_latitude") ?: TASHKENT_LATITUDE
    }

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (requireContext().isCheckLocationPermission()) {
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
        binding.recyclerAdjust.adapter = placeAdapter

        binding.etAddress.setOnQueryTextListener(this)
        binding.buttonConform.setOnClickListener(this)
        binding.fabLocation.setOnClickListener(this)
        binding.menuRegion.setOnClickListener(this)

        /* ping point*/
        mapController?.setZoom(15)
        val startPoint = GeoPoint(mLatitude, mLongitude)
        mapController?.setCenter(startPoint)
        mapController?.animateTo(startPoint)
    }

    override fun onClick(item: View?) {
        when (item?.id) {
            R.id.button_conform -> {
                val bundle = bundleOf(
                    Pair("key_latitude", mLatitude),
                    Pair("key_longitude", mLongitude),
                )
                setFragmentResult("key_location", bundle)
                navController.popBackStack()
            }

            R.id.button_cancel -> navController.navigateUp()
            R.id.fabLocation -> {}
            R.id.menu_region -> {
                viewModel.provinceAll()
            }
        }
    }

    private fun setUpObserver() {
        val roadManager: RoadManager = OSRMRoadManager(context, "")
        handlerThread.start()
        val handler = Handler(handlerThread.looper)
        handler.post {
            val road = roadManager.getRoad(roadList)
            val roadOverlay: Polyline =
                buildRoadOverlay(road, resources.getColor(R.color.colorRoute), 14f)
            map!!.overlays.add(roadOverlay)
        }
        viewModel.livePolygonData.observe(viewLifecycleOwner) {
            it.forEach { polygonData ->
                val polygons = mutableListOf<GeoPoint>()
                polygonData.geojson.coordinates.forEach {
                    it.forEach { map ->
                        polygons.add(GeoPoint(map[1] ?: 0.0, map[0] ?: 0.0))
                    }
                }
                val polygon = Polygon()
                polygon.points = polygons
                polygon.fillColor = resources.getColor(R.color.colorAccentTransparent)
                polygon.strokeColor = resources.getColor(R.color.colorRoute)
                polygon.strokeWidth = 4.0f
                map?.overlays?.add(polygon)
            }
        }



        viewModel.livePlace.observe(viewLifecycleOwner) {
            shimmer.cancel()
            address = it.name
            binding.addressDescription.text = it.name
        }
        viewModel.livePlaceList.observe(viewLifecycleOwner) {
            binding.recyclerAdjust.isVisible = it.isNotEmpty()
            placeAdapter.submitList(it)
        }

        viewModel.liveRegions.observe(viewLifecycleOwner) {
            DialogRegions().apply {
                setData(it)
                onItemClickListener = ::onSelectedRegion
            }.also { dialog ->
                dialog.show(childFragmentManager, "RegionDialog")
            }
        }

        viewModel.liveDistricts.observe(viewLifecycleOwner) {
            DialogDistricts().apply {
                setData(it)
                onItemClickListener = ::onSelectedDistrict
            }.also { dialog ->
                dialog.show(childFragmentManager, "DistrictDialog")
            }
        }


    }

    private fun onSelectedRegion(id: RegionData) {
        regionName=id.nameUzKr
        viewModel.districtsById(id.id)
    }

    private fun onSelectedDistrict(item: DistrictData) {
        val startPoint = GeoPoint(item.latitude, item.longitude)
        mapController?.setCenter(startPoint)
        mapController?.animateTo(startPoint)
        //viewModel.getRegionPolygon("${item.nameUzLt},${regionName.split(" ")[0]?:""}")
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
                    mLatitude = center?.latitude ?: TASHKENT_LATITUDE
                    mLongitude = center?.longitude ?: TASHKENT_LONGITUDE
                    viewModel.geoCode(
                        center?.latitude ?: 0.0,
                        center?.longitude ?: 0.0,
                        preference.language
                    )
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
            mLatitude = center?.latitude ?: TASHKENT_LATITUDE
            mLongitude = center?.longitude ?: TASHKENT_LONGITUDE
            viewModel.geoCode(
                center?.latitude ?: 0.0,
                center?.longitude ?: 0.0,
                preference.language
            )
            shimmer.start(binding.addressDescription)
        }
        return false
    }

    override fun onNearPlace(place: NearbyPlace) {
        placeAdapter.submitList(emptyList())
        val geoPoint = GeoPoint(place.latitude, place.longitude)
        mapController?.animateTo(geoPoint)
        val center = map?.mapCenter
        viewModel.geoCode(
            latitude = center?.latitude ?: 0.0,
            longitude = center?.longitude ?: 0.0,
            lang = "uz")
        shimmer.start(binding.addressDescription)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onQueryTextSubmit(text: String?) = false

    override fun onQueryTextChange(newText: String): Boolean {
        if (newText.length > 2)
            viewModel.searchPlace(newText, preference.language)
        else if (newText.isEmpty()) {
            viewModel.loadNearbyPlaces()
        }
        return true
    }


}
