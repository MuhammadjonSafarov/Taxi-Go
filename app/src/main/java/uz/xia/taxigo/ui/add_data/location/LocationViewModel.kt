package uz.xia.taxigo.ui.add_data.location

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
import uz.xia.taxigo.data.IPreference
import uz.xia.taxigo.data.local.AppDataBase
import uz.xia.taxigo.data.local.entity.DistrictData
import uz.xia.taxigo.data.local.entity.RegionData
import uz.xia.taxigo.data.local.entity.StationData
import uz.xia.taxigo.data.local.remote.entity.RoadStationJoin
import uz.xia.taxigo.data.remote.model.nomination.NearEmpty
import uz.xia.taxigo.data.remote.model.nomination.NearLoading
import uz.xia.taxigo.data.remote.model.nomination.NearbyPlace
import uz.xia.taxigo.data.remote.model.nomination.PlaceMapper
import uz.xia.taxigo.data.remote.model.nomination.polygon.PolygonData
import uz.xia.taxigo.network.NominationService
import uz.xia.taxigo.utils.SingleLiveEvent
import java.util.Date
import javax.inject.Inject

interface ILocationViewModel {

    val livePlaceList: LiveData<List<NearbyPlace>>
    val livePlace: LiveData<NearbyPlace>

    val liveRegions: LiveData<List<RegionData>>
    val liveDistricts: LiveData<List<DistrictData>>
    val livePolygonData: LiveData<List<PolygonData>>
    fun searchPlace(query: String, lang: String)
    fun loadNearbyPlaces()

    fun geoCode(latitude: Double, longitude: Double, lang: String)

    fun provinceAll()
    fun districtsById(regionId: Long)
    fun getRegionPolygon(name: String)
    fun saveStation(latitude: Double, longitude: Double, name: String, districtId: Long)
    fun saveStationJoinRoad(roadId: Long, stationId: Long)

    fun locationUpdate(districtId:Long,latitude: Double, longitude: Double)
}

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val nominationService: NominationService,
    private val preference: IPreference,
    private val dataBase: AppDataBase
) : ViewModel(), ILocationViewModel {
    override val livePlaceList = MutableLiveData<List<NearbyPlace>>()
    override val livePlace = MutableLiveData<NearbyPlace>()
    override val liveRegions = SingleLiveEvent<List<RegionData>>()
    override val liveDistricts = SingleLiveEvent<List<DistrictData>>()
    override val livePolygonData = SingleLiveEvent<List<PolygonData>>()

    private var lastSearchJob: Job? = null
    private val placeToNearMapper = PlaceMapper(preference.latitude, preference.longitude)


    init {
        getRegionPolygon("Oʻzbekiston")
    }
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

    override fun provinceAll() {
        viewModelScope.launch {
            val provinces = dataBase.regionDao().getAll()
            liveRegions.postValue(provinces)
        }
    }

    override fun districtsById(regionId: Long) {
        viewModelScope.launch {
            val provinces = dataBase.districtDao().getByRegionIdAll(regionId)
            liveDistricts.postValue(provinces)
        }
    }

    override fun saveStation(latitude: Double, longitude: Double, name: String, districtId: Long) {
        viewModelScope.launch {
            val station = StationData(
                latitude = latitude,
                longitude = longitude,
                districtId = districtId,
                nameUzLt = name
            )
            dataBase.stationDao().insert(station)
        }
    }

    override fun getRegionPolygon(name: String) {
        viewModelScope.launch {
            try {
                val res = nominationService.getPolygons(query = "$name")
                if (res.isSuccessful) {
                    livePolygonData.postValue(res.body())
                } else {

                }
            } catch (e: java.lang.Exception) {

            }

        }
    }



    override fun saveStationJoinRoad(roadId: Long, stationId: Long) {
        viewModelScope.launch {
            val orderId = dataBase.roadStationJoin().getLastOrderId() ?: 0L
            val roadStation = RoadStationJoin(roadId, stationId,orderId = (orderId + 1))
            dataBase.roadStationJoin().insert(roadStation)
        }
    }

    override fun locationUpdate(districtId: Long, latitude: Double, longitude: Double) {
        viewModelScope.launch {
            dataBase.districtDao().updateLocation(districtId,longitude,latitude)
        }
    }
}
