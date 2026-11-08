package com.vayulabs.ripple.presentation.grounding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vayulabs.ripple.haptics.HapticEngine
import com.vayulabs.ripple.presentation.components.AppleLargeHeader
import com.vayulabs.ripple.presentation.components.IbmBlue
import com.vayulabs.ripple.presentation.components.IbmBlueLight
import com.vayulabs.ripple.presentation.components.IbmCoral
import com.vayulabs.ripple.presentation.components.IbmPurpleLight
import com.vayulabs.ripple.theme.RippleColors
import com.vayulabs.ripple.theme.ThemeState
import kotlin.math.PI
import kotlin.math.sin

data class GroundingStep(
    val stage: Int, // 5, 4, 3, 2, 1
    val senseName: String,
    val count: Int,
    val prompt: String,
    val subPrompt: String,
    val items: List<String>
)

@Composable
fun Grounding54321Screen(
    hapticEngine: HapticEngine,
    onNavigateBack: () -> Unit,
    onNavigateToBreathe: () -> Unit
) {
    val isDark = ThemeState.isDarkMode.value

    val steps = remember {
        listOf(
            GroundingStep(
                stage = 5,
                senseName = "SEE",
                count = 5,
                prompt = "Acknowledge 5 things you can see",
                subPrompt = "Scan your immediate physical room. Notice small details: shadows, textures, colors.",
                items = listOf("First object", "Second object", "Third object", "Fourth object", "Fifth object")
            ),
            GroundingStep(
                stage = 4,
                senseName = "TOUCH",
                count = 4,
                prompt = "Notice 4 physical textures",
                subPrompt = "Feel the phone glass, fabric of your clothes, back of the chair, or feet on the floor.",
                items = listOf("First texture", "Second texture", "Third texture", "Fourth texture")
            ),
            GroundingStep(
                stage = 3,
                senseName = "HEAR",
                count = 3,
                prompt = "Listen for 3 distinct sounds",
                subPrompt = "Tune your auditory cortex: distant hum of appliances, outdoor breeze, your own breath.",
                items = listOf("First sound", "Second sound", "Third sound")
            ),
            GroundingStep(
                stage = 2,
                senseName = "SMELL",
                count = 2,
                prompt = "Identify 2 scents around you",
                subPrompt = "Notice the air in the room, paper, laundry soap, or simply the neutral freshness of air.",
                items = listOf("First scent", "Second scent")
            ),
            GroundingStep(
                stage = 1,
                senseName = "SENSATION",
                count = 1,
                prompt = "Notice 1 physical sensation or taste",
                subPrompt = "Rest your tongue gently against the roof of your mouth. Notice cool air entering your nostrils.",
                items = listOf("Physical anchor")
            )
        )
    }

    var currentStepIndex by remember { mutableStateOf(0) }
    val completedItemsByStep = remember { mutableStateMapOf<Int, MutableSet<Int>>() }

    val currentStep = steps[currentStepIndex]
    val completedInThisStep = completedItemsByStep.getOrPut(currentStep.stage) { mutableSetOf() }
    val isStepFullyCompleted = completedInThisStep.size >= currentStep.count

    // Gentle breathing ring animation
    val infiniteTransition = rememberInfiniteTransition(label = "GroundingRingPulse")
    val ringPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RingPhase"
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

                    // Reset Flow Button
                    Surface(
                        shape = CircleShape,
                        color = if (isDark) Color(0x26FFFFFF) else Color(0x14000000),
                        modifier = Modifier
                            .size(38.dp)
                            .clickable {
                                currentStepIndex = 0
                                completedItemsByStep.clear()
                                hapticEngine.triggerBubblePop()
                            },
                        border = BorderStroke(0.5.dp, RippleColors.cardBorder)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Restart Grounding",
                                tint = RippleColors.textPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Header
                AppleLargeHeader(
                    eyebrow = "Acute Panic De-escalation",
                    title = "5-4-3-2-1 Reset"
                )

                // Stage Indicator Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    steps.forEachIndexed { idx, step ->
                        val isDone = (completedItemsByStep[step.stage]?.size ?: 0) >= step.count
                        val isCurrent = idx == currentStepIndex
                        val barColor = when {
                            isDone -> IbmBlueLight
                            isCurrent -> if (isDark) Color.White else IbmBlue
                            else -> if (isDark) Color(0x26FFFFFF) else Color(0x1A000000)
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(barColor)
                        )
                    }
                }
            }

            // Main Interactive Content Area
            AnimatedContent(
                targetState = currentStepIndex,
                transitionSpec = {
                    (fadeIn(tween(350)) + slideInHorizontally { width -> width / 3 })
                        .togetherWith(fadeOut(tween(200)) + slideOutHorizontally { width -> -width / 3 })
                },
                label = "GroundingStepMorph",
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) { stepIdx ->
                val step = steps[stepIdx]
                val completedSet = completedItemsByStep.getOrPut(step.stage) { mutableSetOf() }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Large Somatic Number Emblem
                    Box(
                        modifier = Modifier.size(110.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val r = size.minDimension / 2f
                            // Ambient breathing halo
                            drawCircle(
                                color = IbmBlueLight.copy(alpha = if (isDark) 0.18f else 0.12f),
                                radius = r * (0.88f + 0.10f * sin(ringPhase * 2f * PI.toFloat())),
                                style = Stroke(width = 1.5.dp.toPx())
                            )
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        IbmBlueLight.copy(alpha = if (isDark) 0.22f else 0.15f),
                                        Color.Transparent
                                    )
                                ),
                                radius = r
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${step.stage}",
                                color = IbmBlueLight,
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Light,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = step.senseName,
                                color = RippleColors.textSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 2.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = step.prompt,
                        color = RippleColors.textPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Light,
                        textAlign = TextAlign.Center,
                        letterSpacing = 0.4.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = step.subPrompt,
                        color = RippleColors.textSecondary,
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Light,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    Spacer(modifier = Modifier.height(26.dp))

                    // Interactive Tap Anchors
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        for (itemIdx in 0 until step.count) {
                            val isChecked = itemIdx in completedSet
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isChecked) {
                                    IbmBlueLight.copy(alpha = if (isDark) 0.22f else 0.14f)
                                } else {
                                    if (isDark) Color(0x18FFFFFF) else Color(0x0C000000)
                                },
                                border = BorderStroke(
                                    0.8.dp,
                                    if (isChecked) IbmBlueLight else RippleColors.cardBorder
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (isChecked) {
                                            completedSet.remove(itemIdx)
                                        } else {
                                            completedSet.add(itemIdx)
                                            hapticEngine.triggerStoneFriction()
                                        }
                                    }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 18.dp, vertical = 14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${itemIdx + 1}. ${step.items.getOrElse(itemIdx) { "Anchor ${itemIdx + 1}" }}",
                                        color = if (isChecked) RippleColors.textPrimary else RippleColors.textSecondary,
                                        fontSize = 14.5.sp,
                                        fontWeight = if (isChecked) FontWeight.Medium else FontWeight.Light,
                                        letterSpacing = 0.3.sp
                                    )

                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isChecked) IbmBlueLight
                                                else if (isDark) Color(0x28FFFFFF)
                                                else Color(0x1A000000)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isChecked) {
                                            Icon(
                                                Icons.Default.Check,
                                                contentDescription = "Acknowledged",
                                                tint = Color.White,
                                                modifier = Modifier.size(15.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Flow Action Controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (currentStepIndex < steps.size - 1) {
                    Button(
                        onClick = {
                            if (currentStepIndex < steps.size - 1) {
                                currentStepIndex++
                                hapticEngine.triggerBubblePop()
                            }
                        },
                        enabled = isStepFullyCompleted,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = IbmBlueLight,
                            contentColor = Color.White,
                            disabledContainerColor = if (isDark) Color(0x22FFFFFF) else Color(0x12000000),
                            disabledContentColor = RippleColors.textSecondary
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (isStepFullyCompleted) "Continue to Step ${steps[currentStepIndex + 1].stage}" else "Acknowledge all anchors to proceed",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 0.5.sp
                            )
                            if (isStepFullyCompleted) {
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                } else {
                    // Final Completion Stage
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onNavigateToBreathe,
                            enabled = isStepFullyCompleted,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = IbmBlueLight,
                                contentColor = Color.White,
                                disabledContainerColor = if (isDark) Color(0x22FFFFFF) else Color(0x12000000),
                                disabledContentColor = RippleColors.textSecondary
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                        ) {
                            Text(
                                text = "Transition to Physiological Reset Breath",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 0.5.sp
                            )
                        }

                        TextButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "I am grounded and safe • Return Home",
                                color = RippleColors.textSecondary,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Light,
                                letterSpacing = 0.4.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
