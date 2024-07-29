package uz.xia.taxigo.data.remote.model.car

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class CarDataRequest(
    var id: Long = 0L,
    var color: String = "",
    var number: String = "",
    var carManufactureDate: Long = 0L,
    var stove: Boolean = true,
    var conditioner: Boolean = false,
    var baggage: Boolean = true,
    var switcherBaggage: Boolean = false,
    var autoData: AutoModel? = null,
    var createAt: Long = Date().time,
    var updateAt: Long = Date().time,
    var longitude: Double = 0.0,
    var latitude: Double = 0.0
    ) : Parcelable
