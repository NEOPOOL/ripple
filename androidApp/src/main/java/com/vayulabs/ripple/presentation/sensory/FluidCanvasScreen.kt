package com.vayulabs.ripple.presentation.sensory

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vayulabs.ripple.theme.*

data class FluidDrop(
    val id: Long,
    var position: Offset,
    var velocity: Offset,
    var radius: Float,
    var alpha: Float,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FluidCanvasScreen(onNavigateBack: () -> Unit) {
    val drops = remember { mutableStateListOf<FluidDrop>() }
    var dropCounter by remember { mutableStateOf(0L) }
    val isDark = ThemeState.isDarkMode.value

    val palette = listOf(
        AccentAzureLight,
        AccentSageLight,
        AccentDune,
        AccentLavender
    )

    // 120Hz VSYNC choreographer frame loop
    LaunchedEffect(Unit) {
        var lastTime = withFrameMillis { it }
        while (true) {
            val currentTime = withFrameMillis { it }
            val delta = ((currentTime - lastTime) / 1000f).coerceIn(0.001f, 0.05f)
            lastTime = currentTime

            val iterator = drops.listIterator()
            while (iterator.hasNext()) {
                val drop = iterator.next()
                drop.alpha -= 0.65f * delta
                drop.radius += 70f * delta
                drop.position = drop.position + (drop.velocity * (delta * 60f))
                drop.velocity = drop.velocity * 0.95f

                if (drop.alpha <= 0.01f) {
                    iterator.remove()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Liquid Silk", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = RippleColors.textPrimary) },
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
                            onDrag = { change, dragAmount ->
                                change.consume()
                                val color = palette[(dropCounter % palette.size).toInt()]
                                drops.add(
                                    FluidDrop(
                                        id = dropCounter++,
                                        position = change.position,
                                        velocity = Offset(dragAmount.x * 0.3f, dragAmount.y * 0.3f),
                                        radius = 26f,
                                        alpha = if (isDark) 0.75f else 0.55f,
                                        color = color
                                    )
                                )
                                if (drops.size > 80) {
                                    drops.removeAt(0)
                                }
                            }
                        )
                    }
            ) {
                drops.forEach { drop ->
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                drop.color.copy(alpha = drop.alpha.coerceIn(0f, 1f)),
                                drop.color.copy(alpha = (drop.alpha * 0.4f).coerceIn(0f, 1f)),
                                Color.Transparent
                            ),
                            center = drop.position,
                            radius = drop.radius
                        ),
                        radius = drop.radius,
                        center = drop.position
                    )
                }
            }

            Text(
                text = "Slowly drag your finger to swirl silk currents",
                color = RippleColors.textSecondary.copy(alpha = 0.7f),
                fontSize = 13.sp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
            )
        }
    }
}
