package uz.xia.taxigo.ui.add_data.station.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import dagger.hilt.android.AndroidEntryPoint
import uz.xia.taxigo.R
import uz.xia.taxigo.data.local.entity.StationData
import uz.xia.taxigo.databinding.FragmentStationsBinding
import uz.xia.taxigo.ui.add_data.station.list.adapter.StationsAdapter
import uz.xia.taxigo.utils.lazyFast

@AndroidEntryPoint
class StationListFragment : Fragment(), StationsAdapter.Callback {

    private var _binding: FragmentStationsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<StationListViewModel>()
    private val roadsAdapter by lazyFast { StationsAdapter(this) }
    private var ids = mutableListOf<Int>()
    private var roadId: Long = 0L
    private val navController by lazyFast {
        Navigation.findNavController(
            requireActivity(), R.id.nav_host_fragment_content_main
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStationsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        roadId = arguments?.getLong("key_road_id") ?: 0
        ids.addAll(arguments?.getIntegerArrayList("key_station_ids").orEmpty())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setUpObserver()
    }

    private fun setUpViews() {
        binding.rvStations.adapter = roadsAdapter
        binding.floatingActionButton.setOnClickListener {
            navController.navigate(R.id.nav_add_station)
        }

    }

    private fun setUpObserver() {
        viewModel.getStations(ids)
        viewModel.liveRoadList.observe(viewLifecycleOwner) {
           // roadsAdapter.submitList(it)
        }
    }

    override fun itemClicked(item: StationData) {
        /* val bundle = bundleOf(
             Pair("key_road_id", item.id),
             Pair("key_road_name",item.name),
             Pair("key_road_distance", item.destination)
         )
         navController.navigate(R.id.roadEditFragment, bundle)*/
    }

    override fun itemLongClicked(item: StationData) {
        ids.add(item.id.toInt())
        viewModel.setStationJoinRoad(ids,roadId, item.id)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
