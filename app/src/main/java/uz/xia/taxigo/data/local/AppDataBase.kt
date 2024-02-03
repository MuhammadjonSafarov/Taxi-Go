package uz.xia.taxigo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import uz.xia.taxigo.data.local.dao.CarDao
import uz.xia.taxigo.data.local.dao.CategoryDao
import uz.xia.taxigo.data.local.dao.DistrictDao
import uz.xia.taxigo.data.local.dao.LocationDao
import uz.xia.taxigo.data.local.dao.ParkingDao
import uz.xia.taxigo.data.local.dao.RegionDao
import uz.xia.taxigo.data.local.dao.RoadDao
import uz.xia.taxigo.data.local.dao.StationDao
import uz.xia.taxigo.data.local.dao.UserAddressDao
import uz.xia.taxigo.data.local.entity.CarData
import uz.xia.taxigo.data.local.entity.CategoryData
import uz.xia.taxigo.data.local.entity.DistrictData
import uz.xia.taxigo.data.local.entity.LocationData
import uz.xia.taxigo.data.local.entity.ParkingData
import uz.xia.taxigo.data.local.entity.RegionData
import uz.xia.taxigo.data.local.entity.RoadData
import uz.xia.taxigo.data.local.entity.StationData
import uz.xia.taxigo.data.local.entity.UserAddress
import uz.xia.taxigo.data.local.remote.dao.ParkingRoadJoinDao
import uz.xia.taxigo.data.local.remote.dao.RoadStationJoinDao
import uz.xia.taxigo.data.local.remote.entity.ParkingRoadJoin
import uz.xia.taxigo.data.local.remote.entity.RoadStationJoin
import uz.xia.taxigo.data.remote.converter.AddressStatusConverter
import uz.xia.taxigo.data.remote.converter.DescriptionConverter
import uz.xia.taxigo.data.remote.converter.PolygonConverter

@Database(
    entities = [
        CarData::class,
        LocationData::class,
        RegionData::class,
        CategoryData::class,
        UserAddress::class,
        RoadData::class,
        StationData::class,
        ParkingData::class,
        RoadStationJoin::class,
        ParkingRoadJoin::class,
        DistrictData::class],
    version = 4, //old version 1
    exportSchema = false
)
@TypeConverters(
    value = [
        PolygonConverter::class,
        AddressStatusConverter::class,
        DescriptionConverter::class]
)

abstract class AppDataBase : RoomDatabase() {

    abstract fun carDao():CarDao
    abstract fun locationDao(): LocationDao
    abstract fun regionDao(): RegionDao
    abstract fun districtDao(): DistrictDao
    abstract fun userAddressDao(): UserAddressDao
    abstract fun categoryDao(): CategoryDao

    abstract fun roadDao(): RoadDao
    abstract fun stationDao(): StationDao
    abstract fun parkingDao(): ParkingDao
    abstract fun roadStationJoin(): RoadStationJoinDao
    abstract fun parkingRoadJoin(): ParkingRoadJoinDao
}
