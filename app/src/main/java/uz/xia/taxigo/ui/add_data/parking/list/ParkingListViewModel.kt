package uz.xia.taxigo.ui.add_data.parking.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uz.xia.taxigo.data.local.AppDataBase
import uz.xia.taxigo.data.local.entity.StationData
import uz.xia.taxigo.data.local.remote.model.ParkingWithRegions
import javax.inject.Inject

@HiltViewModel
class ParkingListViewModel @Inject constructor(
    private val appDataBase: AppDataBase,
) : ViewModel() {
    val liveParkingList = MutableLiveData<List<ParkingWithRegions>>()
    fun getParkings() {
        viewModelScope.launch {
            val parks = appDataBase.parkingDao().getParkingList()
            liveParkingList.postValue(parks)
        }
    }

}
