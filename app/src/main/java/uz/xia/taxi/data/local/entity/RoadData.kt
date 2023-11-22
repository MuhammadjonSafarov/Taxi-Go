package uz.xia.taxi.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "road_data")
data class RoadData(
    @PrimaryKey(autoGenerate = true)
    val id:Long=0,
    val name:String,
    val destination:Long)
