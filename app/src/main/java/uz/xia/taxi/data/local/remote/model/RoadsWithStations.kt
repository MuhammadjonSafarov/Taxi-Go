package uz.xia.taxi.data.local.remote.model

import uz.xia.taxi.data.local.entity.RoadData
import uz.xia.taxi.data.local.entity.StationData

data class RoadWithStations(
    val roadData: RoadData,
    val stations: Array<String>
)
