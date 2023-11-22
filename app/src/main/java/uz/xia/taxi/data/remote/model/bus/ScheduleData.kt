package uz.xia.taxi.data.remote.model.bus

import com.squareup.moshi.Json

data class ScheduleData(
    val success:Boolean,
    @field:Json(name = "data")
    val mData:List<Schedule>
)
