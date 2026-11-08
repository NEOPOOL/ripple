package com.vayulabs.ripple.presentation.sensory

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vayulabs.ripple.theme.*

data class MandalaStroke(
    val start: Offset,
    val end: Offset,
    val color: Color,
    var alpha: Float = 0.85f
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KaleidoscopeScreen(onNavigateBack: () -> Unit) {
    val strokes = remember { mutableStateListOf<MandalaStroke>() }
    var previousPoint by remember { mutableStateOf<Offset?>(null) }
    val isDark = ThemeState.isDarkMode.value

    val palette = listOf(
        AccentAzureLight,
        AccentSageLight,
        AccentDune,
        AccentLavender
    )
    var colorIndex by remember { mutableStateOf(0) }

    // 120Hz VSYNC fading loop
    LaunchedEffect(Unit) {
        var lastTime = withFrameMillis { it }
        while (true) {
            val currentTime = withFrameMillis { it }
            val delta = ((currentTime - lastTime) / 1000f).coerceIn(0.001f, 0.05f)
            lastTime = currentTime

            val iterator = strokes.listIterator()
            while (iterator.hasNext()) {
                val stroke = iterator.next()
                stroke.alpha -= 0.18f * delta
                if (stroke.alpha <= 0.02f) {
                    iterator.remove()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mandala Drift", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = RippleColors.textPrimary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = RippleColors.textPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { ThemeState.toggle() }) {
                        Icon(
                            imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme",
                            tint = RippleColors.textPrimary
                        )
                    }
                    IconButton(onClick = { strokes.clear() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Clear", tint = RippleColors.textPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RippleColors.background)
            )
        },
        containerColor = RippleColors.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                previousPoint = offset
                            },
                            onDragEnd = {
                                previousPoint = null
                            },
                            onDragCancel = {
                                previousPoint = null
                            },
                            onDrag = { change, _ ->
                                change.consume()
                                val current = change.position
                                val prev = previousPoint
                                if (prev != null) {
                                    strokes.add(
                                        MandalaStroke(
                                            start = prev,
                                            end = current,
                                            color = palette[colorIndex % palette.size],
                                            alpha = if (isDark) 0.85f else 0.7f
                                        )
                                    )
                                    colorIndex++
                                    if (strokes.size > 220) {
                                        strokes.removeAt(0)
                                    }
                                }
                                previousPoint = current
                            }
                        )
                    }
            ) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val symmetryFolds = 6
                val angleStep = 360f / symmetryFolds

                strokes.forEach { stroke ->
                    for (i in 0 until symmetryFolds) {
                        rotate(degrees = i * angleStep, pivot = center) {
                            drawLine(
                                color = stroke.color.copy(alpha = stroke.alpha.coerceIn(0f, 1f)),
                                start = stroke.start,
                                end = stroke.end,
                                strokeWidth = 3.5f,
                                cap = StrokeCap.Round
                            )
                        }
                    }
                }
            }

            Text(
                text = "Draw mindful shapes; watch them bloom and gently dissolve",
                color = RippleColors.textSecondary.copy(alpha = 0.7f),
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
            )
        }
    }
}
