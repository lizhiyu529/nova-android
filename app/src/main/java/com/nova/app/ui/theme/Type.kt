package com.nova.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * 字号阶梯（基准值）。全局所有文字都必须从这里取，
 * 不要在页面里写死 fontSize，否则字体缩放会失效。
 *
 * 层级：页面标题 > 区块标题 > 正文 > 次要说明 > 辅助标签
 */
object NovaType {

    /** 页面主标题：设置、数据管理等顶栏。 */
    val pageTitle = TextStyle(
        fontSize = 17.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.SemiBold,
    )

    /** 首页问候语主标题。 */
    val greetingTitle = TextStyle(
        fontSize = 22.sp,
        lineHeight = 30.sp,
        fontWeight = FontWeight.SemiBold,
    )

    /** 首页问候语副标题。 */
    val greetingSubtitle = TextStyle(
        fontSize = 15.sp,
        lineHeight = 22.sp,
        fontWeight = FontWeight.Normal,
    )

    /** 卡片标题、设置项、对话条目。 */
    val itemTitle = TextStyle(
        fontSize = 15.sp,
        lineHeight = 22.sp,
        fontWeight = FontWeight.Normal,
    )

    /** 正文内容。 */
    val body = TextStyle(
        fontSize = 14.sp,
        lineHeight = 21.sp,
        fontWeight = FontWeight.Normal,
    )

    /** 右侧值、次要说明。 */
    val caption = TextStyle(
        fontSize = 13.sp,
        lineHeight = 19.sp,
        fontWeight = FontWeight.Normal,
    )

    /** 输入框文字、搜索框。 */
    val input = TextStyle(
        fontSize = 16.sp,
        lineHeight = 22.sp,
        fontWeight = FontWeight.Normal,
    )

    /** 分组标签：账户、应用、关于、今天、昨天。 */
    val sectionLabel = TextStyle(
        fontSize = 13.sp,
        lineHeight = 18.sp,
        fontWeight = FontWeight.Normal,
    )

    /** 日志等极小的辅助文字。 */
    val mono = TextStyle(
        fontSize = 11.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Normal,
    )

    /** 按钮文字。 */
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
