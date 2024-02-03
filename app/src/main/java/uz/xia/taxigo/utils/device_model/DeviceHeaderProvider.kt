package uz.xia.taxigo.utils.device_model

interface DeviceHeaderProvider {
    fun getDeviceUniqueId(): String
    fun getDeviceName(): String
    fun getDeviceModel(): String
    fun getDeviceType(): String
}
