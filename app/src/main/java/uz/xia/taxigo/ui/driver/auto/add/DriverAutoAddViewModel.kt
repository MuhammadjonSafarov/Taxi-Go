package uz.xia.taxigo.ui.driver.auto.add

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uz.xia.taxigo.data.remote.model.car.CarColorType
import uz.xia.taxigo.data.remote.model.car.CarDataRequest
import uz.xia.taxigo.data.remote.model.car.CarDataResponse
import uz.xia.taxigo.network.ApiService
import uz.xia.taxigo.utils.SingleLiveEvent
import uz.xia.taxigo.utils.widget.base.BaseViewModel
import uz.xia.taxigo.utils.widget.base.ResourceUI
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class DriverAutoAddViewModel @Inject constructor(
    private val apiService: ApiService
) : BaseViewModel() {

    var carData = CarDataRequest(carManufactureDate = Date().time)

    val liveCarDataStatus = SingleLiveEvent<ResourceUI<CarDataResponse>?>()

    val liveTypeState = MutableLiveData(false)
    val liveColorState = MutableLiveData(false)
    val liveNumberState = MutableLiveData(false)
    val liveManufactureDateState = MutableLiveData(false)

    fun validateForms(userId:Long) {
        if (carData.autoData != null &&
            carData.color.isNotEmpty() &&
            carData.number.isNotEmpty() &&
            carData.carManufactureDate != null
        ) {
            sendDataAuto(userId)
        } else {
            liveTypeState.value = carData.autoData == null
            liveColorState.value = carData.color.isEmpty()
            liveNumberState.value = carData.number.isEmpty()
            liveManufactureDateState.value = carData.carManufactureDate == null
        }
    }

    private fun sendDataAuto(userId:Long) {
        viewModelScope.launch {
            liveCarDataStatus.value = ResourceUI.Loading
            liveCarDataStatus.value = handleResponse {
                apiService.sendCar(userId,carData)
            }
        }
    }

    fun setColorType(position: Int) {
        CarColorType.values().forEach {
            if (it.v == position) {
                carData.color = it.name
                return@forEach
            }
        }
    }
}