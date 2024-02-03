package uz.xia.taxigo.data.remote.model.nomination

import com.squareup.moshi.Json

data class Address(
    @field:Json(name = "boundingbox")
    val boundingBox: List<String>,
    @field:Json(name = "class")
    val mClass: String,
    val display_name: String,
    val icon: String,
    val importance: Double,
    val lat: String,
    val licence: String,
    val lon: String,
    val osm_id: Long,
    val osm_type: String,
    val place_id: Int,
    val type: String
)