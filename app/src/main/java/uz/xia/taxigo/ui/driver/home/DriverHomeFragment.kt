package uz.xia.taxigo.ui.driver.home

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.ncorti.slidetoact.SlideToActView
import dagger.hilt.android.AndroidEntryPoint
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import uz.xia.taxigo.R
import uz.xia.taxigo.data.IPreference
import uz.xia.taxigo.databinding.FragmentHomeDriverBinding
import uz.xia.taxigo.utils.getBitmapFromVector
import uz.xia.taxigo.utils.lazyFast
import javax.inject.Inject

@AndroidEntryPoint
class DriverHomeFragment : Fragment(R.layout.fragment_home_driver) {
    private var _binding: FragmentHomeDriverBinding? = null
    private val binding get() = _binding!!
    private var map: MapView? = null
    private var mapController: IMapController? = null
    private val navController by lazyFast {
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main)
    }

    @Inject
    lateinit var preferences: IPreference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeDriverBinding.inflate(inflater, container, false)
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
        mapController?.animateTo(startPoint,13.9,1_500L)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.exampleGrayOnGreen.onSlideCompleteListener = object :
            SlideToActView.OnSlideCompleteListener {
            override fun onSlideComplete(view: SlideToActView) {
                navController.navigate(R.id.nav_home)
               // SelectStationFragment().show(childFragmentManager,"dialog_tools")
            }
        }
        val point = GeoPoint(preferences.latitude,preferences.longitude)
        val marker = Marker(map)
        marker.position = point
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        val bitmap = requireContext().getBitmapFromVector(R.drawable.car_white)
        val dr: Drawable = BitmapDrawable(resources, bitmap)
        marker.icon = dr
        map?.overlays?.add(marker)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}