package com.nova.app.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object NovaIcons {

    val Menu: ImageVector by lazy {
        ImageVector.Builder(
            name = "Menu",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(4f, 6.5f)
                horizontalLineTo(20f)
                verticalLineTo(8.5f)
                horizontalLineTo(4f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(4f, 11f)
                horizontalLineTo(20f)
                verticalLineTo(13f)
                horizontalLineTo(4f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(4f, 15.5f)
                horizontalLineTo(14f)
                verticalLineTo(17.5f)
                horizontalLineTo(4f)
                close()
            }
        }.build()
    }

    val NewChat: ImageVector by lazy {
        ImageVector.Builder(
            name = "NewChat",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.9f,
                strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Round,
                strokeLineJoin = androidx.compose.ui.graphics.StrokeJoin.Round,
            ) {
                moveTo(20f, 11.5f)
                verticalLineTo(15f)
                curveTo(20f, 17.8f, 17.8f, 20f, 15f, 20f)
                horizontalLineTo(9f)
                curveTo(6.2f, 20f, 4f, 17.8f, 4f, 15f)
                verticalLineTo(9f)
                curveTo(4f, 6.2f, 6.2f, 4f, 9f, 4f)
                horizontalLineTo(12.5f)
            }
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.9f,
                strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Round,
            ) {
                moveTo(18f, 2.8f)
                verticalLineTo(8.2f)
            }
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.9f,
                strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Round,
            ) {
                moveTo(20.7f, 5.5f)
                horizontalLineTo(15.3f)
            }
        }.build()
    }

    val Send: ImageVector by lazy {
        ImageVector.Builder(
            name = "Send",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(12f, 4.2f)
                lineTo(19.2f, 11.4f)
                lineTo(17.8f, 12.8f)
                lineTo(13f, 8f)
                verticalLineTo(20f)
                horizontalLineTo(11f)
                verticalLineTo(8f)
                lineTo(6.2f, 12.8f)
                lineTo(4.8f, 11.4f)
                close()
            }
        }.build()
    }

    val Plus: ImageVector by lazy {
        ImageVector.Builder(
            name = "Plus",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.9f,
                strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Round,
            ) {
                moveTo(12f, 5f)
                verticalLineTo(19f)
            }
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.9f,
                strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Round,
            ) {
                moveTo(5f, 12f)
                horizontalLineTo(19f)
            }
        }.build()
    }

    val Close: ImageVector by lazy {
        ImageVector.Builder(
            name = "Close",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.9f,
                strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Round,
            ) {
                moveTo(6f, 6f)
                lineTo(18f, 18f)
            }
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.9f,
                strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Round,
            ) {
                moveTo(18f, 6f)
                lineTo(6f, 18f)
            }
        }.build()
    }
}
