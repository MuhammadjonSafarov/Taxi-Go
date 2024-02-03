package uz.xia.taxigo.ui.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import dagger.hilt.android.AndroidEntryPoint
import uz.xia.taxigo.R
import uz.xia.taxigo.data.IPreference
import uz.xia.taxigo.databinding.FragmentWelcomeBinding
import uz.xia.taxigo.ui.MainActivity
import uz.xia.taxigo.utils.lazyFast
import javax.inject.Inject

@AndroidEntryPoint
class WelcomeFragment : Fragment(), Runnable {

    private val navController by lazyFast {
        Navigation.findNavController(
            requireActivity(),
            R.id.nav_host_fragment_auth
        )
    }
    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!
    private val handler = Handler(Looper.getMainLooper())

    @Inject
    lateinit var preference: IPreference

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
        handler.postDelayed(this, 1_000L)
    }

    override fun run() {
        if (preference.accessToken.isEmpty()) navController.navigate(R.id.loginFragment)
        else Intent(requireContext(), MainActivity::class.java).apply {
            startActivity(this)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(this)
        _binding = null
    }
}
