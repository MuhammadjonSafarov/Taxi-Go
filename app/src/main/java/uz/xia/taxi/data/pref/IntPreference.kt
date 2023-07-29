package uz.xia.taxi.data.pref

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class IntPreference(
    private val preferences: SharedPreferences,
    private val key: String,
    private val defValue: Int = 0
) : ReadWriteProperty<Any, Int> {
    override fun getValue(thisRef: Any, property: KProperty<*>) =
        preferences.getInt(key, defValue)

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Int) =
        preferences.edit { putInt(key, value) }
}