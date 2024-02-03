package uz.xia.taxigo.data.local.remote.model

import uz.xia.taxigo.data.local.entity.DistrictData
import uz.xia.taxigo.data.local.entity.ParkingData
import uz.xia.taxigo.data.local.entity.RegionData

data class ParkingWithRegionDistrict(
    val parkingData: ParkingData,
    val regionData: RegionData,
    val districtData: DistrictData
)