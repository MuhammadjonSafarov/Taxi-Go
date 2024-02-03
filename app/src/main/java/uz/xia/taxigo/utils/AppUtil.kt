package uz.xia.taxigo.utils

import android.content.Context
import android.os.Build

object AppUtil {
    fun getVersionName(context: Context): String = try {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName.toString()
    } catch (ignore: Exception) {
        "v1.0.0"
    }

    fun getVersionCode(context: Context): Int = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            context.packageManager.getPackageInfo(context.packageName, 0).longVersionCode.toInt()
        } else {
            context.packageManager.getPackageInfo(context.packageName, 0).versionCode
        }
    } catch (ignore: Exception) {
        0
    }
}