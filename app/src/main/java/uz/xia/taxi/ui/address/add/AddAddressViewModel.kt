package uz.xia.taxi.ui.address.add

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import uz.xia.taxi.data.IPreference
import uz.xia.taxi.data.local.dao.UserAddressDao
import uz.xia.taxi.data.local.entity.UserAddress
import uz.xia.taxi.data.remote.enumrition.AddressStatus
import uz.xia.taxi.data.remote.model.nomination.NearEmpty
import uz.xia.taxi.data.remote.model.nomination.NearLoading
import uz.xia.taxi.data.remote.model.nomination.NearbyPlace
import uz.xia.taxi.data.remote.model.nomination.PlaceMapper
import uz.xia.taxi.network.NominationService
import uz.xia.taxi.utils.SingleLiveEvent
import java.util.Date
import javax.inject.Inject

interface IAddAddressViewModel {
    val livePlaceList: LiveData<List<NearbyPlace>>
    val livePlace: LiveData<NearbyPlace>
    val isAddressSaveLiveData:LiveData<Boolean>
    fun searchPlace(query: String, lang: String)
    fun loadNearbyPlaces()

    fun geoCode(latitude: Double, longitude: Double, lang: String)
    fun saveAddress(position: Int, addressName: String, address: String)
}

private const val TAG = "AddAddressViewModel"

@HiltViewModel
class AddAddressViewModel @Inject constructor(
    private val nominationService: NominationService,
    private val preference: IPreference,
    private val userAddressDao: UserAddressDao
) : ViewModel(), IAddAddressViewModel {
    override val livePlaceList = MutableLiveData<List<NearbyPlace>>()
    override val livePlace = MutableLiveData<NearbyPlace>()
    override val isAddressSaveLiveData = SingleLiveEvent<Boolean>()

    // private val addressDao = AppDatabase.getInstance(app).addressDao()
    private var lastSearchJob: Job? = null
    private val placeToNearMapper = PlaceMapper(preference.latitude, preference.longitude)

    override fun loadNearbyPlaces() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                livePlaceList.postValue(listOf(NearLoading()))
                val response = nominationService.searchPlaces("uzbekistan", "uz")
                if (response.isNotEmpty()) {
                    val listNearbyPlace = ArrayList<NearbyPlace>()
                    for (place in response) {
                        place.latitude = place.lat.toDouble()
                        place.longitude = place.lon.toDouble()

                        val lastIndexComma = place.title.lastIndexOf(',')
                        place.title = if (lastIndexComma == -1) place.title
                        else place.title.substring(0, lastIndexComma)
                        place.title = place.title.replace(Regex(", \\d{6,}"), "")
                        listNearbyPlace.add(placeToNearMapper.transform(place))
                    }
                    livePlaceList.postValue(listNearbyPlace)
                } else livePlaceList.postValue(listOf(NearEmpty()))
            } catch (e: Exception) {
                Timber.d(e)
                livePlaceList.postValue(listOf(NearEmpty()))
            }
        }
    }

    override fun searchPlace(query: String, lang: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                livePlaceList.postValue(listOf(NearLoading()))
                delay(100)
                val response = nominationService.searchPlaces("$query,uzbekistan", lang)
                if (response.isNotEmpty()) {
                    val listNearbyPlace = ArrayList<NearbyPlace>()
                    for (place in response) {
                        place.latitude = place.lat.toDouble()
                        place.longitude = place.lon.toDouble()

                        val lastIndexComma = place.title.lastIndexOf(',')
                        place.title =
                            if (lastIndexComma == -1) place.title else place.title.substring(
                                0, lastIndexComma
                            )
                        place.title = place.title.replace(Regex(", \\d{6,}"), "")

                        Location.distanceBetween(
                            preference.latitude,
                            preference.longitude,
                            place.latitude!!,
                            place.longitude!!,
                            place.distance
                        )
                        listNearbyPlace.add(
                            NearbyPlace(
                                place.placeId,
                                place.title,
                                place.title,
                                place.distance[0],
                                place.latitude!!,
                                place.longitude!!
                            )
                        )
                    }
                    listNearbyPlace.sortBy { it.distance }
                    livePlaceList.postValue(listNearbyPlace)
                } else livePlaceList.postValue(listOf(NearEmpty()))
            } catch (e: Exception) {
                Timber.d(e)
                livePlaceList.postValue(listOf(NearEmpty()))
            }
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
                    latitude, longitude, distance
                )
                livePlace.postValue(
                    NearbyPlace(
                        Date().time, title, title, distance[0], latitude, longitude
                    )
                )
            } catch (e: Exception) {
                Timber.d(e)
                //livePlaceList.postValue(listOf(NearEmpty()))
            }
        }
    }

    override fun saveAddress(position: Int, addressName: String, address: String) {
        viewModelScope.launch {
            val type = when (position) {
                0 -> AddressStatus.HOME
                1 -> AddressStatus.WORK
                else -> AddressStatus.OTHER
            }
            val time = Date().time
            val userAddress = UserAddress(
                name = addressName,
                description = address,
                latitude = preference.latitude,
                longitude = preference.longitude,
                createAt = time,
                updateAt = time,
                type = type
            )
            userAddressDao.insert(userAddress)
            isAddressSaveLiveData.postValue(true)
        }
    }


}
