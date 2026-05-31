package com.example.vinylstore.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Burgundy80,
    onPrimary = WarmWhite,
    primaryContainer = Burgundy40,
    onPrimaryContainer = WarmWhite,
    secondary = MediumBrown,
    onSecondary = WarmWhite,
    secondaryContainer = LightBrown,
    onSecondaryContainer = DarkBrown,
    tertiary = Copper,
    background = Beige,
    onBackground = DarkBrown,
    surface = Cream,
    onSurface = DarkBrown,
    surfaceVariant = RoseRed,
    onSurfaceVariant = DarkBrown,
    error = Color(0xFFB00020),
    onError = WarmWhite
)

private val DarkColorScheme = darkColorScheme(
    primary = Burgundy40,
    onPrimary = WarmWhite,
    primaryContainer = DeepRed,
    onPrimaryContainer = WarmWhite,
    secondary = LightBrown,
    onSecondary = DarkBrown,
    secondaryContainer = MediumBrown,
    onSecondaryContainer = WarmWhite,
    tertiary = GoldAccent,
    background = DarkBrown,
    onBackground = WarmWhite,
    surface = MediumBrown,
    onSurface = WarmWhite,
    surfaceVariant = WineRed,
    onSurfaceVariant = WarmWhite,
    error = Color(0xFFCF6679),
    onError = DarkBrown
)

@Composable
fun VinylStoreTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}