package com.example.loginpage.data.model

data class GeocodeResponse(
    val type: String? = null,
    val features: List<GeocodeFeature>
)

data class GeocodeFeature(
    val type: String,
    val geometry: GeocodeGeometry,
    val properties: Map<String, Any>?
)

data class GeocodeGeometry(
    val coordinates: List<Double>,  // [lng, lat]
    val type: String
)
