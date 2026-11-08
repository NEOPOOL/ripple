package com.vayulabs.ripple.wear.presentation

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import com.vayulabs.ripple.shared.domain.model.BreathingPhase
import com.vayulabs.ripple.shared.statemachine.BreathingStateMachine
import com.vayulabs.ripple.wear.haptics.WearHapticDriver

@Composable
fun WearBreathingScreen(
    stateMachine: BreathingStateMachine,
    hapticDriver: WearHapticDriver
) {
    val state by stateMachine.state.collectAsState()

    // Trigger silent wrist haptics on phase transition
    LaunchedEffect(state.currentPhase) {
        hapticDriver.onPhaseChanged(state.currentPhase)
    }

    val targetProgress = when (state.currentPhase) {
        BreathingPhase.IDLE -> 0f
        BreathingPhase.INHALE -> 1f
        BreathingPhase.HOLD_AFTER_INHALE -> 1f
        BreathingPhase.EXHALE -> 0f
        BreathingPhase.HOLD_AFTER_EXHALE -> 0f
    }

    val duration = if (state.isRunning && state.currentStepDurationMillis > 0) {
        state.currentStepDurationMillis.toInt()
    } else {
        600
    }

    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = duration, easing = EaseInOutSine),
        label = "WatchProgress"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable { stateMachine.toggle() },
        contentAlignment = Alignment.Center
    ) {
        // Circular edge progress ring
        Canvas(modifier = Modifier.fillMaxSize().padding(10.dp)) {
            val strokeWidth = 6.dp.toPx()

            // Inactive track
            drawCircle(
                color = Color(0xFF21262D),
                style = Stroke(width = strokeWidth)
            )

            // Active phase arc
            if (state.isRunning) {
                val sweepAngle = 360f * animatedProgress
                drawArc(
                    color = when (state.currentPhase) {
                        BreathingPhase.INHALE -> Color(0xFF9AB9C9)
                        BreathingPhase.HOLD_AFTER_INHALE, BreathingPhase.HOLD_AFTER_EXHALE -> Color(0xFFA89F91)
                        BreathingPhase.EXHALE -> Color(0xFFA0B7AA)
                        BreathingPhase.IDLE -> Color(0xFF648BA2)
                    },
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
        }

        // Center typography and status
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Text(
                text = state.currentPhase.label.uppercase(),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (state.isRunning) {
                    if (state.cycleCount > 0) "Cycle ${state.cycleCount + 1}" else "Breathe gently"
                } else {
                    "Tap to start"
                },
                color = Color(0xFF8B949E),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = state.technique.title,
                color = Color(0xFF648BA2),
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
