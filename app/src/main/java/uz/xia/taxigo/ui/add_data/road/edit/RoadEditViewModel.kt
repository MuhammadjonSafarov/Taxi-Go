package uz.xia.taxigo.ui.add_data.road.edit


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import uz.xia.taxigo.data.local.AppDataBase
import uz.xia.taxigo.data.local.entity.RoadData
import uz.xia.taxigo.data.local.remote.model.StationWithRegions
import javax.inject.Inject

private const val TAG = "RoadEditViewModel"

@HiltViewModel
class RoadEditViewModel @Inject constructor(
    private val appDataBase: AppDataBase
) : ViewModel() {
    val liveStations = MutableLiveData<List<StationWithRegions>>()
    private var roadId: Long = 0L

    fun getRoadById(id: Long) {
        roadId = id
        viewModelScope.launch {
            val stations = appDataBase.roadStationJoin().getStationsWithRegionForRoads(id)
            Timber.d("$TAG $stations")
            liveStations.postValue(stations)
        }
    }


    fun saveRoad(name: String, destination: Long) {
        viewModelScope.launch {
            val roadData = RoadData(
                name = name,
                destination = destination
            )
            appDataBase.roadDao().insert(roadData)
        }
    }

    fun removeRoadJoinStation(stationId: Long) {
        viewModelScope.launch {
            val station=appDataBase.roadStationJoin().getByStationId(roadId,
                stationId)
            appDataBase.roadStationJoin().deleteByStationId(
                roadId, stationId)
            Timber.d("$TAG $station roadId $roadId stationId $stationId")
        }.invokeOnCompletion {
            getRoadById(roadId)
        }
    }

}
