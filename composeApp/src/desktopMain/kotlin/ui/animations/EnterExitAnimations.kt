package ui.animations

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.IntOffset

@Stable
fun slideInFromLeft(): EnterTransition = slideIn(
    initialOffset = { IntOffset(-it.width, 0) },
    animationSpec = tween(250)
)

@Stable
fun slideInFromRight(): EnterTransition = slideIn(
    initialOffset = { IntOffset(it.width, 0) },
    animationSpec = tween(250)
)

@Stable
fun slideOutToLeft(): ExitTransition = slideOut(
    targetOffset = { IntOffset(-it.width, 0) },
    animationSpec = tween(250)
)

@Stable
fun slideOutToRight(): ExitTransition = slideOut(
    targetOffset = { IntOffset(it.width, 0) },
    animationSpec = tween(250)
)