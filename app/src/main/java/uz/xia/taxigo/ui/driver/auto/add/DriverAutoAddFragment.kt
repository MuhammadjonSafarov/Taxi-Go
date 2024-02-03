package uz.xia.taxigo.ui.driver.auto.add

import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.AllCaps
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import uz.xia.taxigo.R
import uz.xia.taxigo.data.remote.model.car.AutoModel
import uz.xia.taxigo.data.remote.model.car.CarColorType
import uz.xia.taxigo.data.remote.model.car.CarData
import uz.xia.taxigo.databinding.FragmentDriverAutoAddBinding
import uz.xia.taxigo.utils.MaskWatcher
import uz.xia.taxigo.utils.dateFormat
import uz.xia.taxigo.utils.gone
import uz.xia.taxigo.utils.lazyFast
import uz.xia.taxigo.utils.show
import uz.xia.taxigo.utils.widget.IWatcher
import uz.xia.taxigo.utils.widget.SimpleSpinner
import uz.xia.taxigo.utils.widget.SimpleTextWatcher
import uz.xia.taxigo.utils.widget.base.BaseFragment
import uz.xia.taxigo.utils.widget.base.ResourceUI
import java.time.Instant
import java.util.Date
import java.util.Timer

@AndroidEntryPoint
class DriverAutoAddFragment : BaseFragment(R.layout.fragment_driver_auto_add) {

    private val binding: FragmentDriverAutoAddBinding by viewBinding()
    private val mViewModel: DriverAutoAddViewModel by viewModels()
    private val navController by lazyFast {
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main)
    }
    private val carStatusObserver = Observer<ResourceUI<CarData>?> {
        when (it) {
            ResourceUI.Loading -> binding.progressBar.show()
            is ResourceUI.Resource -> {
                binding.progressBar.gone()
                navController.navigateUp()
            }

            is ResourceUI.Error -> {
                binding.progressBar.gone()
                toast(it.error.message ?: "")
            }

            else -> binding.progressBar.gone()
        }
    }

    private val carTypeObserver = Observer<Boolean> {
        binding.errorCarType.isVisible = it
    }

    private val carColorObserver = Observer<Boolean> {
        binding.errorCarColor.isVisible = it
    }

    private val carNumberObserver = Observer<Boolean> {
        binding.InputLayoutAutoNumber.error =
            if (it) binding.InputLayoutAutoNumber
                .context.getString(R.string.auto_number_not_fount) else null
    }

    private val manufactureDate = Observer<Boolean> {
        binding.InputLayoutAutoDate.error =
            if (it) binding.InputLayoutAutoDate
                .context.getString(R.string.auto_date_not_fount) else null
    }

    private val autoColorTypeSelectListener = object : SimpleSpinner.ItemSelectedListener {
        override fun onItemSelected(position: Int) {
            mViewModel.setColorType(position)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val carData = arguments?.getParcelable<CarData>("car_data")
        mViewModel.carData = carData ?: CarData()
    }

    override fun setup() {
        super.setup()
        setFragmentResultListener("auto_model") { _, bundle ->
            val autoModel = bundle.getParcelable<AutoModel>("auto_data")
            mViewModel.carData.autoData = autoModel
            binding.etAutoType.text =
                HtmlCompat.fromHtml(autoModel?.text ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
        binding.spAutoColor.setSpinnerData(resources.getStringArray(R.array.colors).toList(), true)
        binding.spAutoColor.itemSelectedListener = autoColorTypeSelectListener
        binding.etAutoNumber.addTextChangedListener(SimpleTextWatcher(object : IWatcher {
            override fun onTextChange(text: String) {
                mViewModel.carData.number = text
            }
        }))
        binding.etAutoDate.addTextChangedListener(SimpleTextWatcher(object : IWatcher {
            override fun onTextChange(text: String) {
                mViewModel.carData.carManufactureDate = dateFormat().format(Date())
            }
        }))
        setData(mViewModel.carData)
    }

    override fun observe() {
        super.observe()
        mViewModel.liveCarDataStatus.observe(viewLifecycleOwner, carStatusObserver)
        mViewModel.liveTypeState.observe(viewLifecycleOwner, carTypeObserver)
        mViewModel.liveColorState.observe(viewLifecycleOwner, carColorObserver)
        mViewModel.liveNumberState.observe(viewLifecycleOwner, carNumberObserver)
        mViewModel.liveManufactureDateState.observe(viewLifecycleOwner, manufactureDate)

    }

    override fun clicks() {
        super.clicks()
        with(binding) {
            etAutoType.setOnClickListener {
                navController.navigate(R.id.nav_driverAutoModel)
            }
            buttonCancel.setOnClickListener {
                navController.navigateUp()
            }
            buttonConform.setOnClickListener {
                mViewModel.validateForms()
            }
        }
    }

    private fun setData(model: CarData) {
        var colorPosition = 0
        val colorTypes = CarColorType.values()
        for (it in colorTypes.indices) {
            if (colorTypes[it].name == model.color) {
                colorPosition = it
                break
            }
        }
        binding.spAutoColor.setSelection(colorPosition)
        binding.etAutoType.text =
            HtmlCompat.fromHtml(model.autoData?.text ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding.etAutoNumber.setText(model.number)

        if (model.carManufactureDate.isEmpty()) {

        } else {
            binding.etAutoDate.setText(model.carManufactureDate)
        }
        binding.switcherStove.isChecked = model.stove
        binding.switcherAirConditioner.isChecked = model.conditioner
        binding.switcherBaggage.isChecked = model.baggage
        binding.switcherRoofBaggage.isChecked = model.switcherBaggage
    }

}
