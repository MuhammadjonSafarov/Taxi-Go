package uz.xia.taxigo.ui.driver.auto.list

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uz.xia.taxigo.data.remote.model.car.CarData
import uz.xia.taxigo.data.remote.model.paging.PagingResponse
import uz.xia.taxigo.network.ApiService
import uz.xia.taxigo.utils.SingleLiveEvent
import uz.xia.taxigo.utils.widget.base.BaseViewModel
import uz.xia.taxigo.utils.widget.base.ResourceUI
import javax.inject.Inject

@HiltViewModel
class DriverCarsViewModel @Inject constructor(
    private val apiService: ApiService
) : BaseViewModel() {
    val liveCarsData = SingleLiveEvent<ResourceUI<PagingResponse<List<CarData>>>?>()

    fun getCars() {
        viewModelScope.launch {
            liveCarsData.value = ResourceUI.Loading
            liveCarsData.value = handleResponse {
                apiService.getCars()
            }
        }
    }
}