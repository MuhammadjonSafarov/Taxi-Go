package uz.xia.taxigo.utils.widget.base

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonSyntaxException
import com.google.gson.stream.MalformedJsonException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import timber.log.Timber
import uz.xia.taxigo.common.NetworkErrorCodes
import uz.xia.taxigo.utils.SingleLiveEvent
import java.io.IOException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException
import javax.net.ssl.SSLHandshakeException

abstract class BaseViewModel : ViewModel() {

    private val _loader = MutableLiveData<Pair<Boolean, String?>>()
    val loader: LiveData<Pair<Boolean, String?>> get() = _loader
    private val _errorEntity = SingleLiveEvent<Pair<Throwable?, String?>>()
    val errorEntity: LiveData<Pair<Throwable?, String?>> get() = _errorEntity

    fun <T> Flow<T>.proceed(apiName: String? = null, action: suspend (T) -> Unit = { }): Job {
        return onStart { start(apiName) }
            .catch {
                Timber.e(it, "EXCEPTION " + this::class.java.simpleName)
                loader(false, apiName)
            }
            .onEach {
                action.invoke(it)
                loader(false, apiName)
            }
            .launchIn(viewModelScope)
    }

    fun <T> Flow<Pair<T?, Throwable?>>.proceedWithDefaultErrorHandler(
        apiName: String? = null,
        successAction: suspend (T?) -> Unit
    ): Job {
        return this.proceed(apiName) { (successResult: T?, error: Throwable?) ->
            when {
                error != null -> setErrorEntity(error)
                else -> successAction(successResult)
            }
        }
    }

    open fun start(apiName: String? = null) {
        loader(true, apiName)
    }

    open fun loader(isLoading: Boolean, apiName: String? = null) {
        _loader.postValue(Pair(isLoading, apiName))
    }

    open fun setErrorEntity(entity: Throwable?, type: String? = null) {
        _errorEntity.value = Pair(entity, type)
    }

    protected suspend inline fun <T> proceed(
        apiName: String? = null,
        showLoader: Boolean = false,
        crossinline request: suspend () -> BaseResponseWrapper<T>
    ): T? {
        runCatching {
            if (showLoader) loader(true, apiName)
            val response = request.invoke()
            val body = response.body()
            if (showLoader) loader(false, apiName)

            if (response.isSuccessful && body != null && body != null) {
                return body
            }
        }
        if (showLoader) loader(false, apiName)
        return null
    }

/*    protected suspend fun <T> asResource(body: suspend () -> BaseResponseWrapper<T>): ResourceUI<T> =
        wrapperX(resProvider, body)*/

    protected suspend fun <T> handleResponse(body: suspend () -> BaseResponseWrapper<T>): ResourceUI<T> {
        return try {
            val response = body.invoke()
            Timber.d("RESPONSE:")
            Timber.d("response is successful: ${response.isSuccessful}")
            Timber.d("response code: ${response.code()}")
            Timber.d("response body: ${response.body()}")
            Timber.d("response error body: ${response.errorBody()}")
            Timber.d("response message: ${response.message()}")

            checkStatus(response)
        } catch (expected: Exception) {
            Log.d("TAG_LOG","Error : ${expected.message}")
            mapException(expected)
        }
    }

    private suspend fun <T> checkStatus(
        response: BaseResponseWrapper<T>
    ): ResourceUI<T> {
        val body = response.body()
        return when (val code: Int = response.code()) {
            in SUCCESS_CODE_START..SUCCESS_CODE_END -> {
                    if (body != null) {
                        ResourceUI.Resource(body, code)
                    } else {
                        ResourceUI.Error(
                            Exception(
                                response.errorBody().toString() ?: "NullPointerException"
                            ),
                            code
                        )
                    }
            }

            else -> {
                val message: String = handleError(response.errorBody())
                ResourceUI.Error(
                    Exception(message),
                    code
                )
            }
        }
    }

    private suspend fun handleError(body: ResponseBody?): String {
        val tempError = """{ "message" = "Some Error from network" }"""
        val byteArray: ByteArray = body?.bytes() ?: tempError.toByteArray()
        return try {
            JSONObject(String(byteArray)).getString("message")
        } catch (ignore: JSONException) {
            return try {
                JSONObject(String(byteArray)).getString("error")
            } catch (ignore: Exception) {
                ""
            }
        }
    }

    @Suppress("CyclomaticComplexMethod")
    private suspend fun mapException(e: Exception) = when (e) {
        is JsonSyntaxException -> ResourceUI.Error(
            Exception("JsonSyntaxException"),
            NetworkErrorCodes.LOCAL_ERROR_JSON_SYNTAX_EXCEPTION_CODE
        )

        is JSONException -> ResourceUI.Error(
            Exception("JSONException"),
            NetworkErrorCodes.LOCAL_ERROR_JSON_EXCEPTION_CODE
        )

        is HttpException -> ResourceUI.Error(
            Exception("HttpException"),
            NetworkErrorCodes.LOCAL_ERROR_HTTP_EXCEPTION_CODE
        )

        is MalformedJsonException -> ResourceUI.Error(
            Exception("MalformedJsonException"),
            NetworkErrorCodes.LOCAL_ERROR_MALFORMED_JSON_EXCEPTION_CODE
        )

        is SSLHandshakeException -> ResourceUI.Error(
            Exception("SSLHandshakeException"),
            NetworkErrorCodes.LOCAL_ERROR_SSL_HANDSHAKE_EXCEPTION_CODE
        )

        is SSLException -> ResourceUI.Error(
            Exception("SSLException"),
            NetworkErrorCodes.LOCAL_ERROR_SSL_EXCEPTION_CODE
        )

        is SocketException -> ResourceUI.Error(
            Exception("SocketException"),
            NetworkErrorCodes.LOCAL_ERROR_SOCKET_EXCEPTION_CODE
        )

        is SocketTimeoutException -> ResourceUI.Error(
            Exception("SocketTimeoutException"),
            NetworkErrorCodes.LOCAL_ERROR_SOCKET_TIMEOUT_EXCEPTION_CODE
        )

        is UnknownHostException -> ResourceUI.Error(
            Throwable("UnknownHostException"),
            NetworkErrorCodes.LOCAL_ERROR_UNKNOWN_HOST_EXCEPTION_CODE
        )

        is IOException -> ResourceUI.Error(
            Exception("IOException"),
            NetworkErrorCodes.LOCAL_ERROR_IO_EXCEPTION_CODE
        )

        is KotlinNullPointerException -> ResourceUI.Error(
            Exception("KotlinNullPointerException"),
            NetworkErrorCodes.LOCAL_ERROR_KOTLIN_NULL_POINTER_EXCEPTION_CODE
        )

        is NullPointerException -> ResourceUI.Error(
            Exception("NullPointerException"),
            NetworkErrorCodes.LOCAL_ERROR_NULL_POINTER_EXCEPTION_CODE
        )

        is CancellationException -> ResourceUI.Error(
            e,
            NetworkErrorCodes.LOCAL_ERROR_UNKNOWN_EXCEPTION_CODE
        )

        else -> ResourceUI.Error(
            Exception(e.message ?: e.localizedMessage ?: ""),
            NetworkErrorCodes.LOCAL_ERROR_UNKNOWN_EXCEPTION_CODE
        )
    }

    companion object {
        private const val SUCCESS_CODE_START = 200
        private const val SUCCESS_CODE_END = 299
    }
}
