package com.vayulabs.ripple.presentation.sensory

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vayulabs.ripple.R
import com.vayulabs.ripple.haptics.HapticEngine
import com.vayulabs.ripple.presentation.components.AppleLargeHeader
import com.vayulabs.ripple.presentation.components.IbmBlue
import com.vayulabs.ripple.presentation.components.IbmBlueLight
import com.vayulabs.ripple.presentation.components.IbmPurpleLight
import com.vayulabs.ripple.theme.*
import kotlin.math.sqrt

@Composable
fun WorryStoneScreen(
    hapticEngine: HapticEngine,
    onNavigateBack: () -> Unit
) {
    var rubCount by remember { mutableStateOf(0) }
    var isTouching by remember { mutableStateOf(false) }
    var accumulatedDistance by remember { mutableStateOf(0f) }
    var touchPosition by remember { mutableStateOf<Offset?>(null) }
    val isDark = ThemeState.isDarkMode.value

    // Elastic physical spring depression when thumb presses onto stone
    val pressScale by animateFloatAsState(
        targetValue = if (isTouching) 0.965f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "StonePressSpring"
    )

    // Ambient friction warmth animation
    val warmthAlpha by animateFloatAsState(
        targetValue = if (isTouching) 0.50f else 0.0f,
        animationSpec = tween(220),
        label = "WarmthAlpha"
    )

    // Friction ripple wave phase
    val infiniteTransition = rememberInfiniteTransition(label = "FrictionRipple")
    val ripplePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RipplePhase"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Top Navigation Bar with Back Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (isDark) Color(0x26FFFFFF) else Color(0x14000000),
                        modifier = Modifier
                            .size(38.dp)
                            .clickable(onClick = onNavigateBack),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, RippleColors.cardBorder)
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
                }

                // Clean IBM Header with Reset Button & Single Theme Toggle
                AppleLargeHeader(
                    eyebrow = "Tactile Grounding",
                    title = "Tactile Stone",
                    extraAction = {
                        Surface(
                            shape = CircleShape,
                            color = if (isDark) Color(0x26FFFFFF) else Color(0x14000000),
                            modifier = Modifier
                                .size(36.dp)
                                .clickable {
                                    rubCount = 0
                                    hapticEngine.triggerStoneFriction()
                                },
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, RippleColors.cardBorder)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Refresh,
                                    contentDescription = "Reset Count",
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
                    border = androidx.compose.foundation.BorderStroke(
                        0.8.dp,
                        IbmBlueLight.copy(alpha = if (isDark) 0.45f else 0.35f)
                    ),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (rubCount > 0) "$rubCount GROUNDING STROKES • ANCHORING" else "REST THUMB & RUB SMOOTHLY",
                        color = IbmBlueLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 2.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp)
                    )
                }
            }

            // Real Beach Pebble / Zen Worry Stone with Interactive Haptic Detection
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(310.dp, 330.dp)
                        .scale(pressScale)
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    isTouching = true
                                    touchPosition = offset
                                    hapticEngine.triggerStoneFriction()
                                },
                                onDragEnd = {
                                    isTouching = false
                                    touchPosition = null
                                },
                                onDragCancel = {
                                    isTouching = false
                                    touchPosition = null
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    touchPosition = change.position
                                    val dist = sqrt(dragAmount.x * dragAmount.x + dragAmount.y * dragAmount.y)
                                    accumulatedDistance += dist

                                    // Trigger realistic micro-friction haptics as thumb moves
                                    if (accumulatedDistance > 24f) {
                                        accumulatedDistance = 0f
                                        rubCount++
                                        hapticEngine.triggerStoneFriction()
                                    }
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // Dynamic ambient contact shadow
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawOval(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    (if (isDark) Color(0x75000000) else Color(0x30000000)),
                                    Color.Transparent
                                ),
                                center = Offset(size.width * 0.5f, size.height * 0.54f),
                                radius = size.width * 0.44f
                            ),
                            topLeft = Offset(size.width * 0.10f, size.height * 0.44f),
                            size = Size(size.width * 0.80f, size.height * 0.38f)
                        )
                    }

                    // Photographic Real Basalt Worry Stone Image
                    Image(
                        painter = painterResource(id = R.drawable.real_worry_stone),
                        contentDescription = "Real Tumbled Basalt Worry Stone",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )

                    // Interactive Friction Glow & Radiating Tactile Waves
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val cx = size.width / 2f
                        val cy = size.height * 0.48f

                        if (warmthAlpha > 0.01f) {
                            val activePos = touchPosition ?: Offset(cx, cy)

                            // Radiating friction wave
                            val maxRadius = 75.dp.toPx()
                            val curRadius = 25.dp.toPx() + maxRadius * ripplePhase
                            drawCircle(
                                color = IbmBlueLight.copy(alpha = warmthAlpha * (1f - ripplePhase) * 0.55f),
                                center = activePos,
                                radius = curRadius,
                                style = Stroke(width = 1.6.dp.toPx())
                            )

                            // Warm bio-feedback glow beneath contact point
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        IbmBlueLight.copy(alpha = warmthAlpha * 0.45f),
                                        IbmPurpleLight.copy(alpha = warmthAlpha * 0.15f),
                                        Color.Transparent
                                    ),
                                    center = activePos,
                                    radius = 70.dp.toPx()
                                ),
                                center = activePos,
                                radius = 70.dp.toPx()
                            )
                        }
                    }
                }
            }

            // Somatic Grounding Prompt
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp)
            ) {
                Text(
                    text = "Slow, repetitive strokes on the smooth stone surface stimulate slow-adapting Merkel mechanoreceptors, sending steadying parasympathetic impulses along the median nerve.",
                    color = RippleColors.textSecondary,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.3.sp
                )
            }
        }
    }
}
