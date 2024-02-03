package uz.xia.taxigo.data.remote.model.nomination.polygon

import com.squareup.moshi.Json

data class PolygonData(
    val addresstype: String,
    val boundingbox: List<String>,
    @field:Json(name = "class")
    val mClass: String,
    @field:Json(name = "display_name")
    val displayName: String,
    val geojson: GeoJson,
    val importance: Double,
    val lat: String,
    val lon: String,
    val name: String,
    val osm_id: Int,
    val osm_type: String,
    val place_id: Int,
    val place_rank: Int,
    val type: String,
    val licence: String
)