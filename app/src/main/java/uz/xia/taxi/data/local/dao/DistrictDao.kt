package uz.xia.taxi.data.local.dao

import androidx.room.*
import uz.xia.taxi.data.local.entity.DistrictData

@Dao
interface DistrictDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(district: DistrictData)

    @Query("SELECT * FROM district_data")
    suspend fun getAll(): List<DistrictData>

    @Query("SELECT * FROM district_data WHERE region_id=:regionId")
    suspend fun getByRegionIdAll(regionId: Long): List<DistrictData>

    @Query("UPDATE district_data SET longitude=:mLongitude,latitude=:mLatitude WHERE name_uz_lt like :name")
    suspend fun updateLocation(name: String, mLongitude: Double, mLatitude: Double)

    @Query("SELECT * FROM district_data WHERE id=:Id")
    suspend fun getById(Id: Int): DistrictData

    @Delete
    suspend fun delete(location: DistrictData)
}
