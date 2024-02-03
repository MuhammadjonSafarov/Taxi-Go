package uz.xia.taxigo.data.remote.model.car

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.Instant

@Parcelize
data class CarData(
    var id: Long = 0L,
    var color: String = "",
    var number: String = "",
    var switcherBaggage: Boolean = false,
    var baggage: Boolean = true,
    var conditioner: Boolean = false,
    var stove: Boolean = true,
    var carManufactureDate: String = "",
    var autoData: AutoModel? = null
) : Parcelable
