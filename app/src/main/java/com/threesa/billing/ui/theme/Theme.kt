package com.threesa.billing.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = PrimaryOrange,
    onPrimary = SurfaceWhite,
    primaryContainer = SurfaceMuted,
    onPrimaryContainer = BrandRed,

    secondary = SuccessGreen,
    onSecondary = SurfaceWhite,

    error = ErrorRed,
    onError = SurfaceWhite,
    errorContainer = ErrorRedBg,
    onErrorContainer = ErrorRed,

    background = BackgroundCream,
    onBackground = TextPrimary,

    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceMuted,
    onSurfaceVariant = TextSecondary,

    outline = BorderLight
)

private val DarkColors = darkColorScheme(
    primary = PrimaryOrange,
    onPrimary = SurfaceWhite,
    background = BackgroundDark,
    onBackground = SurfaceWhite,
    surface = SurfaceDark,
    onSurface = SurfaceWhite,
    error = ErrorRed,
    onError = SurfaceWhite
)

@Composable
fun BillingSystemTheme(
    darkTheme: Boolean = false,
    // Dynamic color (Material You) is disabled by default so the app always
    // shows the RetailAdmin/BillingSystem brand palette instead of the user's
    // wallpaper-derived colors. Flip to true if you want Android 12+ theming.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = BillingSystemTypography,
        shapes = BillingSystemShapes,
        content = content
    )
}