package uz.xia.taxigo.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import uz.xia.taxigo.common.EMPTY_STRING

@Entity(tableName = "parking_data")
data class ParkingData(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String = "ASC",
    @ColumnInfo(name = "name_uz_kr") val nameUzKr: String = EMPTY_STRING,
    @ColumnInfo(name = "name_uz_lt") val nameUzLt: String = EMPTY_STRING,
    @ColumnInfo(name = "name_en") val nameRu: String = EMPTY_STRING,
    @ColumnInfo(name = "name_ru") val nameEn: String = EMPTY_STRING,
    @ColumnInfo(name = "district_id") val districtId: Long = 0,
    val longitude: Double = 0.0,
    val latitude: Double = 0.0
)

data class GeoLocation(
    val longitude: Double,
    val latitude: Double
)
