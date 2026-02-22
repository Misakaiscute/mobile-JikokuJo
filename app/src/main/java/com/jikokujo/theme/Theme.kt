package com.jikokujo.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Green60,
    onPrimary = Black100,
    secondary = Green20,
    onSecondary = Black100,
    tertiary = LightGreen15,
    onTertiary = Black100,
    background = Black90,
    onBackground = White100,
    surface = Black80,
    onSurface = White100,
    surfaceVariant = DarkGray70,
    onSurfaceVariant = White100,
    error = Red60
)

private val LightColorScheme = lightColorScheme(
    primary = Green60,
    onPrimary = White100,
    secondary = Green40,
    onSecondary = White100,
    tertiary = DarkGreen20,
    onTertiary = White100,
    background = White90,
    onBackground = Black100,
    surface = White80,
    onSurface = Black100,
    surfaceVariant = LightGray70,
    onSurfaceVariant = Black100,
    error = Red80
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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