package uz.xia.taxigo.data.remote.model

data class StationMarkerData(
    val id:Long,
    val name:String,
    val longitude:Double,
    val latitude:Double,
    val regionName:String,
    val isJoined:Boolean
)