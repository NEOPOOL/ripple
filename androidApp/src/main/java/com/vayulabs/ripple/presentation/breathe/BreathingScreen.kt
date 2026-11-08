package com.vayulabs.ripple.presentation.breathe

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vayulabs.ripple.haptics.HapticEngine
import com.vayulabs.ripple.presentation.components.AppleLargeHeader
import com.vayulabs.ripple.presentation.components.IbmBlue
import com.vayulabs.ripple.presentation.components.IbmBlueLight
import com.vayulabs.ripple.presentation.components.IbmPurple
import com.vayulabs.ripple.presentation.components.IbmPurpleLight
import com.vayulabs.ripple.presentation.components.IbmCoral
import com.vayulabs.ripple.theme.*
import com.vayulabs.ripple.shared.domain.model.BreathingPhase
import com.vayulabs.ripple.shared.domain.model.BreathingTechnique
import com.vayulabs.ripple.shared.statemachine.BreathingStateMachine
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun BreathingScreen(
    stateMachine: BreathingStateMachine,
    hapticEngine: HapticEngine,
    onNavigateBack: () -> Unit
) {
    val state by stateMachine.state.collectAsState()
    val isDark = ThemeState.isDarkMode.value
    var showCrisisDialog by remember { mutableStateOf(false) }

    val techniques = remember { BreathingTechnique.values().toList() }
    val pagerState = rememberPagerState(
        initialPage = techniques.indexOf(state.technique).coerceAtLeast(0)
    ) {
        techniques.size
    }

    // When user swipes horizontally, select the corresponding technique
    LaunchedEffect(pagerState.currentPage) {
        val selected = techniques[pagerState.currentPage]
        if (selected != state.technique) {
            stateMachine.selectTechnique(selected)
            hapticEngine.triggerStoneFriction()
        }
    }

    // Synchronize haptics on phase transition
    LaunchedEffect(state.currentPhase) {
        hapticEngine.onBreathingPhaseChanged(state.currentPhase)
    }

    // Hardware-synchronized 120Hz smooth animation curve for the breathing orb
    val targetScale = when (state.currentPhase) {
        BreathingPhase.IDLE -> 0.70f
        BreathingPhase.INHALE -> 1.0f
        BreathingPhase.HOLD_AFTER_INHALE -> 1.0f
        BreathingPhase.EXHALE -> 0.58f
        BreathingPhase.HOLD_AFTER_EXHALE -> 0.58f
    }

    val animationDuration = if (state.isRunning && state.currentStepDurationMillis > 0) {
        state.currentStepDurationMillis.toInt()
    } else {
        800
    }

    val animatedScale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = tween(
            durationMillis = animationDuration,
            easing = EaseInOutSine
        ),
        label = "OrbBreathingScale"
    )

    // Continuous IBM Line Morphing Phase Loop
    val infiniteTransition = rememberInfiniteTransition(label = "BreatheLineMorph")
    val morphPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2.0 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "MorphPhase"
    )

    // Subtle gentle breathing pulse during hold/idle
    val holdPulse by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(3400, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "HoldPulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 96.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // IBM Clean, Thin, Big Header
            AppleLargeHeader(
                eyebrow = "Pacing",
                title = "Breathe",
                extraAction = {
                    // Restored Prominent Coral Support Icon
                    Surface(
                        shape = CircleShape,
                        color = IbmCoral.copy(alpha = if (isDark) 0.18f else 0.15f),
                        modifier = Modifier
                            .size(34.dp)
                            .clickable { showCrisisDialog = true },
                        border = androidx.compose.foundation.BorderStroke(
                            0.5.dp,
                            IbmCoral.copy(alpha = 0.45f)
                        )
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Emergency,
                                contentDescription = "Crisis Help",
                                tint = IbmCoral,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            )

            // Minimal Swipeable Carousel Header with Thin Typography
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp, bottom = 4.dp)
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxWidth()
                ) { page ->
                    val tech = techniques[page]
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                    ) {
                        Text(
                            text = tech.title,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Light, // Clean & thin
                            color = RippleColors.textPrimary,
                            textAlign = TextAlign.Center,
                            letterSpacing = 0.6.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = tech.subtitle,
                            fontSize = 12.sp,
                            color = IbmBlueLight,
                            fontWeight = FontWeight.Normal,
                            letterSpacing = 0.4.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Minimal Pager Dots in IBM Blue
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(techniques.size) { index ->
                        val isSelected = pagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .size(if (isSelected) 16.dp else 5.dp, 5.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    if (isSelected) IbmBlueLight else RippleColors.textTertiary.copy(alpha = 0.30f)
                                )
                        )
                    }
                }
            }

            // IBM Carbon Morphing Line Animation & Somatic Diagram
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier.size(280.dp)
                ) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val baseRadius = (size.minDimension / 2f) * 0.70f
                    val scale = (animatedScale * (if (state.currentPhase == BreathingPhase.HOLD_AFTER_INHALE || !state.isRunning) holdPulse else 1f)).coerceIn(0.5f, 1.15f)
                    val coreRadius = baseRadius * scale

                    // 1. IBM Architectural Cardinal Axis Lines
                    drawLine(
                        color = IbmBlue.copy(alpha = if (isDark) 0.16f else 0.12f),
                        start = Offset(center.x - coreRadius * 1.55f, center.y),
                        end = Offset(center.x + coreRadius * 1.55f, center.y),
                        strokeWidth = 0.8.dp.toPx()
                    )
                    drawLine(
                        color = IbmBlue.copy(alpha = if (isDark) 0.16f else 0.12f),
                        start = Offset(center.x, center.y - coreRadius * 1.55f),
                        end = Offset(center.x, center.y + coreRadius * 1.55f),
                        strokeWidth = 0.8.dp.toPx()
                    )

                    // 2. Precision Outer Orbital Ring with 4 Cardinal Micro-Nodes
                    val outerOrbit = coreRadius * 1.40f
                    drawCircle(
                        color = IbmBlueLight.copy(alpha = if (isDark) 0.22f else 0.18f),
                        radius = outerOrbit,
                        center = center,
                        style = Stroke(width = 1.dp.toPx())
                    )
                    for (i in 0 until 4) {
                        val angle = (i * PI / 2.0).toFloat()
                        val tickPos = Offset(
                            x = center.x + outerOrbit * cos(angle),
                            y = center.y + outerOrbit * sin(angle)
                        )
                        drawCircle(
                            color = IbmPurpleLight.copy(alpha = if (isDark) 0.7f else 0.5f),
                            radius = 2.dp.toPx(),
                            center = tickPos
                        )
                    }

                    // 3. Dynamic Morphing Harmonic Wave Vector Line (Living Wireframe)
                    val wavePath = Path()
                    val pointCount = 64
                    for (i in 0..pointCount) {
                        val angle = (i.toFloat() / pointCount) * (2f * PI.toFloat())
                        val rOffset = (8.dp.toPx() * sin(angle * 4f + morphPhase)) +
                                (4.dp.toPx() * cos(angle * 2f - morphPhase * 0.8f))
                        val r = (coreRadius * 1.18f) + rOffset
                        val x = center.x + (r * cos(angle))
                        val y = center.y + (r * sin(angle))
                        if (i == 0) wavePath.moveTo(x, y) else wavePath.lineTo(x, y)
                    }
                    wavePath.close()

                    drawPath(
                        path = wavePath,
                        brush = Brush.linearGradient(
                            colors = listOf(IbmBlue, IbmPurpleLight, IbmBlueLight),
                            start = Offset(center.x - coreRadius, center.y - coreRadius),
                            end = Offset(center.x + coreRadius, center.y + coreRadius)
                        ),
                        style = Stroke(width = 1.5.dp.toPx())
                    )

                    // 4. Core Luminous IBM Node
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = if (isDark) 0.96f else 0.90f),
                                IbmBlueLight,
                                IbmBlue
                            ),
                            center = center.copy(y = center.y - coreRadius * 0.14f),
                            radius = coreRadius
                        ),
                        radius = coreRadius,
                        center = center
                    )
                }

                // Centered Minimal Phase Word with Thin Typography
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    AnimatedContent(
                        targetState = state.currentPhase.label,
                        transitionSpec = { fadeIn(tween(350)) togetherWith fadeOut(tween(250)) },
                        label = "PhaseWordTransition"
                    ) { label ->
                        Text(
                            text = label.uppercase(),
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium, // Thin & clean
                            letterSpacing = 2.5.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    if (state.isRunning && state.currentStepDurationMillis > 0) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${state.currentStepDurationMillis / 1000}s",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Light
                        )
                    }
                }
            }

            // Somatic Instruction & Guidance (Concise & Thin)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                AnimatedContent(
                    targetState = state.currentPhase.instruction,
                    transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) },
                    label = "InstructionTextTransition"
                ) { instruction ->
                    Text(
                        text = instruction,
                        color = RippleColors.textSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Light,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }

                if (state.isRunning && state.cycleCount > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "CYCLE ${state.cycleCount + 1}",
                        color = RippleColors.textTertiary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal,
                        letterSpacing = 1.4.sp
                    )
                }
            }

            // Minimal Pill Action Button in IBM Blue with Fluid Morphing
            var isActionPressed by remember { mutableStateOf(false) }
            val actionScale by animateFloatAsState(
                targetValue = if (isActionPressed) 0.88f else 1.0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "ActionScale"
            )

            Surface(
                onClick = {
                    isActionPressed = true
                    hapticEngine.triggerStoneFriction()
                    if (state.isRunning) {
                        stateMachine.stop()
                    } else {
                        stateMachine.start()
                    }
                },
                shape = RoundedCornerShape(22.dp),
                color = if (state.isRunning) RippleColors.surfaceVariant else IbmBlue,
                border = if (state.isRunning) androidx.compose.foundation.BorderStroke(0.5.dp, RippleColors.cardBorder) else null,
                modifier = Modifier
                    .wrapContentWidth()
                    .height(46.dp)
                    .scale(actionScale)
                    .padding(horizontal = 32.dp)
            ) {
                LaunchedEffect(isActionPressed) {
                    if (isActionPressed) {
                        kotlinx.coroutines.delay(120)
                        isActionPressed = false
                    }
                }
                Row(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    AnimatedContent(
                        targetState = state.isRunning,
                        transitionSpec = {
                            (scaleIn(initialScale = 0.5f, animationSpec = spring(Spring.DampingRatioMediumBouncy)) + fadeIn())
                                .togetherWith(scaleOut(targetScale = 0.5f) + fadeOut())
                        },
                        label = "BreathePlayPauseIconMorph"
                    ) { isRunning ->
                        Icon(
                            imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = if (isRunning) RippleColors.textPrimary else Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    AnimatedContent(
                        targetState = state.isRunning,
                        transitionSpec = {
                            fadeIn(tween(200)) togetherWith fadeOut(tween(120))
                        },
                        label = "BreathePlayPauseTextMorph"
                    ) { isRunning ->
                        Text(
                            text = if (isRunning) "Pause" else "Begin",
                            color = if (isRunning) RippleColors.textPrimary else Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }

    // Support Resources Dialog
    if (showCrisisDialog) {
        AlertDialog(
            onDismissRequest = { showCrisisDialog = false },
            title = {
                Text(
                    "Support Resources",
                    color = RippleColors.textPrimary,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    letterSpacing = 0.5.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "Free, 24/7 confidential crisis helplines:",
                        color = RippleColors.textSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Light
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "• US & Canada: 988\n• UK: 111 or Text SHOUT to 85258\n• India: Tele-MANAS (14416)\n• International: 112",
                        color = IbmBlueLight,
                        fontSize = 13.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showCrisisDialog = false }) {
                    Text("Close", color = IbmBlueLight, fontWeight = FontWeight.Medium)
                }
            },
            containerColor = RippleColors.surface,
            shape = RoundedCornerShape(18.dp)
        )
    }
}
