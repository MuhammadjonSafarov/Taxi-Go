package uz.xia.taxigo.network.intercepter

import okhttp3.Interceptor
import okhttp3.Response
import uz.xia.taxigo.BuildConfig
import uz.xia.taxigo.common.Language
import uz.xia.taxigo.data.IPreference
import uz.xia.taxigo.utils.device_model.DeviceHeaderProvider

class HeaderInterceptor(
    private val deviceHeaderProvider: DeviceHeaderProvider,
    private val preference : IPreference
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val appLang = when (preference.language) {
            Language.EN.v -> Language.EN.name
            Language.RU.v -> Language.RU.name
            Language.UZ_KR.v -> Language.UZ_KR.name
            Language.UZ_LT.v -> Language.UZ_LT.name
            else -> Language.UZ_KR.name
        }
        val devMode = if (BuildConfig.DEBUG) "debug" else "release"
        val currRequest = chain.request()
        val requestBuilder = currRequest.newBuilder()
        requestBuilder
            .addHeader("Content-Type", "application/json")
            .addHeader("X-App-Version", BuildConfig.VERSION_NAME)
            .addHeader("X-App-Build", BuildConfig.VERSION_CODE.toString())
            .addHeader("X-Device-Type", deviceHeaderProvider.getDeviceType())
            .addHeader("X-Device-Model", deviceHeaderProvider.getDeviceModel())
            .addHeader("X-Device-ID", deviceHeaderProvider.getDeviceUniqueId())
            .addHeader("X-OS", deviceHeaderProvider.getDeviceName())
            .addHeader("X-Lang", appLang)
            .addHeader("X-Dev-Mode", devMode)
            .header("Connection", "close")
            .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI5OTg5MDA2MzY2OTAiLCJyb2xlIjoiVVNFUiIsImlhdCI6MTcwNjk1NTM1MywiZXhwIjoxNzA3MDQxNzUzfQ.oVHxXfyae8N6UHiak6gvaBKNZ9kes5AivwguPZg327w")

        if (preference.fcmToken.isNotEmpty()) {
            requestBuilder.addHeader("X-Fcm-Token", preference.fcmToken)
        }
        return chain.proceed(requestBuilder.build())
    }
}
