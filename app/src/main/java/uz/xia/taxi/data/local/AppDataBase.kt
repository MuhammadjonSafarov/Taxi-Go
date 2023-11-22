package uz.xia.taxi.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import uz.xia.taxi.data.local.dao.CarDao
import uz.xia.taxi.data.local.dao.CategoryDao
import uz.xia.taxi.data.local.dao.DistrictDao
import uz.xia.taxi.data.local.dao.LocationDao
import uz.xia.taxi.data.local.dao.ParkingDao
import uz.xia.taxi.data.local.dao.RegionDao
import uz.xia.taxi.data.local.dao.RoadDao
import uz.xia.taxi.data.local.dao.StationDao
import uz.xia.taxi.data.local.dao.UserAddressDao
import uz.xia.taxi.data.local.entity.CarData
import uz.xia.taxi.data.local.entity.CategoryData
import uz.xia.taxi.data.local.entity.DistrictData
import uz.xia.taxi.data.local.entity.LocationData
import uz.xia.taxi.data.local.entity.ParkingData
import uz.xia.taxi.data.local.entity.RegionData
import uz.xia.taxi.data.local.entity.RoadData
import uz.xia.taxi.data.local.entity.StationData
import uz.xia.taxi.data.local.entity.UserAddress
import uz.xia.taxi.data.local.remote.dao.ParkingRoadJoinDao
import uz.xia.taxi.data.local.remote.dao.RoadStationJoinDao
import uz.xia.taxi.data.local.remote.entity.ParkingRoadJoin
import uz.xia.taxi.data.local.remote.entity.RoadStationJoin
import uz.xia.taxi.data.remote.converter.AddressStatusConverter
import uz.xia.taxi.data.remote.converter.DescriptionConverter
import uz.xia.taxi.data.remote.converter.PolygonConverter

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
    version = 3, //old version 1
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
