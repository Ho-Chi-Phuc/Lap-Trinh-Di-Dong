package com.example.lab8.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val Lab8ColorScheme = lightColorScheme()

@Composable
fun Lab8Theme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = Lab8ColorScheme,
        content = content
    )
}