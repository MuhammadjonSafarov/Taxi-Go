package uz.xia.taxigo.ui.driver.auto.add.model_type

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import uz.xia.taxigo.R
import uz.xia.taxigo.data.remote.model.car.AutoModel
import uz.xia.taxigo.data.remote.model.paging.PagingResponse
import uz.xia.taxigo.databinding.FragmentAutoModelSelectedBinding
import uz.xia.taxigo.utils.gone
import uz.xia.taxigo.utils.lazyFast
import uz.xia.taxigo.utils.show
import uz.xia.taxigo.utils.widget.base.BaseFragment
import uz.xia.taxigo.utils.widget.base.ResourceUI

@AndroidEntryPoint
class DriverAutoModelFragment : BaseFragment(R.layout.fragment_auto_model_selected),
    DriverAutoModelAdapter.Callback {

    private val binding: FragmentAutoModelSelectedBinding by viewBinding()
    private val viewModel: DriverAutoModelViewModel by viewModels()
    private val mAdapter by lazyFast { DriverAutoModelAdapter(this) }
    private val navController by lazyFast {
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main)
    }
    private val dataObserver = Observer<ResourceUI<PagingResponse<List<AutoModel>>>?> {
        when (it) {
            ResourceUI.Loading -> {
                binding.progressBar.show()
            }
            is ResourceUI.Resource -> {
                binding.progressBar.gone()
                mAdapter.submitList(it.data?.content)
            }
            is ResourceUI.Error -> {
                binding.progressBar.gone()
                toast(it.error.message?:"")
            }
            else -> binding.progressBar.gone()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.driverAutos("")
    }

    override fun setup() {
        super.setup()
        binding.rvDriverCars.adapter = mAdapter
        binding.etAutoModel.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isNotEmpty() && newText.length > 2) {
                    viewModel.driverAutos(newText)
                }
                return true
            }

        })
    }

    override fun observe() {
        super.observe()
        viewModel.liveData.observe(viewLifecycleOwner, dataObserver)
    }

    override fun itemClicked(item: AutoModel) {
        val bundle = bundleOf()
        bundle.putParcelable("auto_data",item)
        setFragmentResult("auto_model",bundle)
        navController.navigateUp()
    }
}