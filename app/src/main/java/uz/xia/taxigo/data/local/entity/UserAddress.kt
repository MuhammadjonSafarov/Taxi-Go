package uz.xia.taxigo.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import uz.xia.taxigo.common.EMPTY_STRING
import uz.xia.taxigo.data.remote.enumrition.AddressStatus

@Entity(tableName = "user_address")
open class UserAddress(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String = EMPTY_STRING,
    val description: String = EMPTY_STRING,
    @ColumnInfo(name = "create_at")
    val createAt: Long =0,
    @ColumnInfo(name = "update_at")
    val updateAt: Long = 0,
    val type: AddressStatus = AddressStatus.OTHER,
    val longitude: Double = 0.0,
    val latitude: Double = 0.0
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UserAddress) return false

        if (id != other.id) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (createAt != other.createAt) return false
        if (updateAt != other.updateAt) return false
        if (type != other.type) return false
        if (longitude != other.longitude) return false
        if (latitude != other.latitude) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + createAt.hashCode()
        result = 31 * result + updateAt.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + longitude.hashCode()
        result = 31 * result + latitude.hashCode()
        return result
    }
}
