package uz.xia.taxi.utils

import android.annotation.TargetApi
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import uz.xia.taxi.common.Language
import uz.xia.taxi.common.UZ_KR
import uz.xia.taxi.data.IPreference
import java.util.*

class LocaleHelper(private var preferences: IPreference) {

    fun getLanguage(): String {
        return getPersistedData()
    }

    fun getNetworkLang(): String {
        val lang = getPersistedData()
        if (lang == Language.UZ_KR.v)
            return Language.UZ_KR.v
        else if (lang == Language.UZ_LT.v)
            return Language.UZ_LT.v
        else if (lang == Language.EN.v)
            return Language.EN.v
        else if (lang == Language.RU.v)
            return Language.RU.v
        return UZ_KR
    }

    private fun getPersistedData() = preferences.language

    fun setLocale(context: Context, language: String) {
        persist(language)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateResources(context, language)
        }
        updateResourcesLegacy(context, language)
    }

    fun setCurrentLocale(context: Context): Context {
        persist(preferences.language)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateResources(context, preferences.language)
        }
        return updateResourcesLegacy(context, preferences.language)
    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun updateResources(context: Context, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val configuration: Configuration = context.resources.configuration
        configuration.setLocale(locale)
        context.createConfigurationContext(configuration)
    }

    private fun updateResourcesLegacy(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val resources: Resources = context.resources
        val configuration = Configuration()
        configuration.locale = locale
        resources.updateConfiguration(configuration, resources.displayMetrics)
        return context
    }

    private fun persist(language: String) {
        preferences.language = language
    }
}
