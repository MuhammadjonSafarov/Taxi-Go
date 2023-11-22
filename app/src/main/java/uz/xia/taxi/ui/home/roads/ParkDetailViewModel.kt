package uz.xia.taxi.ui.home.roads

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uz.xia.taxi.data.local.AppDataBase
import uz.xia.taxi.data.local.entity.RoadData
import uz.xia.taxi.data.local.remote.model.RoadWithStations
import uz.xia.taxi.utils.SingleLiveEvent
import java.lang.Exception
import javax.inject.Inject

interface IParkDetailViewModel {
    val roadsLiveData: LiveData<List<RoadWithStations>>
    fun loadRoads(parkId: Long)
}

@HiltViewModel
class ParkDetailViewModel @Inject constructor(
    private val appDataBase: AppDataBase
) : ViewModel(), IParkDetailViewModel {
    override val roadsLiveData = SingleLiveEvent<List<RoadWithStations>>()

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
            } catch (e: Exception) {

            }
        }
    }

}
