package uz.xia.taxigo.ui.driver.auto.list

import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import uz.xia.taxigo.R
import uz.xia.taxigo.data.IPreference
import uz.xia.taxigo.data.remote.model.car.CarDataResponse
import uz.xia.taxigo.data.remote.model.paging.PagingResponse
import uz.xia.taxigo.data.remote.model.user.UserData
import uz.xia.taxigo.databinding.FragmentDriverAutosBinding
import uz.xia.taxigo.ui.driver.auto.list.adapter.DriverCarsAdapter
import uz.xia.taxigo.ui.driver.auto.list.adapter.MyDriverCarsAdapter
import uz.xia.taxigo.utils.gone
import uz.xia.taxigo.utils.lazyFast
import uz.xia.taxigo.utils.show
import uz.xia.taxigo.utils.widget.base.BaseFragment
import uz.xia.taxigo.utils.widget.base.ResourceUI
import javax.inject.Inject

@AndroidEntryPoint
class DriverCarsFragment : BaseFragment(R.layout.fragment_driver_autos),
    MyDriverCarsAdapter.Callback {

    private val mViewModel by viewModels<DriverCarsViewModel>()
    private val binding: FragmentDriverAutosBinding by viewBinding()

    @Inject
    lateinit var preference: IPreference
    private val mAdapter by lazyFast { MyDriverCarsAdapter(this) }

    private val carsObserver = Observer<ResourceUI<PagingResponse<List<CarDataResponse>>>?> {
        when (it) {
            ResourceUI.Loading -> binding.progressBar.show()
            is ResourceUI.Resource -> {
                binding.progressBar.gone()
                setCarsData(it.data?.content)
            }

            is ResourceUI.Error -> {
                binding.progressBar.gone()
                Timber.d("response $it")
            }

            else -> binding.progressBar.gone()
        }
    }

    private fun setCarsData(content: List<CarDataResponse>?) {
        mAdapter.setCarData(preference.mainCarId)
        mAdapter.setList(content)
    }

    private val userObserver = Observer<ResourceUI<CarDataResponse>?> {
        when (it) {
            is ResourceUI.Error -> binding.progressBar.gone()
            ResourceUI.Loading -> binding.progressBar.show()
            is ResourceUI.Resource -> {
                binding.progressBar.gone()
                preference.mainCarId = it.data?.id ?: 0L
                mAdapter.setCarData(preference.mainCarId)
                val gson = Gson()
                preference.userCarData = gson.toJson(it.data,CarDataResponse::class.java)
                mViewModel.getCars(preference.userId)
            }
            else -> binding.progressBar.gone()
        }
    }

    private val navController by lazyFast {
        Navigation.findNavController(
            requireActivity(),
            R.id.nav_host_fragment_content_main
        )
    }

    override fun setup() {
        super.setup()
        binding.rvDriverCars.adapter = mAdapter
        binding.floatingActionButton.setOnClickListener {
            navController.navigate(R.id.nav_car_add)
        }

    }

    override fun initialize(view: View) {
        super.initialize(view)
        mViewModel.getCars(preference.userId)
    }

    override fun observe() {
        super.observe()
        mViewModel.liveCarsData.observe(viewLifecycleOwner, carsObserver)
        mViewModel.liveCarMainChange.observe(viewLifecycleOwner, userObserver)
    }

    override fun itemClicked(item: CarDataResponse) {
        val bundle = bundleOf()
        bundle.putParcelable("car_data", item)
        navController.navigate(R.id.nav_car_add, bundle)
    }

    override fun itemMainCardChange(cardId: Long) {
        mViewModel.updateMainCard(preference.userId, cardId)
    }
}
