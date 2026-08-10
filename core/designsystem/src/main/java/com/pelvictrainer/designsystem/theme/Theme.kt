package com.pelvictrainer.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PtPrimary,
    onPrimary = PtOnPrimary,
    primaryContainer = PtPrimaryContainer,
    onPrimaryContainer = PtOnPrimaryContainer,
    secondary = BordeauxDark,
    onSecondary = PtOnPrimary,
    secondaryContainer = BordeauxContainer,
    onSecondaryContainer = BordeauxLight,
    tertiary = Color(0xFF6C757D),
    onTertiary = Color(0xFFFFFFFF),
    background = PtBackground,
    onBackground = PtOnSurface,
    surface = PtSurface,
    onSurface = PtOnSurface,
    surfaceVariant = PtSurfaceVariant,
    onSurfaceVariant = PtOnSurfaceVariant,
    outline = PtOutline,
    outlineVariant = Color(0xFF3A3F46),
    error = PtError,
    onError = PtOnError,
    errorContainer = Color(0xFF450A0A),
    onErrorContainer = Color(0xFFFFDAD6),
    inverseSurface = PtOnSurface,
    inverseOnSurface = PtBackground,
    inversePrimary = BordeauxLight
)

private val LightColorScheme = lightColorScheme(
    primary = Bordeaux,
    onPrimary = Color.White,
    primaryContainer = BordeauxLight,
    onPrimaryContainer = BordeauxDark,
    secondary = Bordeaux,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE5E3),
    onSecondaryContainer = BordeauxDark,
    tertiary = Color(0xFF6C757D),
    onTertiary = Color.White,
    background = PtBackgroundLight,
    onBackground = PtOnSurfaceLight,
    surface = PtSurfaceLight,
    onSurface = PtOnSurfaceLight,
    surfaceVariant = PtSurfaceVariantLight,
    onSurfaceVariant = PtOnSurfaceVariantLight,
    outline = PtOutlineLight,
    outlineVariant = Color(0xFFCAC4D0),
    error = PtError,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    inverseSurface = PtOnSurfaceLight,
    inverseOnSurface = PtBackgroundLight,
    inversePrimary = BordeauxLight
)

@Composable
fun PelvicTrainerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    primary: Color = Bordeaux,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme.copy(
            primary = primary,
            secondary = primary
        )
    } else {
        LightColorScheme.copy(
            primary = primary,
            secondary = primary
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = PtTypography,
        shapes = PtShapes,
        content = content
    )
}