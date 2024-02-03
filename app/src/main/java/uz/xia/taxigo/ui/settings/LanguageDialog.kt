package uz.xia.taxigo.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import uz.xia.taxigo.R
import uz.xia.taxigo.common.Language
import uz.xia.taxigo.databinding.DialogLanguageBinding

class LanguageDialog(private val listener: ILanguageListener) : DialogFragment(),
    View.OnClickListener {
    private var _binding: DialogLanguageBinding? = null
    private val binding get() = _binding!!
    private var lang = ""
    override fun getTheme(): Int = R.style.FullScreenDialogTransparent
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogLanguageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lang = arguments?.getString("key_language") ?: "en"
        val drawableUz = ResourcesCompat.getDrawable(resources, R.drawable.ic_lang_uz, null)
        val drawableEn = ResourcesCompat.getDrawable(resources, R.drawable.ic_lang_en, null)
        val drawableRu = ResourcesCompat.getDrawable(resources, R.drawable.ic_lang_ru, null)
        val drawableCheck = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_radio_button, null)
        when (lang) {
            "en" -> {
                binding.btnLangKrill.setCompoundDrawablesWithIntrinsicBounds(
                    drawableUz, null, drawableCheck, null)
            }
            "uz" -> {
                binding.btnLangLatin.setCompoundDrawablesWithIntrinsicBounds(
                    drawableUz, null, drawableCheck, null)
            }
            "zh" -> binding.btnLangEng.setCompoundDrawablesWithIntrinsicBounds(
                drawableEn, null, drawableCheck, null)

            "ru" -> binding.btnLangRu.setCompoundDrawablesWithIntrinsicBounds(
                drawableRu, null, drawableCheck, null)

        }

        binding.btnLangLatin.setOnClickListener(this)
        binding.btnLangKrill.setOnClickListener(this)
        binding.btnLangRu.setOnClickListener(this)
        binding.btnLangEng.setOnClickListener(this)
        binding.btnCloseTop.setOnClickListener(this)
        binding.btnCloseFooter.setOnClickListener(this)
    }


    override fun onClick(item: View?) {
        when (item?.id) {
            R.id.btn_lang_krill -> {
                if (lang != Language.UZ_KR.v) {
                    listener.onSelectLang(Language.UZ_KR.v)
                    dismiss()
                }
            }

            R.id.btn_lang_latin -> {
                if (lang != Language.UZ_LT.v) {
                    listener.onSelectLang(Language.UZ_LT.v)
                    dismiss()
                }
            }

            R.id.btn_lang_ru -> {
                if (lang != Language.RU.v) {
                    listener.onSelectLang(Language.RU.v)
                    dismiss()
                }
            }

            R.id.btn_lang_eng -> {
                if (lang != Language.EN.v) {
                    listener.onSelectLang(Language.EN.v)
                    dismiss()
                }
            }

            R.id.btn_close_top -> dismiss()
            R.id.btn_close_footer -> dismiss()
        }
    }

    companion object {
        fun getInstaince(lang: String, listener: ILanguageListener): LanguageDialog {
            val bundle = bundleOf(Pair("key_language", lang))
            val fragment = LanguageDialog(listener)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

interface ILanguageListener {
    fun onSelectLang(lang: String)
}
