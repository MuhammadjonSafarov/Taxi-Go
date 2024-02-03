package uz.xia.taxigo.data.local.remote.model

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import uz.xia.taxigo.data.local.entity.DistrictData
import uz.xia.taxigo.data.local.entity.ParkingData
import uz.xia.taxigo.data.local.entity.RegionData

data class ParkingWithRegions(
    @Embedded
    val parkingData: ParkingData,
    @Relation(
        entity = DistrictData::class,
        parentColumn = "district_id",
        entityColumn = "id"
    )
    val districtData: DistrictData,
)