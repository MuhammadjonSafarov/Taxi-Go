package uz.xia.taxigo.ui.driver.home.dialog

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uz.xia.taxigo.data.local.AppDataBase
import uz.xia.taxigo.data.local.entity.ParkingData
import uz.xia.taxigo.data.local.entity.RoadData
import uz.xia.taxigo.data.local.remote.model.ParkingWithRegionDistrict
import uz.xia.taxigo.data.remote.enumrition.DriverCarType
import uz.xia.taxigo.data.remote.model.car.CarDataDto
import uz.xia.taxigo.data.remote.model.car.CarDataResponse
import uz.xia.taxigo.network.ApiService
import uz.xia.taxigo.utils.SingleLiveEvent
import uz.xia.taxigo.utils.widget.base.BaseViewModel
import uz.xia.taxigo.utils.widget.base.ResourceUI
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class DriverCarDetailViewModel @Inject constructor(
    private val appDataBase: AppDataBase,
    private val apiService: ApiService
) : BaseViewModel() {
    val liveParkData = SingleLiveEvent<Pair<ParkingWithRegionDistrict?, RoadData>>()
    val liveStatus = SingleLiveEvent<ResourceUI<CarDataResponse>?>()
    private var mParking: ParkingData? = null
    private var mRoadId: Long = 0L

    fun loadParkingRoad(parkId: Long, roadId: Long) {
        viewModelScope.launch {
            val parking = appDataBase.parkingDao().getParkingWithRegionById(parkId)
            mParking = parking.parkingData
            val districtData = parking.districtData
            val regionData = appDataBase.regionDao().getById(districtData.regionId)
            val parkingData = ParkingWithRegionDistrict(
                parkingData = parking.parkingData,
                regionData = regionData,
                districtData = districtData
            )
            val roadData = appDataBase.roadDao().getRoadById(roadId)
            mRoadId = roadData.id
            val data = Pair(parkingData, roadData)
            liveParkData.postValue(data)
        }
    }

    fun sendCarDetailParking(carId: Long) {
        val request = CarDataDto(
            walkingTime = Date().time,
            parkingId = mParking?.id ?: 0L,
            roadId = mRoadId,
            longitude = mParking?.longitude ?: 0.0,
            latitude = mParking?.latitude ?: 0.0,
            driverCarType = DriverCarType.PARKING.name
        )
        viewModelScope.launch {
            liveStatus.value = ResourceUI.Loading
            liveStatus.value = handleResponse {
                apiService.parkingCar(carId, request)
            }
        }
    }
}