package uz.xia.taxigo.data.remote.converter

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import uz.xia.taxigo.data.remote.model.LatLng
import java.lang.reflect.Type

class PolygonConverter {
    private val moshi: Moshi = Moshi.Builder().build()
    private val type: Type = Types.newParameterizedType(List::class.java, LatLng::class.java)
    private val jsonAdapter = moshi.adapter<List<LatLng>>(type)

    @TypeConverter
    fun stringToObject(text: String): List<LatLng>? {
        return jsonAdapter.lenient().fromJson(text)
    }

    @TypeConverter
    fun objectToString(points: List<LatLng>): String {
        return  jsonAdapter.lenient().toJson(points)
    }
}