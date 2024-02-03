package uz.xia.taxigo.utils.widget.base

typealias StringResource = ResourceUI<String>
typealias MessageResource = ResourceUI<MessageData>

@DslMarker
annotation class ResourceDSL

@ResourceDSL
sealed class ResourceUI<out T> {

    object Loading : ResourceUI<Nothing>()

    data class Resource<T>(
        val data: T?,
        val code: Int = 200
    ) : ResourceUI<T>()

    data class Error(
        val error: Throwable,
        val code: Int = -1
    ) : ResourceUI<Nothing>() {

        override fun getMessage(localeError: String) = error.message
            ?: error.localizedMessage
            ?: localeError
    }

    open fun getMessage(localeError: String) = localeError
}
