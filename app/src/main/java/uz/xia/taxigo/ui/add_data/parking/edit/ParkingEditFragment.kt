package uz.xia.taxigo.ui.add_data.parking.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import dagger.hilt.android.AndroidEntryPoint
import uz.xia.taxigo.R
import uz.xia.taxigo.databinding.FragmentParkingEditBinding
import uz.xia.taxigo.ui.add_data.parking.edit.adapter.IRoadListener
import uz.xia.taxigo.ui.add_data.parking.edit.adapter.RoadsAdapter
import uz.xia.taxigo.utils.lazyFast
import uz.xia.taxigo.utils.widget.SimpleSpinner

@AndroidEntryPoint
class ParkingEditFragment : Fragment(), IRoadListener {

    private var _binding: FragmentParkingEditBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<ParkingEditViewModel>()
    private val roadAdapter by lazyFast { RoadsAdapter(this) }
    private val roadIds = mutableListOf<Int>()

    private var parkingId: Long = 0
    private var mLongitude: Double = 0.0
    private var mLatitude: Double = 0.0

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
        _binding = FragmentParkingEditBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parkingId = arguments?.getLong("key_parking_id") ?: 0L

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setUpObserver()
    }

    private fun setUpViews() {
        binding.rvRoads.adapter = roadAdapter
        binding.spProvince.itemSelectedListener = itemProvince
        binding.spRegion.itemSelectedListener = itemRegion

        binding.location.setOnClickListener {
            val bundle = bundleOf(
                Pair("key_latitude", mLatitude),
                Pair("key_longitude", mLongitude)
            )
            navController.navigate(R.id.nav_location, bundle)
        }
        binding.roadAdd.setOnClickListener {
            val bundle = bundleOf(
                Pair("key_road_ids", roadIds),
                Pair("key_parking_id", parkingId)
            )
            navController.navigate(R.id.roadListFragment, bundle)
        }
        setFragmentResultListener("key_location") { _, b ->
            val latitude = b.getDouble("key_latitude")
            val longitude = b.getDouble("key_longitude")
            viewModel.setLocation(latitude, longitude)
            binding.etLocation.setText("$latitude, $longitude")
        }
        binding.buttonCancel.setOnClickListener {
            navController.navigateUp()
        }

        binding.buttonConform.setOnClickListener {
            val name = binding.etAddressName.text.toString()
            viewModel.saveParking(name)
            binding.etAddressName.setText("")
            binding.etLocation.setText("")
            navController.navigateUp()
        }
    }

    private fun setUpObserver() {
        viewModel.getParkingById(parkingId)
        viewModel.liveRegions.observe(viewLifecycleOwner) {
            binding.spProvince.setSpinnerData(it.second, true)
            binding.spProvince.setSelection(it.first)
        }

        viewModel.liveDistricts.observe(viewLifecycleOwner) {
            binding.spRegion.setSpinnerData(it.second, true)
            binding.spRegion.setSelection(it.first)
        }
        viewModel.liveRoads.observe(viewLifecycleOwner) {
            roadIds.clear()
            it.forEach { r ->
                roadIds.add(r.id.toInt())
            }
            roadAdapter.submitList(it)
        }
        viewModel.liveParkingData.observe(viewLifecycleOwner) {
            binding.etAddressName.setText(it.nameUzLt)
            binding.etLocation.setText("${it.longitude},${it.latitude}")
            mLongitude = it.longitude
            mLatitude = it.latitude
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDelete(id: Long) {
        viewModel.deleteRoads(id)
    }
}
