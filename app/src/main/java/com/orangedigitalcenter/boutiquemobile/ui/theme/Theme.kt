package com.orangedigitalcenter.boutiquemobile.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = OrangeFonce,
    onPrimary = Color.White,
    primaryContainer = OrangeClair,
    onPrimaryContainer = Color(0xFF3A1500),
    secondary = MarronDoux,
    tertiary = VertBenefice
)

private val DarkColors = darkColorScheme(
    primary = OrangeSombre,
    onPrimary = Color(0xFF552100),
    primaryContainer = OrangeSombreContainer,
    onPrimaryContainer = OrangeClair,
    secondary = Color(0xFFE5BFA8),
    tertiary = Color(0xFF81C784)
)

@Composable
fun BoutiqueTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}
