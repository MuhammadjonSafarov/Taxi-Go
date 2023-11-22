package uz.xia.taxi.ui.add.location

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import uz.xia.taxi.data.IPreference
import uz.xia.taxi.data.remote.model.nomination.NearbyPlace
import uz.xia.taxi.network.NominationService
import java.util.Date
import javax.inject.Inject

interface ILocationViewModel{
    val livePlace: LiveData<NearbyPlace>
    fun geoCode(latitude: Double, longitude: Double, lang: String)
}
@HiltViewModel
class LocationViewModel @Inject constructor(
    private val nominationService: NominationService,
    private val preference: IPreference,
):ViewModel(),ILocationViewModel {
    private var lastSearchJob: Job? = null
    override val livePlace = MutableLiveData<NearbyPlace>()

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
}
