package uz.xia.taxi.data

interface IPreference {
    var language:String
    var authToken: String
    var expires: Long
    var loginTime: Long

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

    var isSqliteData: Boolean

    var userConnectionId:String
    var userConnectionAutoId:String
}
