package uz.xia.taxigo.data.local.dao

import androidx.room.*
import uz.xia.taxigo.data.local.entity.RegionData

@Dao
interface RegionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(regionData: RegionData)

    @Query("SELECT * FROM region_data")
    suspend fun getAll(): List<RegionData>

    @Query("SELECT * FROM region_data WHERE id=:Id")
    suspend fun getById(Id:Long): RegionData

    @Delete
    suspend fun delete(location: RegionData)
}