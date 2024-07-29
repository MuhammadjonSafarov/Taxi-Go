package uz.xia.taxigo.data.remote.model.car

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CarDataResponse(
    var id: Long = 0L,
    var color: String = "",
    var number: String = "",
    var switcherBaggage: Boolean = false,
    var baggage: Boolean = true,
    var conditioner: Boolean = false,
    var stove: Boolean = true,
    var carManufactureDate: String = "",
    var mainCar: Boolean = false,
    var autoData: AutoModel? = null,
    val longitude: Double = 0.0,
    val latitude: Double = 0.0
) : Parcelable {

    override fun toString(): String {
        return "{\"id\":\"$id\",\"color\":\"$color\",\"number\":\"$number\",\"switcherBaggage\":\"$switcherBaggage\",\"baggage\":\"$baggage\",\"conditioner\":\"$conditioner\",\"stove\":\"$stove\",\"carManufactureDate\":\"$carManufactureDate\"}"
    }
}
