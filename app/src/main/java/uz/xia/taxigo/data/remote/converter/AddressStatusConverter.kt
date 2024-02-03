package uz.xia.taxigo.data.remote.converter

import androidx.room.TypeConverter
import uz.xia.taxigo.data.remote.enumrition.AddressStatus

class AddressStatusConverter {
    @TypeConverter
    fun stringToObject(name: String): AddressStatus {
        val type = when (name) {
            "HOME" -> AddressStatus.HOME
            "WORK" -> AddressStatus.WORK
            else -> AddressStatus.OTHER
        }
        return type
    }

    @TypeConverter
    fun objectToString(type: AddressStatus): String {
        return type.name
    }
}