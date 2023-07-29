package uz.xia.taxi.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import uz.xia.taxi.data.local.dao.CategoryDao
import uz.xia.taxi.data.local.dao.LocationDao
import uz.xia.taxi.data.local.dao.RegionDao
import uz.xia.taxi.data.local.dao.UserAddressDao
import uz.xia.taxi.data.local.entity.CategoryData
import uz.xia.taxi.data.local.entity.LocationData
import uz.xia.taxi.data.local.entity.RegionData
import uz.xia.taxi.data.local.entity.UserAddress
import uz.xia.taxi.data.remote.converter.AddressStatusConverter
import uz.xia.taxi.data.remote.converter.PolygonConverter

@Database(
    entities = [
        LocationData::class,
        RegionData::class,
        CategoryData::class,
        UserAddress::class],
    version = 1, //old version 1
    exportSchema = false
)
@TypeConverters(
    value = [PolygonConverter::class, AddressStatusConverter::class]
)

abstract class AppDataBase : RoomDatabase() {

    abstract fun locationDao(): LocationDao
    abstract fun regionDao(): RegionDao
    abstract fun userAddressDao(): UserAddressDao
    abstract fun categoryDao(): CategoryDao
}
