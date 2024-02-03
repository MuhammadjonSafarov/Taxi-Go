package uz.xia.taxigo.ui.bus

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import uz.xia.taxigo.common.Status
import uz.xia.taxigo.data.remote.model.bus.Schedule
import uz.xia.taxigo.network.AutoTicketApiService
import uz.xia.taxigo.utils.SingleLiveEvent
import javax.inject.Inject

interface IBusesViewModel {
    val liveData: LiveData<List<Schedule>>
    val liveStatus: LiveData<Status>
    fun loadData()
    fun loadSearchByName(text: String)
}

private const val TAG = "BusesViewModel"

@HiltViewModel
class BusesViewModel @Inject constructor(
    private val avtoticketService: AutoTicketApiService
) : ViewModel(), IBusesViewModel {
    override val liveData = MutableLiveData<List<Schedule>>()
    override val liveStatus = SingleLiveEvent<Status>()
    private val scheduleList = mutableListOf<Schedule>()

    override fun loadData() {
        viewModelScope.launch {
            try {
                liveStatus.postValue(Status.Loading)
                val res = avtoticketService.getScheduledList()
                val schedules = res.mData?.mData.orEmpty()
                scheduleList.addAll(schedules)
                liveData.postValue(scheduleList)
                liveStatus.postValue(Status.Success)
            } catch (e: Exception) {
                liveStatus.postValue(Status.Error())
                Timber.d("$TAG error ${e.message}")
            }
        }
    }

    override fun loadSearchByName(text: String) {
        val list = scheduleList.filter {
            it.nameUz.lowercase().contains(text.lowercase())
        }
        liveData.value = list
    }

}
