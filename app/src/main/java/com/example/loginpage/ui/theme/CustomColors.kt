package com.example.loginpage.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import coil.compose.AsyncImagePainter

/**
 * Data class representing a collection of custom colors used in the app's theme.
 * */
data class CustomColors(
    val cardBg: Color,
    val buttonBg: Color,
    val titleText: Color,
    val subtitleText: Color,
    val primaryText : Color,
    val primaryBg : Color,
    val inactiveGrey : Color,
    val Transparent : Color,
    val errorText : Color,
    val success: Color,
    val saveAxis: Color

)

// Throws an error if no [CustomColors] are provided in the current Composition.
val LocalCustomColors = staticCompositionLocalOf<CustomColors> {
    error("No CustomColors provided")
}


// Light theme custom colors used in the app.
val LightCustomColors = CustomColors(
    cardBg = Color.White,
    buttonBg = Color(0xFFE0E0E0),
    titleText = Color(0xFF000000),
    subtitleText = Color(0xFF666666),
    primaryText = Color(0xFF000044),
    primaryBg = Color(0xFFF3F4F9),
    inactiveGrey = Color(0xFF808080),
    Transparent =  Color.Transparent,
    errorText = Color.Red,
    success = Color(0xFF4CAF50),
    saveAxis = Color(0xFF7f7faa)
)

// Dark theme custom colors used in the app.
val DarkCustomColors = CustomColors(
    cardBg = Color(0xFF2c2c2c),
    buttonBg = Color(0xFFE0E0E0),
    titleText = Color(0xFF000000),
    subtitleText = Color(0xFF666666),
    primaryText = Color(0xFFF3F4F9),
    primaryBg = Color(0xFF00001d),
    inactiveGrey = Color(0xFF808080),
    Transparent =  Color.Transparent,
    errorText = Color.Red,
    success = Color(0xFF4CAF50),
    saveAxis = Color(0xFF929295)
)
