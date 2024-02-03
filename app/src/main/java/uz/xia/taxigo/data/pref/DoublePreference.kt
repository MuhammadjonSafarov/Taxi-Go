package uz.xia.taxigo.data.pref

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class DoublePreference(
    private val preference: SharedPreferences,
    private val key: String,
    private val defValue: String = "0.0"
) : ReadWriteProperty<Any, Double> {
    override fun getValue(thisRef: Any, property: KProperty<*>): Double {
        return (preference.getString(key, defValue) ?: defValue).toDouble()
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Double) {
        preference.edit { putString(key, value.toString()) }
    }
}
