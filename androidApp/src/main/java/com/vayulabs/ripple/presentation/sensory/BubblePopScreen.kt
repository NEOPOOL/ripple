package com.vayulabs.ripple.presentation.sensory

import androidx.compose.animation.core.*
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vayulabs.ripple.audio.BubbleSoundEngine
import com.vayulabs.ripple.haptics.HapticEngine
import com.vayulabs.ripple.presentation.components.AppleLargeHeader
import com.vayulabs.ripple.presentation.components.IbmBlue
import com.vayulabs.ripple.presentation.components.IbmBlueLight
import com.vayulabs.ripple.presentation.components.IbmPurple
import com.vayulabs.ripple.presentation.components.IbmPurpleLight
import com.vayulabs.ripple.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Natural organic floating bubble model with randomized size, position, and micro-current drift.
 */
data class NaturalBubble(
    val id: Int,
    val relX: Float,
    val relY: Float,
    val radiusDp: Float,
    val floatSpeed: Float,
    val floatOffset: Float
)

@Composable
fun BubblePopScreen(
    hapticEngine: HapticEngine,
    onNavigateBack: () -> Unit
) {
    // 30 Organically scattered natural bubbles with varied radii (20dp to 54dp)
    val bubbles = remember {
        listOf(
            NaturalBubble(id = 0, relX = 0.174f, relY = 0.717f, radiusDp = 28f, floatSpeed = 1.0f, floatOffset = 0.0f),
            NaturalBubble(id = 1, relX = 0.699f, relY = 0.659f, radiusDp = 26f, floatSpeed = 1.51f, floatOffset = 0.47f),
            NaturalBubble(id = 2, relX = 0.576f, relY = 0.079f, radiusDp = 24f, floatSpeed = 0.87f, floatOffset = 0.94f),
            NaturalBubble(id = 3, relX = 0.265f, relY = 0.58f, radiusDp = 42f, floatSpeed = 1.45f, floatOffset = 1.41f),
            NaturalBubble(id = 4, relX = 0.717f, relY = 0.194f, radiusDp = 22f, floatSpeed = 1.14f, floatOffset = 1.88f),
            NaturalBubble(id = 5, relX = 0.211f, relY = 0.911f, radiusDp = 34f, floatSpeed = 1.07f, floatOffset = 2.35f),
            NaturalBubble(id = 6, relX = 0.399f, relY = 0.373f, radiusDp = 24f, floatSpeed = 1.08f, floatOffset = 2.82f),
            NaturalBubble(id = 7, relX = 0.544f, relY = 0.796f, radiusDp = 24f, floatSpeed = 1.29f, floatOffset = 3.29f),
            NaturalBubble(id = 8, relX = 0.384f, relY = 0.223f, radiusDp = 52f, floatSpeed = 0.86f, floatOffset = 3.76f),
            NaturalBubble(id = 9, relX = 0.798f, relY = 0.83f, radiusDp = 24f, floatSpeed = 1.1f, floatOffset = 4.23f),
            NaturalBubble(id = 10, relX = 0.614f, relY = 0.378f, radiusDp = 48f, floatSpeed = 1.1f, floatOffset = 4.7f),
            NaturalBubble(id = 11, relX = 0.816f, relY = 0.333f, radiusDp = 20f, floatSpeed = 1.32f, floatOffset = 5.17f),
            NaturalBubble(id = 12, relX = 0.918f, relY = 0.174f, radiusDp = 30f, floatSpeed = 1.19f, floatOffset = 5.64f),
            NaturalBubble(id = 13, relX = 0.12f, relY = 0.149f, radiusDp = 44f, floatSpeed = 1.3f, floatOffset = 6.11f),
            NaturalBubble(id = 14, relX = 0.401f, relY = 0.947f, radiusDp = 42f, floatSpeed = 1.22f, floatOffset = 6.58f),
            NaturalBubble(id = 15, relX = 0.803f, relY = 0.06f, radiusDp = 54f, floatSpeed = 1.38f, floatOffset = 7.05f),
            NaturalBubble(id = 16, relX = 0.823f, relY = 0.726f, radiusDp = 28f, floatSpeed = 1.41f, floatOffset = 7.52f),
            NaturalBubble(id = 17, relX = 0.174f, relY = 0.441f, radiusDp = 38f, floatSpeed = 1.16f, floatOffset = 7.99f),
            NaturalBubble(id = 18, relX = 0.721f, relY = 0.535f, radiusDp = 38f, floatSpeed = 1.42f, floatOffset = 8.46f),
            NaturalBubble(id = 19, relX = 0.695f, relY = 0.784f, radiusDp = 24f, floatSpeed = 1.58f, floatOffset = 8.93f),
            NaturalBubble(id = 20, relX = 0.564f, relY = 0.269f, radiusDp = 20f, floatSpeed = 1.18f, floatOffset = 9.4f),
            NaturalBubble(id = 21, relX = 0.421f, relY = 0.483f, radiusDp = 26f, floatSpeed = 1.49f, floatOffset = 9.87f),
            NaturalBubble(id = 22, relX = 0.218f, relY = 0.052f, radiusDp = 22f, floatSpeed = 1.11f, floatOffset = 10.34f),
            NaturalBubble(id = 23, relX = 0.884f, relY = 0.946f, radiusDp = 36f, floatSpeed = 1.24f, floatOffset = 10.81f),
            NaturalBubble(id = 24, relX = 0.894f, relY = 0.571f, radiusDp = 30f, floatSpeed = 1.23f, floatOffset = 11.28f),
            NaturalBubble(id = 25, relX = 0.804f, relY = 0.413f, radiusDp = 28f, floatSpeed = 1.55f, floatOffset = 11.75f),
            NaturalBubble(id = 26, relX = 0.088f, relY = 0.609f, radiusDp = 24f, floatSpeed = 1.25f, floatOffset = 12.22f),
            NaturalBubble(id = 27, relX = 0.524f, relY = 0.651f, radiusDp = 28f, floatSpeed = 1.24f, floatOffset = 12.69f),
            NaturalBubble(id = 28, relX = 0.858f, relY = 0.263f, radiusDp = 26f, floatSpeed = 0.93f, floatOffset = 13.16f),
            NaturalBubble(id = 29, relX = 0.344f, relY = 0.813f, radiusDp = 20f, floatSpeed = 1.38f, floatOffset = 13.63f)
        )
    }

    val bubbleCount = bubbles.size
    val poppedStates = remember { mutableStateMapOf<Int, Boolean>() }
    val burstProgress = remember { bubbles.map { Animatable(0f) } }
    val scope = rememberCoroutineScope()
    var totalPopped by remember { mutableStateOf(0) }
    val isDark = ThemeState.isDarkMode.value
    val context = LocalContext.current

    // Synchronous acoustic bubble synthesizer
    val soundEngine = remember { BubbleSoundEngine(context.applicationContext) }
    DisposableEffect(Unit) {
        onDispose {
            soundEngine.release()
        }
    }

    // Dynamic runtime positions for touch/glide hit-testing
    val livePositions = remember { mutableStateMapOf<Int, Pair<Offset, Float>>() }

    // Ambient floating harmonic drift
    val infiniteTransition = rememberInfiniteTransition(label = "NaturalBubbleDrift")
    val driftTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2.0 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "DriftTime"
    )

    fun popBubble(bubble: NaturalBubble) {
        if (poppedStates[bubble.id] == true) return
        poppedStates[bubble.id] = true
        totalPopped++

        // Modulate pitch based on bubble size: smaller = higher crisp snap, larger = deeper pop
        val pitchModifier = 36f / bubble.radiusDp
        soundEngine.playPop(pitchModifier)
        hapticEngine.triggerBubblePop()

        // Trigger burst particle dispersion
        scope.launch {
            burstProgress[bubble.id].snapTo(0f)
            burstProgress[bubble.id].animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing)
            )
        }

        // Smooth organic spring reinflation after 2.4s
        scope.launch {
            delay(2400L)
            poppedStates[bubble.id] = false
            burstProgress[bubble.id].snapTo(0f)
        }
    }

    fun checkHit(pos: Offset) {
        bubbles.forEach { bubble ->
            val posData = livePositions[bubble.id]
            if (posData != null) {
                val (center, radius) = posData
                val dx = pos.x - center.x
                val dy = pos.y - center.y
                val dist = sqrt(dx * dx + dy * dy)
                if (dist <= radius * 1.15f) {
                    popBubble(bubble)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Clean Top Bar with Back Button
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

            // Big, Clean IBM Header with Reset Button & Single Theme Toggle
            AppleLargeHeader(
                eyebrow = "Tactile Grounding",
                title = "Tactile Bubbles",
                extraAction = {
                    Surface(
                        shape = CircleShape,
                        color = if (isDark) Color(0x26FFFFFF) else Color(0x14000000),
                        modifier = Modifier
                            .size(36.dp)
                            .clickable {
                                poppedStates.clear()
                                totalPopped = 0
                                hapticEngine.triggerStoneFriction()
                            },
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, RippleColors.cardBorder)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Reset Bubbles",
                                tint = RippleColors.textPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            )

            // Dynamic Counter Pill with IBM Blue Light Glow
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
                    text = "$totalPopped BURSTS • RELEASING TENSION",
                    color = IbmBlueLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Natural Organic Floating Meadow with Multi-Touch & Glide Sweeping
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .pointerInput(Unit) {
                        detectTapGestures { tapOffset ->
                            checkHit(tapOffset)
                        }
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { startOffset -> checkHit(startOffset) },
                            onDrag = { change, _ ->
                                change.consume()
                                checkHit(change.position)
                            }
                        )
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    bubbles.forEach { bubble ->
                        val isPopped = poppedStates[bubble.id] ?: false
                        val burst = burstProgress[bubble.id].value

                        // Gentle harmonic thermal drift
                        val driftX = 4.5.dp.toPx() * sin(driftTime * bubble.floatSpeed + bubble.floatOffset)
                        val driftY = 6.5.dp.toPx() * cos(driftTime * bubble.floatSpeed * 0.85f + bubble.floatOffset)

                        val cx = (bubble.relX * w) + driftX
                        val cy = (bubble.relY * h) + driftY
                        val baseRadius = bubble.radiusDp.dp.toPx()

                        // Calculate dynamic visual scale (spring inflate / pop disappear)
                        val bubbleScale = if (isPopped) 0f else 1f
                        val radius = baseRadius * bubbleScale
                        val center = Offset(cx, cy)

                        // Update live position for hit-testing
                        livePositions[bubble.id] = center to baseRadius

                        // 1. BURST DISPERSION (Shockwave ring & iridescent micro-droplets)
                        if (burst > 0.01f && burst < 0.99f) {
                            val shockRadius = baseRadius * (0.85f + burst * 0.75f)
                            val shockAlpha = (1f - burst).coerceIn(0f, 1f)

                            // Delicate expanding shockwave ring
                            drawCircle(
                                color = IbmBlueLight.copy(alpha = shockAlpha * 0.75f),
                                radius = shockRadius,
                                center = center,
                                style = Stroke(width = (1.5.dp * (1f - burst)).toPx())
                            )

                            // Radiating micro-droplets
                            val particleCount = 7
                            for (p in 0 until particleCount) {
                                val angle = (p.toFloat() * (2f * PI.toFloat() / particleCount)) + (bubble.id * 0.35f)
                                val dist = baseRadius * (0.70f + burst * 0.80f)
                                val dropletCenter = Offset(
                                    center.x + dist * cos(angle),
                                    center.y + dist * sin(angle)
                                )
                                val dropRadius = (2.2.dp * (1f - burst * 0.80f)).toPx()
                                drawCircle(
                                    color = if (p % 2 == 0) IbmBlueLight.copy(alpha = shockAlpha)
                                    else IbmPurpleLight.copy(alpha = shockAlpha),
                                    radius = dropRadius,
                                    center = dropletCenter
                                )
                            }
                        }

                        // 2. NATURAL AMBIENT & CAUSTIC DROP-SHADOW
                        if (bubbleScale > 0.04f) {
                            val shadowOffsetX = radius * 0.18f
                            val shadowOffsetY = radius * 0.28f
                            val shadowCenter = Offset(cx + shadowOffsetX, cy + shadowOffsetY)
                            val sWidth = radius * 2.05f
                            val sHeight = radius * 1.30f

                            // Soft Penumbra shadow
                            drawOval(
                                brush = Brush.radialGradient(
                                    colors = listOf<Color>(
                                        if (isDark) Color(0x55000000) else Color(0x24000000),
                                        Color.Transparent
                                    ),
                                    center = shadowCenter,
                                    radius = sWidth * 0.5f
                                ),
                                topLeft = Offset(shadowCenter.x - sWidth * 0.5f, shadowCenter.y - sHeight * 0.5f),
                                size = Size(sWidth, sHeight)
                            )

                            // Refractive Caustic Focal Point (light passes through the lens of the bubble)
                            if (!isDark) {
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.38f),
                                    center = shadowCenter,
                                    radius = radius * 0.22f
                                )
                            }
                        }

                        // 3. REFINED IRIDESCENT SOAP / GLASS BUBBLE
                        if (bubbleScale > 0.04f) {
                            // A. Volumetric 3D Membrane Body (Spherical falloff)
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf<Color>(
                                        if (isDark) IbmBlue.copy(alpha = 0.28f) else IbmBlueLight.copy(alpha = 0.20f),
                                        if (isDark) IbmPurple.copy(alpha = 0.16f) else IbmPurpleLight.copy(alpha = 0.12f),
                                        Color.Transparent
                                    ),
                                    center = Offset(center.x - radius * 0.16f, center.y - radius * 0.16f),
                                    radius = radius
                                ),
                                radius = radius,
                                center = center
                            )

                            // B. Internal Caustic Refraction Glow
                            drawCircle(
                                color = if (isDark) IbmBlueLight.copy(alpha = 0.14f) else IbmBlue.copy(alpha = 0.08f),
                                radius = radius * 0.82f,
                                center = center
                            )

                            // C. Thin-Film Iridescent Chromatic Rim
                            drawCircle(
                                brush = Brush.sweepGradient(
                                    colors = listOf(
                                        IbmBlueLight.copy(alpha = if (isDark) 0.85f else 0.70f),
                                        IbmPurpleLight.copy(alpha = if (isDark) 0.75f else 0.60f),
                                        Color.White.copy(alpha = if (isDark) 0.90f else 0.85f),
                                        Color(0xFF8CE8FF).copy(alpha = if (isDark) 0.80f else 0.65f),
                                        IbmBlueLight.copy(alpha = if (isDark) 0.85f else 0.70f)
                                    ),
                                    center = center
                                ),
                                radius = radius,
                                center = center,
                                style = Stroke(width = (1.2.dp).toPx())
                            )

                            // D. Primary Specular Crescent Glint (Upper-Left Reflection)
                            val glintCenter = Offset(
                                x = center.x - radius * 0.38f,
                                y = center.y - radius * 0.38f
                            )
                            rotate(degrees = -38f, pivot = glintCenter) {
                                drawOval(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            Color.White.copy(alpha = if (isDark) 0.94f else 0.96f),
                                            Color.White.copy(alpha = if (isDark) 0.40f else 0.50f),
                                            Color.Transparent
                                        ),
                                        center = glintCenter,
                                        radius = radius * 0.38f
                                    ),
                                    topLeft = Offset(
                                        glintCenter.x - radius * 0.35f,
                                        glintCenter.y - radius * 0.15f
                                    ),
                                    size = Size(radius * 0.70f, radius * 0.30f)
                                )
                            }

                            // E. Micro Sparkle Pin-Dot (Upper-Left Rim Highlight)
                            drawCircle(
                                color = Color.White.copy(alpha = if (isDark) 0.95f else 0.90f),
                                radius = radius * 0.08f,
                                center = Offset(
                                    center.x - radius * 0.18f,
                                    center.y - radius * 0.52f
                                )
                            )

                            // F. Secondary Ambient Ground Reflection (Lower-Right Crescent Arc)
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        IbmPurpleLight.copy(alpha = if (isDark) 0.42f else 0.32f),
                                        Color.Transparent
                                    ),
                                    center = Offset(
                                        center.x + radius * 0.32f,
                                        center.y + radius * 0.32f
                                    ),
                                    radius = radius * 0.45f
                                ),
                                radius = radius * 0.35f,
                                center = Offset(
                                    center.x + radius * 0.28f,
                                    center.y + radius * 0.28f
                                )
                            )
                        }
                    }
                }
            }

            // Somatic Instruction Footer with IBM Open Tracking
            Text(
                text = "Tap or sweep across to release somatic tension",
                color = RippleColors.textSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
            )
        }
    }
}
