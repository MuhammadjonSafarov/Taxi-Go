package uz.xia.taxigo.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import uz.xia.taxigo.data.remote.model.car.AutoModel
import uz.xia.taxigo.data.remote.model.car.CarDataDto
import uz.xia.taxigo.data.remote.model.car.CarDataRequest
import uz.xia.taxigo.data.remote.model.car.CarDataResponse
import uz.xia.taxigo.data.remote.model.chat.ChatMessageData
import uz.xia.taxigo.data.remote.model.chat.ChatMessageRequest
import uz.xia.taxigo.data.remote.model.login.UserLoginRequest
import uz.xia.taxigo.data.remote.model.login.UserLoginResponse
import uz.xia.taxigo.data.remote.model.paging.PagingResponse
import uz.xia.taxigo.data.remote.model.user.UserData
import uz.xia.taxigo.ui.driver.auto.list.UserCarId
import uz.xia.taxigo.utils.widget.base.BaseResponseWrapper

interface ApiService {

    @POST("login")
    suspend fun login(@Body request: UserLoginRequest): BaseResponseWrapper<UserLoginResponse>

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
    suspend fun sendData(@Body request: ChatMessageRequest): BaseResponseWrapper<ChatMessageData>

    @GET("autos")
    suspend fun getAutoModels(
        @Query("query") query: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 15
    ): BaseResponseWrapper<PagingResponse<List<AutoModel>>>

    @GET("cars/{id}")
    suspend fun getCars(
        @Path("id") id: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 15
    ): BaseResponseWrapper<PagingResponse<List<CarDataResponse>>>


    @POST("user/update_car")
    suspend fun updateUserCar(
        @Body request: UserCarId
    ): BaseResponseWrapper<CarDataResponse>

    @POST("car/parking/{id}")
    suspend fun parkingCar(
        @Path("id") id: Long,
        @Body request: CarDataDto
    ): BaseResponseWrapper<CarDataResponse>

    @POST("car/{id}")
    suspend fun sendCar(
        @Path("id") id: Long,
        @Body carData: CarDataRequest
    ): BaseResponseWrapper<CarDataResponse>
}
