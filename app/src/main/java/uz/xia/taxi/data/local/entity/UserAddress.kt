package uz.xia.taxi.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import uz.xia.taxi.common.EMPTY_STRING
import uz.xia.taxi.data.remote.enumrition.AddressStatus

@Entity(tableName = "user_address")
data class UserAddress(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String = EMPTY_STRING,
    val description: String = EMPTY_STRING,
    @ColumnInfo(name = "create_at")
    val createAt: Long,
    @ColumnInfo(name = "update_at")
    val updateAt: Long,
    val type: AddressStatus,
    val longitude: Double,
    val latitude: Double
)
