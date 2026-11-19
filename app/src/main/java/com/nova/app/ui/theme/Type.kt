package com.nova.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val Base = Typography()

val NovaTypography = Typography(
    headlineLargeEmphasized = Base.headlineLargeEmphasized.copy(
        fontSize = 22.sp,
        lineHeight = 30.sp,
        fontWeight = FontWeight.SemiBold,
    ),
    headlineMediumEmphasized = Base.headlineMediumEmphasized.copy(
        fontSize = 20.sp,
        lineHeight = 27.sp,
        fontWeight = FontWeight.SemiBold,
    ),
)
