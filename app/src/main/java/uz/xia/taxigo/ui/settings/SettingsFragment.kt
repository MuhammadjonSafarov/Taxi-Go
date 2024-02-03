package uz.xia.taxigo.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.xia.taxigo.R
import uz.xia.taxigo.databinding.FragmentSettingsBinding
import uz.xia.taxigo.utils.AppUtil
import uz.xia.taxigo.utils.LocaleHelper
import uz.xia.taxigo.utils.lazyFast
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment(), ISettingsListener, ILanguageListener {

    private var _binding: FragmentSettingsBinding? = null
    private val navController by lazyFast {
        Navigation.findNavController(
            requireActivity(), R.id.nav_host_fragment_content_main
        )
    }
    private val binding get() = _binding!!
    private val settingsAdapter by lazyFast { SettingsAdapter(this) }
    private val mViewModel by viewModels<SettingsViewModel>()

    @Inject
    lateinit var localeHelper: LocaleHelper

    private val changeLangObserver = Observer<String> {
        val dialog = LanguageDialog.getInstaince(it, this)
        dialog.show(childFragmentManager, "dialog_language")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setUpObserver()
    }

    private fun setUpObserver() {
        mViewModel.liveData.observe(viewLifecycleOwner) { settingsAdapter.setList(it) }
        mViewModel.liveChangeLanguage.observe(viewLifecycleOwner, changeLangObserver)
    }

    private fun setUpViews() {
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        binding.recyclerSettings.setHasFixedSize(false)
        binding.recyclerSettings.adapter = settingsAdapter
        binding.recyclerSettings.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.VERTICAL, false
        )
        binding.recyclerSettings.alpha = 0.0f
        binding.recyclerSettings.scaleX = 0.8f
        binding.recyclerSettings.scaleY = 0.8f
        binding.recyclerSettings
            .animate()
            .scaleX(1f).scaleY(1f)
            .alpha(1f).setStartDelay(100)
            .setDuration(200)
            .setInterpolator(OvershootInterpolator())
            .start()

        val version = AppUtil.getVersionName(requireContext())
        binding.tvAppVersion.text = getString(R.string.app_version, version)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(id: Long) {
        when (id) {
            1L -> navController.navigate(R.id.nav_profile)
            2L -> mViewModel.showChangeLanguage()
            else -> {}
        }

    }

    override fun onSelectLang(lang: String) {
        localeHelper.setLocale(requireContext(), lang)
        mViewModel.setChangeLang(lang)
        settingsAdapter.notifyDataSetChanged()
    }
}
