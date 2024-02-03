package uz.xia.taxigo.data.local.remote.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import uz.xia.taxigo.data.local.entity.RoadData
import uz.xia.taxigo.data.local.entity.StationData

@Entity(
    tableName = "road_station_join",
    primaryKeys = ["road_id", "station_id"],
    foreignKeys = [ForeignKey(
        entity = RoadData::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("road_id")
    ), ForeignKey(
        entity = StationData::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("station_id")
    )]
)
data class RoadStationJoin(
    @ColumnInfo(name = "road_id")
    val roadId: Long,
    @ColumnInfo(name = "station_id")
    val stationId: Long,
    @ColumnInfo(name = "order_id")
    val orderId: Long = 0L
)
