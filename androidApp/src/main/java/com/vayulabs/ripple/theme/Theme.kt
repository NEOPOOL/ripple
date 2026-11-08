package com.vayulabs.ripple.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color

// Premium IBM Carbon & iOS-Inspired Palette
// Dark Mode (OLED Midnight Obsidian)
val DarkBackground = Color(0xFF000000)
val DarkSurface = Color(0xFF11141A)
val DarkSurfaceVariant = Color(0xFF1A1F28)
val DarkCardBorder = Color(0x1AFFFFFF)
val DarkTextPrimary = Color(0xFFF2F4F8)
val DarkTextSecondary = Color(0xFF8E95A2)
val DarkTextTertiary = Color(0xFF565D6A)

// Light Mode (Apple Porcelain & Clean Crisp Surfaces)
val LightBackground = Color(0xFFF5F6F9)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFEDF0F5)
val LightCardBorder = Color(0x0F000000)
val LightTextPrimary = Color(0xFF13171F)
val LightTextSecondary = Color(0xFF6B7280)
val LightTextTertiary = Color(0xFF9CA3AF)

// Calming Accents (Harmonized for both modes)
val AccentAzure = Color(0xFF5A89A6)
val AccentAzureLight = Color(0xFF82ACCA)
val AccentSage = Color(0xFF598572)
val AccentSageLight = Color(0xFF86B39E)
val AccentDune = Color(0xFFA69A89)
val AccentLavender = Color(0xFF8D89A5)
val AccentCoral = Color(0xFFD66B60)

private val DarkColors = darkColorScheme(
    primary = AccentAzureLight,
    onPrimary = Color(0xFF090D13),
    primaryContainer = AccentAzure,
    onPrimaryContainer = Color.White,
    secondary = AccentSageLight,
    onSecondary = Color(0xFF090D13),
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkCardBorder
)

private val LightColors = lightColorScheme(
    primary = AccentAzure,
    onPrimary = Color.White,
    primaryContainer = AccentAzureLight,
    onPrimaryContainer = Color(0xFF090D13),
    secondary = AccentSage,
    onSecondary = Color.White,
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = LightSurfaceVariant,
    onBackground = LightTextPrimary,
    onSurface = LightTextPrimary,
    onSurfaceVariant = LightTextSecondary,
    outline = LightCardBorder
)

// Global Theme State Holder
object ThemeState {
    val isDarkMode = mutableStateOf(true)

    fun toggle() {
        isDarkMode.value = !isDarkMode.value
    }

    fun toggleDarkMode() {
        isDarkMode.value = !isDarkMode.value
    }
}

val LocalThemeIsDark = compositionLocalOf { true }

@Composable
fun RippleTheme(
    darkTheme: Boolean = ThemeState.isDarkMode.value,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
