package uz.xia.taxigo.ui.home.roads

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uz.xia.taxigo.data.local.AppDataBase
import uz.xia.taxigo.data.local.entity.ParkingData
import uz.xia.taxigo.data.local.remote.model.ParkingWithRegionDistrict
import uz.xia.taxigo.data.local.remote.model.RoadWithStations
import uz.xia.taxigo.utils.SingleLiveEvent
import java.lang.Exception
import javax.inject.Inject

interface IParkDetailViewModel {
    val roadsLiveData: LiveData<List<RoadWithStations>>
    val liveParkingData:LiveData<ParkingWithRegionDistrict>
    fun loadRoads(parkId: Long)
}

@HiltViewModel
class ParkDetailViewModel @Inject constructor(
    private val appDataBase: AppDataBase
) : ViewModel(), IParkDetailViewModel {
    override val roadsLiveData = SingleLiveEvent<List<RoadWithStations>>()
    override val liveParkingData = MutableLiveData<ParkingWithRegionDistrict>()

    override fun loadRoads(parkId: Long) {
        viewModelScope.launch {
            try {
                val roads = appDataBase.parkingRoadJoin().getRoadsForParking(parkId)
                val roadsWithStations = mutableListOf<RoadWithStations>()
                roads.forEach { r ->
                    val stations = appDataBase.roadStationJoin().getStationNamesForRoads(r.id)
                    val roadWithStations = RoadWithStations(r, stations)
                    roadsWithStations.add(roadWithStations)
                }
                roadsLiveData.postValue(roadsWithStations)
                val parking = appDataBase.parkingDao().getParkingWithRegionById(parkId)
                val districtData=parking.districtData
                val regionData = appDataBase.regionDao().getById(districtData.regionId)
                val parkingData = ParkingWithRegionDistrict(
                    parkingData = parking.parkingData,
                    regionData = regionData,
                    districtData = districtData
                )
                liveParkingData.postValue(parkingData)
            } catch (e: Exception) {

            }
        }
    }

}
