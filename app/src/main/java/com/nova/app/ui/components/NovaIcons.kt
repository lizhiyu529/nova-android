package com.nova.app.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
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
                strokeLineCap = StrokeCap.Round,
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
                strokeLineCap = StrokeCap.Round,
            ) {
                moveTo(18f, 2.8f)
                verticalLineTo(8.2f)
            }
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.9f,
                strokeLineCap = StrokeCap.Round,
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
                strokeLineCap = StrokeCap.Round,
            ) {
                moveTo(12f, 5f)
                verticalLineTo(19f)
            }
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.9f,
                strokeLineCap = StrokeCap.Round,
            ) {
                moveTo(5f, 12f)
                horizontalLineTo(19f)
            }
        }.build()
    }

    val Search: ImageVector by lazy {
        ImageVector.Builder(
            name = "Search",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.9f,
                strokeLineCap = StrokeCap.Round,
            ) {
                moveTo(11f, 4f)
                curveTo(14.9f, 4f, 18f, 7.1f, 18f, 11f)
                curveTo(18f, 14.9f, 14.9f, 18f, 11f, 18f)
                curveTo(7.1f, 18f, 4f, 14.9f, 4f, 11f)
                curveTo(4f, 7.1f, 7.1f, 4f, 11f, 4f)
                close()
            }
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.9f,
                strokeLineCap = StrokeCap.Round,
            ) {
                moveTo(16.2f, 16.2f)
                lineTo(20f, 20f)
            }
        }.build()
    }

    val ChevronDown: ImageVector by lazy {
        ImageVector.Builder(
            name = "ChevronDown",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.9f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = androidx.compose.ui.graphics.StrokeJoin.Round,
            ) {
                moveTo(6.5f, 9.5f)
                lineTo(12f, 15f)
                lineTo(17.5f, 9.5f)
            }
        }.build()
    }

    val Sort: ImageVector by lazy {
        ImageVector.Builder(
            name = "Sort",            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.9f,
                strokeLineCap = StrokeCap.Round,
            ) {
                moveTo(4f, 7f)
                horizontalLineTo(20f)
            }
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.9f,
                strokeLineCap = StrokeCap.Round,
            ) {
                moveTo(4f, 12f)
                horizontalLineTo(14f)
            }
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.9f,
                strokeLineCap = StrokeCap.Round,
            ) {
                moveTo(4f, 17f)
                horizontalLineTo(9f)
            }
        }.build()
    }

    val More: ImageVector by lazy {
        ImageVector.Builder(
            name = "More",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(5.6f, 10.4f)
                curveTo(6.5f, 10.4f, 7.2f, 11.1f, 7.2f, 12f)
                curveTo(7.2f, 12.9f, 6.5f, 13.6f, 5.6f, 13.6f)
                curveTo(4.7f, 13.6f, 4f, 12.9f, 4f, 12f)
                curveTo(4f, 11.1f, 4.7f, 10.4f, 5.6f, 10.4f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(12f, 10.4f)
                curveTo(12.9f, 10.4f, 13.6f, 11.1f, 13.6f, 12f)
                curveTo(13.6f, 12.9f, 12.9f, 13.6f, 12f, 13.6f)
                curveTo(11.1f, 13.6f, 10.4f, 12.9f, 10.4f, 12f)
                curveTo(10.4f, 11.1f, 11.1f, 10.4f, 12f, 10.4f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(18.4f, 10.4f)
                curveTo(19.3f, 10.4f, 20f, 11.1f, 20f, 12f)
                curveTo(20f, 12.9f, 19.3f, 13.6f, 18.4f, 13.6f)
                curveTo(17.5f, 13.6f, 16.8f, 12.9f, 16.8f, 12f)
                curveTo(16.8f, 11.1f, 17.5f, 10.4f, 18.4f, 10.4f)
                close()
            }
        }.build()
    }

    val Person: ImageVector by lazy {
        ImageVector.Builder(
            name = "Person",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(12f, 4f)
                curveTo(14.2f, 4f, 16f, 5.8f, 16f, 8f)
                curveTo(16f, 10.2f, 14.2f, 12f, 12f, 12f)
                curveTo(9.8f, 12f, 8f, 10.2f, 8f, 8f)
                curveTo(8f, 5.8f, 9.8f, 4f, 12f, 4f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(12f, 14f)
                curveTo(16f, 14f, 19.5f, 16f, 20f, 19f)
                curveTo(20.1f, 19.6f, 19.6f, 20f, 19f, 20f)
                horizontalLineTo(5f)
                curveTo(4.4f, 20f, 3.9f, 19.6f, 4f, 19f)
                curveTo(4.5f, 16f, 8f, 14f, 12f, 14f)
                close()
            }
        }.build()
    }

    val SelectAll: ImageVector by lazy {
        ImageVector.Builder(
            name = "SelectAll",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round,
            ) {
                moveTo(4.5f, 8f)
                horizontalLineTo(19.5f)
            }
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round,
            ) {
                moveTo(4.5f, 12.5f)
                horizontalLineTo(12f)
            }
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round,
            ) {
                moveTo(4.5f, 17f)
                horizontalLineTo(12f)
            }
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round,
            ) {
                moveTo(16f, 14.5f)
                lineTo(18f, 16.5f)
                lineTo(21.5f, 12.5f)
            }
        }.build()
    }

    val Check: ImageVector by lazy {
        ImageVector.Builder(
            name = "Check",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.4f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = androidx.compose.ui.graphics.StrokeJoin.Round,
            ) {
                moveTo(5f, 12.5f)
                lineTo(10f, 17.5f)
                lineTo(19f, 7f)
            }
        }.build()
    }

    val Pin: ImageVector by lazy {
        ImageVector.Builder(
            name = "Pin",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = androidx.compose.ui.graphics.StrokeJoin.Round,
            ) {
                moveTo(12f, 3.5f)
                lineTo(19f, 8f)
                lineTo(16.5f, 9.5f)
                lineTo(16.5f, 14f)
                lineTo(19f, 16.5f)
                lineTo(5f, 16.5f)
                lineTo(7.5f, 14f)
                lineTo(7.5f, 9.5f)
                lineTo(5f, 8f)
                close()
            }
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round,
            ) {
                moveTo(12f, 16.5f)
                verticalLineTo(21f)
            }
        }.build()
    }

    val Trash: ImageVector by lazy {
        ImageVector.Builder(
            name = "Trash",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = androidx.compose.ui.graphics.StrokeJoin.Round,
            ) {
                moveTo(4.5f, 6.5f)
                horizontalLineTo(19.5f)
            }
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = androidx.compose.ui.graphics.StrokeJoin.Round,
            ) {
                moveTo(6.5f, 6.5f)
                lineTo(7.5f, 19.5f)
                horizontalLineTo(16.5f)
                lineTo(17.5f, 6.5f)
            }
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round,
            ) {
                moveTo(9.5f, 6.5f)
                verticalLineTo(4.5f)
                horizontalLineTo(14.5f)
                verticalLineTo(6.5f)
            }
        }.build()
    }

    val Back: ImageVector by lazy {
        ImageVector.Builder(
            name = "Back",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = androidx.compose.ui.graphics.StrokeJoin.Round,
            ) {
                moveTo(14.5f, 5f)
                lineTo(7.5f, 12f)
                lineTo(14.5f, 19f)
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
                strokeLineCap = StrokeCap.Round,
            ) {
                moveTo(6f, 6f)
                lineTo(18f, 18f)
            }
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.9f,
                strokeLineCap = StrokeCap.Round,
            ) {
                moveTo(18f, 6f)
                lineTo(6f, 18f)
            }
        }.build()
    }
}
