package com.example.excel_reader.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.sp
import com.example.excel_reader.R

// Bahij Font Family
val BahijFontFamily = FontFamily(
    Font(R.font.bahij_black, FontWeight.Black),
    Font(R.font.bahij_bold, FontWeight.Bold),
    Font(R.font.bahij_extra_bold, FontWeight.ExtraBold),
    Font(R.font.bahij_extra_light, FontWeight.ExtraLight),
    Font(R.font.bahij_plain, FontWeight.Normal),
    Font(R.font.bahij_semi_bold, FontWeight.SemiBold),
    Font(R.font.bahij_semi_light, FontWeight.Light)
)

val GalanoFontFamily = FontFamily(
    Font(R.font.galano_grotesque_black, FontWeight.Black),
    Font(R.font.galano_grotesque_bold, FontWeight.Bold),
    Font(R.font.galano_grotesque_heavy, FontWeight.ExtraBold),
    Font(R.font.galano_grotesque_medium, FontWeight.Medium),
    Font(R.font.galano_grotesque_regular, FontWeight.Normal),
    Font(R.font.galano_grotesque_semibold, FontWeight.SemiBold),
    Font(R.font.galano_grotesque_thin, FontWeight.Thin)
)
// Helper Function to Get Font Family
@Composable
fun getFontFamily(): FontFamily {
    val layoutDirection = LocalLayoutDirection.current
    return if (layoutDirection == LayoutDirection.Rtl) BahijFontFamily else GalanoFontFamily
}

// Custom Typography
@Composable
fun dynamicTypography(): Typography {
    val fontFamily = getFontFamily() // Call the composable function directly

    // Define typography with the selected font family
    return Typography(
        displayLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Black,
            fontSize = 30.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        ),
        labelSmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Light,
            fontSize = 12.sp
        )
    )
}
