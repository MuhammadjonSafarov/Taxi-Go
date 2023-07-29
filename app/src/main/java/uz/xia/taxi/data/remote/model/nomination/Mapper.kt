package uz.xia.taxi.data.remote.model.nomination

import android.location.Location

interface Mapper<SRC, DST> {
    fun transform(data: SRC): DST
}
class PlaceMapper(
    private val latitude: Double,
    private val longitude: Double
) : Mapper<Place, NearbyPlace> {
    override fun transform(data: Place): NearbyPlace {
        val distance = FloatArray(1)
        Location.distanceBetween(
            latitude,
            longitude,
            data.latitude!!,
            data.longitude!!,
            distance
        )
        return NearbyPlace(
            data.placeId,
            data.title,
            data.title,
            distance[0],
            data.latitude!!,
            data.longitude!!
        )
    }
}