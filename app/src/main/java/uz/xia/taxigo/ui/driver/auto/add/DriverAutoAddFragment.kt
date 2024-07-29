package uz.xia.taxigo.ui.driver.auto.add

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.CompoundButton
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import uz.xia.taxigo.R
import uz.xia.taxigo.data.IPreference
import uz.xia.taxigo.data.remote.model.car.AutoModel
import uz.xia.taxigo.data.remote.model.car.CarColorType
import uz.xia.taxigo.data.remote.model.car.CarDataRequest
import uz.xia.taxigo.data.remote.model.car.CarDataResponse
import uz.xia.taxigo.databinding.FragmentDriverAutoAddBinding
import uz.xia.taxigo.utils.dateFormat
import uz.xia.taxigo.utils.gone
import uz.xia.taxigo.utils.lazyFast
import uz.xia.taxigo.utils.show
import uz.xia.taxigo.utils.widget.IWatcher
import uz.xia.taxigo.utils.widget.SimpleSpinner
import uz.xia.taxigo.utils.widget.SimpleTextWatcher
import uz.xia.taxigo.utils.widget.base.BaseFragment
import uz.xia.taxigo.utils.widget.base.ResourceUI
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class DriverAutoAddFragment : BaseFragment(R.layout.fragment_driver_auto_add),
    CompoundButton.OnCheckedChangeListener {

    private val binding: FragmentDriverAutoAddBinding by viewBinding()
    private val mViewModel: DriverAutoAddViewModel by viewModels()
    private val navController by lazyFast {
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main)
    }

    @Inject
    lateinit var preference: IPreference
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy")
    private val autoYearCalendar = Calendar.getInstance()


    private val autoYearDateListener =
        DatePickerDialog.OnDateSetListener { dialog, year: Int, month: Int, day: Int ->
            autoYearCalendar.set(year, month, day)
            val selectDate = dateFormat.format(autoYearCalendar.time)
            binding.etAutoDate.text = selectDate
            mViewModel.carData.carManufactureDate = autoYearCalendar.timeInMillis
        }

    private val autoYearDateDialog by lazyFast {
        DatePickerDialog(
            requireContext(),
            R.style.MyDatePickerTheme,
            autoYearDateListener,
            autoYearCalendar.get(Calendar.YEAR),
            autoYearCalendar.get(Calendar.MONTH),
            autoYearCalendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    private val carStatusObserver = Observer<ResourceUI<CarDataResponse>?> {
        when (it) {
            ResourceUI.Loading -> binding.progressBar.show()
            is ResourceUI.Resource -> {
                val data = it.data
                binding.progressBar.gone()
                val gson = Gson()
                if (data?.mainCar == true) {
                    preference.mainCarId = data.id
                    preference.userCarData = gson.toJson(data,CarDataResponse::class.java)
                }
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
        binding.errorAutoDate.isVisible = it
    }

    private val autoColorTypeSelectListener = object : SimpleSpinner.ItemSelectedListener {
        override fun onItemSelected(position: Int) {
            mViewModel.setColorType(position)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val carData = arguments?.getParcelable<CarDataResponse>("car_data")
        if (carData != null) {
            mViewModel.carData = CarDataRequest(
                id = carData.id,
                autoData = carData.autoData,
                color = carData.color,
                number = carData.number,
                carManufactureDate = dateFormat().parse(carData.carManufactureDate).time,
                stove = carData.stove,
                conditioner = carData.conditioner,
                baggage = carData.baggage,
                switcherBaggage = carData.switcherBaggage
            )
        }

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
        binding.switcherStove.setOnCheckedChangeListener(this)
        binding.switcherAirConditioner.setOnCheckedChangeListener(this)
        binding.switcherBaggage.setOnCheckedChangeListener(this)
        binding.switcherRoofBaggage.setOnCheckedChangeListener(this)
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
            etAutoDate.setOnClickListener {
                autoYearDateDialog.show()
            }
            buttonCancel.setOnClickListener {
                navController.navigateUp()
            }
            buttonConform.setOnClickListener {
                mViewModel.validateForms(preference.userId)
            }
        }
    }

    private fun setData(model: CarDataRequest) {
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
        binding.etAutoDate.text = dateFormat.format(Date(model.carManufactureDate))
        autoYearCalendar.timeInMillis = model.carManufactureDate

        binding.switcherStove.isChecked = model.stove
        binding.switcherAirConditioner.isChecked = model.conditioner
        binding.switcherBaggage.isChecked = model.baggage
        binding.switcherRoofBaggage.isChecked = model.switcherBaggage
    }

    override fun onCheckedChanged(view: CompoundButton?, isChecked: Boolean) {
        when (view?.id) {
            R.id.switcher_stove -> mViewModel.carData.stove = isChecked
            R.id.switcher_air_conditioner -> mViewModel.carData.conditioner = isChecked
            R.id.switcher_baggage -> mViewModel.carData.baggage = isChecked
            R.id.switcher_roof_baggage -> mViewModel.carData.switcherBaggage = isChecked
            else -> {}
        }
    }

}
