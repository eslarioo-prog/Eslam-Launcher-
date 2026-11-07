package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

fun getPixelColorScheme(paletteName: String, isDark: Boolean): ColorScheme {
    return when (paletteName) {
        "Bay Cyan" -> if (isDark) {
            darkColorScheme(
                primary = PixelBayPrimary,
                secondary = PixelBaySecondary,
                tertiary = PixelBayTertiary,
                background = PixelBayDark,
                surface = PixelBaySurface,
                surfaceVariant = PixelBayContainer,
                onSurfaceVariant = PixelBayOnContainer
            )
        } else {
            lightColorScheme(
                primary = Color(0xFF006689),
                secondary = Color(0xFF4C626C),
                tertiary = Color(0xFF5D5B8D),
                background = Color(0xFFF6FAFE),
                surface = Color(0xFFE4F3FA),
                surfaceVariant = Color(0xFFCCE8F4),
                onSurfaceVariant = Color(0xFF001F2B)
            )
        }
        "Meadow Mint" -> if (isDark) {
            darkColorScheme(
                primary = PixelMeadowPrimary,
                secondary = PixelMeadowSecondary,
                tertiary = PixelMeadowTertiary,
                background = PixelMeadowDark,
                surface = PixelMeadowSurface,
                surfaceVariant = PixelMeadowContainer,
                onSurfaceVariant = PixelMeadowOnContainer
            )
        } else {
            lightColorScheme(
                primary = Color(0xFF1B6C34),
                secondary = Color(0xFF526350),
                tertiary = Color(0xFF386568),
                background = Color(0xFFF7FBF4),
                surface = Color(0xFFE3F3E6),
                surfaceVariant = Color(0xFFD3E8D6),
                onSurfaceVariant = Color(0xFF00210B)
            )
        }
        "Coral Rose" -> if (isDark) {
            darkColorScheme(
                primary = PixelCoralPrimary,
                secondary = PixelCoralSecondary,
                tertiary = PixelCoralTertiary,
                background = PixelCoralDark,
                surface = PixelCoralSurface,
                surfaceVariant = PixelCoralContainer,
                onSurfaceVariant = PixelCoralOnContainer
            )
        } else {
            lightColorScheme(
                primary = Color(0xFF904A42),
                secondary = Color(0xFF775652),
                tertiary = Color(0xFF715B2E),
                background = Color(0xFFFFF8F6),
                surface = Color(0xFFFBE7E4),
                surfaceVariant = Color(0xFFF4DDD9),
                onSurfaceVariant = Color(0xFF2C1513)
            )
        }
        "Lilac Lavender" -> if (isDark) {
            darkColorScheme(
                primary = PixelLilacPrimary,
                secondary = PixelLilacSecondary,
                tertiary = PixelLilacTertiary,
                background = PixelLilacDark,
                surface = PixelLilacSurface,
                surfaceVariant = PixelLilacContainer,
                onSurfaceVariant = PixelLilacOnContainer
            )
        } else {
            lightColorScheme(
                primary = Color(0xFF744A8E),
                secondary = Color(0xFF67586F),
                tertiary = Color(0xFF814D60),
                background = Color(0xFFFDF7FF),
                surface = Color(0xFFF3E7FA),
                surfaceVariant = Color(0xFFEADBEE),
                onSurfaceVariant = Color(0xFF2B0042)
            )
        }
        else -> if (isDark) { // Obsidian Slate Default
            darkColorScheme(
                primary = PixelObsidianPrimary,
                secondary = PixelObsidianSecondary,
                tertiary = PixelObsidianTertiary,
                background = PixelObsidianDark,
                surface = PixelObsidianSurface,
                surfaceVariant = PixelObsidianContainer,
                onSurfaceVariant = PixelObsidianOnContainer
            )
        } else {
            lightColorScheme(
                primary = Color(0xFF006495),
                secondary = Color(0xFF50606E),
                tertiary = Color(0xFF65587B),
                background = Color(0xFFF8FAFD),
                surface = Color(0xFFE4ECF4),
                surfaceVariant = Color(0xFFD6E2ED),
                onSurfaceVariant = Color(0xFF001F31)
            )
        }
    }
}

@Composable
fun MyApplicationTheme(
    paletteName: String = "Obsidian Slate",
    darkModePreference: String = "SYSTEM",
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (darkModePreference) {
        "DARK" -> true
        "LIGHT" -> false
        else -> isSystemDark
    }

    val context = LocalContext.current
    val colorScheme = if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        getPixelColorScheme(paletteName, isDark)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
