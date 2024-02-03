package uz.xia.taxigo.data.remote.model.nomination

open class NearbyPlace(
    val id: Long,
    val name: String,
    val streetName: String,
    val distance: Float,
    val latitude: Double,
    val longitude: Double
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NearbyPlace

        if (id != other.id) return false
        if (name != other.name) return false
        if (streetName != other.streetName) return false
        if (distance != other.distance) return false
        if (latitude != other.latitude) return false
        if (longitude != other.longitude) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + streetName.hashCode()
        result = 31 * result + distance.hashCode()
        result = 31 * result + latitude.hashCode()
        result = 31 * result + longitude.hashCode()
        return result
    }

    override fun toString(): String {
        return "NearbyPlace(id=$id, name='$name', streetName='$streetName', distance=$distance, latitude=$latitude, longitude=$longitude)"
    }

}

class NearLoading : NearbyPlace(0L, "", "", 0f, 0.0, 0.0)
class NearEmpty : NearbyPlace(-1L, "", "", 0f, 0.0, 0.0)