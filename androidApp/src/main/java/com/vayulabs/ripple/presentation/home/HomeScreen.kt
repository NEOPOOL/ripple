package com.vayulabs.ripple.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vayulabs.ripple.data.UserPreferences
import com.vayulabs.ripple.navigation.Screen
import com.vayulabs.ripple.presentation.components.AppleLargeHeader
import com.vayulabs.ripple.presentation.components.AppleThemeToggleButton
import com.vayulabs.ripple.presentation.components.IbmBlue
import com.vayulabs.ripple.presentation.components.IbmBlueLight
import com.vayulabs.ripple.presentation.components.IbmPurple
import com.vayulabs.ripple.presentation.components.IbmPurpleLight
import com.vayulabs.ripple.presentation.components.IbmCoral
import com.vayulabs.ripple.theme.*
import com.vayulabs.ripple.shared.repository.QuotesRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

data class MoodCheckIn(
    val label: String,
    val targetRoute: String
)

@Composable
fun HomeScreen(
    quotesRepository: QuotesRepository,
    onNavigate: (String) -> Unit
) {
    val dailyQuote = remember { quotesRepository.getDailyQuote() }
    var showCrisisDialog by remember { mutableStateOf(false) }
    var showPreferenceMenu by remember { mutableStateOf(false) }
    val isDark = ThemeState.isDarkMode.value

    val userName = UserPreferences.currentUserName.value?.takeIf { it.isNotBlank() } ?: "Friend"

    // Time-aware greeting personalized with user's name
    val greeting = remember(userName) {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val timeGreeting = when (hour) {
            in 5..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            in 17..20 -> "Good evening"
            else -> "Good night"
        }
        "$timeGreeting, $userName"
    }

    val todayDateFormatted = remember {
        val sdf = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
        sdf.format(Date())
    }

    // Clean, ghost-style focus paths
    val moodPills = listOf(
        MoodCheckIn("Acute Surge", Screen.Grounding54321.route),
        MoodCheckIn("Overwhelmed", Screen.FluidCanvas.route),
        MoodCheckIn("Racing Mind", Screen.Breathe.route),
        MoodCheckIn("Restless", Screen.WorryStone.route),
        MoodCheckIn("Night Flow", Screen.Sounds.route),
        MoodCheckIn("Release", Screen.ThoughtDissolver.route)
    )

    // Smooth subtle breathing pulse
    val infiniteTransition = rememberInfiniteTransition(label = "HomePulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(3800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    // Continuous IBM Harmonic Line Morphing Phase
    val morphPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2.0 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(8500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "LineMorphPhase"
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
            // Header with Hamburger Menu on left & Support on right
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular Hamburger Menu Button with Fade Interaction
                Surface(
                    shape = CircleShape,
                    color = if (isDark) Color(0x26FFFFFF) else Color(0x14000000),
                    modifier = Modifier
                        .size(38.dp)
                        .clickable { showPreferenceMenu = true },
                    border = androidx.compose.foundation.BorderStroke(
                        0.5.dp,
                        RippleColors.cardBorder
                    )
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Preferences Menu",
                            tint = RippleColors.textPrimary,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppleThemeToggleButton()

                    // Prominent Coral Support Icon
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
            }

            // Big, Clean, Thin Personalized Greeting Title (IBM Carbon Tracking)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                Text(
                    text = todayDateFormatted.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 2.5.sp,
                    color = RippleColors.textSecondary
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = greeting,
                    fontSize = 35.sp,
                    fontWeight = FontWeight.Light,
                    letterSpacing = 0.6.sp,
                    color = RippleColors.textPrimary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Daily Ripple: Clean Transparent Hairline Card (NO heavy background block)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp)
            ) {
                Surface(
                    onClick = { onNavigate(Screen.Breathe.route) },
                    shape = RoundedCornerShape(22.dp),
                    color = if (isDark) Color(0x14FFFFFF) else Color(0x0A000000), // Clean transparent hairline
                    border = androidx.compose.foundation.BorderStroke(0.8.dp, RippleColors.cardBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(118.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(18.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .fillMaxHeight(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "DAILY PACING • 3 MIN",
                                color = IbmBlueLight,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Normal,
                                letterSpacing = 2.sp
                            )

                            Column {
                                Text(
                                    text = "Physiological Reset",
                                    color = RippleColors.textPrimary,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Light,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "Two inhales, slow exhale",
                                    color = RippleColors.textSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Light,
                                    letterSpacing = 0.3.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }

                        Surface(
                            shape = CircleShape,
                            color = IbmBlue,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(23.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Substantial Frosted Ghost Pills (Comfortably Larger, Zero Black Background)
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(moodPills) { pill ->
                    Surface(
                        onClick = { onNavigate(pill.targetRoute) },
                        shape = RoundedCornerShape(24.dp),
                        color = if (isDark) Color(0x18FFFFFF) else Color(0x0D000000), // Pure frosted glass pill
                        border = androidx.compose.foundation.BorderStroke(
                            0.8.dp,
                            if (isDark) Color(0x33FFFFFF) else Color(0x22000000)
                        )
                    ) {
                        Text(
                            text = pill.label,
                            color = RippleColors.textPrimary,
                            fontSize = 15.sp, // Made comfortably bigger
                            fontWeight = FontWeight.Normal, // Clean & thin
                            letterSpacing = 0.6.sp, // IBM open tracking
                            modifier = Modifier.padding(horizontal = 22.dp, vertical = 11.dp) // Substantial touch padding
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // IBM Carbon Morphing Line Animation Diagram
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .size(230.dp)
                        .clip(CircleShape)
                        .clickable { onNavigate(Screen.Breathe.route) }
                ) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val baseR = (size.minDimension / 2f) * pulseScale * 0.72f

                    // 1. Cardinal Axis Cross-Hairs
                    drawLine(
                        color = IbmBlue.copy(alpha = if (isDark) 0.18f else 0.12f),
                        start = Offset(center.x - baseR * 1.45f, center.y),
                        end = Offset(center.x + baseR * 1.45f, center.y),
                        strokeWidth = 0.8.dp.toPx()
                    )
                    drawLine(
                        color = IbmBlue.copy(alpha = if (isDark) 0.18f else 0.12f),
                        start = Offset(center.x, center.y - baseR * 1.45f),
                        end = Offset(center.x, center.y + baseR * 1.45f),
                        strokeWidth = 0.8.dp.toPx()
                    )

                    // 2. Precision Outer Orbital Ring
                    val outerOrbit = baseR * 1.35f
                    drawCircle(
                        color = IbmBlueLight.copy(alpha = if (isDark) 0.25f else 0.20f),
                        radius = outerOrbit,
                        center = center,
                        style = Stroke(width = 1.dp.toPx())
                    )

                    // 3. Four Cardinal Micro-Nodes on Orbit
                    for (i in 0 until 4) {
                        val angle = (i * PI / 2.0).toFloat()
                        val tickPos = Offset(
                            x = center.x + outerOrbit * cos(angle),
                            y = center.y + outerOrbit * sin(angle)
                        )
                        drawCircle(
                            color = IbmPurpleLight.copy(alpha = if (isDark) 0.8f else 0.6f),
                            radius = 2.2.dp.toPx(),
                            center = tickPos
                        )
                    }

                    // 4. Dynamic Morphing Harmonic Wave Vector Line (Living Wireframe)
                    val wavePath = Path()
                    val pointCount = 64
                    for (i in 0..pointCount) {
                        val angle = (i.toFloat() / pointCount) * (2f * PI.toFloat())
                        val rOffset = (7.dp.toPx() * sin(angle * 3f + morphPhase)) +
                                (4.dp.toPx() * cos(angle * 5f - morphPhase * 0.7f))
                        val r = (baseR * 1.15f) + rOffset
                        val x = center.x + (r * cos(angle))
                        val y = center.y + (r * sin(angle))
                        if (i == 0) wavePath.moveTo(x, y) else wavePath.lineTo(x, y)
                    }
                    wavePath.close()

                    drawPath(
                        path = wavePath,
                        brush = Brush.linearGradient(
                            colors = listOf(IbmBlue, IbmPurpleLight, IbmBlueLight),
                            start = Offset(center.x - baseR, center.y - baseR),
                            end = Offset(center.x + baseR, center.y + baseR)
                        ),
                        style = Stroke(width = 1.4.dp.toPx())
                    )

                    // 5. Core Luminous IBM Node
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = if (isDark) 0.98f else 0.90f),
                                IbmBlueLight,
                                IbmBlue
                            ),
                            center = center.copy(y = center.y - baseR * 0.12f),
                            radius = baseR
                        ),
                        radius = baseR,
                        center = center
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onNavigate(Screen.Breathe.route) }
                ) {
                    Icon(
                        Icons.Default.Air,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "BREATHE",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 2.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quiet Anchor Card with Thin Typography
            Surface(
                onClick = { onNavigate(Screen.Quotes.route) },
                shape = RoundedCornerShape(18.dp),
                color = RippleColors.surface,
                border = androidx.compose.foundation.BorderStroke(0.8.dp, RippleColors.cardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "ANCHOR",
                        color = IbmBlueLight,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "“${dailyQuote.text}”",
                        color = RippleColors.textPrimary,
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        letterSpacing = 0.4.sp,
                        fontWeight = FontWeight.Light
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Minimal Grounding Grid with Thin Fonts
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = "EXPLORE",
                    color = RippleColors.textSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MinimalActionCard(
                        modifier = Modifier.weight(1f),
                        title = "Sensory",
                        subtitle = "5 tactile tools",
                        icon = Icons.Default.WaterDrop,
                        accentColor = IbmBlueLight,
                        onClick = { onNavigate(Screen.SensoryHub.route) }
                    )
                    MinimalActionCard(
                        modifier = Modifier.weight(1f),
                        title = "Audio",
                        subtitle = "Ambient soundscapes",
                        icon = Icons.Default.Headphones,
                        accentColor = IbmPurpleLight,
                        onClick = { onNavigate(Screen.Sounds.route) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MinimalActionCard(
                        modifier = Modifier.weight(1f),
                        title = "Dissolve",
                        subtitle = "Release thoughts",
                        icon = Icons.Default.DeleteSweep,
                        accentColor = IbmBlueLight,
                        onClick = { onNavigate(Screen.ThoughtDissolver.route) }
                    )
                    MinimalActionCard(
                        modifier = Modifier.weight(1f),
                        title = "Anchors",
                        subtitle = "Affirmations",
                        icon = Icons.Default.BookmarkBorder,
                        accentColor = IbmPurpleLight,
                        onClick = { onNavigate(Screen.Quotes.route) }
                    )
                }
            }

            // Comfortable clearance so explore cards glide underneath the floating pill
            Spacer(modifier = Modifier.height(130.dp))
        }
    }

    // Hamburger Menu Dialog with Smooth Fade Animation for User Preferences
    if (showPreferenceMenu) {
        var editingName by remember { mutableStateOf(false) }
        var tempName by remember { mutableStateOf(userName) }

        AlertDialog(
            onDismissRequest = { showPreferenceMenu = false },
            title = {
                Text(
                    text = "Preferences",
                    color = RippleColors.textPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Light,
                    letterSpacing = 0.5.sp
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // User Identity Section
                    Text(
                        text = "YOUR NAME",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal,
                        letterSpacing = 1.5.sp,
                        color = IbmBlueLight
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    if (editingName) {
                        OutlinedTextField(
                            value = tempName,
                            onValueChange = { tempName = it },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = IbmBlueLight,
                                unfocusedBorderColor = RippleColors.cardBorder,
                                focusedTextColor = RippleColors.textPrimary,
                                unfocusedTextColor = RippleColors.textPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        TextButton(
                            onClick = {
                                if (tempName.isNotBlank()) {
                                    UserPreferences.setUserName(tempName)
                                }
                                editingName = false
                            }
                        ) {
                            Text("Save Name", color = IbmBlueLight)
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = userName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                color = RippleColors.textPrimary
                            )
                            TextButton(onClick = { editingName = true }) {
                                Text("Edit", color = IbmBlueLight, fontSize = 13.sp)
                            }
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        thickness = 0.5.dp,
                        color = RippleColors.cardBorder
                    )

                    // Appearance Setting
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Appearance",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                color = RippleColors.textPrimary
                            )
                            Text(
                                text = if (isDark) "OLED Carbon" else "Porcelain",
                                fontSize = 12.sp,
                                color = RippleColors.textSecondary
                            )
                        }
                        Switch(
                            checked = isDark,
                            onCheckedChange = { ThemeState.toggle() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = IbmBlue
                            )
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        thickness = 0.5.dp,
                        color = RippleColors.cardBorder
                    )

                    // Offline Privacy Guarantee
                    Text(
                        text = "VAYU LABS PRIVACY",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal,
                        letterSpacing = 1.5.sp,
                        color = IbmBlueLight
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "100% offline-first. Procedural sound synthesis running on-device. Zero telemetry.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Light,
                        lineHeight = 17.sp,
                        color = RippleColors.textSecondary
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showPreferenceMenu = false }) {
                    Text("Done", color = IbmBlueLight, fontWeight = FontWeight.Medium)
                }
            },
            containerColor = RippleColors.surface,
            shape = RoundedCornerShape(20.dp)
        )
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
                Button(
                    onClick = {
                        showCrisisDialog = false
                        onNavigate(Screen.Grounding54321.route)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IbmCoral),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Start 5-4-3-2-1 Grounding", color = Color.White, fontSize = 12.5.sp, fontWeight = FontWeight.Medium)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCrisisDialog = false }) {
                    Text("Close", color = RippleColors.textSecondary, fontWeight = FontWeight.Medium)
                }
            },
            containerColor = RippleColors.surface,
            shape = RoundedCornerShape(18.dp)
        )
    }
}

/**
 * Minimalist Action Card with IBM clean thin typography and subtle accent.
 */
@Composable
fun MinimalActionCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    val isDark = ThemeState.isDarkMode.value

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = RippleColors.surface,
        border = androidx.compose.foundation.BorderStroke(0.8.dp, RippleColors.cardBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = if (isDark) 0.16f else 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(19.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                color = RippleColors.textPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 0.4.sp
            )
            Text(
                text = subtitle,
                color = RippleColors.textSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = 0.3.sp,
                modifier = Modifier.padding(top = 1.dp)
            )
        }
    }
}
