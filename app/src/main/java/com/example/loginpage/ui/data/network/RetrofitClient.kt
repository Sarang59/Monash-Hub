package com.example.loginpage.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// RetrofitClient is a singleton object responsible for configuring and providing an instance of Retrofit for accessing the OpenRouteService API.
object RetrofitClient {

    // Base URL of the OpenRouteService API. All endpoint paths in the DirectionsApiService interface will be appended to this base.
    private const val BASE_URL = "https://api.openrouteservice.org/"

    // Create and configure the Retrofit instance:
    // - Uses GsonConverterFactory to automatically parse JSON responses into data models.
    // - Points to the base URL defined above.
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Exposes the API interface (DirectionsApiService) so it can be used throughout the application to make network calls.
    val api: DirectionsApiService = retrofit.create(DirectionsApiService::class.java)
}
