package uz.xia.taxigo.utils.device_model

import android.content.Context
import android.os.Build
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG

import java.io.File
import java.util.Locale

class DeviceInfoInterector internal constructor(private val context: Context) :
    DeviceHeaderProvider {

    private val genyFiles = listOf("/dev/socket/genyd", "/dev/socket/baseband_genyd")
    private val pipes = listOf("/dev/socket/qemud", "/dev/qemu_pipe")

    private val x86Files = listOf(
        "ueventd.android_x86.rc",
        "x86.prop",
        "ueventd.ttVM_x86.rc",
        "init.ttVM_x86.rc",
        "fstab.ttVM_x86",
        "fstab.vbox86",
        "init.vbox86.rc",
        "ueventd.vbox86.rc"
    )

    private val andyFiles = listOf("fstab.andy", "ueventd.andy.rc")
    private val noxFiles = listOf("fstab.nox", "init.nox.rc", "ueventd.nox.rc")

    override fun getDeviceUniqueId() = DeviceUtil.getDeviceUniqueId(context)

    override fun getDeviceName(): String {
//        val manufacturer = Build.MANUFACTURER
//        val model = Build.MODEL
//        if (model.startsWith(manufacturer)) {
//            return model.replaceFirstChar(Char::titlecase)
//        }
//        return manufacturer.replaceFirstChar(Char::titlecase) + " " + model + " | Android " + Build.VERSION.RELEASE

        return Build.MODEL + " | Android " + Build.VERSION.RELEASE
    }

    override fun getDeviceModel(): String {
        return Build.MODEL
    }

    override fun getDeviceType(): String {
        return "Android"
    }

    fun isBiometricHardwareAvailable() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        BiometricManager.from(context).canAuthenticate(BIOMETRIC_STRONG) !in setOf(
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE
        )
    } else {
        false
    }

    fun isEmulator() = checkEmulatorFiles() || checkBuildConfig()

    fun isRooted() = DeviceUtil.isRooted(context)

    @Suppress("CyclomaticComplexMethod")
    private fun checkBuildConfig() = Build.MANUFACTURER.contains("Genymotion") ||
        Build.MODEL.contains("google_sdk") ||
        Build.MODEL.lowercase(Locale.getDefault()).contains("droid4x") ||
        Build.MODEL.contains("Emulator") ||
        Build.MODEL.contains("Android SDK built for x86") ||
        Build.HARDWARE == "goldfish" ||
        Build.HARDWARE == "vbox86" ||
        Build.HARDWARE.lowercase(Locale.getDefault()).contains("nox") ||
        Build.FINGERPRINT.startsWith("generic") ||
        Build.PRODUCT == "sdk" ||
        Build.PRODUCT == "google_sdk" ||
        Build.PRODUCT == "sdk_x86" ||
        Build.PRODUCT == "vbox86p" ||
        Build.PRODUCT.lowercase(Locale.getDefault()).contains("nox") ||
        Build.BOARD.lowercase(Locale.getDefault()).contains("nox") ||
        Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")

    private fun checkEmulatorFiles() = checkFiles(genyFiles) ||
        checkFiles(andyFiles) ||
        checkFiles(noxFiles) ||
        checkFiles(x86Files) ||
        checkFiles(pipes)

    private fun checkFiles(targets: List<String>): Boolean {
        for (pipe in targets) {
            val file = File(pipe)
            if (file.exists()) {
                return true
            }
        }
        return false
    }
}
