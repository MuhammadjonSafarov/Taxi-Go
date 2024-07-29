package uz.xia.taxigo.ui.participants.chat.location

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import dagger.hilt.android.AndroidEntryPoint
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import uz.xia.taxigo.R
import uz.xia.taxigo.common.EMPTY_STRING
import uz.xia.taxigo.common.TASHKENT_LATITUDE
import uz.xia.taxigo.common.TASHKENT_LONGITUDE
import uz.xia.taxigo.data.IPreference
import uz.xia.taxigo.databinding.FragmentChatViewLocationBinding
import uz.xia.taxigo.utils.getBitmapFromVector
import uz.xia.taxigo.utils.isLocationPermissionGranted
import uz.xia.taxigo.utils.lazyFast
import javax.inject.Inject

private const val TAG = "LocationSelectFragment"

@AndroidEntryPoint
class LocationMessageDetailFragment : Fragment(R.layout.fragment_chat_view_location) {
    private var _binding: FragmentChatViewLocationBinding? = null
    private val binding get() = _binding!!
    private var map: MapView? = null
    private var mapController: IMapController? = null
    private var address: String = EMPTY_STRING
    private var selectLongitude: Double = TASHKENT_LONGITUDE
    private var selectLatitude: Double = TASHKENT_LATITUDE
    private val navController by lazyFast {
        Navigation.findNavController(
            requireActivity(), R.id.nav_host_fragment_content_main
        )
    }

    @Inject
    lateinit var preference: IPreference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatViewLocationBinding.inflate(inflater, container, false)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectLongitude = arguments?.getDouble("key_longitude") ?: TASHKENT_LONGITUDE
        selectLatitude = arguments?.getDouble("key_latitude") ?: TASHKENT_LATITUDE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
    }

    private fun setUpViews() {
        val mRotationGestureOverlay = RotationGestureOverlay(requireContext(), map)
        mRotationGestureOverlay.isEnabled = true
        map!!.overlays.add(mRotationGestureOverlay)

        val point = GeoPoint(selectLatitude, selectLongitude)
        mapController?.setCenter(point)
        val marker = Marker(map)
        marker.position = point
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        val bitmap = requireContext().getBitmapFromVector(R.drawable.ic_yandex_maps_icon)
        val dr: Drawable = BitmapDrawable(resources, bitmap)
        marker.icon = dr
        map?.overlays?.add(marker)

        mapController?.animateTo(point)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
