package uz.xia.taxigo.ui.home.roads

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import org.osmdroid.api.IMapController
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import timber.log.Timber
import uz.xia.taxigo.R
import uz.xia.taxigo.data.IPreference
import uz.xia.taxigo.databinding.FragmentRoadBinding
import uz.xia.taxigo.utils.getBitmapFromVector
import uz.xia.taxigo.utils.isCheckLocationPermission
import javax.inject.Inject

private const val TAG = "RoadFragment"
@AndroidEntryPoint
class RoadFragment : Fragment() {
    private var _binding: FragmentRoadBinding? = null
    private val binding get() = _binding!!
    private var map: MapView? = null
    private var mapController: IMapController? = null

    @Inject
    lateinit var preferences: IPreference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoadBinding.inflate(inflater, container, false)
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
        mapController?.setZoom(15)
        val startPoint = GeoPoint(preferences.latitude, preferences.longitude)
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
        val list = arrayListOf<GeoPoint>()
        val startPoint=GeoPoint(41.257016, 69.192334)
        val point1=GeoPoint(40.895045, 68.681595)
        val point2=GeoPoint(40.607358, 68.698109)
        val point3=GeoPoint(39.651057, 66.963285)
        list.add(startPoint)
        list.add(point1)
        list.add(point2)
        list.add(point3)
        RouteTask(list, requireContext()).execute()
        val centerPoint=getCenterBetweenPoints(startPoint,point3)
        mapController?.setZoom(getZoomLevel(startPoint,point3))
        mapController?.setCenter(centerPoint)
        mapController?.animateTo(centerPoint)
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
        return Math.min(Math.max(zoom, 0.0), ZOOM_MAX.toDouble())+3
    }

    inner class RouteTask(
        private val points: ArrayList<GeoPoint>, context: Context
    ) : AsyncTask<Void, Void, String>() {
        private val roadManager: RoadManager = OSRMRoadManager(context, "")
        override fun doInBackground(vararg p0: Void?): String {
            try {
                val road = roadManager.getRoad(points)
                val roadOverlay: Polyline = RoadManager.buildRoadOverlay(road, Color.BLUE, 14f)
                Log.d(TAG, "overlys ${roadOverlay.points}")
                map!!.overlays.add(roadOverlay)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return ""
        }

        override fun onPostExecute(result: String?) {
            Timber.d("$TAG task finished")
            map?.invalidate()
            super.onPostExecute(result)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
