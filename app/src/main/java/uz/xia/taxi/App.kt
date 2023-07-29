package uz.xia.taxi

import androidx.multidex.MultiDexApplication
import com.chuckerteam.chucker.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import uz.xia.taxi.utils.CrashReportingTree

@HiltAndroidApp
class App: MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }
    }
}