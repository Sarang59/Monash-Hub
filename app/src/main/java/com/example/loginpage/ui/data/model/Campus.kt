package com.example.loginpage.data.model

//Data class for list of campuses for maps
data class Campus(val name: String, val lat: Double, val lng: Double)

val campuses = listOf(
    Campus("Clayton", -37.910904784129194, 145.13665166743525),
    Campus("Caulfield", -37.8770, 145.0440),
    Campus("Peninsula", -38.1524053315245, 145.13598060217723),
    Campus("Parkville", -37.78386960339787, 144.95871360975738)
)
