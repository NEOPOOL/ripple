package com.vayulabs.ripple.presentation.sensory

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vayulabs.ripple.R
import com.vayulabs.ripple.audio.ZenSandAudioEngine
import com.vayulabs.ripple.haptics.HapticEngine
import com.vayulabs.ripple.presentation.components.AppleLargeHeader
import com.vayulabs.ripple.presentation.components.IbmBlue
import com.vayulabs.ripple.presentation.components.IbmBlueLight
import com.vayulabs.ripple.theme.RippleColors
import com.vayulabs.ripple.theme.ThemeState
import kotlinx.coroutines.launch
import kotlin.math.sqrt

enum class ZenRakeType(val displayName: String, val tines: Int, val spacingDp: Float) {
    STYLUS("Fine Stylus", 1, 0f),
    STANDARD_3("3-Prong Rake", 3, 12f),
    COMB_5("5-Prong Comb", 5, 8.5f)
}

data class SandStroke(
    val points: List<Offset>,
    val rakeType: ZenRakeType
)

@Composable
fun ZenSandScreen(
    hapticEngine: HapticEngine,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val isDark = ThemeState.isDarkMode.value
    val sandAudio = remember { ZenSandAudioEngine(context) }

    DisposableEffect(Unit) {
        onDispose {
            sandAudio.release()
        }
    }

    val strokes = remember { mutableStateListOf<SandStroke>() }
    var currentPoints by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var selectedRake by remember { mutableStateOf(ZenRakeType.STANDARD_3) }

    var accumulatedAudioDist by remember { mutableFloatStateOf(0f) }
    var accumulatedHapticDist by remember { mutableFloatStateOf(0f) }
    var totalRakedDistance by remember { mutableFloatStateOf(0f) }

    // Smooth breeze dissolve animation when leveling sand
    val breezeAnim = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    fun levelSand() {
        scope.launch {
            breezeAnim.snapTo(0f)
            hapticEngine.triggerStoneFriction()
            sandAudio.playBreeze()
            breezeAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(750, easing = FastOutSlowInEasing)
            )
            strokes.clear()
            currentPoints = emptyList()
            totalRakedDistance = 0f
            breezeAnim.snapTo(0f)
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Top Navigation Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.Start,
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
                }

                // Apple/IBM Large Header with Level Sand Action
                AppleLargeHeader(
                    eyebrow = "Tactile Grounding",
                    title = "Zen Sand Garden",
                    extraAction = {
                        Surface(
                            shape = CircleShape,
                            color = if (isDark) Color(0x26FFFFFF) else Color(0x14000000),
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { levelSand() },
                            border = BorderStroke(0.5.dp, RippleColors.cardBorder)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Refresh,
                                    contentDescription = "Level Sand",
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
                        text = if (totalRakedDistance > 50f) "MINDFUL RAKE PATTERNS • FOCUSED INTENTION" else "DRAG SLOWLY TO RAKE NATURAL SAND",
                        color = IbmBlueLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 2.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp)
                    )
                }
            }

            // Real Sand Garden Tray Frame
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(if (isDark) Color(0xFF0F1218) else Color(0xFFDCD2C0))
            ) {
                // Layer 1: Photorealistic Zen Sand Texture Base
                Image(
                    painter = painterResource(id = R.drawable.zen_sand_texture),
                    contentDescription = "Natural Zen Garden Sand",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Dark mode slate/volcanic sand overlay filter
                if (isDark) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF0C0F17).copy(alpha = 0.58f))
                    )
                }

                // Layer 2: Traditional Karesansui River Stones with concentric sand ripples
                // Primary Tumbled Basalt River Stone (Top Right)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 36.dp, end = 26.dp)
                        .size(92.dp, 96.dp)
                ) {
                    // Concentric sand wave ripples carved around the rock
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val cx = size.width / 2f
                        val cy = size.height / 2f
                        val rippleColorShadow = if (isDark) Color(0x55000000) else Color(0x35000000)
                        val rippleColorLight = if (isDark) Color(0x25FFFFFF) else Color(0x45FFFFFF)

                        listOf(48.dp.toPx(), 58.dp.toPx()).forEach { r ->
                            drawCircle(
                                color = rippleColorShadow,
                                radius = r,
                                center = Offset(cx, cy + 1.5f),
                                style = Stroke(width = 2.8.dp.toPx())
                            )
                            drawCircle(
                                color = rippleColorLight,
                                radius = r,
                                center = Offset(cx, cy - 1f),
                                style = Stroke(width = 1.2.dp.toPx())
                            )
                        }

                        // Ambient contact occlusion shadow under the stone
                        drawOval(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0x90000000), Color.Transparent),
                                center = Offset(cx, cy + 8f),
                                radius = size.width * 0.48f
                            ),
                            topLeft = Offset(cx - size.width * 0.45f, cy - size.height * 0.25f),
                            size = Size(size.width * 0.90f, size.height * 0.65f)
                        )
                    }

                    Image(
                        painter = painterResource(id = R.drawable.real_worry_stone),
                        contentDescription = "Basalt Zen River Stone",
                        modifier = Modifier
                            .fillMaxSize()
                            .rotate(15f),
                        contentScale = ContentScale.Fit
                    )
                }

                // Companion River Pebble (Bottom Left)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(bottom = 44.dp, start = 30.dp)
                        .size(54.dp, 58.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val cx = size.width / 2f
                        val cy = size.height / 2f
                        drawCircle(
                            color = if (isDark) Color(0x45000000) else Color(0x28000000),
                            radius = 32.dp.toPx(),
                            center = Offset(cx, cy + 1f),
                            style = Stroke(width = 2.dp.toPx())
                        )
                        drawOval(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0x75000000), Color.Transparent),
                                center = Offset(cx, cy + 4f),
                                radius = size.width * 0.45f
                            ),
                            topLeft = Offset(cx - size.width * 0.40f, cy - size.height * 0.20f),
                            size = Size(size.width * 0.80f, size.height * 0.55f)
                        )
                    }

                    Image(
                        painter = painterResource(id = R.drawable.real_worry_stone),
                        contentDescription = "Companion Zen Pebble",
                        modifier = Modifier
                            .fillMaxSize()
                            .rotate(-70f),
                        contentScale = ContentScale.Fit
                    )
                }

                // Layer 3: Interactive Multi-Prong Sand Rake Gesture Detector & Canvas
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(selectedRake) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    currentPoints = listOf(offset)
                                    hapticEngine.triggerStoneFriction()
                                    sandAudio.playRakeScrape(0.8f)
                                },
                                onDragEnd = {
                                    if (currentPoints.size >= 2) {
                                        strokes.add(SandStroke(currentPoints, selectedRake))
                                    }
                                    currentPoints = emptyList()
                                },
                                onDragCancel = {
                                    if (currentPoints.size >= 2) {
                                        strokes.add(SandStroke(currentPoints, selectedRake))
                                    }
                                    currentPoints = emptyList()
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    val newPoint = change.position
                                    currentPoints = currentPoints + newPoint

                                    val dist = sqrt(dragAmount.x * dragAmount.x + dragAmount.y * dragAmount.y)
                                    accumulatedAudioDist += dist
                                    accumulatedHapticDist += dist
                                    totalRakedDistance += dist

                                    // Real acoustic sand raking friction sound (procedural audio)
                                    if (accumulatedAudioDist > 16f) {
                                        accumulatedAudioDist = 0f
                                        val speedFactor = (dist / 14f).coerceIn(0.6f, 1.8f)
                                        sandAudio.playRakeScrape(speedFactor)
                                    }

                                    // Granular haptic feedback matching the sand grain friction
                                    if (accumulatedHapticDist > 22f) {
                                        accumulatedHapticDist = 0f
                                        hapticEngine.triggerStoneFriction()
                                    }
                                }
                            )
                        }
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val fadeAlpha = (1f - breezeAnim.value).coerceIn(0f, 1f)
                        val density = this

                        // Helper function to render a multi-prong sand stroke with parallel tines
                        fun renderSandStroke(stroke: SandStroke) {
                            val pts = stroke.points
                            if (pts.size < 2) return

                            val tineCount = stroke.rakeType.tines
                            val spacingPx = with(density) { stroke.rakeType.spacingDp.dp.toPx() }

                            // Generate paths for each tine in the rake
                            for (tineIdx in 0 until tineCount) {
                                val tineOffsetFrac = if (tineCount > 1) {
                                    tineIdx - (tineCount - 1) / 2.0f
                                } else 0f

                                val shadowPath = Path()
                                val troughPath = Path()
                                val crestPath = Path()

                                for (i in pts.indices) {
                                    val cur = pts[i]
                                    // Tangent vector
                                    val tangent = when {
                                        i == 0 -> pts[1] - cur
                                        i == pts.size - 1 -> cur - pts[i - 1]
                                        else -> pts[i + 1] - pts[i - 1]
                                    }
                                    val len = sqrt(tangent.x * tangent.x + tangent.y * tangent.y)
                                    val normal = if (len > 0.001f) {
                                        Offset(-tangent.y / len, tangent.x / len)
                                    } else Offset(0f, 1f)

                                    val tinePos = cur + normal * (tineOffsetFrac * spacingPx)

                                    if (i == 0) {
                                        shadowPath.moveTo(tinePos.x, tinePos.y + 2.5f)
                                        troughPath.moveTo(tinePos.x, tinePos.y)
                                        crestPath.moveTo(tinePos.x - 1.2f, tinePos.y - 1.8f)
                                    } else {
                                        shadowPath.lineTo(tinePos.x, tinePos.y + 2.5f)
                                        troughPath.lineTo(tinePos.x, tinePos.y)
                                        crestPath.lineTo(tinePos.x - 1.2f, tinePos.y - 1.8f)
                                    }
                                }

                                val strokeWidthBase = when (stroke.rakeType) {
                                    ZenRakeType.STYLUS -> with(density) { 7.5.dp.toPx() }
                                    ZenRakeType.STANDARD_3 -> with(density) { 5.2.dp.toPx() }
                                    ZenRakeType.COMB_5 -> with(density) { 3.8.dp.toPx() }
                                }

                                // 1. Soft Penumbra Drop Shadow (Carved sand trough depth)
                                drawPath(
                                    path = shadowPath,
                                    color = (if (isDark) Color(0x65000000) else Color(0x403B3020)).copy(alpha = fadeAlpha * 0.85f),
                                    style = Stroke(
                                        width = strokeWidthBase * 1.35f,
                                        cap = StrokeCap.Round,
                                        join = StrokeJoin.Round
                                    )
                                )

                                // 2. Sand Furrow Depression (Ambient occlusion trough bottom)
                                drawPath(
                                    path = troughPath,
                                    color = (if (isDark) Color(0x90000000) else Color(0x552C2216)).copy(alpha = fadeAlpha * 0.90f),
                                    style = Stroke(
                                        width = strokeWidthBase * 0.70f,
                                        cap = StrokeCap.Round,
                                        join = StrokeJoin.Round
                                    )
                                )

                                // 3. Displaced Sand Grain Ridge (Gentle ridge highlight)
                                drawPath(
                                    path = crestPath,
                                    color = (if (isDark) Color(0x18FFFFFF) else Color(0x35FFFFFF)).copy(alpha = fadeAlpha * 0.80f),
                                    style = Stroke(
                                        width = strokeWidthBase * 0.40f,
                                        cap = StrokeCap.Round,
                                        join = StrokeJoin.Round
                                    )
                                )
                            }
                        }

                        // Render all saved strokes
                        strokes.forEach { stroke ->
                            renderSandStroke(stroke)
                        }

                        // Render active drawing stroke
                        if (currentPoints.isNotEmpty()) {
                            renderSandStroke(SandStroke(currentPoints, selectedRake))
                        }

                        // Wind Breeze sweep overlay when leveling sand
                        if (breezeAnim.value > 0.005f) {
                            val progress = breezeAnim.value
                            val sweepX = size.width * progress
                            drawRect(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        (if (isDark) IbmBlueLight else Color.White).copy(alpha = 0.40f * (1f - progress)),
                                        Color.Transparent
                                    ),
                                    startX = sweepX - 100.dp.toPx(),
                                    endX = sweepX + 100.dp.toPx()
                                )
                            )
                        }
                    }
                }

                // Inner Sand Tray Frame Border (Subtle carved tray border)
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRoundRect(
                        color = (if (isDark) Color(0x40FFFFFF) else Color(0x30000000)),
                        style = Stroke(width = 1.2.dp.toPx()),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(24.dp.toPx())
                    )
                }
            }

            // Bottom Rake Tool Selector Pills (Stylus, 3-Prong Rake, 5-Prong Comb)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ZenRakeType.entries.forEach { rake ->
                    val isSelected = selectedRake == rake
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) {
                            IbmBlueLight.copy(alpha = if (isDark) 0.24f else 0.16f)
                        } else {
                            if (isDark) Color(0x16FFFFFF) else Color(0x0C000000)
                        },
                        border = BorderStroke(
                            0.8.dp,
                            if (isSelected) IbmBlueLight else RippleColors.cardBorder
                        ),
                        modifier = Modifier.clickable {
                            selectedRake = rake
                            hapticEngine.triggerBubblePop()
                        }
                    ) {
                        Text(
                            text = rake.displayName,
                            color = if (isSelected) IbmBlueLight else RippleColors.textSecondary,
                            fontSize = 11.5.sp,
                            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Light,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            // Somatic Instruction Footer
            Text(
                text = "Carving slow, deliberate sand furrows decants sympathetic arousal into tactile spatial presence.",
                color = RippleColors.textSecondary,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center,
                letterSpacing = 0.3.sp,
                modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 8.dp)
            )
        }
    }
}
