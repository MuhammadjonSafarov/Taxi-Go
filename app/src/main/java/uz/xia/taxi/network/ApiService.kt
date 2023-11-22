package uz.xia.taxi.network

import retrofit2.http.GET

interface ApiService{
    @GET("users")
    suspend fun getUsers():List<MyUser>
}
data class MyUser(
    val id:Long,
    val userName:String,
    val gender:String,
    val status:Long
)
