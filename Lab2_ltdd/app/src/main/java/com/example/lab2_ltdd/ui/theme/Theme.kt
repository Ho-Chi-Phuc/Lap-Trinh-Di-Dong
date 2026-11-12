package com.example.lab2_ltdd.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = ElectricCyan,
    onPrimary = NavyBackground,
    primaryContainer = DeepNavy,
    onPrimaryContainer = ElectricCyan,
    secondary = SkyCyan,
    onSecondary = NavyBackground,
    secondaryContainer = NavyCardBorder,
    onSecondaryContainer = SkyCyan,
    background = NavyBackground,
    onBackground = TextPrimary,
    surface = NavyCard,
    onSurface = TextPrimary,
    surfaceVariant = NavyCardBorder,
    onSurfaceVariant = TextSecondary,
    outline = NavyCardBorder
)

@Composable
fun Lab2_ltddTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
