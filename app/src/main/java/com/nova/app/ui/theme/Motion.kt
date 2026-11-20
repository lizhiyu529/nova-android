package com.nova.app.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.material3.MotionScheme

object NovaMotion {

    const val Instant = 90
    const val Fast = 150
    const val Standard = 240
    const val Emphasized = 340
    const val Slow = 440
    const val Shimmer = 900

    const val ShimmerMin = 0.35f
    const val ShimmerMax = 0.75f

    const val PressScale = 0.94f
    const val PressScaleTight = 0.88f
    const val PressDamping = 0.72f
    const val PressStiffness = 520f

    const val PageDelay = 40
    const val PageOffsetFraction = 0.5f

    val EasingStandard: Easing = FastOutSlowInEasing
    val EasingEnter: Easing = LinearOutSlowInEasing
    val EasingExit: Easing = FastOutLinearInEasing
    val EasingLinear: Easing = androidx.compose.animation.core.LinearEasing
    val EasingEmphasized: Easing = CubicBezierEasing(0.2f, 0f, 0f, 1f)

    fun <T> fade(durationMillis: Int = Standard, delayMillis: Int = 0): FiniteAnimationSpec<T> =
        tween(durationMillis = durationMillis, delayMillis = delayMillis, easing = EasingStandard)

    fun <T> enter(durationMillis: Int = Emphasized, delayMillis: Int = 0): FiniteAnimationSpec<T> =
        tween(durationMillis = durationMillis, delayMillis = delayMillis, easing = EasingEnter)

    fun <T> exit(durationMillis: Int = Fast, delayMillis: Int = 0): FiniteAnimationSpec<T> =
        tween(durationMillis = durationMillis, delayMillis = delayMillis, easing = EasingExit)

    fun <T> emphasized(durationMillis: Int = Slow, delayMillis: Int = 0): FiniteAnimationSpec<T> =
        tween(durationMillis = durationMillis, delayMillis = delayMillis, easing = EasingEmphasized)

    fun <T> press(): FiniteAnimationSpec<T> =
        spring(dampingRatio = PressDamping, stiffness = PressStiffness)

    fun <T> settle(): FiniteAnimationSpec<T> =
        spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow)

    fun <T> bouncy(): FiniteAnimationSpec<T> =
        spring(dampingRatio = 0.82f, stiffness = Spring.StiffnessMedium)
}

val NovaMotionScheme: MotionScheme = object : MotionScheme {

    override fun <T> defaultSpatialSpec(): FiniteAnimationSpec<T> = NovaMotion.settle()

    override fun <T> fastSpatialSpec(): FiniteAnimationSpec<T> =
        spring(dampingRatio = 0.9f, stiffness = Spring.StiffnessHigh)

    override fun <T> slowSpatialSpec(): FiniteAnimationSpec<T> =
        spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessLow)

    override fun <T> defaultEffectsSpec(): FiniteAnimationSpec<T> = NovaMotion.fade()

    override fun <T> fastEffectsSpec(): FiniteAnimationSpec<T> = NovaMotion.fade(NovaMotion.Fast)

    override fun <T> slowEffectsSpec(): FiniteAnimationSpec<T> = NovaMotion.fade(NovaMotion.Slow)
}
