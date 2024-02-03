package uz.xia.taxigo.ui.add_data.parking.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import dagger.hilt.android.AndroidEntryPoint
import uz.xia.taxigo.R
import uz.xia.taxigo.databinding.FragmentParkingAddBinding
import uz.xia.taxigo.utils.lazyFast
import uz.xia.taxigo.utils.widget.SimpleSpinner
@AndroidEntryPoint
class ParkingCreateFragment : Fragment() {

    private var _binding: FragmentParkingAddBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<ParkingCreateViewModel>()

    private val itemProvince = object : SimpleSpinner.ItemSelectedListener {
        override fun onItemSelected(position: Int) {
            viewModel.districtsAll(position)
        }
    }

    private val itemRegion = object : SimpleSpinner.ItemSelectedListener {
        override fun onItemSelected(position: Int) {
            viewModel.setDistrict(position)
        }
    }

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
        _binding = FragmentParkingAddBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.liveRegions.observe(viewLifecycleOwner) {
            binding.spProvince.setSpinnerData(it, true)
        }
        viewModel.liveDistricts.observe(viewLifecycleOwner) {
            binding.spRegion.setSpinnerData(it, true)
        }
        binding.spProvince.itemSelectedListener = itemProvince
        binding.spRegion.itemSelectedListener = itemRegion

        binding.location.setOnClickListener {
            navController.navigate(R.id.nav_location)
        }
        setFragmentResultListener("key_location") { _, b ->
            val latitude = b.getDouble("key_latitude")
            val longitude = b.getDouble("key_longitude")
            viewModel.setLocation(latitude, longitude)
            binding.etLocation.setText("$latitude, $longitude")
        }
        binding.buttonConform.setOnClickListener {
            val name = binding.etAddressName.text.toString()
            viewModel.saveParking(name)
            binding.etAddressName.setText("")
            binding.etLocation.setText("")
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
