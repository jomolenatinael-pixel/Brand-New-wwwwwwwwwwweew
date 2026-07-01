package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val CustomColorScheme = darkColorScheme(
    primary = ElectricBlue,
    secondary = PurpleAccent,
    tertiary = EmeraldGreen,
    background = DeepNavy,
    surface = CardNavy,
    onPrimary = TextWhite,
    onSecondary = TextWhite,
    onTertiary = TextWhite,
    onBackground = TextWhite,
    onSurface = TextWhite
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force premium dark theme for sci-fi feel
    dynamicColor: Boolean = false, // Disable dynamic colors to enforce the ultra-premium branding
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = CustomColorScheme,
        typography = Typography,
        content = content
    )
}
