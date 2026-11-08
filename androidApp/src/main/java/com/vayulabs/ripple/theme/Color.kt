package com.vayulabs.ripple.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Context-aware dynamic color getters for clean IBM Carbon & Apple Porcelain design
object RippleColors {
    val isDark: Boolean
        @Composable get() = ThemeState.isDarkMode.value

    val background: Color
        @Composable get() = if (isDark) DarkBackground else LightBackground

    val surface: Color
        @Composable get() = if (isDark) DarkSurface else LightSurface

    val surfaceVariant: Color
        @Composable get() = if (isDark) DarkSurfaceVariant else LightSurfaceVariant

    val cardBorder: Color
        @Composable get() = if (isDark) DarkCardBorder else LightCardBorder

    val textPrimary: Color
        @Composable get() = if (isDark) DarkTextPrimary else LightTextPrimary

    val textSecondary: Color
        @Composable get() = if (isDark) DarkTextSecondary else LightTextSecondary

    val textTertiary: Color
        @Composable get() = if (isDark) DarkTextTertiary else LightTextTertiary

    val orbCore: Color
        @Composable get() = if (isDark) AccentAzureLight else AccentAzure

    val orbAura: Color
        @Composable get() = if (isDark) AccentAzure.copy(alpha = 0.3f) else AccentAzureLight.copy(alpha = 0.25f)
}
