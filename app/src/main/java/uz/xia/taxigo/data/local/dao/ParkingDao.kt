package uz.xia.taxigo.data.local.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import uz.xia.taxigo.data.local.entity.GeoLocation
import uz.xia.taxigo.data.local.entity.ParkingData
import uz.xia.taxigo.data.local.remote.model.ParkingWithRegions

@Dao
interface ParkingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg parkingData: ParkingData)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(parkingData: ParkingData)

    @Transaction
    @Query("SELECT longitude,latitude FROM parking_data")
    suspend fun getParkingLots():List<GeoLocation>?

    @Query("SELECT * FROM parking_data WHERE longitude=:Longitude and latitude=:Latitude")
    suspend fun getParking(Longitude:Double,Latitude:Double):ParkingData

    @Transaction
    @Query("SELECT * FROM parking_data")
    suspend fun getParkingList():List<ParkingWithRegions>

    @Query("SELECT * FROM parking_data WHERE id=:Id")
    suspend fun getParkingById(Id:Long):ParkingData

    @Transaction
    @Query("SELECT * FROM parking_data WHERE id=:Id")
    suspend fun getParkingWithRegionById(Id:Long):ParkingWithRegions
}
