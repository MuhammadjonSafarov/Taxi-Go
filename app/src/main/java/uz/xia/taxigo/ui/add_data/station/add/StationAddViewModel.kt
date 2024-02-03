package uz.xia.taxigo.ui.add_data.station.add

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uz.xia.taxigo.data.local.AppDataBase
import uz.xia.taxigo.data.local.entity.DistrictData
import uz.xia.taxigo.data.local.entity.ParkingData
import uz.xia.taxigo.data.local.entity.RegionData
import uz.xia.taxigo.data.local.entity.StationData
import javax.inject.Inject

@HiltViewModel
class StationAddViewModel @Inject constructor(
    private val appDataBase: AppDataBase
) : ViewModel() {
    val liveRegions = MutableLiveData<List<String>>()
    val liveDistricts = MutableLiveData<List<String>>()
    val regions = mutableListOf<RegionData>()
    val districts = mutableListOf<DistrictData>()
    private var districtId: Long = 0
    private var longitude: Double = 0.0
    private var latitude: Double = 0.0

    init {
        regionsAll()
    }

    fun regionsAll() {
        viewModelScope.launch {
            val list = appDataBase.regionDao().getAll()
            regions.clear()
            regions.addAll(list)
            val regionNames = mutableListOf<String>()
            regions.forEach {
                regionNames.add(it.nameUzLt)
            }
            liveRegions.postValue(regionNames)
        }
    }

    fun districtsAll(position: Int) {
        viewModelScope.launch {
            val list = appDataBase.districtDao().getByRegionIdAll(regions[position].id)
            districts.clear()
            districts.addAll(list)
            val districtNames = mutableListOf<String>()
            districts.forEach {
                districtNames.add(it.nameUzLt)
            }
            liveDistricts.postValue(districtNames)
        }
    }

    fun setDistrict(position: Int) {
        districtId = districts[position].id
    }

    fun setLocation(latitude: Double, longitude: Double) {
        this.latitude = latitude
        this.longitude = longitude
    }

    fun saveStation(name: String) {
        viewModelScope.launch {
            val stationData = StationData(
                nameUzLt = name,
                districtId = districtId,
                latitude = latitude,
                longitude = longitude
            )
            appDataBase.stationDao().insert(stationData)
        }
    }


}
