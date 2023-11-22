package uz.xia.taxi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import uz.xia.taxi.data.local.entity.RoadData
import uz.xia.taxi.data.local.remote.model.RoadWithStations

@Dao
interface RoadDao {

    @Insert
    suspend fun insert(it:RoadData)

 /*   @Transaction
    @Query("SELECT * FROM road_data")
    suspend fun getRoads(parkId:Long):List<RoadWithStations>*/
}
