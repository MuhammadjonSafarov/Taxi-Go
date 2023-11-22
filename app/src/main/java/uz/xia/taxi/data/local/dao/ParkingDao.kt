package uz.xia.taxi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import uz.xia.taxi.data.local.entity.GeoLocation
import uz.xia.taxi.data.local.entity.ParkingData

@Dao
interface ParkingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg parkingData: ParkingData)

    @Transaction
    @Query("SELECT longitude,latitude FROM parking_data")
    suspend fun getParkingLots():List<GeoLocation>?

    @Query("SELECT * FROM parking_data WHERE longitude=:Longitude and latitude=:Latitude")
    suspend fun getParking(Longitude:Double,Latitude:Double):ParkingData
}
