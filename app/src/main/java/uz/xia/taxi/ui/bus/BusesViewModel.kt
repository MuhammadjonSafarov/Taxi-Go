package uz.xia.taxi.ui.bus

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import uz.xia.taxi.common.Status
import uz.xia.taxi.data.remote.model.bus.Schedule
import uz.xia.taxi.network.AutoTicketApiService
import uz.xia.taxi.utils.SingleLiveEvent
import javax.inject.Inject

interface IBusesViewModel {
    val liveData: LiveData<List<Schedule>>
    val liveStatus:LiveData<Status>
    fun loadData()
}

private const val TAG = "BusesViewModel"

@HiltViewModel
class BusesViewModel @Inject constructor(
    private val avtoticketService: AutoTicketApiService
) : ViewModel(), IBusesViewModel {
    override val liveData = MutableLiveData<List<Schedule>>()
    override val liveStatus = SingleLiveEvent<Status>()
    override fun loadData() {
        viewModelScope.launch {
            try {
                liveStatus.postValue(Status.Loading)
                val res = avtoticketService.getScheduledList()
                val schedules = res.mData?.mData ?: listOf()
                liveData.postValue(schedules)
                liveStatus.postValue(Status.Success)
            } catch (e: Exception) {
                liveStatus.postValue(Status.Error())
                Timber.d("$TAG error ${e.message}")
            }
        }
    }

}
