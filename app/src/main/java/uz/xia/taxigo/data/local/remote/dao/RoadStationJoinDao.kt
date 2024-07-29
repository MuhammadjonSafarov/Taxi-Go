package uz.xia.taxigo.data.local.remote.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import uz.xia.taxigo.data.local.entity.RoadData
import uz.xia.taxigo.data.local.remote.entity.RoadStationJoin
import uz.xia.taxigo.data.local.entity.StationData
import uz.xia.taxigo.data.local.remote.model.StationWithRegions

@Dao
interface RoadStationJoinDao {
    @Insert
    suspend fun insert(vararg userRepoJoin: RoadStationJoin)

    @Query("SELECT * FROM  road_data r INNER JOIN road_station_join ON r.id=road_station_join.road_id WHERE road_station_join.station_id=:stationId")
    suspend fun getRoadsForStation(stationId: Long): List<RoadData>

    @Query("SELECT * FROM station_data s INNER JOIN road_station_join ON s.id = road_station_join.station_id WHERE road_station_join.road_id =:roadId")
    suspend fun getStationsForRoads(roadId: Long): List<StationData>

    @Query("SELECT * FROM station_data s INNER JOIN road_station_join r ON s.id = r.station_id WHERE r.road_id =:roadId GROUP BY s.id ORDER BY r.order_id")
    suspend fun getStationsWitRoad(roadId: Long): List<StationData>

    @Query("SELECT * FROM station_data s INNER JOIN road_station_join r ON s.id = r.station_id WHERE r.road_id =:roadId GROUP BY s.id ORDER BY r.order_id")
    suspend fun getStationsWithRegionForRoads(roadId: Long): List<StationWithRegions>

    @Query("SELECT s.name_uz_lt FROM station_data s INNER JOIN road_station_join  r ON s.id = r.station_id WHERE r.road_id =:roadId GROUP BY s.id ORDER BY r.order_id ASC")
    suspend fun getStationNamesForRoads(roadId: Long): Array<String>

    @Query("DELETE FROM road_station_join WHERE station_id=:stationId AND road_id=:roadId ")
    suspend fun deleteByStationId(roadId: Long, stationId: Long)

    @Query("SELECT * FROM road_station_join WHERE station_id=:stationId AND road_id=:roadId ")
    suspend fun getByStationId(roadId: Long, stationId: Long):RoadStationJoin?

    @Query("SELECT order_id FROM road_station_join ORDER BY order_id DESC LIMIT 1")
    suspend fun getLastOrderId(): Long?
}
