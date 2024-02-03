package uz.xia.taxigo.ui.add_data.road.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import uz.xia.taxigo.R
import uz.xia.taxigo.databinding.FragmentRoadEditBinding
import uz.xia.taxigo.ui.add_data.road.edit.adapter.StationsAdapter
import uz.xia.taxigo.utils.lazyFast

private const val TAG = "RoadEditFragment"
@AndroidEntryPoint
class RoadEditFragment : Fragment(), StationsAdapter.ICallback {

    private var _binding: FragmentRoadEditBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<RoadEditViewModel>()
    private val stationAdapter by lazyFast { StationsAdapter(this) }

    private val navController by lazyFast {
        Navigation.findNavController(
            requireActivity(), R.id.nav_host_fragment_content_main
        )
    }
    private var roadId: Long = 0
    private var roadName: String = ""
    private var roadDistance: Long = 0

    private val stationIds = mutableListOf<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoadEditBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        roadId = arguments?.getLong("key_road_id") ?: 0L
        roadName = arguments?.getString("key_road_name") ?: ""
        roadDistance = arguments?.getLong("key_road_distance") ?: 0L
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setUpObserver()
    }

    private fun setUpViews() {
        binding.rvStations.adapter = stationAdapter
        binding.etRoadName.setText(roadName)
        binding.etDestination.setText("$roadDistance")

        binding.stationAdd.setOnClickListener {
            val bundle = bundleOf(
                Pair("key_road_id", roadId),
                Pair("key_station_ids", stationIds)
            )
            Timber.d("$TAG $stationIds")
            navController.navigate(R.id.stationMapFragment, bundle)
        }

        binding.stationJoin.setOnClickListener {
            val bundle = bundleOf(
                Pair("key_road_id", roadId),
                Pair("key_station_ids", stationIds)
            )
            navController.navigate(R.id.stationJoinMapFragment, bundle)
        }



        binding.buttonCancel.setOnClickListener {
            navController.navigateUp()
        }
        binding.buttonSave.setOnClickListener {
            val name = binding.etRoadName.text.toString()
            val destination = binding.etDestination.text.toString().toLong()
            viewModel.saveRoad(name, destination)
            navController.navigateUp()
        }

    }
    private fun setUpObserver() {
        viewModel.getRoadById(roadId)
        viewModel.liveStations.observe(viewLifecycleOwner) {
            stationIds.clear()
            it.forEach { s ->
                stationIds.add(s.stationData.id.toInt())
            }
            stationAdapter.submitList(it)
        }
    }

    override fun itemRemove(itemId: Long) {
        viewModel.removeRoadJoinStation(itemId)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
