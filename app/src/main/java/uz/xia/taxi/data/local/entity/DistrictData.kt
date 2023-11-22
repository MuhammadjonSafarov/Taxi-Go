package uz.xia.taxi.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import uz.xia.taxi.common.EMPTY_STRING
import uz.xia.taxi.data.remote.model.LatLng

@Entity(tableName = "district_data")
data class DistrictData(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "region_id") val regionId:Long=0,
    @ColumnInfo(name = "name_uz_kr") val nameUzKr: String = EMPTY_STRING,
    @ColumnInfo(name = "name_uz_lt") val nameUzLt: String = EMPTY_STRING,
    @ColumnInfo(name = "name_ru") val nameRu: String = EMPTY_STRING,
    @ColumnInfo(name = "name_en") val nameEn: String = EMPTY_STRING,
    @ColumnInfo(name = "longitude") val longitude: Double = 0.0,
    @ColumnInfo(name = "latitude") val latitude: Double = 0.0,
)
