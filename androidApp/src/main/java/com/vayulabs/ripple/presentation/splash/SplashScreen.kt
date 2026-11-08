package com.vayulabs.ripple.presentation.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vayulabs.ripple.haptics.HapticEngine
import com.vayulabs.ripple.presentation.components.IbmBlue
import com.vayulabs.ripple.presentation.components.IbmBlueLight
import com.vayulabs.ripple.presentation.components.IbmPurpleLight
import com.vayulabs.ripple.theme.RippleColors
import com.vayulabs.ripple.theme.ThemeState
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    hapticEngine: HapticEngine,
    onSplashFinished: () -> Unit
) {
    val isDark = ThemeState.isDarkMode.value

    // Harmonic expansion pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "SplashRipples")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RipplePulse"
    )

    // Entrance fade-in and scale
    val entranceAlpha = remember { Animatable(0f) }
    val entranceScale = remember { Animatable(0.92f) }

    LaunchedEffect(Unit) {
        hapticEngine.triggerSplashBloom()
        entranceAlpha.animateTo(1f, animationSpec = tween(500, easing = LinearOutSlowInEasing))
        entranceScale.animateTo(1f, animationSpec = tween(600, easing = EaseOutCubic))
        delay(1100)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDark) Color(0xFF080B11) else Color(0xFFF7F8FA)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            // Minimal Animated Ripple Emblem
            Box(
                modifier = Modifier
                    .size(140.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cx = size.width / 2f
                    val cy = size.height / 2f

                    // Ambient radiant backdrop glow
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                IbmBlue.copy(alpha = 0.35f * entranceAlpha.value),
                                IbmPurpleLight.copy(alpha = 0.15f * entranceAlpha.value),
                                Color.Transparent
                            ),
                            center = Offset(cx, cy),
                            radius = 65.dp.toPx()
                        ),
                        radius = 65.dp.toPx()
                    )

                    // Concentric expanding liquid ripple waves
                    val maxRadius = 55.dp.toPx()
                    val r1 = (20.dp.toPx() + maxRadius * ((pulse + 0.0f) % 1f))
                    val r2 = (20.dp.toPx() + maxRadius * ((pulse + 0.38f) % 1f))
                    val r3 = (20.dp.toPx() + maxRadius * ((pulse + 0.72f) % 1f))

                    val a1 = (1f - ((pulse + 0.0f) % 1f)).coerceIn(0f, 1f) * 0.45f * entranceAlpha.value
                    val a2 = (1f - ((pulse + 0.38f) % 1f)).coerceIn(0f, 1f) * 0.45f * entranceAlpha.value
                    val a3 = (1f - ((pulse + 0.72f) % 1f)).coerceIn(0f, 1f) * 0.45f * entranceAlpha.value

                    drawCircle(
                        color = IbmBlueLight.copy(alpha = a1),
                        center = Offset(cx, cy),
                        radius = r1,
                        style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawCircle(
                        color = IbmBlue.copy(alpha = a2),
                        center = Offset(cx, cy),
                        radius = r2,
                        style = Stroke(width = 1.4.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawCircle(
                        color = IbmPurpleLight.copy(alpha = a3),
                        center = Offset(cx, cy),
                        radius = r3,
                        style = Stroke(width = 1.2.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Minimal Core Water Droplet
                    val dropPath = Path().apply {
                        moveTo(cx, cy - 14.dp.toPx())
                        cubicTo(
                            cx - 5.dp.toPx(), cy - 8.dp.toPx(),
                            cx - 10.dp.toPx(), cy - 1.dp.toPx(),
                            cx - 10.dp.toPx(), cy + 5.dp.toPx()
                        )
                        cubicTo(
                            cx - 10.dp.toPx(), cy + 11.dp.toPx(),
                            cx - 5.5.dp.toPx(), cy + 15.dp.toPx(),
                            cx, cy + 15.dp.toPx()
                        )
                        cubicTo(
                            cx + 5.5.dp.toPx(), cy + 15.dp.toPx(),
                            cx + 10.dp.toPx(), cy + 11.dp.toPx(),
                            cx + 10.dp.toPx(), cy + 5.dp.toPx()
                        )
                        cubicTo(
                            cx + 10.dp.toPx(), cy - 1.dp.toPx(),
                            cx + 5.dp.toPx(), cy - 8.dp.toPx(),
                            cx, cy - 14.dp.toPx()
                        )
                        close()
                    }

                    drawPath(
                        path = dropPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(IbmBlueLight, IbmBlue),
                            startY = cy - 14.dp.toPx(),
                            endY = cy + 15.dp.toPx()
                        ),
                        alpha = entranceAlpha.value
                    )

                    // Specular Highlight
                    drawCircle(
                        color = Color.White.copy(alpha = 0.85f * entranceAlpha.value),
                        center = Offset(cx - 2.5.dp.toPx(), cy + 2.dp.toPx()),
                        radius = 2.2.dp.toPx()
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Minimalist Brand Typography
            Text(
                text = "R I P P L E",
                fontSize = 24.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = 6.sp,
                color = RippleColors.textPrimary.copy(alpha = entranceAlpha.value)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "SOMATIC GROUNDING & ANXIETY RELIEF",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 2.2.sp,
                color = IbmBlueLight.copy(alpha = 0.90f * entranceAlpha.value)
            )
        }

        // Clean Vayu Labs Attribution at Bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp)
        ) {
            Text(
                text = "V A Y U   L A B S",
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 3.sp,
                color = RippleColors.textSecondary.copy(alpha = 0.65f * entranceAlpha.value)
            )
        }
    }
}
