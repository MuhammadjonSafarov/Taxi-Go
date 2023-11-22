package uz.xia.taxi.data.local.remote.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import uz.xia.taxi.data.local.entity.RoadData
import uz.xia.taxi.data.local.remote.entity.ParkingRoadJoin
import uz.xia.taxi.data.local.remote.model.RoadWithStations

@Dao
interface ParkingRoadJoinDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(parkingRoadJoin: ParkingRoadJoin)

    @Transaction
    @Query("SELECT * FROM road_data r INNER JOIN parking_road_join ON r.id = parking_road_join.road_id WHERE parking_road_join.parking_id=:parkId")
    suspend fun getRoadsForParking(parkId: Long): List<RoadData>
}
