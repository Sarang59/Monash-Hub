package com.example.loginpage.data.model

data class GeoJsonResponse(
    val type: String,
    val features: List<RouteFeature>
)

data class RouteFeature(
    val type: String,
    val geometry: RouteGeometry,
    val properties: Map<String, Any>?
)

data class RouteGeometry(
    val coordinates: List<List<Double>>,  // List of [lng, lat]
    val type: String
)
