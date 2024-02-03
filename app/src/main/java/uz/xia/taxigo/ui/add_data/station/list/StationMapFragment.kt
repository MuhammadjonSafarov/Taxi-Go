package uz.xia.taxigo.ui.add_data.station.list

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.airbnb.lottie.LottieDrawable
import dagger.hilt.android.AndroidEntryPoint
import org.osmdroid.api.IMapController
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
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
import uz.xia.taxigo.data.remote.model.StationMarkerData
import uz.xia.taxigo.data.remote.model.nomination.NearbyPlace
import uz.xia.taxigo.databinding.FragmentStationsMapBinding
import uz.xia.taxigo.ui.add_data.location.DialogDistricts
import uz.xia.taxigo.ui.add_data.location.DialogRegions
import uz.xia.taxigo.ui.add_data.location.LocationViewModel
import uz.xia.taxigo.ui.address.add.adapter.LocationAdapter
import uz.xia.taxigo.utils.getBitmapFromVector
import uz.xia.taxigo.utils.isCheckLocationPermission
import uz.xia.taxigo.utils.lazyFast
import javax.inject.Inject


private const val TAG = "StationMapFragment"

@AndroidEntryPoint
class StationMapFragment : Fragment(), View.OnClickListener,
    LocationAdapter.OnPlaceClickListener, SearchView.OnQueryTextListener, View.OnTouchListener,
    MapListener, ValueAnimator.AnimatorUpdateListener,
    Marker.OnMarkerLongClickListener {
    private var _binding: FragmentStationsMapBinding? = null
    private val binding get() = _binding!!
    private var map: MapView? = null
    private var mapController: IMapController? = null
    private val viewModel by viewModels<LocationViewModel>()
    private val stationViewModel by viewModels<StationListViewModel>()
    private val animator = ValueAnimator.ofFloat(0.5f, 1f)
    private var districtId: Long = 0L
    private var districtName:String=""

    private val navController by lazyFast {
        Navigation.findNavController(
            requireActivity(),
            R.id.nav_host_fragment_content_main
        )
    }
    private var regionName: String = ""
    private var addStationEnable:Boolean=false

    private val roadList = arrayListOf(
        GeoPoint(41.258830, 69.194940),
        GeoPoint(40.030424, 65.969301)
    )

    private var mLongitude: Double = TASHKENT_LONGITUDE
    private var mLatitude: Double = TASHKENT_LATITUDE

    private val handlerThread = HandlerThread("")
    private val placeAdapter by lazyFast { LocationAdapter(this) }
    private var ids = mutableListOf<Int>()
    private var roadId: Long = 0L

    @Inject
    lateinit var preference: IPreference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        roadId = arguments?.getLong("key_road_id") ?: 0
        ids.addAll(arguments?.getIntegerArrayList("key_station_ids").orEmpty())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStationsMapBinding.inflate(inflater, container, false)
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
        binding.menuRegion.setOnClickListener(this)
        binding.fabAddStation.setOnClickListener(this)

        binding.buttonConform.setOnClickListener(this)
        binding.buttonCancel.setOnClickListener(this)

        binding.fabAddRoad.setOnLongClickListener {
            roadList.clear()
            binding.animationView.visibility = View.VISIBLE
            binding.llButtons.visibility = View.VISIBLE
            binding.fabAddRoad.visibility = View.GONE
            binding.fabAddStation.visibility = View.GONE
            true
        }
        binding.fabAddStation.setOnLongClickListener {
            binding.animationView.visibility = View.VISIBLE
            addStationEnable=true
            true
        }
        /* ping point */
        mapController?.setZoom(15)
        val startPoint = GeoPoint(mLatitude, mLongitude)
        mapController?.setCenter(startPoint)
        mapController?.animateTo(startPoint)
    }

    override fun onClick(item: View?) {
        when (item?.id) {
            R.id.menu_region -> {
                viewModel.provinceAll()
            }
            R.id.button_conform -> {
                Toast.makeText(requireContext(), "$mLongitude, $mLatitude", Toast.LENGTH_SHORT)
                    .show()
                roadList.add(GeoPoint(mLatitude, mLongitude))
                if (roadList.size == 2) {
                    binding.animationView.visibility = View.GONE
                    binding.llButtons.visibility = View.GONE

                    binding.fabAddRoad.visibility = View.VISIBLE
                    binding.fabAddStation.visibility = View.VISIBLE
                    val roadManager: RoadManager = OSRMRoadManager(context, EMPTY_STRING)
                    val mHandlerThread = HandlerThread("nimadur")
                    mHandlerThread.start()
                    val handler = Handler(mHandlerThread.looper)
                    handler.post {
                        val road = roadManager.getRoad(roadList)
                        val roadOverlay: Polyline = RoadManager.buildRoadOverlay(
                            road,
                            resources.getColor(R.color.colorRoute),
                            15f
                        )
                        map!!.overlays.add(roadOverlay)
                        map?.invalidate()
                    }
                }
            }

            R.id.fab_add_station -> {
               if (addStationEnable)
               // viewModel.locationUpdate(districtId,mLatitude,mLongitude)
                createStation()
                else
                   Toast.makeText(requireContext(), "button disable", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createStation() {
        val editText = EditText(requireContext())
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        editText.layoutParams = lp
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Bekat yaratish")
            .setMessage("Bekat nomini kiriting \n$districtName")
            .setView(editText)
            .setPositiveButton("Saqlash") { d, _ ->
                val stateName = editText.text.toString()
                if (stateName.isNotEmpty()) {
                    viewModel.saveStation(
                        latitude = mLatitude,
                        longitude = mLongitude,
                        name = stateName,
                        districtId = districtId
                    )
                    setStartPoint(StationMarkerData(0,stateName,mLongitude,mLatitude,districtName,false))
                    binding.animationView.visibility = View.GONE
                    addStationEnable=false
                    Toast.makeText(requireContext(), "qo\'shildi", Toast.LENGTH_SHORT).show()
                } else {

                }
                d.dismiss()
            }
            .setNegativeButton("Bekor qilish") { d, _ ->
                d.dismiss()
            }
        alertDialog
            .create()
            .show()
    }

    private fun setUpObserver() {
        val roadManager: RoadManager = OSRMRoadManager(context, EMPTY_STRING)
        handlerThread.start()
        val handler = Handler(handlerThread.looper)
        handler.post {
            val road = roadManager.getRoad(roadList)
            val roadOverlay: Polyline = RoadManager.buildRoadOverlay(road, resources.getColor(R.color.colorRoute), 14f)
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

        stationViewModel.getStations(ids)
        stationViewModel.liveRoadList.observe(viewLifecycleOwner) {
           it.forEach { s ->
                setStartPoint(s)
            }
        }
        stationViewModel.liveDistricts.observe(viewLifecycleOwner){
            it.forEach { s ->
                val point = GeoPoint(s.districtData.latitude, s.districtData.longitude)
                val marker = Marker(map)
                marker.position = point
                marker.markerId = s.districtData.id
                marker.title = s.districtData.nameUzLt
                marker.subDescription = s.regionData.nameUzLt
                val bitmap = requireContext().getBitmapFromVector(R.drawable.district_marker)
                marker.setOnMarkerLongClickListener(this)
                val dr: Drawable = BitmapDrawable(resources, bitmap)
                marker.icon = dr
                map?.overlays?.add(marker)
            }
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

                    val center = map?.mapCenter
                    mLatitude = center?.latitude ?: TASHKENT_LATITUDE
                    mLongitude = center?.longitude ?: TASHKENT_LONGITUDE
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
        }
        return false
    }

    private fun setStartPoint(stationMarkerData: StationMarkerData) {
        val point = GeoPoint(stationMarkerData.latitude, stationMarkerData.longitude)
        val marker = Marker(map)
        marker.position = point
        marker.markerId = stationMarkerData.id
        marker.title = stationMarkerData.name
        marker.subDescription = stationMarkerData.regionName
        marker.setOnMarkerLongClickListener(this)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        val bitmap = if (stationMarkerData.isJoined)
            requireContext().getBitmapFromVector(R.drawable.marker_station_marker_join)
            else requireContext().getBitmapFromVector(R.drawable.marker_station_marker)
        val dr: Drawable = BitmapDrawable(resources, bitmap)
        marker.icon = dr
        map?.overlays?.add(marker)
    }

    private fun onSelectedRegion(id: RegionData) {
        regionName = id.nameUzKr
        viewModel.districtsById(id.id)
    }

    private fun onSelectedDistrict(item: DistrictData) {
        districtId = item.id
        districtName = item.nameUzLt
        val startPoint = GeoPoint(item.latitude, item.longitude)
        // mapController?.setCenter(startPoint)
        // mapController?.animateTo(startPoint)
        viewModel.getRegionPolygon("${item.nameUzLt},${regionName.split(" ")[0]}")
    }

    override fun onNearPlace(place: NearbyPlace) {
        placeAdapter.submitList(emptyList())
        val geoPoint = GeoPoint(place.latitude, place.longitude)
        mapController?.animateTo(geoPoint)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMarkerLongClick(marker: Marker?, mapView: MapView?): Boolean {
        regionName = marker?.subDescription?:""
        districtId = marker?.markerId?:0
        binding.animationView.visibility = View.VISIBLE
        addStationEnable=true
        return true
    }

//    override fun onMarkerLongClick(marker: Marker?, mapView: MapView?): Boolean {
//        val stationId=marker?.markerId?:0
//        viewModel.saveStationJoinRoad(roadId,stationId)
//        mapView?.overlays?.remove(marker)
//        val bitmap=requireContext().getBitmapFromVector(R.drawable.marker_station_marker_join)
//        val dr: Drawable = BitmapDrawable(resources, bitmap)
//        marker?.icon = dr
//        mapView?.overlays?.add(marker)
//        Toast.makeText(requireContext(), "${marker?.title ?: ""} $stationId", Toast.LENGTH_SHORT).show()
//        return true
//    }
}