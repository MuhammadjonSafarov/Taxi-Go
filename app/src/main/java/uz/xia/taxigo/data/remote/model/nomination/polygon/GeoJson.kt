package uz.xia.taxigo.data.remote.model.nomination.polygon

data class GeoJson(
    val coordinates: List<List<List<Double?>>>,
    val type: String
)