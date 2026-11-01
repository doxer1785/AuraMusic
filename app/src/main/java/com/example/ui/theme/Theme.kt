package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val AuraDarkColorScheme = darkColorScheme(
    primary = AuraGreen,
    onPrimary = AuraBackground,
    primaryContainer = AuraSurfaceVariant,
    onPrimaryContainer = AuraGreenGlow,
    secondary = AuraViolet,
    onSecondary = AuraTextPrimary,
    secondaryContainer = AuraSurfaceVariant,
    onSecondaryContainer = AuraViolet,
    tertiary = AuraPink,
    onTertiary = AuraTextPrimary,
    background = AuraBackground,
    onBackground = AuraTextPrimary,
    surface = AuraSurface,
    onSurface = AuraTextPrimary,
    surfaceVariant = AuraSurfaceVariant,
    onSurfaceVariant = AuraTextSecondary
)

@Composable
fun AuraMusicTheme(
    darkTheme: Boolean = true, // Music player optimized for luxury dark theme
    content: @Composable () -> Unit
) {
    val colorScheme = AuraDarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = AuraBackground.toArgb()
            window.navigationBarColor = AuraBackground.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
