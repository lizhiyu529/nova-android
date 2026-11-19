package com.nova.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val Base = Typography()

val NovaTypography = Typography(
    headlineLargeEmphasized = Base.headlineLargeEmphasized.copy(
        fontSize = 30.sp,
        lineHeight = 38.sp,
        fontWeight = FontWeight.SemiBold,
    ),
    headlineMediumEmphasized = Base.headlineMediumEmphasized.copy(
        fontSize = 26.sp,
        lineHeight = 33.sp,
        fontWeight = FontWeight.SemiBold,
    ),
)
