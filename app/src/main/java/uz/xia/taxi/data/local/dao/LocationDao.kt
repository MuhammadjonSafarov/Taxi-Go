package uz.xia.taxi.data.local.dao

import androidx.room.*
import uz.xia.taxi.data.local.entity.LocationData
import uz.xia.taxi.data.remote.model.LatLng

@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(location: LocationData)

    @Query("SELECT * FROM location")
    suspend fun getAll(): List<LocationData>

    @Transaction
    @Query("SELECT  latitude ,longitude FROM location ORDER BY time DESC LIMIT 1")
    suspend fun getLocation(): LatLng?

    @Delete
    suspend fun delete(location: LocationData)
}