package uz.xia.taxigo.data.remote.model.login

data class UserData(
    val createAt: String,
    val dateOfBirth: String,
    val email: String,
    val firstName: String,
    val gender: String,
    val id: Long,
    val image: String,
    val lastName: String,
    val password: String,
    val phone: String,
    val role: String,
    val updateAt: String,
    val userName: String
)