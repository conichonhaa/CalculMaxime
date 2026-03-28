package com.example.calculmaxime

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlinx.coroutines.delay
import kotlin.math.max
import kotlin.math.sqrt

/**
 * Splash screen avec révélation circulaire progressive.
 * Démarre centré sur la tête/chapeau, puis révèle successivement
 * tous les éléments mathématiques autour.
 *
 * Image requise : app/src/main/res/drawable/splash_maxime.png
 */
@Composable
fun SplashScreen(onSplashComplete: () -> Unit) {
    val revealProgress = remember { Animatable(0.18f) }

    LaunchedEffect(Unit) {
        delay(700) // Montre la tête seule 700ms
        revealProgress.animateTo(
            targetValue = 1.6f,
            animationSpec = tween(
                durationMillis = 2500,
                easing = FastOutSlowInEasing
            )
        )
        delay(400)
        onSplashComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.splash_maxime),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .clip(
                    CircularRevealShape(
                        progress = revealProgress.value,
                        centerFractionX = 0.5f,
                        centerFractionY = 0.38f // centré sur la tête/chapeau
                    )
                )
        )
    }
}

private class CircularRevealShape(
    private val progress: Float,
    private val centerFractionX: Float,
    private val centerFractionY: Float
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val cx = size.width * centerFractionX
        val cy = size.height * centerFractionY
        val dx = max(cx, size.width - cx)
        val dy = max(cy, size.height - cy)
        val maxRadius = sqrt(dx * dx + dy * dy)
        return Outline.Generic(
            Path().apply {
                addOval(Rect(center = Offset(cx, cy), radius = maxRadius * progress))
            }
        )
    }
}
