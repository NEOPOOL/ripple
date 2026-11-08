package com.vayulabs.ripple.presentation.sounds

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vayulabs.ripple.audio.BinauralMode
import com.vayulabs.ripple.audio.SoundscapeAudioEngine
import com.vayulabs.ripple.presentation.components.AppleLargeHeader
import com.vayulabs.ripple.presentation.components.IbmBlue
import com.vayulabs.ripple.presentation.components.IbmBlueLight
import com.vayulabs.ripple.presentation.components.IbmBlueUltraLight
import com.vayulabs.ripple.presentation.components.IbmEasing
import com.vayulabs.ripple.presentation.components.IbmPurple
import com.vayulabs.ripple.presentation.components.IbmPurpleLight
import com.vayulabs.ripple.theme.*
import com.vayulabs.ripple.shared.domain.model.SoundscapeCatalog
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun SoundMixerScreen(
    audioEngine: SoundscapeAudioEngine,
    onNavigateBack: () -> Unit
) {
    val catalogTracks = remember { SoundscapeCatalog.tracks }
    var selectedTimerMinutes by remember { mutableStateOf<Int?>(30) }
    val isDark = ThemeState.isDarkMode.value
    val pagerState = rememberPagerState(pageCount = { catalogTracks.size })

    // Animated phase for soundwave previews
    val infiniteTransition = rememberInfiniteTransition(label = "SoundWaveMorph")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2.0 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(6500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WavePhase"
    )

    // Helper functions to read live state directly from reactive audioEngine
    fun isTrackActive(trackId: String): Boolean {
        val vol = when (trackId) {
            "deep_brown_noise" -> audioEngine.brownNoiseVolume
            "soft_rain" -> audioEngine.rainVolume
            "ocean_swell" -> audioEngine.oceanVolume
            "forest_wind" -> audioEngine.windVolume
            "theta_drone_432hz" -> audioEngine.droneVolume
            else -> 0f
        }
        return vol > 0.01f && audioEngine.isPlaying
    }

    fun getTrackVolume(trackId: String): Float {
        val vol = when (trackId) {
            "deep_brown_noise" -> audioEngine.brownNoiseVolume
            "soft_rain" -> audioEngine.rainVolume
            "ocean_swell" -> audioEngine.oceanVolume
            "forest_wind" -> audioEngine.windVolume
            "theta_drone_432hz" -> audioEngine.droneVolume
            else -> 0f
        }
        return if (vol > 0.01f) vol else 0.5f
    }

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
            // Header with Glass Toggle Pill & Theme Toggle
            AppleLargeHeader(
                eyebrow = "Acoustic Shielding",
                title = "Soundscapes"
            )

            // Sleek Horizontal Sleep Timer Selector Row (Zero Modal Dialogs)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = IbmBlueLight,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SLEEP TIMER",
                        color = IbmBlueLight,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Normal,
                        letterSpacing = 2.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val timerOptions = listOf(null to "Off", 15 to "15m", 30 to "30m", 45 to "45m", 60 to "60m")
                    timerOptions.forEach { (minutes, label) ->
                        val isSelected = selectedTimerMinutes == minutes
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) IbmBlue else (if (isDark) Color(0x14FFFFFF) else Color(0x0A000000)),
                            border = androidx.compose.foundation.BorderStroke(
                                0.8.dp,
                                if (isSelected) IbmBlueLight else RippleColors.cardBorder
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(34.dp)
                                .clickable { selectedTimerMinutes = minutes }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = label,
                                    color = if (isSelected) Color.White else RippleColors.textSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Light,
                                    letterSpacing = 0.4.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Brainwave Entrainment & Breath-Coupled Pacing Controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Binaural Beats Selector
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = IbmBlueLight,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "BINAURAL BEATS • BRAINWAVE TUNING",
                            color = IbmBlueLight,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Normal,
                            letterSpacing = 2.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BinauralMode.values().forEach { mode ->
                            val isSelected = audioEngine.binauralMode == mode
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) IbmBlue else (if (isDark) Color(0x14FFFFFF) else Color(0x0A000000)),
                                border = androidx.compose.foundation.BorderStroke(
                                    0.8.dp,
                                    if (isSelected) IbmBlueLight else RippleColors.cardBorder
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(34.dp)
                                    .clickable { audioEngine.setBinaural(mode) }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = mode.label,
                                        color = if (isSelected) Color.White else RippleColors.textSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Light,
                                        letterSpacing = 0.3.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Breath-Coupled Pacing Toggle Pill
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (audioEngine.isBreathCoupled) IbmBlue.copy(alpha = 0.18f) else (if (isDark) Color(0x14FFFFFF) else Color(0x0A000000)),
                    border = androidx.compose.foundation.BorderStroke(
                        0.8.dp,
                        if (audioEngine.isBreathCoupled) IbmBlueLight else RippleColors.cardBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { audioEngine.toggleBreathCoupling() }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (audioEngine.isBreathCoupled) IbmBlueLight else RippleColors.textSecondary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Breath-Coupled Ocean/Wind Pacing",
                                color = if (audioEngine.isBreathCoupled) RippleColors.textPrimary else RippleColors.textSecondary,
                                fontSize = 12.sp,
                                fontWeight = if (audioEngine.isBreathCoupled) FontWeight.Medium else FontWeight.Light,
                                letterSpacing = 0.4.sp
                            )
                        }
                        Text(
                            text = if (audioEngine.isBreathCoupled) "ON • 10S CYCLE" else "OFF",
                            color = if (audioEngine.isBreathCoupled) IbmBlueLight else RippleColors.textSecondary,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Coherent Swipe-Based Soundscape Carousel Deck
            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(horizontal = 24.dp),
                pageSpacing = 16.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(410.dp)
            ) { page ->
                val track = catalogTracks[page]
                val isPlaying = isTrackActive(track.id)
                val currentVol = getTrackVolume(track.id)

                val tagLabel = when (track.id) {
                    "soft_rain" -> "ACOUSTIC SHIELDING • HIGH FREQUENCY"
                    "deep_brown_noise" -> "BRAINWAVE CALM • LOW PASS"
                    "ocean_swell" -> "RESPIRATORY SURGE • 8-SEC CYCLE"
                    "forest_wind" -> "ORGANIC CANOPY • DIFFUSED"
                    else -> "6 HZ THETA • BINAURAL DRONE"
                }

                Surface(
                    shape = RoundedCornerShape(26.dp),
                    color = RippleColors.surface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isPlaying) IbmBlueLight.copy(alpha = 0.65f) else RippleColors.cardBorder
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
                        // Ambient Soundwave Canvas Preview
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            if (isPlaying) IbmBlue.copy(alpha = if (isDark) 0.28f else 0.18f)
                                            else (if (isDark) Color(0x33101524) else Color(0x1F0F62FE)),
                                            if (isDark) Color(0x1A141A2E) else Color(0x0A8A3FFC)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val center = Offset(size.width / 2f, size.height / 2f)

                                when (track.id) {
                                    "soft_rain" -> {
                                        val dropOffsets = listOf(
                                            Offset(-40.dp.toPx(), -20.dp.toPx()),
                                            Offset(-15.dp.toPx(), 25.dp.toPx()),
                                            Offset(35.dp.toPx(), -15.dp.toPx()),
                                            Offset(0.dp.toPx(), 18.dp.toPx())
                                        )
                                        dropOffsets.forEachIndexed { i, off ->
                                            val rBase = (20 + i * 8).dp.toPx()
                                            val rMod = rBase * (0.85f + 0.15f * sin(wavePhase + i * 1.5f))
                                            drawCircle(
                                                color = IbmBlueLight.copy(alpha = if (isPlaying) 0.8f else 0.45f),
                                                radius = rMod,
                                                center = Offset(center.x + off.x, center.y + off.y),
                                                style = Stroke(1.2.dp.toPx())
                                            )
                                        }
                                    }

                                    "deep_brown_noise" -> {
                                        for (w in 0..2) {
                                            val path = Path()
                                            val yBase = center.y + (w - 1) * 20.dp.toPx()
                                            for (x in 0..size.width.toInt() step 8) {
                                                val xF = x.toFloat()
                                                val yOff = 16.dp.toPx() * sin((xF / 60f) + wavePhase * 0.7f + w)
                                                if (x == 0) path.moveTo(xF, yBase + yOff)
                                                else path.lineTo(xF, yBase + yOff)
                                            }
                                            drawPath(
                                                path = path,
                                                color = IbmBlue.copy(alpha = if (isPlaying) 0.85f else 0.45f),
                                                style = Stroke(1.8.dp.toPx())
                                            )
                                        }
                                    }

                                    "ocean_swell" -> {
                                        val path = Path()
                                        for (x in 0..size.width.toInt() step 6) {
                                            val xF = x.toFloat()
                                            val yOff = 22.dp.toPx() * sin((xF / 80f) + wavePhase * 0.5f)
                                            if (x == 0) path.moveTo(xF, center.y + yOff)
                                            else path.lineTo(xF, center.y + yOff)
                                        }
                                        drawPath(
                                            path = path,
                                            brush = Brush.horizontalGradient(listOf(IbmBlue, IbmPurpleLight)),
                                            style = Stroke(2.2.dp.toPx())
                                        )
                                    }

                                    "forest_wind" -> {
                                        for (l in -2..2) {
                                            val y = center.y + l * 18.dp.toPx()
                                            val xOffset = 30.dp.toPx() * sin(wavePhase + l * 0.5f)
                                            drawLine(
                                                color = IbmPurpleLight.copy(alpha = if (isPlaying) 0.75f else 0.40f),
                                                start = Offset(center.x - 60.dp.toPx() + xOffset, y),
                                                end = Offset(center.x + 60.dp.toPx() + xOffset, y),
                                                strokeWidth = 1.3.dp.toPx()
                                            )
                                        }
                                    }

                                    else -> {
                                        val r = 40.dp.toPx() * (0.92f + 0.08f * sin(wavePhase * 1.5f))
                                        drawCircle(
                                            brush = Brush.radialGradient(
                                                colors = listOf(
                                                    IbmBlueLight.copy(alpha = if (isPlaying) 0.6f else 0.35f),
                                                    IbmPurpleLight.copy(alpha = 0.15f),
                                                    Color.Transparent
                                                ),
                                                center = center,
                                                radius = r * 1.4f
                                            ),
                                            radius = r,
                                            center = center
                                        )
                                        drawCircle(
                                            color = IbmBlueLight,
                                            radius = r,
                                            center = center,
                                            style = Stroke(1.4.dp.toPx())
                                        )
                                    }
                                }
                            }

                            if (isPlaying) {
                                Surface(
                                    shape = CircleShape,
                                    color = IbmBlue,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(10.dp)
                                        .size(28.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.GraphicEq,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                }
                            }
                        }

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
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = track.title,
                                color = RippleColors.textPrimary,
                                fontSize = 23.sp,
                                fontWeight = FontWeight.Light,
                                letterSpacing = 0.6.sp
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = track.description,
                                color = RippleColors.textSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Light,
                                letterSpacing = 0.3.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }

                        // Controls: Play Toggle + Embedded Volume Slider
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AnimatedVisibility(
                                visible = isPlaying,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Volume",
                                        color = RippleColors.textSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Light,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Slider(
                                        value = currentVol,
                                        onValueChange = { newVol ->
                                            audioEngine.updateTrackVolume(track.id, true, newVol)
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = SliderDefaults.colors(
                                            thumbColor = IbmBlueLight,
                                            activeTrackColor = IbmBlueLight,
                                            inactiveTrackColor = if (isDark) Color(0x26FFFFFF) else Color(0x1A000000)
                                        )
                                    )
                                }
                            }

                            // Fully Transitional Play / Pause Pill
                            var isPillTapped by remember { mutableStateOf(false) }
                            val pillScale by animateFloatAsState(
                                targetValue = if (isPillTapped) 0.88f else 1.0f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMedium
                                ),
                                label = "PillScale"
                            )

                            Surface(
                                onClick = {
                                    isPillTapped = true
                                    val willPlay = !isPlaying
                                    audioEngine.updateTrackVolume(track.id, willPlay, currentVol)
                                },
                                shape = RoundedCornerShape(22.dp),
                                color = if (isPlaying) RippleColors.surfaceVariant else IbmBlue,
                                border = if (isPlaying) androidx.compose.foundation.BorderStroke(0.8.dp, RippleColors.cardBorder) else null,
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .height(44.dp)
                                    .scale(pillScale)
                            ) {
                                LaunchedEffect(isPillTapped) {
                                    if (isPillTapped) {
                                        delay(120)
                                        isPillTapped = false
                                    }
                                }
                                Row(
                                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 9.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    AnimatedContent(
                                        targetState = isPlaying,
                                        transitionSpec = {
                                            (scaleIn(initialScale = 0.5f, animationSpec = spring(Spring.DampingRatioMediumBouncy)) + fadeIn())
                                                .togetherWith(scaleOut(targetScale = 0.5f) + fadeOut())
                                        },
                                        label = "TrackPlayPauseIconMorph"
                                    ) { playing ->
                                        Icon(
                                            imageVector = if (playing) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = null,
                                            tint = if (playing) RippleColors.textPrimary else Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    AnimatedContent(
                                        targetState = isPlaying,
                                        transitionSpec = {
                                            fadeIn(tween(220)) togetherWith fadeOut(tween(140))
                                        },
                                        label = "TrackPlayPauseTextMorph"
                                    ) { playing ->
                                        Text(
                                            text = if (playing) "Pause Track" else "Play Soundscape",
                                            color = if (playing) RippleColors.textPrimary else Color.White,
                                            fontSize = 13.5.sp,
                                            fontWeight = FontWeight.Medium,
                                            letterSpacing = 0.6.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sleek Animated IBM Page Indicator Dots
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(catalogTracks.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    val dotWidth by animateDpAsState(
                        targetValue = if (isSelected) 24.dp else 6.dp,
                        animationSpec = tween(350, easing = IbmEasing),
                        label = "DotWidth"
                    )
                    val dotColor by animateColorAsState(
                        targetValue = if (isSelected) IbmBlueLight else (if (isDark) Color(0x38FFFFFF) else Color(0x26000000)),
                        animationSpec = tween(350),
                        label = "DotColor"
                    )

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .height(6.dp)
                            .width(dotWidth)
                            .clip(CircleShape)
                            .background(dotColor)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Multi-Layer Soundscape Mixer Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = "ACTIVE SOUND LAYERS",
                    color = RippleColors.textSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                // Quick Layer Toggle Pills
                catalogTracks.forEach { track ->
                    val isPlaying = isTrackActive(track.id)
                    val currentVol = getTrackVolume(track.id)

                    Surface(
                        onClick = {
                            audioEngine.updateTrackVolume(track.id, !isPlaying, currentVol)
                        },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isPlaying) IbmBlue.copy(alpha = if (isDark) 0.18f else 0.12f) else RippleColors.surface,
                        border = androidx.compose.foundation.BorderStroke(
                            0.8.dp,
                            if (isPlaying) IbmBlueLight.copy(alpha = 0.5f) else RippleColors.cardBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(if (isPlaying) IbmBlue else (if (isDark) Color(0x22FFFFFF) else Color(0x10000000))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AnimatedContent(
                                        targetState = isPlaying,
                                        transitionSpec = {
                                            (scaleIn(initialScale = 0.5f) + fadeIn()) togetherWith (scaleOut(targetScale = 0.5f) + fadeOut())
                                        },
                                        label = "LayerIconMorph"
                                    ) { playing ->
                                        Icon(
                                            imageVector = if (playing) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = null,
                                            tint = if (playing) Color.White else RippleColors.textPrimary,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = track.title,
                                        color = RippleColors.textPrimary,
                                        fontSize = 14.5.sp,
                                        fontWeight = FontWeight.Normal,
                                        letterSpacing = 0.4.sp
                                    )
                                    Text(
                                        text = if (isPlaying) "Active in mix • ${(currentVol * 100).toInt()}%" else "Off",
                                        color = if (isPlaying) IbmBlueLight else RippleColors.textTertiary,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Light
                                    )
                                }
                            }

                            if (isPlaying) {
                                Icon(
                                    Icons.Default.GraphicEq,
                                    contentDescription = null,
                                    tint = IbmBlueLight,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Clearance so content glides smoothly under floating bottom pill
            Spacer(modifier = Modifier.height(130.dp))
        }
    }
}
