package uz.xia.taxigo.data

interface IPreference {
    var language:String
    var accessToken: String
    var refreshToken: String
    var fcmToken: String
    var expires: Long
    var loginTime: Long

    var userId:Long
    var userName: String
    var userPassword: String
    var userLogin: String
    var userPicture: String
    var userFireUid:String
    var userRole:String
    /* current location  */
    var locationTime: Long

    var longitude: Double
    var latitude: Double
    var mapZoomLevel:Double

    var isSqliteData: Boolean

    var userConnectionId:String
    var userConnectionAutoId:String

    var isYandexMapRoute:Boolean

    var isNotMoreChoosingMap:Boolean
}
