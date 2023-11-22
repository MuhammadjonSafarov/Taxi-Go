package uz.xia.taxi.ui.driver

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import uz.xia.taxi.R
import uz.xia.taxi.databinding.FragmentDriverBinding
import uz.xia.taxi.utils.lazyFast

class DriverFragment : Fragment() {

    private var _binding: FragmentDriverBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val navController by lazyFast { Navigation.findNavController(requireActivity(),
    R.id.nav_host_fragment_content_main) }
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDriverBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.floatingActionButton.setOnClickListener {
            navController.navigate(R.id.nav_car_add)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}
