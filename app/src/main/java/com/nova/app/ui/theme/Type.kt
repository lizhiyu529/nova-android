package com.nova.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object NovaType {

    val pageTitle = TextStyle(
        fontSize = 17.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.SemiBold,
    )

    val greetingTitle = TextStyle(
        fontSize = 22.sp,
        lineHeight = 30.sp,
        fontWeight = FontWeight.SemiBold,
    )

    val greetingSubtitle = TextStyle(
        fontSize = 15.sp,
        lineHeight = 22.sp,
        fontWeight = FontWeight.Normal,
    )

    val itemTitle = TextStyle(
        fontSize = 15.sp,
        lineHeight = 22.sp,
        fontWeight = FontWeight.Normal,
    )

    val body = TextStyle(
        fontSize = 14.sp,
        lineHeight = 21.sp,
        fontWeight = FontWeight.Normal,
    )

    val caption = TextStyle(
        fontSize = 13.sp,
        lineHeight = 19.sp,
        fontWeight = FontWeight.Normal,
    )

    val input = TextStyle(
        fontSize = 16.sp,
        lineHeight = 22.sp,
        fontWeight = FontWeight.Normal,
    )

    val sectionLabel = TextStyle(
        fontSize = 13.sp,
        lineHeight = 18.sp,
        fontWeight = FontWeight.Normal,
    )

    val mono = TextStyle(
        fontSize = 11.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Normal,
    )

    val button = TextStyle(
        fontSize = 15.sp,
        lineHeight = 21.sp,
        fontWeight = FontWeight.Medium,
    )
}

val NovaTypography = Typography(
    headlineLarge = NovaType.greetingTitle,
    headlineLargeEmphasized = NovaType.greetingTitle,
    headlineMedium = NovaType.greetingTitle,
    headlineMediumEmphasized = NovaType.greetingTitle,
    headlineSmall = NovaType.pageTitle,
    titleLarge = NovaType.pageTitle,
    titleMedium = NovaType.itemTitle,
    titleSmall = NovaType.sectionLabel,
    bodyLarge = NovaType.itemTitle,
    bodyMedium = NovaType.body,
    bodySmall = NovaType.caption,
    labelLarge = NovaType.button,
    labelMedium = NovaType.sectionLabel,
    labelSmall = NovaType.caption,
)
