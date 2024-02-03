package uz.xia.taxigo.ui.home

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import uz.xia.taxigo.data.IPreference
import uz.xia.taxigo.data.local.AppDataBase
import uz.xia.taxigo.data.local.entity.GeoLocation
import uz.xia.taxigo.data.local.entity.ParkingData
import uz.xia.taxigo.data.local.remote.entity.RoadStationJoin
import uz.xia.taxigo.data.remote.model.nomination.NearbyPlace
import uz.xia.taxigo.network.ApiService
import uz.xia.taxigo.network.NominationService
import uz.xia.taxigo.utils.SingleLiveEvent
import java.util.Date
import javax.inject.Inject

interface IHomeViewModel {
    val livePlace: LiveData<NearbyPlace>
    val liveParkingLotsLocations: LiveData<List<GeoLocation>>
    val liveParking: LiveData<ParkingData>
    fun geoCode(latitude: Double, longitude: Double, lang: String)
    fun loadParkingLots()
    fun loadParking(longitude: Double, latitude: Double)
}

private const val TAG = "HomeViewModel"
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val nominationService: NominationService,
    private val appDataBase: AppDataBase,
    private val preference: IPreference
) : ViewModel(), IHomeViewModel {
    override val livePlace = MutableLiveData<NearbyPlace>()
    private var lastSearchJob: Job? = null
    override val liveParkingLotsLocations = SingleLiveEvent<List<GeoLocation>>()
    override val liveParking = SingleLiveEvent<ParkingData>()

    init {
        loadParkingLots()
    }

    override fun loadParkingLots() {
        viewModelScope.launch {
            val list = appDataBase.parkingDao().getParkingLots() ?: listOf()
            liveParkingLotsLocations.postValue(list)
        }
    }

    override fun loadParking(longitude: Double, latitude: Double) {
        viewModelScope.launch {
            val geoLocation = appDataBase.parkingDao().getParking(longitude, latitude)
            liveParking.postValue(geoLocation)
        }
    }


    override fun geoCode(latitude: Double, longitude: Double, lang: String) {
        lastSearchJob?.cancel()
        lastSearchJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                //livePlaceList.postValue(listOf(NearLoading()))
                preference.latitude = latitude
                preference.longitude = longitude
                val response = nominationService.loadAddress(latitude, longitude, lang)
                var title = response?.displayName ?: "Empty place name"
                val lastIndexComma = title.lastIndexOf(',')
                title = if (lastIndexComma == -1) title
                else title.substring(0, lastIndexComma)
                title = title.replace(Regex(", \\d{6,}"), "")
                val distance = FloatArray(1)
                Location.distanceBetween(
                    preference.latitude,
                    preference.longitude,
                    latitude, longitude,
                    distance
                )
                livePlace.postValue(
                    NearbyPlace(
                        Date().time,
                        title,
                        title,
                        distance[0],
                        latitude,
                        longitude
                    )
                )
            } catch (e: Exception) {
                Timber.d(e)
                //livePlaceList.postValue(listOf(NearEmpty()))
            }
        }
    }
}
