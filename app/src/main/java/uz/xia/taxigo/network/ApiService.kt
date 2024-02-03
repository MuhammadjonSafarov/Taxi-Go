package uz.xia.taxigo.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import uz.xia.taxigo.data.remote.model.car.AutoModel
import uz.xia.taxigo.data.remote.model.car.CarData
import uz.xia.taxigo.data.remote.model.chat.ChatMessageData
import uz.xia.taxigo.data.remote.model.paging.PagingResponse
import uz.xia.taxigo.data.remote.model.user.UserData
import uz.xia.taxigo.utils.widget.base.BaseResponseWrapper

interface ApiService {

    @GET("users")
    suspend fun getUsers(
        @Query("query") query: String = "",
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 15
    ): BaseResponseWrapper<PagingResponse<List<UserData>>>

    @GET("chats/{sender_id}/{receiver_id}")
    suspend fun getMessages(
        @Path("sender_id") senderId: Long,
        @Path("receiver_id") receiverId: Long
    ): BaseResponseWrapper<PagingResponse<List<ChatMessageData>>>

    @POST("chat")
    suspend fun sendData(@Body request: ChatMessageData):BaseResponseWrapper<ChatMessageData>

    @GET("autos")
    suspend fun getAutoModels(
        @Query("query") query: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 15
    ): BaseResponseWrapper<PagingResponse<List<AutoModel>>>

    @GET("cars")
    suspend fun getCars(): BaseResponseWrapper<PagingResponse<List<CarData>>>

    @POST("car")
    suspend fun sendCar(@Body carData: CarData): BaseResponseWrapper<CarData>
}
