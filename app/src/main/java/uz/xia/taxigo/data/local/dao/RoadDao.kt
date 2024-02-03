package uz.xia.taxigo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import uz.xia.taxigo.data.local.entity.RoadData

@Dao
interface RoadDao {

    @Insert
    suspend fun insert(it:RoadData)

    /*@Transaction
    @Query("SELECT * FROM road_data")
    suspend fun getRoads(parkId:Long):List<RoadWithStations>*/

    @Query("SELECT * FROM road_data WHERE  NOT id IN(:itemsIds)")
    suspend fun getRoadsNotParking(itemsIds:List<Int>):List<RoadData>

}
