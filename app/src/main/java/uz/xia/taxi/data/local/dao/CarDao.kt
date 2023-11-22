package uz.xia.taxi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import uz.xia.taxi.data.local.entity.CarData

@Dao
interface CarDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(carData: CarData)

    @Query("SELECT * FROM car_data")
    suspend fun getData():List<CarData>
}
