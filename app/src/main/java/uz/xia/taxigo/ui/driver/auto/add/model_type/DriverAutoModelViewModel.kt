package uz.xia.taxigo.ui.driver.auto.add.model_type

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uz.xia.taxigo.data.remote.model.car.AutoModel
import uz.xia.taxigo.data.remote.model.paging.PagingResponse
import uz.xia.taxigo.network.ApiService
import uz.xia.taxigo.utils.SingleLiveEvent
import uz.xia.taxigo.utils.widget.base.BaseViewModel
import uz.xia.taxigo.utils.widget.base.ResourceUI
import javax.inject.Inject

@HiltViewModel
class DriverAutoModelViewModel @Inject constructor(
    private val service: ApiService
) : BaseViewModel() {

    val liveData = SingleLiveEvent<ResourceUI<PagingResponse<List<AutoModel>>>?>()

    fun driverAutos(text:String) {
        viewModelScope.launch {
            liveData.value = ResourceUI.Loading
            liveData.value = handleResponse {
                service.getAutoModels(text)
            }
        }
    }
}
