package uz.xia.taxigo.utils.widget.base

import androidx.annotation.Keep

typealias MessageResponse = BaseResponseWrapper<MessageData>

@Keep
data class MessageData(val message: String?)
