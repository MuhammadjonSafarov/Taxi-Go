package uz.xia.taxi.data

import android.content.SharedPreferences
import uz.xia.taxi.data.pref.BooleanPreference
import uz.xia.taxi.data.pref.LongPreference
import uz.xia.taxi.data.pref.StringPreference
import uz.xia.taxi.common.EMPTY_STRING
import uz.xia.taxi.data.pref.DoublePreference

private const val USER_AUTH_TOKEN = "USER_AUTH_TOKEN"
private const val USER_EXPIRES = "USER_EXPIRES"
private const val USER_LOGIN_TIME = "USER_LOGIN_TIME"

private const val USER_NAME = "USER_NAME"
private const val USER_LOGIN = "USER_LOGIN"
private const val USER_ROLE = "USER_ROLE"
private const val USER_PASSWORD = "USER_PASSWORD"
private const val USER_PICTURE = "USER_PICTURE"
private const val USER_CONNECTION_ID = "USER_CONNECTION_ID"
private const val USER_CONNECTION_AUTO_ID = "USER_CONNECTION_AUTO_ID"
private const val USER_FIRE_UID = "USER_FIRE_UID"

private const val LOCATION_TIME = "LOCATION_TIME"
private const val LONGITUDE = "LONGITUDE"
private const val LATITUDE = "LATITUDE"
private const val KEY_AUTO_ID = "KEY_AUTO_ID"
private const val KEY_LANGUAGE = "KEY_LANGUAGE"
private const val KEY_SQLITE_DATA = "KEY_SQLITE_DATA"

class PreferenceManagerImpl(sharedPreference: SharedPreferences) : IPreference {

    override var language: String by StringPreference(sharedPreference, KEY_LANGUAGE,"uz")

    override var authToken: String by StringPreference(sharedPreference, USER_AUTH_TOKEN, EMPTY_STRING)

    override var expires: Long by LongPreference(sharedPreference, USER_EXPIRES, 0L)

    override var loginTime: Long by LongPreference(sharedPreference, USER_LOGIN_TIME, 0L)

    override var userLogin: String by StringPreference(sharedPreference, USER_LOGIN)
    override var userName: String by StringPreference(sharedPreference, USER_NAME)
    override var userPassword: String by StringPreference(sharedPreference, USER_PASSWORD)
    override var userPicture: String by StringPreference(sharedPreference, USER_PICTURE)
    override var userFireUid: String by StringPreference(sharedPreference, USER_FIRE_UID)
    override var userRole: String by StringPreference(sharedPreference, USER_ROLE)
    override var locationTime: Long by LongPreference(sharedPreference, LOCATION_TIME)

    override var latitude: Double by DoublePreference(sharedPreference, LATITUDE,"0.0")
    override var longitude: Double by DoublePreference(sharedPreference, LONGITUDE,"0.0")
    override var isSqliteData: Boolean by BooleanPreference(sharedPreference, KEY_SQLITE_DATA,true)

    override var userConnectionId: String by StringPreference(sharedPreference, USER_CONNECTION_ID)
    override var userConnectionAutoId: String by StringPreference(sharedPreference,
        USER_CONNECTION_AUTO_ID
    )
}
