package uz.xia.taxigo.data

import android.content.SharedPreferences
import uz.xia.taxigo.data.pref.BooleanPreference
import uz.xia.taxigo.data.pref.LongPreference
import uz.xia.taxigo.data.pref.StringPreference
import uz.xia.taxigo.common.EMPTY_STRING
import uz.xia.taxigo.data.pref.DoublePreference
import uz.xia.taxigo.data.remote.enumrition.UserRoleType

/* user authorization */
private const val USER_ACCESS_TOKEN = "USER_ACCESS_TOKEN"
private const val USER_REFRESH_TOKEN = "USER_REFRESH_TOKEN"
private const val USER_FCM_TOKEN = "USER_REFRESH_TOKEN"
private const val USER_EXPIRES = "USER_EXPIRES"
private const val USER_LOGIN_TIME = "USER_LOGIN_TIME"
private const val USER_NAME = "USER_NAME"
private const val USER_LOGIN = "USER_LOGIN"
private const val USER_ROLE = "USER_ROLE"
private const val USER_PASSWORD = "USER_PASSWORD"
private const val USER_PICTURE = "USER_PICTURE"
private const val USER_ID = "USER_ID"


private const val USER_CONNECTION_ID = "USER_CONNECTION_ID"
private const val USER_CONNECTION_AUTO_ID = "USER_CONNECTION_AUTO_ID"
private const val USER_FIRE_UID = "USER_FIRE_UID"

private const val LOCATION_TIME = "LOCATION_TIME"
private const val LONGITUDE = "LONGITUDE"
private const val LATITUDE = "LATITUDE"
private const val MAP_ZOOM_LEVEL = "MAP_ZOOM_LEVEL"
private const val KEY_LANGUAGE = "KEY_LANGUAGE"
private const val KEY_SQLITE_DATA = "KEY_SQLITE_DATA"
private const val KEY_NOT_MORE_CHOOSING_MAP = "KEY_NOT_MORE_CHOOSING_MAP"
private const val KEY_YANDEX_MAP_ROUTING = "KEY_YANDEX_MAP_ROUTING"



class PreferenceManagerImpl(sharedPreference: SharedPreferences) : IPreference {

    override var language: String by StringPreference(sharedPreference, KEY_LANGUAGE,"en")

    override var accessToken: String by StringPreference(sharedPreference, USER_ACCESS_TOKEN, EMPTY_STRING)
    override var refreshToken: String by StringPreference(sharedPreference, USER_REFRESH_TOKEN, EMPTY_STRING)
    override var fcmToken: String by StringPreference(sharedPreference, USER_FCM_TOKEN, EMPTY_STRING)

    override var expires: Long by LongPreference(sharedPreference, USER_EXPIRES, 0L)

    override var loginTime: Long by LongPreference(sharedPreference, USER_LOGIN_TIME, 0L)

    override var userLogin: String by StringPreference(sharedPreference, USER_LOGIN)
    override var userName: String by StringPreference(sharedPreference, USER_NAME)
    override var userPassword: String by StringPreference(sharedPreference, USER_PASSWORD)
    override var userPicture: String by StringPreference(sharedPreference, USER_PICTURE)
    override var userFireUid: String by StringPreference(sharedPreference, USER_FIRE_UID)
    override var userRole: String by StringPreference(sharedPreference, USER_ROLE,UserRoleType.GUEST.name)
    override var userId: Long by LongPreference(sharedPreference,USER_ID,1L)
    override var mainCarId: Long by LongPreference(sharedPreference,"user_car_id",0L)
    override var locationTime: Long by LongPreference(sharedPreference, LOCATION_TIME)
    override var latitude: Double by DoublePreference(sharedPreference, LATITUDE,"41.308772")
    override var longitude: Double by DoublePreference(sharedPreference, LONGITUDE,"69.247162")
    override var mapZoomLevel: Double by DoublePreference(sharedPreference,MAP_ZOOM_LEVEL,"7.0")
    override var isSqliteData: Boolean by BooleanPreference(sharedPreference, KEY_SQLITE_DATA,true)

    override var userConnectionId: String by StringPreference(sharedPreference, USER_CONNECTION_ID)
    override var userConnectionAutoId: String by StringPreference(sharedPreference,USER_CONNECTION_AUTO_ID)

    override var isYandexMapRoute: Boolean by BooleanPreference(sharedPreference, KEY_YANDEX_MAP_ROUTING)
    override var isNotMoreChoosingMap: Boolean by BooleanPreference(sharedPreference,KEY_NOT_MORE_CHOOSING_MAP)

    override var userCarData: String by StringPreference(sharedPreference,"key_car_data")
}
