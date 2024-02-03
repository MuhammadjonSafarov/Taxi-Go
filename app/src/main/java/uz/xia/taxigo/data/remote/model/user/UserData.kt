package uz.xia.taxigo.data.remote.model.user

data class UserData(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val image: String,
    val userName: String,
    val gender: String,
    val status: Long=0L,
    val createAt:String?="",
    val updateAt:String?=""
)