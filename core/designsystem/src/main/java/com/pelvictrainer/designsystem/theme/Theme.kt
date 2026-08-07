package com.pelvictrainer.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val PtBackground = Color(0xFF0B0D10)
val PtSurface = Color(0xFF14181C)
val PtSurfaceVariant = Color(0xFF1E2227)
val PtOnSurface = Color(0xFFE2E2E7)
val PtOnSurfaceVariant = Color(0xFFA1A6AD)
val PtOutline = Color(0xFF2A2F36)
val PtPrimary = Color(0xFFBE1D2C)
val PtPrimaryContainer = Color(0xFF3A1318)
val PtOnPrimary = Color(0xFFFFFFFF)
val PtError = Color(0xFFFF5252)

val PtBackgroundLight = Color(0xFFF7F8FA)
val PtSurfaceLight = Color(0xFFFFFFFF)
val PtSurfaceVariantLight = Color(0xFFEDEFF2)
val PtOnSurfaceLight = Color(0xFF191C1E)
val PtOnSurfaceVariantLight = Color(0xFF5C6167)
val PtOutlineLight = Color(0xFFD2D5DA)

private fun darkScheme(primary: Color) = darkColorScheme(
    primary = primary,
    onPrimary = PtOnPrimary,
    primaryContainer = PtPrimaryContainer,
    onPrimaryContainer = PtOnSurface,
    background = PtBackground,
    onBackground = PtOnSurface,
    surface = PtSurface,
    onSurface = PtOnSurface,
    surfaceVariant = PtSurfaceVariant,
    onSurfaceVariant = PtOnSurfaceVariant,
    outline = PtOutline,
    error = PtError
)

private fun lightScheme(primary: Color) = lightColorScheme(
    primary = primary,
    onPrimary = PtOnPrimary,
    primaryContainer = PtPrimaryContainer,
    onPrimaryContainer = PtOnSurfaceLight,
    background = PtBackgroundLight,
    onBackground = PtOnSurfaceLight,
    surface = PtSurfaceLight,
    onSurface = PtOnSurfaceLight,
    surfaceVariant = PtSurfaceVariantLight,
    onSurfaceVariant = PtOnSurfaceVariantLight,
    outline = PtOutlineLight,
    error = PtError
)

@Composable
fun PelvicTrainerTheme(
    darkTheme: Boolean = true,
    primary: Color = PtPrimary,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) darkScheme(primary) else lightScheme(primary),
        typography = PtTypography,
        shapes = PtShapes,
        content = content
    )
}