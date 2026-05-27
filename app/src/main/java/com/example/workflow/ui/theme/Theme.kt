package com.example.workflow.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Indigo60,
    onPrimary = Color.White,
    primaryContainer = Indigo90,
    onPrimaryContainer = Indigo20,

    secondary = Gray60,
    onSecondary = Color.White,
    secondaryContainer = Gray10,
    onSecondaryContainer = Gray90,

    tertiary = Green40,
    onTertiary = Color.White,
    tertiaryContainer = Green90,
    onTertiaryContainer = Color(0xFF003D31),

    error = Coral40,
    onError = Color.White,
    errorContainer = Coral90,
    onErrorContainer = Color(0xFF5A0010),

    background = IndigoBg,
    onBackground = Gray90,

    surface = Color.White,
    onSurface = Gray90,
    surfaceVariant = Gray10,
    onSurfaceVariant = Gray60,

    outline = Gray20,
    outlineVariant = Gray10,
)

private val DarkColorScheme = darkColorScheme(
    primary = Indigo80,
    onPrimary = Indigo20,
    primaryContainer = Indigo40,
    onPrimaryContainer = Indigo90,

    secondary = Gray20,
    onSecondary = Gray90,
    secondaryContainer = Gray60,
    onSecondaryContainer = Gray10,

    tertiary = Green40,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF003D31),
    onTertiaryContainer = Green90,

    error = Coral60,
    onError = Color.White,
    errorContainer = Coral40,
    onErrorContainer = Coral90,

    background = Color(0xFF0F0F1A),
    onBackground = Gray10,

    surface = Color(0xFF1A1A2E),
    onSurface = Gray10,
    surfaceVariant = Color(0xFF252538),
    onSurfaceVariant = Gray20,

    outline = Gray60,
    outlineVariant = Color(0xFF2E2E44),
)

@Composable
fun WorkFlowTheme(
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
