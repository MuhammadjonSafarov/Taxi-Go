package uz.xia.taxigo.data.local.remote.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import uz.xia.taxigo.data.local.entity.RoadData
import uz.xia.taxigo.data.local.remote.entity.ParkingRoadJoin

@Dao
interface ParkingRoadJoinDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(parkingRoadJoin: ParkingRoadJoin)

    @Transaction
    @Query("SELECT * FROM road_data r INNER JOIN parking_road_join ON r.id = parking_road_join.road_id WHERE parking_road_join.parking_id=:parkId")
    suspend fun getRoadsForParking(parkId: Long): List<RoadData>

    @Delete
    suspend fun delete(parkingRoadJoin: ParkingRoadJoin)
}
