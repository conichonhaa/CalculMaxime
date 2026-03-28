package com.example.calculmaxime

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ── Élément à révéler ────────────────────────────────────────────────────────
// fx/fy = position en fraction de l'image, radiusFraction = rayon / largeur image
private data class Element(val fx: Float, val fy: Float, val radiusFraction: Float)

// Ordre de révélation : sens horaire à partir du haut
private val ELEMENTS = listOf(
    Element(0.33f, 0.10f, 0.18f), // ampoule
    Element(0.62f, 0.09f, 0.24f), // 27+6+?
    Element(0.89f, 0.20f, 0.10f), // ? rouge
    Element(0.86f, 0.40f, 0.10f), // ? violet (droite)
    Element(0.82f, 0.55f, 0.15f), // ? bleu + équerre
    Element(0.70f, 0.76f, 0.24f), // 5+9 + croix verte
    Element(0.14f, 0.73f, 0.18f), // camembert
    Element(0.13f, 0.55f, 0.14f), // X + signe –
    Element(0.12f, 0.40f, 0.18f), // 8*4 + calculatrice
    Element(0.12f, 0.22f, 0.14f), // ? violet (gauche) + + bleu
)

// ── Composable principal ──────────────────────────────────────────────────────
@Composable
fun SplashScreen(onSplashComplete: () -> Unit) {
    val headDrop = remember { Animatable(0f) }                      // 0 → 1
    val elementReveal = remember { List(ELEMENTS.size) { Animatable(0f) } }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        // Phase 1 : la tête tombe avec rebond
        headDrop.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        delay(300)

        // Phase 2 : révélation en sens horaire, élément par élément
        for (i in ELEMENTS.indices) {
            scope.launch {
                elementReveal[i].animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
                )
            }
            delay(180) // décalage entre chaque élément
        }

        // Attendre la fin de la dernière animation
        delay(ELEMENTS.size * 180L + 800L)
        onSplashComplete()
    }

    val painter = painterResource(id = R.drawable.splash_maxime)
    val imageAspect = remember(painter) {
        val s = painter.intrinsicSize
        if (s.height > 0f) s.width / s.height else 0.77f
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        val density = LocalDensity.current
        val screenW = with(density) { maxWidth.toPx() }
        val screenH = with(density) { maxHeight.toPx() }

        // ── Calcul des bornes d'affichage de l'image (ContentScale.Fit) ──────
        val screenAspect = if (screenH > 0f) screenW / screenH else 0.46f
        val imgW: Float
        val imgH: Float
        if (imageAspect > screenAspect) {
            imgW = screenW
            imgH = screenW / imageAspect
        } else {
            imgH = screenH
            imgW = screenH * imageAspect
        }
        val imgLeft = (screenW - imgW) / 2f
        val imgTop  = (screenH - imgH) / 2f

        // ── Position finale de la tête (centre de la tête dans l'image) ──────
        val headFinalX = imgLeft + imgW * 0.50f
        val headFinalY = imgTop  + imgH * 0.38f
        val headRadius = imgW * 0.30f
        val headStartY = -headRadius * 2f            // commence au-dessus de l'écran

        // Capture des valeurs animées pour le Canvas
        val dropVal    = headDrop.value
        val revealVals = elementReveal.map { it.value }

        // ── Image complète (toujours en dessous) ─────────────────────────────
        Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )

        // ── Cache blanc avec trous animés ─────────────────────────────────────
        // CompositingStrategy.Offscreen est indispensable pour que
        // BlendMode.Clear découpe réellement dans le cache blanc.
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
        ) {
            // Fond blanc opaque
            drawRect(Color.White)

            // Trou « tête » : descend de hors-écran jusqu'à la position finale
            val headY = headStartY + (headFinalY - headStartY) * dropVal
            drawCircle(
                color     = Color.Black,
                radius    = headRadius,
                center    = Offset(headFinalX, headY),
                blendMode = BlendMode.Clear
            )

            // Trous « éléments » : apparaissent un par un en sens horaire
            ELEMENTS.forEachIndexed { i, el ->
                val progress = revealVals.getOrElse(i) { 0f }
                if (progress > 0f) {
                    drawCircle(
                        color     = Color.Black,
                        radius    = imgW * el.radiusFraction * progress,
                        center    = Offset(imgLeft + el.fx * imgW, imgTop + el.fy * imgH),
                        blendMode = BlendMode.Clear
                    )
                }
            }
        }
    }
}
