package com.greenroute.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = TextOnGreen,
    primaryContainer = GreenLight,
    onPrimaryContainer = GreenDark,
    secondary = GreenAccent,
    onSecondary = TextOnGreen,
    secondaryContainer = GreenSurface,
    onSecondaryContainer = GreenDark,
    tertiary = GreenLight,
    onTertiary = TextOnGreen,
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = BackgroundWhite,
    onSurface = TextPrimary,
    surfaceVariant = GreenSurface,
    onSurfaceVariant = TextSecondary,
    // Transparent tint prevents Material3 from applying a green elevation overlay
    // to surfaces like NavigationBar, ensuring the same white on all devices
    surfaceTint = Color.Transparent,
    error = StatusError,
    onError = TextOnGreen
)

private val DarkColorScheme = darkColorScheme(
    primary = GreenLight,
    onPrimary = GreenDark,
    primaryContainer = GreenDark,
    onPrimaryContainer = GreenLight,
    secondary = GreenAccent,
    onSecondary = GreenDark,
    secondaryContainer = GreenDark,
    onSecondaryContainer = GreenLight,
    tertiary = GreenLight,
    onTertiary = GreenDark,
    background = BackgroundDark,
    onBackground = TextOnGreen,
    surface = BackgroundDark,
    onSurface = TextOnGreen,
    surfaceVariant = GreenDark,
    onSurfaceVariant = GreenLight,
    surfaceTint = Color.Transparent,
    error = StatusError,
    onError = GreenDark
)

@Composable
fun GreenRouteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // With enableEdgeToEdge() the system bars are transparent —
            // do NOT set window.statusBarColor (deprecated in API 35, causes
            // inconsistency across devices). Just control icon appearance.
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme      // dark icons on green bg
                isAppearanceLightNavigationBars = !darkTheme  // dark icons on white nav bar
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
