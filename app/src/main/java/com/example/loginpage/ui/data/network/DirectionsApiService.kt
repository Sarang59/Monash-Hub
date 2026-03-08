package com.example.loginpage.data.network

import com.example.loginpage.data.model.DirectionsRequest
import com.example.loginpage.data.model.GeoJsonResponse
import com.example.loginpage.data.model.GeocodeResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface DirectionsApiService {

    // Performs a forward geocoding search to convert an address (text) into geographic coordinates (latitude and longitude).
    @GET("geocode/search")
    suspend fun geocode(
        @Query("text") address: String,
        @Query("api_key") apiKey: String
    ): GeocodeResponse

    //Fetches walking directions between two or more points using POST method. This follows the OpenRouteService routing API structure with GeoJSON response.
    // Use POST for routing as per OpenRouteService API
    @POST("v2/directions/foot-walking/geojson")
    suspend fun getDirections(
        @Query("api_key") apiKey: String,
        @Body body: DirectionsRequest
    ): Response<GeoJsonResponse>

    // Provides location suggestions (autocomplete) based on partial user input. This is useful for building a location search bar or address prediction.
    @GET("geocode/autocomplete")
    suspend fun autocomplete(
        @Query("text") input: String,
        @Query("focus.point.lat") lat: Double,
        @Query("focus.point.lon") lon: Double,
        @Query("api_key") apiKey: String
    ): GeocodeResponse
}
