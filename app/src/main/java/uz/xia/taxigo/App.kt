package uz.xia.taxigo

import android.content.Context
import androidx.multidex.MultiDexApplication
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import uz.xia.taxigo.utils.CrashReportingTree

@HiltAndroidApp
class App : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }
        context = this
    }

    companion object {
        var context: Context? = null
    }
}
