package uz.xia.taxigo.data.remote.model.login

data class UserLoginResponse(
    val username:String,
    val token:String,
    val  user:UserData
)