package uz.xia.taxigo.ui.add_data.road.add


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uz.xia.taxigo.data.local.AppDataBase
import uz.xia.taxigo.data.local.entity.RoadData
import javax.inject.Inject

@HiltViewModel
class RoadAddViewModel @Inject constructor(
    private val appDataBase: AppDataBase
) : ViewModel() {

    fun saveRoad(name: String, destination: Long) {
        viewModelScope.launch {
            val roadData = RoadData(
                name = name, destination = destination
            )
            appDataBase.roadDao().insert(roadData)
        }
    }


}
