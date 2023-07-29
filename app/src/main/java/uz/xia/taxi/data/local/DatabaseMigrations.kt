package uz.xia.taxi.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {

   val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `category_data` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `nameRu` TEXT NOT NULL,`nameEn` TEXT NOT NULL,`nameUzLat` TEXT NOT NULL,`nameUzKr` TEXT NOT NULL,`iconUrl` TEXT NOT NULL )")
        }
    }
 /*
   val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `location_temp` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `longitude` REAL NOT NULL, `latitude` REAL NOT NULL, `time` INTEGER NOT NULL)")
            database.execSQL("INSERT INTO `location_temp` (`id`,`time`,`longitude`,`latitude`) SELECT id,time,longitude,latitude FROM location")
            database.execSQL("DROP TABLE location")
            database.execSQL("ALTER TABLE location_temp RENAME TO location")
        }
    }

    val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `locale_image` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `filePath` TEXT NOT NULL, `time` INTEGER NOT NULL, `longitude` REAL NOT NULL, `latitude` REAL NOT NULL, `isSelected` INTEGER NOT NULL)")
            database.execSQL("CREATE TABLE IF NOT EXISTS `real_estate_data` (`id` INTEGER NOT NULL, `createdDate` TEXT NOT NULL, `name` TEXT NOT NULL, `owner` TEXT, `status` TEXT, `source` TEXT NOT NULL, PRIMARY KEY(`id`))")
            database.execSQL("CREATE TABLE IF NOT EXISTS `real_estate_act_image` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `real_estate_id` INTEGER NOT NULL, `filePath` TEXT NOT NULL)")
            database.execSQL("CREATE TABLE IF NOT EXISTS `real_estate_more_data` (`id` INTEGER NOT NULL, `name` TEXT NOT NULL, `status` TEXT NOT NULL, `photos` TEXT, `isActCreated` INTEGER NOT NULL, `storageCount` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            database.execSQL("CREATE TABLE IF NOT EXISTS `real_estate_building_image` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `real_estate_id` INTEGER NOT NULL, `building_id` INTEGER NOT NULL, `filePath` TEXT NOT NULL, `geocoder` TEXT NOT NULL)")
            database.execSQL("CREATE TABLE IF NOT EXISTS `building` (`id` INTEGER NOT NULL, `real_estate_id` INTEGER NOT NULL, `name` TEXT NOT NULL, `photos` TEXT NOT NULL, `storageCount` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        }
    }*/
}
