package uz.xia.taxigo.utils.widget.base

import androidx.annotation.Keep
import retrofit2.Response

typealias BaseResponseWrapper<T> = Response<T>

/**
 * example:
 *          suspend fun getString(): BaseResponseWrapper<String>
 *          suspend fun getPagingString() : BaseResponseWrapper<PagingResponse<String>>
 */

@Keep
data class BaseResponse<T>(
    val success: Boolean,
    val result: Result<T>
) {
    @Keep
    data class Result<T>(
        val code: Int?,
        val message: String?,
        val audit: String?,
        val data: T?
    )
}

@Keep
data class PagingResponse<T>(
    val content: T,
    val empty: Boolean,
    val first: Boolean,
    val last: Boolean,
    val number: Int,
    val numberOfElements: Int,
    val pageable: Pageable,
    val size: Int,
    val sort: Sort,
    val totalElements: Int,
    val totalPages: Int
) {
    @Keep
    data class Pageable(
        val offset: Int,
        val pageNumber: Int,
        val pageSize: Int,
        val paged: Boolean,
        val sort: Sort,
        val unpaged: Boolean
    )

    @Keep
    data class Sort(
        val empty: Boolean,
        val sorted: Boolean,
        val unsorted: Boolean
    )
}
