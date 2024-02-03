package uz.xia.taxigo.data.remote.model.bus

import com.squareup.moshi.Json

data class Schedule(
    val id:Long,
    @field:Json(name = "name_ru")
    val nameRu:String = "",
    @field:Json(name = "name_uz")
    val nameUz:String = "",
    @field:Json(name = "name_en")
    val nameEn:String = "",
    val price:Long=0,
    @field:Json(name="trip_regularity_type")
    val tripRegularityType:Int,
    val days:Any,
    @field:Json(name="departure_at")
    val departureAt:String,
    @field:Json(name="is_international")
    val isInternational:Boolean
)
