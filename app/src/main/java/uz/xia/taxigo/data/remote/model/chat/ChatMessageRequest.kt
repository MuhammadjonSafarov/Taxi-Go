package uz.xia.taxigo.data.remote.model.chat

open class ChatMessageRequest(
    val id: Long = 0L,
    var sender: Long = 0L,
    val receiver: Long = 0L,
    val content: String? = "",
    val type: String = "",
    val longitude: Double = 69.246071,
    val latitude: Double = 41.213383,
    val createAt:Long? = 0L,
    val updateAt:Long? = 0L
)