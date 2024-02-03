package uz.xia.taxigo.data.pref

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class StringPreference(
    private val preferences: SharedPreferences,
    private val key: String,
    private val defValue: String = ""
) : ReadWriteProperty<Any, String> {
    override fun getValue(thisRef: Any, property: KProperty<*>): String =
        preferences.getString(key, defValue) ?: defValue

    override fun setValue(thisRef: Any, property: KProperty<*>, value: String) =
        preferences.edit {
            putString(key, value)
            apply()
        }
}