package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val PlayfairDisplayFontFamily = FontFamily(
    Font(R.font.playfair_display, FontWeight.Normal),
    Font(R.font.playfair_display, FontWeight.Bold)
)

val PoppinsFontFamily = FontFamily(
    Font(R.font.poppins, FontWeight.Normal),
    Font(R.font.poppins, FontWeight.Medium),
    Font(R.font.poppins, FontWeight.SemiBold)
)

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = PlayfairDisplayFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 38.sp,
        color = TextPrimary
    ),
    displayMedium = TextStyle(
        fontFamily = PlayfairDisplayFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 32.sp,
        color = TextPrimary
    ),
    headlineLarge = TextStyle(
        fontFamily = PlayfairDisplayFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        color = TextPrimary
    ),
    headlineMedium = TextStyle(
        fontFamily = PoppinsFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        color = TextPrimary
    ),
    titleLarge = TextStyle(
        fontFamily = PoppinsFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        color = TextPrimary
    ),
    titleMedium = TextStyle(
        fontFamily = PoppinsFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        color = TextSecondary
    ),
    bodyLarge = TextStyle(
        fontFamily = PoppinsFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        color = TextPrimary
    ),
    bodyMedium = TextStyle(
        fontFamily = PoppinsFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        color = TextSecondary
    ),
    labelLarge = TextStyle(
        fontFamily = PoppinsFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        color = TextPrimary
    ),
    labelSmall = TextStyle(
        fontFamily = PoppinsFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        color = TextMuted
    )
)
