package uz.xia.taxi.ui.address

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.xia.taxi.R
import uz.xia.taxi.databinding.FragmentAddressBinding
import uz.xia.taxi.ui.address.adapter.AddressAdapter
import uz.xia.taxi.utils.lazyFast

@AndroidEntryPoint
class AddressFragment : Fragment() {
    private var _binding: FragmentAddressBinding? = null
    private val binding get() = _binding!!
    private val mAdapter: AddressAdapter by lazyFast { AddressAdapter() }
    private val mViewModel :IAddressViewModel by viewModels<AddressViewModel>()
    private val navController by lazyFast {
        Navigation.findNavController(requireActivity(),
        R.id.nav_host_fragment_content_main) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvAddress.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAddress.adapter = mAdapter
        mViewModel.getAddresses().observe(viewLifecycleOwner){
            mAdapter.submitList(it)
        }

        binding.floatingActionButton.setOnClickListener {
            navController.navigate(R.id.nav_address_add)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}