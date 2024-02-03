package uz.xia.taxigo.data.remote.model.car

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AutoModel(
    val id: Long = 0L,
    val uuid: String = "",
    val fullName: String = "",
    val title: String = "",
    val text: String = "",
    val typeOfCar: String = "",
    val fullWeight: Double = 0.0,
    val seats: Int = 0,
):Parcelable