package uz.xia.taxigo.ui.driver.home.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import uz.xia.taxigo.R
import uz.xia.taxigo.data.IPreference
import uz.xia.taxigo.data.local.entity.RoadData
import uz.xia.taxigo.data.local.remote.model.ParkingWithRegionDistrict
import uz.xia.taxigo.data.remote.model.car.CarDataResponse
import uz.xia.taxigo.databinding.FragmentSelectStationBinding
import uz.xia.taxigo.ui.driver.home.dialog.adapter.DriverCarToolsAdapter
import uz.xia.taxigo.utils.lazyFast
import uz.xia.taxigo.utils.widget.base.ResourceUI
import javax.inject.Inject

private const val TAG = "SelectStationDialog"

@AndroidEntryPoint
class DriverCarDetailDialog : BottomSheetDialogFragment() {
    private var _binding: FragmentSelectStationBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<DriverCarDetailViewModel>()
    private val mAdapter by lazyFast { DriverCarToolsAdapter() }

    private val parkingObserver = Observer<ResourceUI<CarDataResponse>?> {
        when (it) {
            is ResourceUI.Error -> {}
            ResourceUI.Loading -> {}
            is ResourceUI.Resource -> {
                dismiss()
            }
            else -> {}
        }
    }

    @Inject
    lateinit var preferences: IPreference

    override fun getTheme(): Int = R.style.CustomBottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectStationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val parkId = arguments?.getLong("key_parking_id") ?: 0L
        val roadId = arguments?.getLong("key_road_id") ?: 0L
        viewModel.loadParkingRoad(parkId, roadId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setUpObserver()

    }

    private fun setUpViews() {
        binding.rvTools.adapter = mAdapter
        binding.buttonClose.setOnClickListener {
            dismiss()
        }
        binding.buttonCancel.setOnClickListener {
            dismiss()
        }
        binding.buttonNext.setOnClickListener {
            startCarParking()
        }
    }

    private fun startCarParking() {
        viewModel.sendCarDetailParking(preferences.mainCarId)
    }

    private fun setUpObserver() {
        viewModel.liveStatus.observe(viewLifecycleOwner,parkingObserver)
        viewModel.liveParkData.observe(viewLifecycleOwner) {
            setData(it)
        }
    }

    private fun setData(it: Pair<ParkingWithRegionDistrict?, RoadData>) {
        binding.tvAddressName.text = it.first?.parkingData?.nameUzLt
        binding.tvAddressDescription.text =
            "${it.first?.regionData?.nameUzLt}, ${it.first?.districtData?.nameUzLt}"
        binding.name.text = it.second.name
        binding.destination.text = it.second.destination.toString()

        val userCarData = if (preferences.userCarData.isNotEmpty()) {
            val gson = Gson()
            gson.fromJson(preferences.userCarData, CarDataResponse::class.java)
        } else {
            CarDataResponse()
        }
        binding.tvCarName.text =
            "${getColorName(userCarData.color)} ${userCarData.autoData?.fullName}"
        binding.tvCarNumber.text = userCarData.number
        val tools = mutableListOf<String>()
        if (userCarData.conditioner) tools.add("Konditsioner")
        if (userCarData.stove) tools.add("Pechka")
        if (userCarData.baggage) tools.add("Yukxona")
        if (userCarData.switcherBaggage) tools.add("Tom yukxona")
        mAdapter.submitList(tools)
    }

    private fun getColorName(color: String): String {
        return when (color) {
            "WHITE" -> "Oq"
            "BLACK" -> "Qora"
            "RED" -> "Qizil"
            "BLUE" -> "Ko'k"
            else -> "Sariq"
        }
    }
}
