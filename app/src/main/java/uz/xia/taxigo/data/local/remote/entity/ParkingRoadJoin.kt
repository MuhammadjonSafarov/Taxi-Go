package uz.xia.taxigo.data.local.remote.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import uz.xia.taxigo.data.local.entity.ParkingData
import uz.xia.taxigo.data.local.entity.RoadData

@Entity(
    tableName = "parking_road_join",
    primaryKeys = ["parking_id", "road_id"],
    foreignKeys = [ForeignKey(
        entity = ParkingData::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("parking_id")
    ), ForeignKey(
        entity = RoadData::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("road_id")
    )]
)
data class ParkingRoadJoin(
    @ColumnInfo(name = "parking_id")
    val parkingId: Long,
    @ColumnInfo(name = "road_id")
    val roadId: Long
)
