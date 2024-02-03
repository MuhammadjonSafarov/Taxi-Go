package uz.xia.taxigo.ui.add_data.road.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uz.xia.taxigo.data.local.AppDataBase
import uz.xia.taxigo.data.local.entity.RoadData
import uz.xia.taxigo.data.local.remote.entity.ParkingRoadJoin
import javax.inject.Inject

@HiltViewModel
class RoadListViewModel @Inject constructor(
    private val appDataBase: AppDataBase
) : ViewModel() {
    val liveRoadList = MutableLiveData<List<RoadData>>()
    fun getRoads(id: List<Int>) {
        viewModelScope.launch {
            val parks = appDataBase.roadDao().getRoadsNotParking(id)
            liveRoadList.postValue(parks)
        }
    }

    fun setRoadJoinParking(ids: List<Int>, id: Long, roadId: Long) {
        viewModelScope.launch {
            val parkingRoad = ParkingRoadJoin(id, roadId)
            appDataBase.parkingRoadJoin().insert(parkingRoad)
        }.invokeOnCompletion {
            getRoads(ids)
        }
    }
}
