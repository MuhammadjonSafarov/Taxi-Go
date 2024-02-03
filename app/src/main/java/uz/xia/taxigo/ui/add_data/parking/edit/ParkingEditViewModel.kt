package uz.xia.taxigo.ui.add_data.parking.edit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uz.xia.taxigo.data.local.AppDataBase
import uz.xia.taxigo.data.local.entity.DistrictData
import uz.xia.taxigo.data.local.entity.ParkingData
import uz.xia.taxigo.data.local.entity.RegionData
import uz.xia.taxigo.data.local.entity.RoadData
import uz.xia.taxigo.data.local.remote.entity.ParkingRoadJoin
import javax.inject.Inject

@HiltViewModel
class ParkingEditViewModel @Inject constructor(
    private val appDataBase: AppDataBase
) : ViewModel() {

    val liveParkingData = MutableLiveData<ParkingData>()
    val liveRegions = MutableLiveData<Pair<Int, List<String>>>()
    val liveDistricts = MutableLiveData<Pair<Int, List<String>>>()
    val regions = mutableListOf<RegionData>()
    val districts = mutableListOf<DistrictData>()
    val liveRoads = MutableLiveData<List<RoadData>>()
    private var parkingId: Long = 0
    private var districtId: Long = 0
    private var longitude: Double = 0.0
    private var latitude: Double = 0.0

    fun getParkingById(id: Long) {
        parkingId = id
        viewModelScope.launch {
            val roads = appDataBase.parkingRoadJoin().getRoadsForParking(id)
            val parking = appDataBase.parkingDao().getParkingById(id)
            districtId = parking.districtId
            setLocation(parking.latitude, parking.longitude)
            val district = appDataBase.districtDao().getById(districtId)
            liveParkingData.postValue(parking)
            liveRoads.postValue(roads)
            val selectRegionId = district.regionId
            val list = appDataBase.regionDao().getAll()
            regions.clear()
            regions.addAll(list)
            val regionNames = mutableListOf<String>()
            var selectRegionPosition = 0
            var regionPosition = 0
            regions.forEach {
                if (it.id == selectRegionId) {
                    regionPosition = selectRegionPosition
                    return@forEach
                } else
                    selectRegionPosition++
            }
            regions.forEach {
                regionNames.add(it.nameUzLt)
            }
            liveRegions.postValue(Pair(regionPosition, regionNames))
        }
    }

    fun districtsAll(position: Int) {
        viewModelScope.launch {
            val list = appDataBase.districtDao().getByRegionIdAll(regions[position].id)
            districts.clear()
            districts.addAll(list)

            val districtNames = mutableListOf<String>()
            districts.forEach { d ->
                districtNames.add(d.nameUzLt)
            }
            var selectDistrictPosition: Int = 0
            var position = 0
            districts.forEach { d ->
                if (d.id == districtId) {
                    position = selectDistrictPosition
                    return@forEach
                } else
                    selectDistrictPosition++
            }
            liveDistricts.postValue(Pair(position, districtNames))
        }
    }

    fun setDistrict(position: Int) {
        districtId = districts[position].id
    }

    fun setLocation(latitude: Double, longitude: Double) {
        this.latitude = latitude
        this.longitude = longitude
    }

    fun saveParking(name: String) {
        viewModelScope.launch {
            val parkingData = ParkingData(
                id = parkingId,
                nameUzLt = name,
                districtId = districtId,
                latitude = latitude,
                longitude = longitude
            )
            appDataBase.parkingDao().update(parkingData)
        }
    }

    fun deleteRoads(id: Long) {
        viewModelScope.launch {
            val parkingRoadJoin = ParkingRoadJoin(
                parkingId = parkingId,
                roadId = id
            )
            appDataBase.parkingRoadJoin().delete(parkingRoadJoin)
            liveRoads.value = liveRoads.value?.filter {
                it.id != id
            }
        }

    }
}
