package uz.xia.taxi.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import uz.xia.taxi.data.remote.model.LatLng

@Entity(tableName = "region_data")
data class RegionData(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nameRu: String = "",
    val nameEn: String = "",
    val nameUzLat: String = "",
    val nameUzKr: String = "",
    val polygonOne:List<LatLng>,
    val polygonTwo:List<LatLng>,
)