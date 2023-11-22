package uz.xia.taxi.ui.auth

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import uz.xia.taxi.R
import uz.xia.taxi.databinding.FragmentProfileBinding
import uz.xia.taxi.databinding.FragmentWelcomeBinding
import uz.xia.taxi.utils.lazyFast

class WelcomeFragment :Fragment(), Runnable {

    private val navController by lazyFast { Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_auth) }
    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Handler().postDelayed(this,1_000L)
    }

    override fun run() {
        navController.navigate(R.id.loginFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
