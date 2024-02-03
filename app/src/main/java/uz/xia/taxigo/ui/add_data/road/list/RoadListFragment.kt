package uz.xia.taxigo.ui.add_data.road.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import dagger.hilt.android.AndroidEntryPoint
import uz.xia.taxigo.R
import uz.xia.taxigo.data.local.entity.RoadData
import uz.xia.taxigo.databinding.FragmentRoadsBinding
import uz.xia.taxigo.ui.add_data.parking.list.adapter.ParkingAdapter
import uz.xia.taxigo.ui.add_data.road.list.adapter.RoadsAdapter
import uz.xia.taxigo.utils.lazyFast

@AndroidEntryPoint
class RoadListFragment : Fragment(), RoadsAdapter.Callback {

    private var _binding: FragmentRoadsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<RoadListViewModel>()
    private val roadsAdapter by lazyFast { RoadsAdapter(this) }
    private var ids = mutableListOf<Int>()
    private var mId:Long=0
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
        _binding = FragmentRoadsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mId = arguments?.getLong("key_parking_id")?:0
        ids.addAll(arguments?.getIntegerArrayList("key_road_ids").orEmpty())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setUpObserver()
    }

    private fun setUpViews() {
        binding.rvRoads.adapter = roadsAdapter
        binding.floatingActionButton.setOnClickListener {
            navController.navigate(R.id.nav_add_road)
        }
    }

    private fun setUpObserver() {
        viewModel.getRoads(ids)
        viewModel.liveRoadList.observe(viewLifecycleOwner) {
            roadsAdapter.submitList(it)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun itemClicked(item: RoadData) {
        val bundle = bundleOf(
            Pair("key_road_id", item.id),
            Pair("key_road_name",item.name),
            Pair("key_road_distance", item.destination)
        )
        navController.navigate(R.id.roadEditFragment, bundle)
    }

    override fun itemLongClicked(item: RoadData) {
        ids.add(item.id.toInt())
        viewModel.setRoadJoinParking(ids,mId, item.id)
    }
}
