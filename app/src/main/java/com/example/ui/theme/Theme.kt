package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = BrightOrange,
    onPrimary = Color.White,
    primaryContainer = CorporateNavy,
    onPrimaryContainer = Color.White,
    secondary = LightOrange,
    onSecondary = Color.Black,
    tertiary = AmberAccent,
    background = NavyDarkTheme,
    surface = SurfaceDarkTheme,
    surfaceVariant = CardDarkTheme,
    onBackground = TextLightDarkTheme,
    onSurface = TextLightDarkTheme
)

private val LightColorScheme = lightColorScheme(
    primary = CorporateNavy,
    onPrimary = Color.White,
    primaryContainer = DeepNavy,
    onPrimaryContainer = Color.White,
    secondary = BrightOrange,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFEDD5),
    onSecondaryContainer = DarkOrange,
    tertiary = AmberAccent,
    background = BackgroundLight,
    surface = SurfaceLight,
    surfaceVariant = SurfaceElevated,
    onBackground = SlateDark,
    onSurface = SlateDark,
    outline = SlateBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep corporate branding consistent
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
