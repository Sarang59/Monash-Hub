package com.example.loginpage.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginpage.data.model.DirectionsRequest
import com.example.loginpage.data.network.RetrofitClient
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ViewModel class that handles logic for the Campus Map screen.
class CampusMapViewModel : ViewModel() {
    // Holds the current list of LatLng points for the route to be displayed on the map
    private val _routePoints = MutableStateFlow<List<LatLng>>(emptyList())
    val routePoints: StateFlow<List<LatLng>> = _routePoints

    // Holds the current list of autocomplete suggestions for user address input
    private val _suggestions = MutableStateFlow<List<String>>(emptyList())
    val suggestions: StateFlow<List<String>> = _suggestions

    //Fetches autocomplete suggestions for the given query and user location using the API.
    fun fetchSuggestions(query: String, location: LatLng, apiKey: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.autocomplete(
                    input = query,
                    lat = location.latitude,
                    lon = location.longitude,
                    apiKey = apiKey
                )

                // Extract the name property from the suggestions
                val suggestionsList = response.features.mapNotNull { feature ->
                    // safely get "name" key from the map as a String
                    feature.properties?.get("name") as? String
                }

                // Update suggestions state
                _suggestions.value = suggestionsList
            } catch (e: Exception) {
                Log.e("CampusMapViewModel", "Failed to fetch suggestions", e)
                _suggestions.value = emptyList()
            }
        }
    }

    //Clears the current list of suggestions.
    fun clearSuggestions() {
        _suggestions.value = emptyList()
    }


    // Fetches a walking route from the origin location to the destination address. It first geocodes the address to coordinates, then fetches directions via API.
    fun getRouteToAddress(destinationAddress: String, origin: LatLng, apiKey: String) {
        viewModelScope.launch {
            try {
                // 1. Geocode destination address
                val geoResponse = RetrofitClient.api.geocode(destinationAddress, apiKey)
                val coordinates = geoResponse.features.firstOrNull()?.geometry?.coordinates
                if (coordinates == null) {
                    Log.e("CampusMapViewModel", "Geocoding failed for address: $destinationAddress")
                    _routePoints.value = emptyList()
                    return@launch
                }




                val destinationLng = coordinates[0]
                val destinationLat = coordinates[1]

                // Step 2: Prepare request body with origin and destination coordinates
                val requestBody = DirectionsRequest(
                    coordinates = listOf(
                        listOf(origin.longitude, origin.latitude),
                        listOf(destinationLng, destinationLat)
                    )
                )

                // Step 3: Call routing API to get directions
                val routeResponse = RetrofitClient.api.getDirections(apiKey, requestBody)

                // Map raw coordinate pairs to LatLng list for display
                if (routeResponse.isSuccessful) {
                    val body = routeResponse.body()
                    val coords = body?.features?.firstOrNull()?.geometry?.coordinates

                    if (!coords.isNullOrEmpty()) {
                        val points = coords.map { LatLng(it[1], it[0]) }
                        _routePoints.value = points

                    } else {
                        Log.e("CampusMapViewModel", "Empty route geometry")
                        _routePoints.value = emptyList()
                    }
                } else {
                    Log.e("CampusMapViewModel", "Routing API error: ${routeResponse.errorBody()?.string()}")
                    _routePoints.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e("CampusMapViewModel", "Failed to get route", e)
                _routePoints.value = emptyList()
            }
        }
    }



}
