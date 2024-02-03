package uz.xia.taxigo.ui.add_data.parking.list

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
import uz.xia.taxigo.databinding.FragmentParkingsBinding
import uz.xia.taxigo.ui.add_data.parking.list.adapter.ParkingAdapter
import uz.xia.taxigo.utils.lazyFast

@AndroidEntryPoint
class ParkingListFragment : Fragment(), ParkingAdapter.IParkingListener {

    private var _binding: FragmentParkingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<ParkingListViewModel>()
    private val parkingAdapter by lazyFast { ParkingAdapter(this) }

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
        _binding = FragmentParkingsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvParking.adapter = parkingAdapter
        viewModel.getParkings()
        viewModel.liveParkingList.observe(viewLifecycleOwner) {
            parkingAdapter.submitList(it)
        }
        binding.floatingActionButton.setOnClickListener {
            navController.navigate(R.id.nav_add_parking)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onParkItemClick(id: Long) {
        val bundle = bundleOf(Pair("key_parking_id", id))
        navController.navigate(R.id.parkingEditFragment, bundle)
    }
}
