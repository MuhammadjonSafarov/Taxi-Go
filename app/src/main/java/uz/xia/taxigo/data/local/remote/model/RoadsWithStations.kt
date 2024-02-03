package uz.xia.taxigo.data.local.remote.model

import uz.xia.taxigo.data.local.entity.RoadData

data class RoadWithStations(
    val roadData: RoadData,
    val stations: Array<String>
)
