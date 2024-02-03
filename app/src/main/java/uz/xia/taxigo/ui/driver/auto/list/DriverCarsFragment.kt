package uz.xia.taxigo.ui.driver.auto.list

import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import uz.xia.taxigo.R
import uz.xia.taxigo.data.remote.model.car.CarData
import uz.xia.taxigo.data.remote.model.paging.PagingResponse
import uz.xia.taxigo.databinding.FragmentDriverAutosBinding
import uz.xia.taxigo.ui.driver.auto.list.adapter.DriverCarsAdapter
import uz.xia.taxigo.utils.gone
import uz.xia.taxigo.utils.lazyFast
import uz.xia.taxigo.utils.show
import uz.xia.taxigo.utils.widget.base.BaseFragment
import uz.xia.taxigo.utils.widget.base.ResourceUI

@AndroidEntryPoint
class DriverCarsFragment : BaseFragment(R.layout.fragment_driver_autos),
    DriverCarsAdapter.Callback {

    private val mViewModel by viewModels<DriverCarsViewModel>()
    private val binding: FragmentDriverAutosBinding by viewBinding()
    private val mAdapter by lazyFast { DriverCarsAdapter(this) }

    private val carsObserver = Observer<ResourceUI<PagingResponse<List<CarData>>>?> {
        when (it) {
            ResourceUI.Loading ->  binding.progressBar.show()
            is ResourceUI.Resource -> {
                binding.progressBar.gone()
                mAdapter.submitList(it.data?.content)
            }
            is ResourceUI.Error -> {
                binding.progressBar.gone()
                Timber.d("response $it")
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
        mViewModel.getCars()
    }
    override fun observe() {
        super.observe()
        mViewModel.liveCarsData.observe(viewLifecycleOwner, carsObserver)
    }

    override fun itemClicked(item: CarData) {
        val bundle= bundleOf()
        bundle.putParcelable("car_data",item)
        navController.navigate(R.id.nav_car_add,bundle)
    }
}
