package uz.xia.taxi.ui.bus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.xia.taxi.common.Status
import uz.xia.taxi.data.remote.model.bus.Schedule
import uz.xia.taxi.databinding.FragmentBusesBinding
import uz.xia.taxi.utils.gone
import uz.xia.taxi.utils.lazyFast
import uz.xia.taxi.utils.show
import uz.xia.taxi.widget.BounceEdgeEffectFactory

@AndroidEntryPoint
class BusesFragment : Fragment() {
    private var _binding: FragmentBusesBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<BusesViewModel>()
    private val mAdapter by lazyFast { BusAdapter() }
    private val statusObserver = Observer<Status>{
        when (it) {
            is Status.Error -> {
                binding.tvMessage.show()
                binding.progressCircular.gone()
                binding.tvMessage.setText(it.errorMsg)
            }
            Status.Loading -> binding.progressCircular.show()
            Status.Success -> binding.progressCircular.gone()
        }
    }
    private val dataObserver= Observer<List<Schedule>>{
        mAdapter.submitList(it)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBusesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setUpObserver()
    }
    private fun setUpViews(){
        val layoutManager=LinearLayoutManager(requireContext())
        binding.rvSchedule.layoutManager=layoutManager
        binding.rvSchedule.edgeEffectFactory=BounceEdgeEffectFactory()
        binding.rvSchedule.adapter = mAdapter
    }

    private fun setUpObserver() {
        viewModel.liveData.observe(viewLifecycleOwner,dataObserver)
        viewModel.liveStatus.observe(viewLifecycleOwner,statusObserver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
