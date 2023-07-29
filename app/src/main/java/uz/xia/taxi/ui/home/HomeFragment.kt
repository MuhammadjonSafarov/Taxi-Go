package uz.xia.taxi.ui.home

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import org.osmdroid.api.IMapController
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.bonuspack.routing.RoadManager.buildRoadOverlay
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Marker.ANCHOR_BOTTOM
import org.osmdroid.views.overlay.Marker.ANCHOR_CENTER
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import timber.log.Timber
import uz.xia.taxi.R
import uz.xia.taxi.databinding.FragmentHomeBinding


private const val TAG = "HomeFragment"
private const val DEFAULT_INACTIVITY_DELAY_IN_MILLISECONDS = 200

class HomeFragment : Fragment(), View.OnTouchListener {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var map: MapView? = null
    private var mapController: IMapController? = null
    private var isTwoClick = false
    private var startPoint: GeoPoint = GeoPoint(41, 69)

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
        mapController?.setZoom(15)
        val startPoint = GeoPoint(41.213383, 69.246071)
        mapController?.setCenter(startPoint)
        mapController?.animateTo(startPoint)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mRotationGestureOverlay = RotationGestureOverlay(requireContext(), map)
        mRotationGestureOverlay.isEnabled = true
        map!!.setMultiTouchControls(true)
        map!!.overlays.add(mRotationGestureOverlay)

        map?.setOnTouchListener(this)
        val overlayEvents = MapEventsOverlay(requireContext(), mReceive)
        map?.overlays?.add(overlayEvents)
    }

    override fun onTouch(p0: View?, event: MotionEvent?): Boolean {
        return false
    }

    private val mReceive: MapEventsReceiver = object : MapEventsReceiver {
        override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
            Toast.makeText(
                requireContext(),
                p.latitude.toString() + " - " + p.longitude,
                Toast.LENGTH_LONG
            ).show()

            if (isTwoClick) {
                RouteTask(startPoint, p, requireContext()).execute()
                val marker = Marker(map)
                marker.position = p
                marker.setAnchor(ANCHOR_CENTER, ANCHOR_BOTTOM)
                marker.title = "BU mening vatanim"
                val d = ResourcesCompat.getDrawable(resources, R.drawable.ic_location, null)
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
                map?.overlays?.add(marker)
                isTwoClick = false
            } else {
                startPoint = p
                isTwoClick = true
            }
            return false
        }

        override fun longPressHelper(p: GeoPoint): Boolean {
            return false
        }
    }


    inner class RouteTask(
        private val startPoint: GeoPoint,
        private val endPoint: GeoPoint, context: Context
    ) : AsyncTask<Void, Void, String>() {
        private val roadManager: RoadManager = OSRMRoadManager(context, "")
        override fun doInBackground(vararg p0: Void?): String {
            val waypoints = ArrayList<GeoPoint>()
            waypoints.add(startPoint)
            waypoints.add(endPoint)
            try {
                val road = roadManager.getRoad(waypoints)
                Log.d(TAG,"roads : ${road.mRouteHigh}")
                val roadOverlay: Polyline = buildRoadOverlay(road, Color.BLUE, 8f)
                Log.d(TAG,"overlys ${roadOverlay.points}")
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