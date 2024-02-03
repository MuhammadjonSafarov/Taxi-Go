package uz.xia.taxigo.utils.device_model

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.provider.Settings
import com.scottyab.rootbeer.RootBeer

object DeviceUtil {
    fun isRooted(context: Context) = RootBeer(context).isRooted
    fun getDeviceUniqueId(context: Context) = getAndroidId(context)

    @SuppressLint("HardwareIds")
    private fun getAndroidId(context: Context): String =
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

    fun getSystemLanguage(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Resources.getSystem().configuration.locales.get(0).language
        } else {
            @Suppress("DEPRECATION")
            Resources.getSystem().configuration.locale.language
        }
    }
}
