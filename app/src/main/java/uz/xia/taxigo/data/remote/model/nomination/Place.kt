package uz.xia.taxigo.data.remote.model.nomination

import com.squareup.moshi.Json

data class Place(
    @field:Json(name = "place_id")
    val placeId: Long = 0L,
    val lat: String = "",
    val lon: String = "",
    var latitude: Double? = null,
    var longitude: Double? = null,
    var distance: FloatArray = FloatArray(1),
    @field:Json(name = "display_name")
    var title: String = "null"
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Place

        if (placeId != other.placeId) return false
        if (lat != other.lat) return false
        if (lon != other.lon) return false

        return true
    }

    override fun hashCode(): Int {
        var result = placeId.hashCode()
        result = 31 * result + lat.hashCode()
        result = 31 * result + lon.hashCode()
        return result
    }

}