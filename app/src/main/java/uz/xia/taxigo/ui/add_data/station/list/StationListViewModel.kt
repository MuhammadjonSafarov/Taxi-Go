package uz.xia.taxigo.ui.add_data.station.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import uz.xia.taxigo.data.local.AppDataBase
import uz.xia.taxigo.data.local.entity.DistrictData
import uz.xia.taxigo.data.local.remote.entity.RoadStationJoin
import uz.xia.taxigo.data.local.remote.model.RegionWithDistrict
import uz.xia.taxigo.data.local.remote.model.StationWithRegions
import uz.xia.taxigo.data.remote.model.StationMarkerData
import uz.xia.taxigo.utils.SingleLiveEvent
import javax.inject.Inject

private const val TAG = "StationListViewModel"
@HiltViewModel
class StationListViewModel @Inject constructor(
    private val appDataBase: AppDataBase
) : ViewModel() {
    val liveRoadList = SingleLiveEvent<List<StationMarkerData>>()
    val liveDistricts = SingleLiveEvent<List<RegionWithDistrict>>()
    init {
        viewModelScope.launch {
            val allDistrict = appDataBase.districtDao().getAllDistrictWithRegion()
            liveDistricts.postValue(allDistrict)
        }
    }

    fun getStations(ids: List<Int>) {
        viewModelScope.launch {
            val customStations = mutableListOf<StationMarkerData>()
            val stations = appDataBase.stationDao().getStationsWithRegions(listOf())
            stations.forEach {
                val customStation=StationMarkerData(
                    it.stationData.id,
                    it.stationData.nameUzLt,
                    it.stationData.longitude,
                    it.stationData.latitude,
                    it.districtData.nameUzLt,
                    isJoined=(it.stationData.id.toInt() in ids)
                )
                customStations.add(customStation)
            }
            liveRoadList.postValue(customStations)
        }
    }

    fun setStationJoinRoad(ids: List<Int>, id: Long, stationId: Long) {
        viewModelScope.launch {
            val orderId = appDataBase.roadStationJoin().getLastOrderId() ?: 0L
            val joinData = RoadStationJoin(roadId = id, stationId = stationId, orderId = (orderId + 1))
            appDataBase.roadStationJoin().insert(joinData)
        }.invokeOnCompletion {
            getStations(ids)
        }
    }
}
