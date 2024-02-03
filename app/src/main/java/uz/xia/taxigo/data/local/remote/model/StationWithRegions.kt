package uz.xia.taxigo.data.local.remote.model

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import uz.xia.taxigo.data.local.entity.DistrictData
import uz.xia.taxigo.data.local.entity.ParkingData
import uz.xia.taxigo.data.local.entity.RegionData
import uz.xia.taxigo.data.local.entity.StationData

data class StationWithRegions(
    @Embedded
    val stationData: StationData,
    @Relation(
        entity = DistrictData::class,
        parentColumn = "district_id",
        entityColumn = "id"
    )
    val districtData: DistrictData,
)