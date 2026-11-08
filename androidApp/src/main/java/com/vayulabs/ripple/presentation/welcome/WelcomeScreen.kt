package com.vayulabs.ripple.presentation.welcome

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vayulabs.ripple.data.UserPreferences
import com.vayulabs.ripple.presentation.components.IbmBlue
import com.vayulabs.ripple.presentation.components.IbmBlueLight
import com.vayulabs.ripple.presentation.components.IbmPurpleLight
import com.vayulabs.ripple.theme.RippleColors
import com.vayulabs.ripple.theme.ThemeState
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(
    onContinue: () -> Unit
) {
    var nameInput by remember { mutableStateOf("") }
    val isDark = ThemeState.isDarkMode.value
    val focusManager = LocalFocusManager.current

    // IBM Morphing Harmonic Line Animation for the Welcome Orb
    val infiniteTransition = rememberInfiniteTransition(label = "WelcomeMorph")
    val morphPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2.0 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(8500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WelcomeMorphPhase"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 28.dp, vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Minimalist Morphing Line Graphic
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .padding(bottom = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val baseR = size.minDimension * 0.38f

                    // Precision Hairline Grid
                    drawLine(
                        color = IbmBlue.copy(alpha = if (isDark) 0.16f else 0.12f),
                        start = Offset(center.x - baseR * 1.5f, center.y),
                        end = Offset(center.x + baseR * 1.5f, center.y),
                        strokeWidth = 0.8.dp.toPx()
                    )
                    drawLine(
                        color = IbmBlue.copy(alpha = if (isDark) 0.16f else 0.12f),
                        start = Offset(center.x, center.y - baseR * 1.5f),
                        end = Offset(center.x, center.y + baseR * 1.5f),
                        strokeWidth = 0.8.dp.toPx()
                    )

                    // Morphing Vector Line
                    val wavePath = Path()
                    val pointCount = 48
                    for (i in 0..pointCount) {
                        val angle = (i.toFloat() / pointCount) * (2f * PI.toFloat())
                        val rOffset = (6.dp.toPx() * sin(angle * 3f + morphPhase))
                        val r = (baseR * 1.12f) + rOffset
                        val x = center.x + (r * cos(angle))
                        val y = center.y + (r * sin(angle))
                        if (i == 0) wavePath.moveTo(x, y) else wavePath.lineTo(x, y)
                    }
                    wavePath.close()

                    drawPath(
                        path = wavePath,
                        brush = Brush.linearGradient(
                            colors = listOf(IbmBlue, IbmPurpleLight)
                        ),
                        style = Stroke(width = 1.4.dp.toPx())
                    )

                    // Core Node
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = if (isDark) 0.95f else 0.85f),
                                IbmBlueLight,
                                IbmBlue
                            ),
                            center = center,
                            radius = baseR
                        ),
                        radius = baseR,
                        center = center
                    )
                }
            }

            // Big, Clean, Thin IBM Title
            Text(
                text = "Welcome to Ripple",
                fontSize = 36.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = 0.8.sp,
                color = RippleColors.textPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "A minimalist space for somatic grounding\nand nervous system regulation.",
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                lineHeight = 22.sp,
                letterSpacing = 0.3.sp,
                color = RippleColors.textSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Thin, Clean Name Input
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "WHAT SHOULD WE CALL YOU?",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 2.5.sp,
                    color = IbmBlueLight,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    placeholder = {
                        Text(
                            text = "Your name or alias",
                            color = RippleColors.textTertiary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Light
                        )
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = RippleColors.textPrimary,
                        unfocusedTextColor = RippleColors.textPrimary,
                        focusedBorderColor = IbmBlueLight,
                        unfocusedBorderColor = if (isDark) Color(0x33FFFFFF) else Color(0x22000000),
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        cursorColor = IbmBlueLight
                    ),
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            if (nameInput.isNotBlank()) {
                                UserPreferences.setUserName(nameInput)
                                onContinue()
                            }
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Minimal Continue Button
            Surface(
                onClick = {
                    val name = if (nameInput.isNotBlank()) nameInput else "Friend"
                    UserPreferences.setUserName(name)
                    onContinue()
                },
                shape = RoundedCornerShape(22.dp),
                color = IbmBlue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Begin Journey",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.4.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }
        }
    }
}
