package com.vayulabs.ripple.presentation.sensory

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vayulabs.ripple.R
import com.vayulabs.ripple.navigation.Screen
import com.vayulabs.ripple.presentation.components.AppleLargeHeader
import com.vayulabs.ripple.presentation.components.IbmBlue
import com.vayulabs.ripple.presentation.components.IbmBlueLight
import com.vayulabs.ripple.presentation.components.IbmBlueUltraLight
import com.vayulabs.ripple.presentation.components.IbmPurple
import com.vayulabs.ripple.presentation.components.IbmPurpleLight
import com.vayulabs.ripple.theme.*
import com.vayulabs.ripple.shared.domain.model.SensoryCatalog
import com.vayulabs.ripple.shared.domain.model.SensoryType
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SensoryHubScreen(
    onNavigateToSensory: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val isDark = ThemeState.isDarkMode.value
    val tools = remember { SensoryCatalog.tools }
    val pagerState = rememberPagerState(pageCount = { tools.size })

    // Continuous ambient morph phase for preview graphics
    val infiniteTransition = rememberInfiniteTransition(label = "SensoryPreviewMorph")
    val animPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2.0 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(7500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "AnimPhase"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // IBM Large Header with Glass & Theme Toggles
            AppleLargeHeader(
                eyebrow = "Tactile Grounding",
                title = "Sensory"
            )

            // Modern Somatic Eyebrow Pill
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = IbmBlue.copy(alpha = if (isDark) 0.16f else 0.10f),
                border = androidx.compose.foundation.BorderStroke(
                    0.8.dp,
                    IbmBlueLight.copy(alpha = if (isDark) 0.40f else 0.30f)
                ),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "3 ACTIVE SPACES • MORE COMING SOON",
                    color = IbmBlueLight,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Coherent Swipe-Based Carousel Deck
            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(horizontal = 24.dp),
                pageSpacing = 16.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(410.dp)
            ) { page ->
                val tool = tools[page]
                val destinationRoute = when (tool.type) {
                    SensoryType.FLUID_CANVAS -> Screen.FluidCanvas.route
                    SensoryType.WORRY_STONE -> Screen.WorryStone.route
                    SensoryType.BUBBLE_POP -> Screen.BubblePop.route
                    SensoryType.ZEN_SAND -> Screen.ZenSand.route
                    SensoryType.SINGING_BOWL -> Screen.SingingBowl.route
                    SensoryType.KALEIDOSCOPE -> Screen.Kaleidoscope.route
                    SensoryType.COLOR_SORT -> Screen.ColorSort.route
                }

                val tagLabel = when (tool.type) {
                    SensoryType.BUBBLE_POP -> "PLASTIC SNAP • ZERO DELAY"
                    SensoryType.FLUID_CANVAS -> "MULTI-TOUCH • GPU FLUID"
                    SensoryType.WORRY_STONE -> "HAPTIC FRICTION • GROUNDING"
                    SensoryType.ZEN_SAND -> "KINETIC RAKE • SPATIAL PRESENCE"
                    SensoryType.SINGING_BOWL -> "432 HZ RESONANCE • SOUND TUNING"
                    SensoryType.KALEIDOSCOPE -> "HARMONIC MANDALA • DRIFT"
                    SensoryType.COLOR_SORT -> "COGNITIVE SORT • SPECTRUM"
                }

                Surface(
                    onClick = {
                        if (tool.isEnabled) {
                            onNavigateToSensory(destinationRoute)
                        }
                    },
                    shape = RoundedCornerShape(26.dp),
                    color = RippleColors.surface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (pagerState.currentPage == page) {
                            if (tool.isEnabled) IbmBlueLight.copy(alpha = 0.55f) else IbmPurpleLight.copy(alpha = 0.35f)
                        } else RippleColors.cardBorder
                    ),
                    shadowElevation = if (isDark) 4.dp else 8.dp,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Interactive Artistic Visual Preview Canvas
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            if (isDark) Color(0x33101524) else Color(0x1F0F62FE),
                                            if (isDark) Color(0x1A141A2E) else Color(0x0A8A3FFC)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (tool.type == SensoryType.WORRY_STONE) {
                                Box(contentAlignment = Alignment.Center) {
                                    Canvas(modifier = Modifier.size(125.dp)) {
                                        drawOval(
                                            brush = Brush.radialGradient(
                                                colors = listOf(
                                                    (if (isDark) Color(0x60000000) else Color(0x24000000)),
                                                    Color.Transparent
                                                ),
                                                center = Offset(size.width * 0.5f, size.height * 0.54f),
                                                radius = size.width * 0.44f
                                            ),
                                            topLeft = Offset(size.width * 0.12f, size.height * 0.45f),
                                            size = Size(size.width * 0.76f, size.height * 0.36f)
                                        )
                                    }
                                    Image(
                                        painter = painterResource(id = R.drawable.real_worry_stone),
                                        contentDescription = "Real Basalt Stone",
                                        modifier = Modifier.size(125.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                    Canvas(modifier = Modifier.size(125.dp)) {
                                        val rRadius = 22.dp.toPx() * (1f + 0.35f * sin(animPhase * 1.5f))
                                        drawCircle(
                                            color = IbmBlueLight.copy(alpha = 0.40f),
                                            center = Offset(size.width / 2f, size.height * 0.48f),
                                            radius = rRadius,
                                            style = Stroke(width = 1.3.dp.toPx())
                                        )
                                    }
                                }
                            } else if (tool.type == SensoryType.ZEN_SAND) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    Image(
                                        painter = painterResource(id = R.drawable.zen_sand_texture),
                                        contentDescription = "Natural Zen Garden Sand",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    if (isDark) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(Color(0xFF0C0F17).copy(alpha = 0.55f))
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.CenterEnd)
                                            .padding(end = 24.dp)
                                            .size(46.dp)
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.real_worry_stone),
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Fit
                                        )
                                    }
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        val rakeCount = 4
                                        for (r in 0 until rakeCount) {
                                            val yOffset = (r - 1.5f) * 14.dp.toPx()
                                            val path = Path()
                                            path.moveTo(size.width * 0.12f, center.y + yOffset)
                                            path.cubicTo(
                                                size.width * 0.35f, center.y + yOffset + 7.dp.toPx() * sin(animPhase + r),
                                                size.width * 0.55f, center.y + yOffset - 7.dp.toPx() * sin(animPhase + r),
                                                size.width * 0.72f, center.y + yOffset
                                            )
                                            drawPath(
                                                path = path,
                                                color = if (isDark) Color(0x75000000) else Color(0x4042382A),
                                                style = Stroke(width = 4.5.dp.toPx(), cap = StrokeCap.Round)
                                            )
                                            drawPath(
                                                path = path,
                                                color = if (isDark) Color(0x35FFFFFF) else Color(0x60FFFFFF),
                                                style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round)
                                            )
                                        }
                                    }
                                }
                            } else {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val center = Offset(size.width / 2f, size.height / 2f)

                                when (tool.type) {
                                    SensoryType.BUBBLE_POP -> {
                                        // Preview: Cluster of delicate glass bubbles
                                        val bubbleCoords = listOf(
                                            Offset(-38.dp.toPx(), -16.dp.toPx()) to 28.dp.toPx(),
                                            Offset(34.dp.toPx(), -22.dp.toPx()) to 22.dp.toPx(),
                                            Offset(0.dp.toPx(), 20.dp.toPx()) to 34.dp.toPx(),
                                            Offset(-44.dp.toPx(), 26.dp.toPx()) to 18.dp.toPx(),
                                            Offset(46.dp.toPx(), 24.dp.toPx()) to 20.dp.toPx()
                                        )

                                        bubbleCoords.forEach { (offset, r) ->
                                            val bCenter = Offset(center.x + offset.x, center.y + offset.y)
                                            drawCircle(
                                                brush = Brush.radialGradient(
                                                    colors = listOf(
                                                        IbmBlueLight.copy(alpha = if (isDark) 0.35f else 0.25f),
                                                        IbmPurpleLight.copy(alpha = 0.15f),
                                                        Color.Transparent
                                                    ),
                                                    center = bCenter,
                                                    radius = r
                                                ),
                                                radius = r,
                                                center = bCenter
                                            )
                                            drawCircle(
                                                color = IbmBlueLight.copy(alpha = if (isDark) 0.75f else 0.60f),
                                                radius = r,
                                                center = bCenter,
                                                style = Stroke(width = 1.dp.toPx())
                                            )
                                            // Primary glint
                                            drawCircle(
                                                color = Color.White.copy(alpha = 0.85f),
                                                radius = r * 0.18f,
                                                center = Offset(bCenter.x - r * 0.35f, bCenter.y - r * 0.35f)
                                            )
                                        }
                                    }

                                    SensoryType.FLUID_CANVAS -> {
                                        // Preview: Harmonically morphing fluid wave streamlines
                                        val waveCount = 3
                                        for (w in 0 until waveCount) {
                                            val path = Path()
                                            val width = size.width
                                            val yBase = center.y + (w - 1) * 22.dp.toPx()
                                            for (x in 0..width.toInt() step 6) {
                                                val xF = x.toFloat()
                                                val yOffset = (14.dp.toPx() * sin((xF / 45f) + animPhase + (w * 0.8f)))
                                                if (x == 0) path.moveTo(xF, yBase + yOffset)
                                                else path.lineTo(xF, yBase + yOffset)
                                            }
                                            drawPath(
                                                path = path,
                                                color = if (w % 2 == 0) IbmBlueLight.copy(alpha = 0.75f)
                                                else IbmPurpleLight.copy(alpha = 0.70f),
                                                style = Stroke(width = 1.4.dp.toPx())
                                            )
                                        }
                                    }

                                    SensoryType.WORRY_STONE -> {
                                        // Handled in photographic preview block above
                                    }

                                    SensoryType.ZEN_SAND -> {
                                        // Handled in photographic preview block above
                                    }

                                    SensoryType.SINGING_BOWL -> {
                                        // Preview: Tibetan bronze bowl with shimmering acoustic resonance rings
                                        val bRadius = 36.dp.toPx()
                                        drawCircle(
                                            brush = Brush.radialGradient(
                                                listOf(
                                                    if (isDark) Color(0xFF1E160D) else Color(0xFF4A3A22),
                                                    if (isDark) Color(0xFFB59357) else Color(0xFF8C6E38)
                                                ),
                                                center = center,
                                                radius = bRadius
                                            ),
                                            radius = bRadius,
                                            center = center
                                        )
                                        drawCircle(
                                            color = Color(0xFFFFF0D0).copy(alpha = 0.85f),
                                            radius = bRadius,
                                            center = center,
                                            style = Stroke(width = 2.dp.toPx())
                                        )
                                        val pulseR = bRadius * (1.1f + 0.22f * sin(animPhase * 2f))
                                        drawCircle(
                                            color = IbmBlueLight.copy(alpha = 0.45f),
                                            radius = pulseR,
                                            center = center,
                                            style = Stroke(width = 1.2.dp.toPx())
                                        )
                                    }

                                    SensoryType.KALEIDOSCOPE -> {
                                        // Preview: Symmetrical rotating mandala wireframe
                                        val arms = 8
                                        val radius = 42.dp.toPx()
                                        for (a in 0 until arms) {
                                            val angle = (a * (2f * PI / arms)).toFloat() + animPhase * 0.5f
                                            val end = Offset(center.x + radius * cos(angle), center.y + radius * sin(angle))
                                            drawLine(
                                                color = IbmPurpleLight.copy(alpha = 0.65f),
                                                start = center,
                                                end = end,
                                                strokeWidth = 1.2.dp.toPx()
                                            )
                                            drawCircle(
                                                color = IbmBlueLight,
                                                radius = 2.5.dp.toPx(),
                                                center = end
                                            )
                                        }
                                        drawCircle(
                                            color = IbmPurpleLight.copy(alpha = 0.35f),
                                            radius = radius * 0.6f,
                                            center = center,
                                            style = Stroke(1.dp.toPx())
                                        )
                                    }

                                    SensoryType.COLOR_SORT -> {
                                        // Preview: Minimalist chromatic spectrum bars
                                        val barCount = 5
                                        val barW = 12.dp.toPx()
                                        val barH = 50.dp.toPx()
                                        val spacing = 8.dp.toPx()
                                        val totalW = barCount * barW + (barCount - 1) * spacing
                                        val startX = center.x - totalW / 2f
                                        val colors = listOf(
                                            Color(0xFF4589FF),
                                            Color(0xFF78A9FF),
                                            Color(0xFFA56EFF),
                                            Color(0xFF8A3FFC),
                                            Color(0xFF0F62FE)
                                        )
                                        for (b in 0 until barCount) {
                                            val bX = startX + b * (barW + spacing)
                                            val heightMod = (10.dp.toPx() * sin(animPhase + b * 0.8f))
                                            drawRoundRect(
                                                color = colors[b],
                                                topLeft = Offset(bX, center.y - (barH + heightMod) / 2f),
                                                size = Size(barW, barH + heightMod),
                                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx())
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Subtle "COMING SOON" badge on disabled previews
                        if (!tool.isEnabled) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(10.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isDark) Color(0x338A3FFC) else Color(0x228A3FFC),
                                    border = androidx.compose.foundation.BorderStroke(
                                        0.7.dp,
                                        IbmPurpleLight.copy(alpha = 0.55f)
                                    )
                                ) {
                                    Text(
                                        text = "COMING SOON",
                                        color = IbmPurpleLight,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Medium,
                                        letterSpacing = 1.2.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Info Section with Clean IBM Typography
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = tagLabel,
                                color = IbmBlueLight,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Normal,
                                letterSpacing = 2.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = tool.title,
                                color = RippleColors.textPrimary,
                                fontSize = 23.sp,
                                fontWeight = FontWeight.Light,
                                letterSpacing = 0.6.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = tool.subtitle,
                                color = RippleColors.textSecondary,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Light,
                                letterSpacing = 0.3.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Action Pill Button (Enter Space if enabled, Coming Soon if disabled)
                        if (tool.isEnabled) {
                            Surface(
                                onClick = { onNavigateToSensory(destinationRoute) },
                                shape = RoundedCornerShape(22.dp),
                                color = IbmBlue,
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .height(44.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 9.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Enter Space",
                                        color = Color.White,
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Medium,
                                        letterSpacing = 0.6.sp
                                    )
                                }
                            }
                        } else {
                            Surface(
                                onClick = {},
                                shape = RoundedCornerShape(22.dp),
                                color = if (isDark) Color(0x18FFFFFF) else Color(0x0C000000),
                                border = androidx.compose.foundation.BorderStroke(
                                    0.8.dp,
                                    if (isDark) Color(0x28FFFFFF) else Color(0x18000000)
                                ),
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .height(44.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 9.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = RippleColors.textSecondary,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Coming Soon",
                                        color = RippleColors.textSecondary,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Normal,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sleek Animated IBM Page Indicator Dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                tools.indices.forEach { index ->
                    val isSelected = pagerState.currentPage == index
                    val dotWidth by animateDpAsState(
                        targetValue = if (isSelected) 22.dp else 6.dp,
                        animationSpec = tween(350),
                        label = "DotWidth"
                    )
                    Box(
                        modifier = Modifier
                            .height(6.dp)
                            .width(dotWidth)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) IbmBlueLight else (if (isDark) Color(0x38FFFFFF) else Color(0x22000000))
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Dynamic Somatic Cognition Card bound to active carousel tool
            val currentTool = tools[pagerState.currentPage]
            val insight = remember(currentTool.type) {
                when (currentTool.type) {
                    SensoryType.BUBBLE_POP -> Triple(
                        "ACUTE TENSION DISCHARGE • SOMATOSENSORY",
                        "Proprioceptive Burst Modulation",
                        "Repetitive tactile micro-snaps deliver immediate somatic closure. The sudden mechanical release stimulates fast-adapting tactile corpuscles in your fingertips, interrupting circular anxious rumination through targeted tactile grounding."
                    )
                    SensoryType.FLUID_CANVAS -> Triple(
                        "VISUOMOTOR PACING • PARASYMPATHETIC",
                        "Fluid Velocity Entrainment",
                        "Tracking slow, non-turbulent fluid streamlines decelerates rapid ocular saccades. Guiding gentle pastel plumes across dark space stimulates the visual cortex to trigger a reflexive reduction in sympathetic fight-or-flight arousal."
                    )
                    SensoryType.WORRY_STONE -> Triple(
                        "TACTILE FRICTION • MERKEL RECEPTORS",
                        "Evolutionary Soothing Friction",
                        "Rhythmic thumb friction against a smooth digital pebble mimics mammalian self-soothing instincts. Low-frequency pressure on slow-adapting Merkel discs sends calming afferent impulses along the median nerve to steady heart rate variability."
                    )
                    SensoryType.ZEN_SAND -> Triple(
                        "KINETIC DISPLACEMENT • SPATIAL ATTENTION",
                        "Intentional Kinetic Raking",
                        "Slow, deliberate physical displacement of sand furrows diverts cognitive processing from rumination into spatial motor focus, activating tactile proprioceptors."
                    )
                    SensoryType.SINGING_BOWL -> Triple(
                        "ACOUSTIC ENTRAINMENT • VAGAL ACTIVATION",
                        "Harmonic Sound Healing",
                        "Sustained circular rim friction creates steady 432 Hz standing sound waves that cohere autonomic nervous system rhythms, gently decreasing resting pulse rate."
                    )
                    SensoryType.KALEIDOSCOPE -> Triple(
                        "OPTIC FLOW REGULATION • ALPHA WAVES",
                        "Radial Symmetrical Entrainment",
                        "Symmetrical radial geometry eliminates chaotic visual surprises, reducing cognitive processing demands. Harmonious rotation gently coaxes occipital brainwave activity toward relaxing 8–12 Hz alpha rhythms."
                    )
                    SensoryType.COLOR_SORT -> Triple(
                        "EXECUTIVE OVERLOAD RESET • PREFRONTAL",
                        "Cognitive Spectrum Anchoring",
                        "During sensory overwhelm, complex problem-solving becomes paralyzed. Organizing simple chromatic spectrums gently re-engages the prefrontal cortex through low-stakes linear ordering without inducing performance fatigue."
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = RippleColors.surface,
                border = androidx.compose.foundation.BorderStroke(0.8.dp, RippleColors.cardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                AnimatedContent(
                    targetState = insight,
                    transitionSpec = {
                        fadeIn(tween(300)) togetherWith fadeOut(tween(180))
                    },
                    label = "SomaticCognitionAnimation"
                ) { (eyebrow, title, explanation) ->
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(IbmBlueLight)
                            )
                            Text(
                                text = eyebrow,
                                color = IbmBlueLight,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Normal,
                                letterSpacing = 2.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = title,
                            color = RippleColors.textPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.4.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = explanation,
                            color = RippleColors.textSecondary,
                            fontSize = 12.5.sp,
                            lineHeight = 19.sp,
                            fontWeight = FontWeight.Light,
                            letterSpacing = 0.3.sp
                        )
                    }
                }
            }

            // Clearance so content glides smoothly under floating bottom pill
            Spacer(modifier = Modifier.height(130.dp))
        }
    }
}
