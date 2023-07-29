package uz.xia.taxi.data.pref

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class LongPreference(
    private val preferences: SharedPreferences,
    private val key: String,
    private val defValue: Long = 0
) : ReadWriteProperty<Any, Long> {
    override fun getValue(thisRef: Any, property: KProperty<*>) =
        preferences.getLong(key, defValue)

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Long) =
        preferences.edit { putLong(key, value) }
}