package uz.xia.taxigo.data.pref

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class BooleanPreference(
    private val preferences: SharedPreferences,
    private val key: String,
    private val defValue: Boolean = false
) : ReadWriteProperty<Any, Boolean> {
    override fun getValue(thisRef: Any, property: KProperty<*>) =
        preferences.getBoolean(key, defValue)

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) =
        preferences.edit { putBoolean(key, value) }
}