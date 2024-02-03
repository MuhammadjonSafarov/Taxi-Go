package uz.xia.taxigo.data.local.remote.model

import androidx.room.Embedded
import androidx.room.Relation
import uz.xia.taxigo.data.local.entity.DistrictData
import uz.xia.taxigo.data.local.entity.RegionData

data class RegionWithDistrict(
    @Embedded
    val districtData: DistrictData,
    @Relation(entity = RegionData::class,
        parentColumn = "region_id",
        entityColumn = "id")
    val regionData: RegionData
)