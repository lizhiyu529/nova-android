package com.nova.app.ui.theme

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically

object NovaTransitions {

    val pageEnter: EnterTransition =
        fadeIn(NovaMotion.enter<Float>(NovaMotion.Standard, NovaMotion.PageDelay)) +
            slideInHorizontally(NovaMotion.emphasized<Int>()) { it / 2 }

    val pageExit: ExitTransition =
        fadeOut(NovaMotion.exit<Float>(NovaMotion.Fast)) +
            slideOutHorizontally(NovaMotion.fade<Int>(NovaMotion.Standard)) { it / 2 }

    val rootEnter: EnterTransition =
        fadeIn(NovaMotion.enter<Float>(NovaMotion.Emphasized, NovaMotion.PageDelay)) +
            slideInHorizontally(NovaMotion.emphasized<Int>(NovaMotion.Slow)) { it }

    val rootExit: ExitTransition =
        fadeOut(NovaMotion.exit<Float>(NovaMotion.Standard)) +
            slideOutHorizontally(NovaMotion.fade<Int>(NovaMotion.Emphasized)) { it }

    val panelEnter: EnterTransition =
        fadeIn(NovaMotion.enter<Float>(NovaMotion.Standard)) +
            slideInVertically(NovaMotion.emphasized<Int>(NovaMotion.Emphasized)) { it / 2 }

    val panelExit: ExitTransition =
        fadeOut(NovaMotion.exit<Float>(NovaMotion.Fast)) +
            slideOutVertically(NovaMotion.exit<Int>(NovaMotion.Standard)) { it / 2 }

    val toastEnter: EnterTransition =
        fadeIn(NovaMotion.enter<Float>(NovaMotion.Fast)) +
            scaleIn(NovaMotion.bouncy<Float>(), initialScale = 0.88f) +
            slideInVertically(NovaMotion.enter<Int>(NovaMotion.Standard)) { it / 3 }

    val toastExit: ExitTransition =
        fadeOut(NovaMotion.exit<Float>(NovaMotion.Standard)) +
            slideOutVertically(NovaMotion.exit<Int>(NovaMotion.Standard)) { it / 4 }

    val inlineEnter: EnterTransition =
        fadeIn(NovaMotion.enter<Float>(NovaMotion.Fast)) +
            slideInVertically(NovaMotion.enter<Int>(NovaMotion.Fast)) { it / 4 }

    val inlineExit: ExitTransition =
        fadeOut(NovaMotion.exit<Float>(NovaMotion.Instant)) +
            slideOutVertically(NovaMotion.exit<Int>(NovaMotion.Fast)) { it / 4 }

    val subtleEnter: EnterTransition = fadeIn(NovaMotion.enter<Float>(NovaMotion.Fast))

    val subtleExit: ExitTransition = fadeOut(NovaMotion.exit<Float>(NovaMotion.Instant))
}
