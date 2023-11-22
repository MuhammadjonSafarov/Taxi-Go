package uz.xia.taxi.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.xia.taxi.R
import uz.xia.taxi.data.IPreference
import uz.xia.taxi.utils.SingleLiveEvent
import javax.inject.Inject

interface ISettingsViewModel {
    val liveData: LiveData<List<Settings>>
    val liveChangeLanguage: LiveData<String>
    fun setChangeLang(lang: String)
    fun showChangeLanguage()
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferences: IPreference
) : ViewModel(), ISettingsViewModel {
    override val liveData = MutableLiveData<List<Settings>>()
    override val liveChangeLanguage = SingleLiveEvent<String>()

    init {
        liveData.postValue(mList)
    }

    override fun showChangeLanguage() {
        val lang = preferences.language
        liveChangeLanguage.postValue(lang)
    }

    override fun setChangeLang(lang: String) {
        preferences.language = lang
       // liveData.postValue(mList)
    }
}

private val mList = listOf(
    Settings(
        id = 1,
        resId = R.string.personal_info,
        iconId = R.drawable.circle_account,
        type = SettingsType.LABEL
    ), Settings(
        id = 2,
        resId = R.string.change_language,
        iconId = R.drawable.ic_language,
        type = SettingsType.LABEL
    ), Settings(
        id = 3,
        resId = R.string.theme_settings,
        iconId = R.drawable.ic_theme,
        type = SettingsType.LABEL
    ), Settings(
        id = 4,
        resId = R.string.vebriation_control,
        iconId = R.drawable.ic_vibration,
        type = SettingsType.CHECKBOX
    ), Settings(
        id = 5,
        resId = R.string.security,
        iconId = R.drawable.ic_security,
        type = SettingsType.LABEL
    ), Settings(
        id = 6, resId = R.string.about, iconId = R.drawable.ic_info, type = SettingsType.LABEL
    )
)
