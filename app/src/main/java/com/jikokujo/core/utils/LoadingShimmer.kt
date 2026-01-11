package com.jikokujo.core.utils

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun Modifier.loadingShimmer(durationMillis: Int): Modifier{
    val transition = rememberInfiniteTransition("")

    val transitionAnimation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 500f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )

    return drawBehind {
        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.LightGray.copy(0.2f),
                    Color.LightGray.copy(1f),
                    Color.LightGray.copy(0.2f)
                ),
                start = Offset(x = transitionAnimation, y = transitionAnimation),
                end = Offset(x = transitionAnimation + 100f, y = transitionAnimation + 100f)
            )
        )
    }
}