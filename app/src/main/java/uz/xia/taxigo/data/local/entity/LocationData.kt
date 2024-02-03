package uz.xia.taxigo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location")
data class LocationData(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val longitude: Double,
    val latitude: Double,
    val time: Long
)