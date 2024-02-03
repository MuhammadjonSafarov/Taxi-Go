package uz.xia.taxigo.network

import retrofit2.http.GET
import uz.xia.taxigo.data.remote.model.bus.BaseScheduleData

interface AutoTicketApiService {

    @GET("bus-schedule")
    suspend fun getScheduledList(): BaseScheduleData
}
