package com.vayulabs.ripple.presentation.sensory

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vayulabs.ripple.audio.SingingBowlAudioEngine
import com.vayulabs.ripple.haptics.HapticEngine
import com.vayulabs.ripple.presentation.components.AppleLargeHeader
import com.vayulabs.ripple.presentation.components.IbmBlue
import com.vayulabs.ripple.presentation.components.IbmBlueLight
import com.vayulabs.ripple.presentation.components.IbmPurpleLight
import com.vayulabs.ripple.theme.RippleColors
import com.vayulabs.ripple.theme.ThemeState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun SingingBowlScreen(
    hapticEngine: HapticEngine,
    onNavigateBack: () -> Unit
) {
    val isDark = ThemeState.isDarkMode.value
    val context = LocalContext.current
    val soundEngine = remember { SingingBowlAudioEngine(context.applicationContext) }

    DisposableEffect(Unit) {
        onDispose {
            soundEngine.release()
        }
    }

    var lastAngle by remember { mutableFloatStateOf(0f) }
    var rimIntensity by remember { mutableFloatStateOf(0f) }
    var strikeWaveProgress by remember { mutableFloatStateOf(1f) }
    var totalRotations by remember { mutableIntStateOf(0) }
    var accumulatedAngle by remember { mutableFloatStateOf(0f) }
    var isTracingRim by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    // Smooth decay of rim resonance when user stops tracing
    LaunchedEffect(isTracingRim) {
        if (!isTracingRim) {
            while (rimIntensity > 0.01f) {
                rimIntensity *= 0.88f
                soundEngine.setRimIntensity(rimIntensity)
                delay(35)
            }
            rimIntensity = 0f
            soundEngine.setRimIntensity(0f)
        }
    }

    // Strike pulse animation
    val strikeAnim = remember { Animatable(0f) }
    fun strikeBowl() {
        soundEngine.playStrike()
        hapticEngine.triggerBubblePop()
        scope.launch {
            strikeAnim.snapTo(0f)
            strikeAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(1200, easing = LinearOutSlowInEasing)
            )
        }
    }

    // Concentric resonant wave phase
    val infiniteTransition = rememberInfiniteTransition(label = "ResonantWave")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WavePhase"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Top Navigation Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (isDark) Color(0x26FFFFFF) else Color(0x14000000),
                        modifier = Modifier
                            .size(38.dp)
                            .clickable(onClick = onNavigateBack),
                        border = BorderStroke(0.5.dp, RippleColors.cardBorder)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = RippleColors.textPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Strike Bell Button
                    Surface(
                        shape = CircleShape,
                        color = if (isDark) Color(0x26FFFFFF) else Color(0x14000000),
                        modifier = Modifier
                            .size(38.dp)
                            .clickable { strikeBowl() },
                        border = BorderStroke(0.5.dp, RippleColors.cardBorder)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Strike Bowl",
                                tint = RippleColors.textPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Apple/IBM Large Header with Theme Toggle
                AppleLargeHeader(
                    eyebrow = "Acoustic Resonance",
                    title = "Singing Bowl",
                    extraAction = {
                        Surface(
                            shape = CircleShape,
                            color = if (isDark) Color(0x26FFFFFF) else Color(0x14000000),
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { strikeBowl() },
                            border = BorderStroke(0.5.dp, RippleColors.cardBorder)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Refresh,
                                    contentDescription = "Strike Bowl",
                                    tint = RippleColors.textPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                )

                // Subtitle Instruction Pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = IbmBlue.copy(alpha = if (isDark) 0.16f else 0.10f),
                    border = BorderStroke(
                        0.8.dp,
                        IbmBlueLight.copy(alpha = if (isDark) 0.45f else 0.35f)
                    ),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = when {
                            rimIntensity > 0.35f -> "432 HZ HARMONIC RESONANCE • PEAK WARMTH"
                            totalRotations > 0 -> "$totalRotations ORBITS • STEADY VELOCITY"
                            else -> "TRACE RIM CIRCLE OR TAP CENTER TO STRIKE"
                        },
                        color = IbmBlueLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 2.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp)
                    )
                }
            }

            // Interactive Singing Bowl Visualization
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(310.dp)
                        .pointerInput(Unit) {
                            detectTapGestures {
                                strikeBowl()
                            }
                        }
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    isTracingRim = true
                                    val cx = 155.dp.toPx()
                                    val cy = 155.dp.toPx()
                                    lastAngle = atan2(offset.y - cy, offset.x - cx)
                                },
                                onDragEnd = {
                                    isTracingRim = false
                                },
                                onDragCancel = {
                                    isTracingRim = false
                                },
                                onDrag = { change, _ ->
                                    change.consume()
                                    val pos = change.position
                                    val cx = 155.dp.toPx()
                                    val cy = 155.dp.toPx()
                                    val curAngle = atan2(pos.y - cy, pos.x - cx)

                                    var delta = curAngle - lastAngle
                                    if (delta > PI) delta -= (2 * PI).toFloat()
                                    if (delta < -PI) delta += (2 * PI).toFloat()
                                    lastAngle = curAngle

                                    val absDelta = kotlin.math.abs(delta)
                                    accumulatedAngle += absDelta
                                    totalRotations = (accumulatedAngle / (2 * PI).toFloat()).toInt()

                                    // Build resonance intensity
                                    rimIntensity = (rimIntensity + absDelta * 0.18f).coerceIn(0f, 1f)
                                    soundEngine.setRimIntensity(rimIntensity)

                                    // Granular rotational haptic click
                                    if (absDelta > 0.18f) {
                                        hapticEngine.triggerStoneFriction()
                                    }
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val cx = size.width / 2f
                        val cy = size.height / 2f
                        val bowlRadius = size.width * 0.40f

                        // 1. Ambient Contact Shadow
                        drawOval(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    if (isDark) Color(0x65000000) else Color(0x28000000),
                                    Color.Transparent
                                ),
                                center = Offset(cx, cy + bowlRadius * 0.15f),
                                radius = bowlRadius * 1.15f
                            ),
                            topLeft = Offset(cx - bowlRadius * 1.1f, cy - bowlRadius * 0.95f),
                            size = Size(bowlRadius * 2.2f, bowlRadius * 2.2f)
                        )

                        // 2. Active Resonant Sonic Wave Ripples
                        val totalIntensity = (rimIntensity + (1f - strikeAnim.value)).coerceIn(0f, 1f)
                        if (totalIntensity > 0.05f) {
                            val maxWave = bowlRadius * 1.35f
                            val curWave = bowlRadius + (maxWave - bowlRadius) * wavePhase
                            val waveAlpha = totalIntensity * (1f - wavePhase) * 0.55f

                            drawCircle(
                                color = IbmBlueLight.copy(alpha = waveAlpha),
                                center = Offset(cx, cy),
                                radius = curWave,
                                style = Stroke(width = 1.6.dp.toPx())
                            )
                        }

                        // 3. Bronze Singing Bowl Outer Rim
                        val bronzeColors = if (isDark) {
                            listOf(Color(0xFF8A6D3B), Color(0xFFC4A462), Color(0xFF5E4924), Color(0xFF9E8047))
                        } else {
                            listOf(Color(0xFFB59357), Color(0xFFDEC388), Color(0xFF8C6E38), Color(0xFFCDB175))
                        }

                        drawCircle(
                            brush = Brush.sweepGradient(bronzeColors, center = Offset(cx, cy)),
                            radius = bowlRadius,
                            center = Offset(cx, cy)
                        )

                        // 4. Polished Bronze Beveled Lip (Highlight Ring)
                        drawCircle(
                            color = Color(0xFFFFF2D4).copy(alpha = if (isDark) 0.50f else 0.70f),
                            radius = bowlRadius * 0.98f,
                            center = Offset(cx, cy),
                            style = Stroke(width = 2.dp.toPx())
                        )

                        // 5. Deep Resonant Inner Basin (Dark Cavity with Copper Depth)
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    if (isDark) Color(0xFF0F0C08) else Color(0xFF332714),
                                    if (isDark) Color(0xFF261D10) else Color(0xFF5C4726),
                                    if (isDark) Color(0xFF4A3A22) else Color(0xFF7A6034)
                                ),
                                center = Offset(cx - bowlRadius * 0.1f, cy - bowlRadius * 0.1f),
                                radius = bowlRadius * 0.88f
                            ),
                            radius = bowlRadius * 0.88f,
                            center = Offset(cx, cy)
                        )

                        // 6. Concentric Hand-Hammered Ring Engravings
                        listOf(0.70f, 0.52f, 0.35f, 0.18f).forEach { scale ->
                            drawCircle(
                                color = Color(0x35DFC593),
                                radius = bowlRadius * scale,
                                center = Offset(cx, cy),
                                style = Stroke(width = 1.1.dp.toPx())
                            )
                        }

                        // 7. Center Meditative Lotus Point (Mallet Strike Target)
                        drawCircle(
                            color = IbmBlueLight.copy(alpha = 0.45f + 0.35f * rimIntensity),
                            radius = 14.dp.toPx() * (1f + 0.12f * sin(wavePhase * 2 * PI.toFloat())),
                            center = Offset(cx, cy)
                        )
                        drawCircle(
                            color = Color.White.copy(alpha = 0.80f),
                            radius = 4.dp.toPx(),
                            center = Offset(cx, cy)
                        )
                    }
                }
            }

            // Somatic Instruction Footer
            Text(
                text = "Continuous circular tracing engages bilateral motor coordination while 432 Hz acoustic entrainment slows sympathetic fight-or-flight arousal.",
                color = RippleColors.textSecondary,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center,
                letterSpacing = 0.3.sp,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }
    }
}
