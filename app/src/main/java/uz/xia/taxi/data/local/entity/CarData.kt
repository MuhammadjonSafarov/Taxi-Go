package uz.xia.taxi.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "car_data")
data class CarData(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val carName: String = "",
    val carType: Int = 0,
    val carNumber: String = "",
    val carColor: Int = 0,
    val isConditioner: Boolean = false,
    val isBaggage: Boolean = false,
    val isTopBaggage: Boolean = false
)
