package com.example.ncs3.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val HospitalBlue = Color(0xFF0D47A1)
val HospitalLight = Color(0xFF00BCD4)
val HospitalTeal = Color(0xFF0097A7)
val HospitalBg = Color(0xFFF0F4F8)

private val LightColorScheme = lightColorScheme(
    primary = HospitalBlue,
    secondary = HospitalLight,
    tertiary = HospitalTeal,
    background = HospitalBg,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White
)

@Composable
fun MediCareTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography(),
        content = content
    )
}
