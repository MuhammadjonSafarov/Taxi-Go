package uz.xia.taxi.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import uz.xia.taxi.data.local.entity.UserAddress

@Dao
interface UserAddressDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(address: UserAddress)

    @Query("SELECT * FROM user_address")
    fun getAll(): LiveData<List<UserAddress>>

    @Query("SELECT * FROM user_address WHERE id=:Id")
    suspend fun getById(Id:Int): UserAddress

    @Delete
    suspend fun delete(location: UserAddress)
}
