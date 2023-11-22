package uz.xia.taxi.data.local.remote.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import uz.xia.taxi.data.local.entity.RoadData
import uz.xia.taxi.data.local.remote.entity.RoadStationJoin
import uz.xia.taxi.data.local.entity.StationData

@Dao
interface RoadStationJoinDao {
    @Insert
    suspend fun insert(vararg userRepoJoin: RoadStationJoin)

    @Query("SELECT * FROM  road_data r INNER JOIN road_station_join ON r.id=road_station_join.road_id WHERE road_station_join.station_id=:stationId")
    suspend fun getRoadsForStation(stationId: Long): List<RoadData>

    @Query("SELECT * FROM station_data s INNER JOIN road_station_join ON s.id = road_station_join.station_id WHERE road_station_join.road_id =:roadId")
    suspend fun getStationsForRoads(roadId: Long): List<StationData>

    @Query("SELECT s.name_uz_lt FROM station_data s INNER JOIN road_station_join ON s.id = road_station_join.station_id WHERE road_station_join.road_id =:roadId")
    suspend fun getStationNamesForRoads(roadId: Long): Array<String>
}
