package com.vayulabs.ripple.presentation.journal

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vayulabs.ripple.theme.*
import kotlin.random.Random

data class DustParticle(
    val offset: Offset,
    val velocity: Offset,
    val color: Color,
    var alpha: Float = 1.0f
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThoughtDissolverScreen(onNavigateBack: () -> Unit) {
    var thoughtText by remember { mutableStateOf("") }
    var isDissolving by remember { mutableStateOf(false) }
    val particles = remember { mutableStateListOf<DustParticle>() }
    val isDark = ThemeState.isDarkMode.value

    val textAlpha by animateFloatAsState(
        targetValue = if (isDissolving) 0f else 1f,
        animationSpec = tween(durationMillis = 1400),
        label = "TextAlpha"
    )

    // 120Hz VSYNC Choreographer loop for dust particles
    LaunchedEffect(isDissolving) {
        if (isDissolving) {
            particles.clear()
            for (i in 0 until 70) {
                particles.add(
                    DustParticle(
                        offset = Offset(Random.nextFloat() * 650f + 80f, Random.nextFloat() * 380f + 260f),
                        velocity = Offset((Random.nextFloat() - 0.5f) * 7f, -Random.nextFloat() * 9f - 2f),
                        color = listOf(AccentAzureLight, AccentSageLight, AccentDune, AccentLavender).random()
                    )
                )
            }

            var lastTime = withFrameMillis { it }
            while (particles.isNotEmpty()) {
                val currentTime = withFrameMillis { it }
                val delta = ((currentTime - lastTime) / 1000f).coerceIn(0.001f, 0.05f)
                lastTime = currentTime

                val it = particles.listIterator()
                while (it.hasNext()) {
                    val p = it.next()
                    p.alpha -= 0.65f * delta
                    if (p.alpha <= 0.04f) {
                        it.remove()
                    }
                }
            }

            thoughtText = ""
            isDissolving = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thought Dissolver", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = RippleColors.textPrimary) },
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "BURN AFTER WRITING",
                        color = RippleColors.orbCore,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Empty your mind here. When ready, tap Release to watch it dissolve into harmless dust.",
                        color = RippleColors.textSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }

                // iOS-Style Text Input Card
                OutlinedTextField(
                    value = thoughtText,
                    onValueChange = { if (!isDissolving) thoughtText = it },
                    placeholder = {
                        Text(
                            "What is overwhelming you right now?",
                            color = RippleColors.textTertiary
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 20.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(RippleColors.surface),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RippleColors.orbCore,
                        unfocusedBorderColor = RippleColors.cardBorder,
                        focusedTextColor = RippleColors.textPrimary.copy(alpha = textAlpha),
                        unfocusedTextColor = RippleColors.textPrimary.copy(alpha = textAlpha)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    enabled = !isDissolving
                )

                Button(
                    onClick = {
                        if (thoughtText.isNotBlank() && !isDissolving) {
                            isDissolving = true
                        }
                    },
                    enabled = thoughtText.isNotBlank() && !isDissolving,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentAzure,
                        disabledContainerColor = RippleColors.surfaceVariant
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Release & Dissolve",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            // Dissolving particles canvas overlay
            if (isDissolving) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    particles.forEach { p ->
                        drawCircle(
                            color = p.color.copy(alpha = p.alpha.coerceIn(0f, 1f)),
                            radius = 3.5f,
                            center = p.offset
                        )
                    }
                }
            }
        }
    }
}
