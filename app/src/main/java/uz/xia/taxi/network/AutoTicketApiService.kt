package uz.xia.taxi.network

import retrofit2.http.GET
import uz.xia.taxi.data.remote.model.bus.BaseScheduleData

interface AutoTicketApiService {

    @GET("bus-schedule")
    suspend fun getScheduledList(): BaseScheduleData
}
