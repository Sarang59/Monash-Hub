package com.example.loginpage.ui.data.network

import com.example.loginpage.ui.data.model.AdzunaResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response
import retrofit2.http.Path

/**
 * An interface which is used to connect to the external API to fetch the data
 */
interface JobListingService {
    @GET("v1/api/jobs/{country}/search/1")
    suspend fun getJobs(
        @Path("country") country: String,
        @Query("app_id") appId: String,
        @Query("app_key") apiKey: String,
        @Query("results_per_page") resultsPerPage: Int = 50
    ): Response<AdzunaResponse>
}

/**
 * This is an helper object which is used to initialise the retrofit
 */
object JobListingHelper {
    private const val BASE_URL = "https://api.adzuna.com/"

    val apiService: JobListingService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(JobListingService::class.java)
    }
}
