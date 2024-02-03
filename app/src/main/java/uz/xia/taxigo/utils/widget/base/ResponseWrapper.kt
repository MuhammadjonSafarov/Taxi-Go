/*
package uz.xia.taxigo.utils.widget.base

import com.google.gson.JsonSyntaxException
import com.google.gson.stream.MalformedJsonException
import kotlinx.coroutines.CancellationException
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import uz.tune.agatagentapp.core.network.NetworkErrorCodes
import uz.tune.agatagentapp.core.resourceprovider.ResProvider
import uz.tune.agatagentapp.strings.AppString
import uz.xia.taxigo.common.NetworkErrorCodes
import java.io.IOException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException
import javax.net.ssl.SSLHandshakeException

@DslMarker
annotation class WrapperDsl

@WrapperDsl
suspend fun <T> wrapperX(
    resProvider: ResProvider,
    body: suspend () -> BaseResponseWrapper<T>
): ResourceUI<T> {
    return try {
        val response = body.invoke()
        checkStatus(response, resProvider)
    } catch (expected: Exception) {
        mapException(expected, resProvider)
    }
}

@Suppress("CyclomaticComplexMethod")
private fun mapException(e: Exception, resProvider: ResProvider) = when (e) {
    is JsonSyntaxException -> ResourceUI.Error(
        Exception(resProvider.getString(AppString.al_err_msg_json_syntax_exception)),
        NetworkErrorCodes.LOCAL_ERROR_JSON_SYNTAX_EXCEPTION_CODE
    )
    is JSONException -> ResourceUI.Error(
        Exception(resProvider.getString(AppString.al_err_msg_json_exception)),
        NetworkErrorCodes.LOCAL_ERROR_JSON_EXCEPTION_CODE
    )
    is HttpException -> ResourceUI.Error(
        Exception(resProvider.getString(AppString.al_err_msg_http_exception)),
        NetworkErrorCodes.LOCAL_ERROR_HTTP_EXCEPTION_CODE
    )
    is MalformedJsonException -> ResourceUI.Error(
        Exception(resProvider.getString(AppString.al_err_msg_malformed_json_exception)),
        NetworkErrorCodes.LOCAL_ERROR_MALFORMED_JSON_EXCEPTION_CODE
    )
    is SSLHandshakeException -> ResourceUI.Error(
        Exception(resProvider.getString(AppString.al_err_msg_ssl_handshake_exception)),
        NetworkErrorCodes.LOCAL_ERROR_SSL_HANDSHAKE_EXCEPTION_CODE
    )
    is SSLException -> ResourceUI.Error(
        Exception(resProvider.getString(AppString.al_err_msg_ssl_exception)),
        NetworkErrorCodes.LOCAL_ERROR_SSL_EXCEPTION_CODE
    )
    is SocketException -> ResourceUI.Error(
        Exception(resProvider.getString(AppString.al_err_msg_socket_exception)),
        NetworkErrorCodes.LOCAL_ERROR_SOCKET_EXCEPTION_CODE
    )
    is SocketTimeoutException -> ResourceUI.Error(
        Exception(resProvider.getString(AppString.al_err_msg_socket_timeout_exception)),
        NetworkErrorCodes.LOCAL_ERROR_SOCKET_TIMEOUT_EXCEPTION_CODE
    )
    is UnknownHostException -> ResourceUI.Error(
        Throwable(resProvider.getString(AppString.al_err_msg_unknown_host_exception)),
        NetworkErrorCodes.LOCAL_ERROR_UNKNOWN_HOST_EXCEPTION_CODE
    )
    is IOException -> ResourceUI.Error(
        Exception(resProvider.getString(AppString.al_err_msg_io_exception)),
        NetworkErrorCodes.LOCAL_ERROR_IO_EXCEPTION_CODE
    )
    is KotlinNullPointerException -> ResourceUI.Error(
        Exception(resProvider.getString(AppString.al_err_msg_kotlin_null_pointer_exception)),
        NetworkErrorCodes.LOCAL_ERROR_KOTLIN_NULL_POINTER_EXCEPTION_CODE
    )
    is NullPointerException -> ResourceUI.Error(
        Exception(resProvider.getString(AppString.al_err_msg_kotlin_null_pointer_exception)),
        NetworkErrorCodes.LOCAL_ERROR_NULL_POINTER_EXCEPTION_CODE
    )
    is CancellationException -> ResourceUI.Error(
        e,
        NetworkErrorCodes.LOCAL_ERROR_UNKNOWN_EXCEPTION_CODE
    )
    else -> ResourceUI.Error(
        Exception(e.message ?: e.localizedMessage ?: resProvider.getString(AppString.lbl_error)),
        NetworkErrorCodes.LOCAL_ERROR_UNKNOWN_EXCEPTION_CODE
    )
}

private const val SUCCESS_CODE_START = 200
private const val SUCCESS_CODE_END = 299

private fun <T> checkStatus(
    response: BaseResponseWrapper<T>,
    resProvider: ResProvider
): ResourceUI<T> {
    val body = response.body()
    val data: T? = body?.result?.data
    return when (val code: Int = response.code()) {
        in SUCCESS_CODE_START..SUCCESS_CODE_END -> {
            if (data != null) {
                ResourceUI.Resource(data, code)
            } else {
                ResourceUI.Error(
                    NullPointerException(
                        body?.result?.message ?: resProvider.getString(AppString.al_msg_error_loading_data)
                    ),
                    code
                )
            }
        }
        else -> {
            val message: String = handleError(response.errorBody(), resProvider)
            ResourceUI.Error(
                Exception(message),
                code
            )
        }
    }
}

private fun handleError(body: ResponseBody?, resProvider: ResProvider): String {
    val tempError = """{ "message" = "Some Error from network" }"""
    val byteArray: ByteArray = body?.bytes() ?: tempError.toByteArray()
    return try {
        JSONObject(String(byteArray)).getString("message")
    } catch (ignore: JSONException) {
        return try {
            JSONObject(String(byteArray)).getString("error")
        } catch (ignore: Exception) {
            resProvider.getString(AppString.al_err_msg_not_found)
        }
    }
}
*/
