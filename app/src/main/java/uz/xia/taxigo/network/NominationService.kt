package uz.xia.taxigo.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import uz.xia.taxigo.data.remote.model.nomination.NominationResponse
import uz.xia.taxigo.data.remote.model.nomination.Place
import uz.xia.taxigo.data.remote.model.nomination.polygon.PolygonData

interface NominationService {
    @GET("reverse.php")
    suspend fun loadAddress(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("accept-language") lang: String = "uz",
        @Query("format") format: String = "json"
    ): NominationResponse?

    @Headers("Accept-Language: uz-UZ, uz;q=0.9, uzc;q=0.8, en;q=0.7, *;q=0.5")
    @GET("search?format=json")
    suspend fun searchPlaces(
        @Query("q") query: String,
        @Query("accept-language") lang: String = "uz"
    ): List<Place>

    @GET("search?format=json")
    suspend fun getPolygons(
        @Query("q") query:String,
        @Query("polygon_geojson") polygonType:Int=1
    ):Response<List<PolygonData>>
}
