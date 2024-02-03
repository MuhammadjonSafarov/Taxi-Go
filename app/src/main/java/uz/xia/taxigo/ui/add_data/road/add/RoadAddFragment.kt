package uz.xia.taxigo.ui.add_data.road.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import dagger.hilt.android.AndroidEntryPoint
import uz.xia.taxigo.R
import uz.xia.taxigo.databinding.FragmentRoadAddBinding
import uz.xia.taxigo.utils.lazyFast

@AndroidEntryPoint
class RoadAddFragment : Fragment() {

    private var _binding: FragmentRoadAddBinding? = null
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
        _binding = FragmentRoadAddBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonCancel.setOnClickListener{
            navController.navigateUp()
        }
        binding.buttonSave.setOnClickListener {
            val name = binding.etRoadName.text.toString()
            val destination = binding.etDestination.text.toString().toLong()
            viewModel.saveRoad(name,destination)
            navController.navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
