package com.example.loginpage.ui.commonFunction

import android.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.hsv
import android.util.Log

// Generates a list of color shades based on a given base color for the pie chart.
fun generateShades(baseColor: Int, count: Int, isDarkTheme: Boolean): List<Int> {
    // Extract red, green, and blue components from the base color
    val baseR = Color.red(baseColor)
    val baseG = Color.green(baseColor)
    val baseB = Color.blue(baseColor)

    // Generate 'count' number of shades by gradually adjusting brightness
    return List(count) { i ->
        val factor = (i + 1).toFloat() / (count + 1) // i + 1 skips the base

        if (isDarkTheme) {
            // For dark themes: generate darker shades by reducing RGB values
            val r = (baseR * (1 - factor)).toInt().coerceIn(0, 255)
            val g = (baseG * (1 - factor)).toInt().coerceIn(0, 255)
            val b = (baseB * (1 - factor)).toInt().coerceIn(0, 255)
            Color.rgb(r, g, b)
        } else {
            // For light themes: generate lighter shades by blending with white
            val factor = (i + 1).toFloat() / (count + 1)  // skip base color
            val r = (baseR + ((255 - baseR) * factor)).toInt().coerceIn(0, 255)
            val g = (baseG + ((255 - baseG) * factor)).toInt().coerceIn(0, 255)
            val b = (baseB + ((255 - baseB) * factor)).toInt().coerceIn(0, 255)
            Color.rgb(r, g, b)
        }
    }
}

