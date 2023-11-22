package uz.xia.taxi.ui.add.road

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import dagger.hilt.android.AndroidEntryPoint
import uz.xia.taxi.R
import uz.xia.taxi.databinding.FragmentAddRoadBinding
import uz.xia.taxi.databinding.FragmentStationBinding
import uz.xia.taxi.utils.lazyFast
import uz.xia.taxi.widget.SimpleSpinner

@AndroidEntryPoint
class RoadAddFragment : Fragment() {

    private var _binding: FragmentAddRoadBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<RoadAddViewModel>()

    private val navController by lazyFast {
        Navigation.findNavController(
            requireActivity(), R.id.nav_host_fragment_content_main
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddRoadBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.stationAdd.setOnClickListener {
            navController.navigate(R.id.nav_add_station)
        }
        binding.buttonConform.setOnClickListener {
            val name = binding.etRoadName.text.toString()
            val destination = binding.etDestination.text.toString().toLong()
            viewModel.saveRoad(name,destination)
            binding.etRoadName.setText("")
            binding.etDestination.setText("")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
