package uz.xia.taxigo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import uz.xia.taxigo.data.local.entity.StationData
import uz.xia.taxigo.data.local.remote.model.StationWithRegions

@Dao
interface StationDao {

    @Insert
    suspend fun insert(vararg it:StationData)

    @Insert
    suspend fun insertAll( it:List<StationData>)

    @Transaction
    @Query("SELECT * FROM station_data WHERE  NOT id IN(:itemsIds)")
    suspend fun getStationsWithRegions(itemsIds:List<Int>): List<StationWithRegions>

}
