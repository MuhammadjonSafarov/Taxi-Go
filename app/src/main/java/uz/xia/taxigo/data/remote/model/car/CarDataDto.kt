package uz.xia.taxigo.data.remote.model.car

import uz.xia.taxigo.data.remote.enumrition.DriverCarType
import java.util.Date

data class CarDataDto(
    var walkingTime: Long = Date().time,
    var parkingId: Long = 0,
    var roadId: Long = 0,
    var longitude: Double = 0.0,
    var latitude: Double = 0.0,
    var driverCarType:String = DriverCarType.AVAILABLE.name
)