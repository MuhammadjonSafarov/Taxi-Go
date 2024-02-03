package uz.xia.taxigo.ui.home.cars

import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import uz.xia.taxigo.R
import uz.xia.taxigo.data.IPreference
import uz.xia.taxigo.databinding.FragmentCarsBinding
import uz.xia.taxigo.ui.home.cars.detail.CarInfoDialogFragment
import uz.xia.taxigo.utils.getBitmapFromVector
import uz.xia.taxigo.utils.isCheckLocationPermission
import javax.inject.Inject
@AndroidEntryPoint
class CarsFragment:Fragment(), Marker.OnMarkerClickListener,
    CarInfoDialogFragment.MyDialogCloseListener {

    private var _binding: FragmentCarsBinding? = null
    private var oldMarker: Marker?=null
    private val binding get() = _binding!!

    private var map: MapView? = null
    private var mapController: IMapController? = null
    @Inject
    lateinit var preference: IPreference

    private val carsList = listOf(
        GeoPoint(41.256874, 69.192289),
        GeoPoint(41.256739, 69.192525),
        GeoPoint(41.256872, 69.192836),
        GeoPoint(41.257132, 69.193260)
    )
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCarsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val ctx: Context? = context?.applicationContext
        Configuration.getInstance().load(
            ctx, androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext())
        )
        map = binding.mapView
        map?.setTileSource(TileSourceFactory.MAPNIK)
        map?.setBuiltInZoomControls(true)
        map?.setMultiTouchControls(true)
        //map?.getOverlayManager()?.getTilesOverlay()?.setColorFilter(TilesOverlay.INVERT_COLORS)
        mapController = map?.controller
        mapController?.setZoom(12)
        val startPoint = GeoPoint(preference.latitude, preference.longitude)
        mapController?.setCenter(startPoint)
        mapController?.animateTo(startPoint)
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
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       // val pointMarkers = RadiusMarkerClusterer(requireContext())
        carsList.forEach {
            setCar(it)
        }
    }
    private fun setCar(point:GeoPoint){
        val marker = Marker(map)
        marker.position = point
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        val bitmap = requireContext().getBitmapFromVector(R.drawable.ic_car_white)
        val dr: Drawable = BitmapDrawable(resources,bitmap)
        marker.icon = dr
        marker.setOnMarkerClickListener(this)
        map?.overlays?.add(marker)
    }

    override fun onMarkerClick(marker: Marker?, mapView: MapView?): Boolean {
        mapView?.overlays?.remove(marker)
        val bitmap = requireContext().getBitmapFromVector(R.drawable.ic_car_white)
        val dr: Drawable = BitmapDrawable(resources, bitmap)
        marker?.icon = dr
        mapView?.overlays?.add(marker)
        oldMarker = marker
        mapView?.invalidate()
        // mapController?.setCenter(marker.position)
        if (mapView!!.zoomLevel < 17) {
            mapController?.setZoom(18)
        }
        val point = marker?.position
        mapController?.animateTo(point)

        val parkDialog = CarInfoDialogFragment.newInstaince(1)
        parkDialog.setListener(this)
        parkDialog.show(childFragmentManager, "tag")
        //homeViewModel.loadParking(point.longitude, point.latitude)
        return true
    }

    override fun onClickListener(latitude: Double, longitude: Double) {

    }

    override fun handleDialogClose(dialog: DialogInterface) {
        if (oldMarker != null) {
            map?.overlays?.remove(oldMarker)
            val bitmap = requireContext().getBitmapFromVector(R.drawable.ic_car_white)
            val dr: Drawable = BitmapDrawable(resources, bitmap)
            oldMarker?.icon = dr
            map?.overlays?.add(oldMarker)
        }
    }
}
