package com.example.loginpage.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Extension properties on ColorScheme to provide custom color values
 * used throughout the app for consistent theming.
 *
 * These properties fetch colors from a custom color provider LocalCustomColors,
 * allowing dynamic theming and easier dark and light theme management.
 */
val ColorScheme.primaryText: Color
    @Composable
    get() = LocalCustomColors.current.primaryText

val ColorScheme.secondaryText: Color
    @Composable
    get() = onSurface.copy(alpha = 0.7f)

val ColorScheme.primaryBg: Color
    @Composable
    get() = LocalCustomColors.current.primaryBg

val ColorScheme.cardBg: Color
    @Composable
    get() = LocalCustomColors.current.cardBg

val ColorScheme.Transparent: Color
    @Composable
    get() = LocalCustomColors.current.Transparent

val ColorScheme.inactiveGrey: Color
    @Composable
    get() = LocalCustomColors.current.inactiveGrey

val ColorScheme.success: Color
    @Composable
    get() = LocalCustomColors.current.success

val ColorScheme.errorText: Color
    @Composable
    get() = LocalCustomColors.current.errorText

val ColorScheme.saveAxis: Color
    @Composable
    get() = LocalCustomColors.current.saveAxis