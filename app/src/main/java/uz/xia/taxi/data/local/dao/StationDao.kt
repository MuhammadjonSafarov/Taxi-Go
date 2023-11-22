package uz.xia.taxi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import uz.xia.taxi.data.local.entity.RoadData
import uz.xia.taxi.data.local.entity.StationData

@Dao
interface StationDao {

    @Insert
    suspend fun insert(vararg it:StationData)
}
