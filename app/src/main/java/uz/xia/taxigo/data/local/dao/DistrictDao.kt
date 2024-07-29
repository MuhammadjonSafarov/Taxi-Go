package uz.xia.taxigo.data.local.dao

import androidx.room.*
import uz.xia.taxigo.data.local.entity.DistrictData
import uz.xia.taxigo.data.local.remote.model.RegionWithDistrict

@Dao
interface DistrictDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(district: DistrictData)

    @Query("SELECT * FROM district_data")
    suspend fun getAll(): List<DistrictData>

    @Query("SELECT * FROM district_data")
    suspend fun getAllDistrictWithRegion(): List<RegionWithDistrict>

    @Query("SELECT * FROM district_data WHERE id=:Id")
    suspend fun getDistrictWithRegion(Id:Long): RegionWithDistrict?

    @Query("SELECT * FROM district_data WHERE region_id=:regionId")
    suspend fun getByRegionIdAll(regionId: Long): List<DistrictData>

    @Query("UPDATE district_data SET longitude=:mLongitude,latitude=:mLatitude WHERE id=:districtId")
    suspend fun updateLocation(districtId: Long, mLongitude: Double, mLatitude: Double)

    @Query("SELECT * FROM district_data WHERE id=:Id")
    suspend fun getById(Id: Long): DistrictData

    @Delete
    suspend fun delete(location: DistrictData)
}
