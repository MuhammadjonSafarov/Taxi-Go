package uz.xia.taxi.common


const val BASE_URL = "http://10.0.2.2:8080/api/"
const val NOMINATION_URL = "https://nominatim.openstreetmap.org/"
const val BASE_AVTOTICKET_URL="https://wapi.avtoticket.uz/api/"

const val ROLE_GENERAL_DIRECTOR = "ROLE_GENERAL_DIRECTOR"
const val DATA_BASE_NAME = "taxi_uz.db"
/* constants params */
const val EMPTY_STRING = ""
const val SPINNER_DEFAULT_VALUE = -1
const val EMPTY_NUMBER = 0L
const val DATE_VALIDATION_PATTERN =
    "^(0?[1-9]|[12][0-9]|3[01])[\\/\\-](0?[1-9]|1[012])[\\/\\-]\\d{4}\$"
const val MASK_PHONE_NUMBER = "+998 ([00]) [000] [00] [00]"
const val MASK_SMS_NUMBER = "[000] [000]"

//const val YANDEX_MAPKIT_API_KEY = "56300cb3-c4fb-41ee-80ed-87cc6d4d9827"
const val YANDEX_MAPKIT_API_KEY = "ccb2d679-b901-4567-8660-7960db6465b1"

const val EN = "en-rUS"
const val RU = "ru-rRU"
const val UZ_LT = "uz-rUZ"
const val UZ_KR = "en"
enum class Language(val v:String){
    EN ( "zh"),
    RU ( "ru"),
    UZ_LT ( "uz"),
    UZ_KR ( "en")
}
/*const val UZ_KR="en"
const val UZ="uz"*/

//public static final String webSocketAddress = "ws://192.168.1.2:8080/my-ws/websocket";

//public static final String webSocketAddress = "ws://192.168.1.2:8080/my-ws/websocket";
//const val volleyUserPostAddress = "http://ec2-13-59-82-93.us-east-2.compute.amazonaws.com:8080/api/users/post"
//const val webSocketAddress: String = "ws://ec2-13-59-82-93.us-east-2.compute.amazonaws.com:8080/my-ws/websocket"
//public static final String volleyUserPostAddress = "http://192.168.1.2:8080/api/users/post";
const val volleyUserPostAddress = "http://10.0.2.2:8080/api/users/post"
const val webSocketAddress: String = "ws://10.0.2.2:8080/my-ws/websocket"


