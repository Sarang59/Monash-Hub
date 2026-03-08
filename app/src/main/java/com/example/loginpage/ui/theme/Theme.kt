package com.example.loginpage.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.ui.graphics.Color
import android.util.Log
import androidx.compose.runtime.CompositionLocalProvider

//Defines the dark color scheme for the app by leveraging custom dark theme colors.
private val DarkColorScheme = darkColorScheme(
    background = DarkCustomColors.primaryBg,
    surface = DarkCustomColors.primaryBg// Slightly lighter than default black

)

// Defines the light color scheme for the app using the custom light theme colors.
private val LightColorScheme = lightColorScheme(
    background = LightCustomColors.primaryBg,
    surface = LightCustomColors.primaryBg

)



@Composable
fun LoginPageTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Retrieve the current Context from the Composition
    val context = LocalContext.current

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        // Use predefined dark or light color schemes otherwise
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Select the corresponding custom colors object based on darkTheme
    val customColors = if (darkTheme) DarkCustomColors else LightCustomColors

// Provide the custom colors to the CompositionLocal for usage by custom components
    CompositionLocalProvider(LocalCustomColors provides customColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}



