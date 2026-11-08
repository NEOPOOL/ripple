package com.vayulabs.ripple.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vayulabs.ripple.navigation.Screen
import com.vayulabs.ripple.theme.*
import kotlinx.coroutines.delay

// Official IBM Carbon Palette
val IbmBlue = Color(0xFF0F62FE)
val IbmBlueLight = Color(0xFF4589FF)
val IbmBlueUltraLight = Color(0xFF78A9FF)
val IbmPurple = Color(0xFF8A3FFC)
val IbmPurpleLight = Color(0xFFA56EFF)
val IbmCoral = Color(0xFFFA4D56)

// Calming unhurried transition curve
val IbmEasing = CubicBezierEasing(0.16f, 1f, 0.3f, 1f)

/**
 * Animated Light / Dark Mode Toggle Button.
 */
@Composable
fun AppleThemeToggleButton(
    modifier: Modifier = Modifier
) {
    val isDark = ThemeState.isDarkMode.value

    var isPressed by remember { mutableStateOf(false) }
    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed) 0.86f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "ThemeToggleScale"
    )

    Surface(
        shape = CircleShape,
        color = if (isDark) Color(0x26FFFFFF) else Color(0x14000000),
        modifier = modifier
            .size(36.dp)
            .scale(buttonScale)
            .clickable {
                isPressed = true
                ThemeState.toggleDarkMode()
            },
        border = androidx.compose.foundation.BorderStroke(0.5.dp, RippleColors.cardBorder)
    ) {
        LaunchedEffect(isPressed) {
            if (isPressed) {
                delay(120)
                isPressed = false
            }
        }

        Box(contentAlignment = Alignment.Center) {
            AnimatedContent(
                targetState = isDark,
                transitionSpec = {
                    (scaleIn(initialScale = 0.5f, animationSpec = spring(Spring.DampingRatioMediumBouncy)) + fadeIn())
                        .togetherWith(scaleOut(targetScale = 0.5f) + fadeOut())
                },
                label = "ThemeToggleMorph"
            ) { dark ->
                Icon(
                    imageVector = if (dark) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle Theme",
                    tint = RippleColors.textPrimary,
                    modifier = Modifier.size(17.dp)
                )
            }
        }
    }
}

/**
 * Clean Header Action Deck with Theme Toggle and optional Action.
 */
@Composable
fun AppleHeaderActions(
    modifier: Modifier = Modifier,
    extraAction: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AppleThemeToggleButton()
        if (extraAction != null) {
            extraAction()
        }
    }
}

/**
 * IBM Carbon-Inspired Large Header: Clean, Thin, and Big.
 */
@Composable
fun AppleLargeHeader(
    eyebrow: String,
    title: String,
    modifier: Modifier = Modifier,
    extraAction: (@Composable () -> Unit)? = null,
    trailingAction: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = eyebrow.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 2.5.sp,
                color = RippleColors.textSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 38.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = 0.8.sp,
                color = RippleColors.textPrimary
            )
        }

        Box(
            modifier = Modifier.padding(bottom = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            if (trailingAction != null) {
                trailingAction()
            } else {
                AppleHeaderActions(extraAction = extraAction)
            }
        }
    }
}

/**
 * Clean hardware-accelerated ambient background.
 */
@Composable
fun FluidAmbientBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = ThemeState.isDarkMode.value

    val bgBrush = remember(isDark) {
        if (isDark) {
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF0E1117),
                    Color(0xFF080A0E),
                    Color(0xFF05070A)
                )
            )
        } else {
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFFFFFFF),
                    Color(0xFFF4F6FA),
                    Color(0xFFEAEEF5)
                )
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgBrush)
    ) {
        content()
    }
}

/**
 * Floating Acrylic Pill Bottom Navigation Bar with IBM Blue/Purple accents.
 */
@Composable
fun AppleMusicBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = ThemeState.isDarkMode.value

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = if (isDark) Color(0xF2161A24) else Color(0xF8FFFFFF),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = if (isDark) Color(0x38FFFFFF) else Color(0x1F000000)
            ),
            shadowElevation = if (isDark) 6.dp else 10.dp
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FloatingPillTab(
                    label = "Listen",
                    icon = Icons.Default.Spa,
                    isSelected = currentRoute == Screen.Home.route,
                    onClick = { onNavigate(Screen.Home.route) }
                )

                FloatingPillTab(
                    label = "Breathe",
                    icon = Icons.Default.Air,
                    isSelected = currentRoute == Screen.Breathe.route,
                    onClick = { onNavigate(Screen.Breathe.route) }
                )

                FloatingPillTab(
                    label = "Sensory",
                    icon = Icons.Default.WaterDrop,
                    isSelected = currentRoute == Screen.SensoryHub.route,
                    onClick = { onNavigate(Screen.SensoryHub.route) }
                )

                FloatingPillTab(
                    label = "Audio",
                    icon = Icons.Default.Headphones,
                    isSelected = currentRoute == Screen.Sounds.route,
                    onClick = { onNavigate(Screen.Sounds.route) }
                )

                FloatingPillTab(
                    label = "Library",
                    icon = Icons.Default.BookmarkBorder,
                    isSelected = currentRoute == Screen.Quotes.route,
                    onClick = { onNavigate(Screen.Quotes.route) }
                )
            }
        }
    }
}

/**
 * Individual Tab in Floating Pill with IBM Blue/Purple selection.
 */
@Composable
fun FloatingPillTab(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val activeColor = IbmBlueLight
    val inactiveColor = RippleColors.textTertiary

    val animatedBgColor by animateColorAsState(
        targetValue = if (isSelected) IbmBlue.copy(alpha = 0.20f) else Color.Transparent,
        animationSpec = tween(450, easing = IbmEasing),
        label = "PillBgColor"
    )

    val animatedIconColor by animateColorAsState(
        targetValue = if (isSelected) activeColor else inactiveColor,
        animationSpec = tween(450, easing = IbmEasing),
        label = "PillIconColor"
    )

    val iconScale by animateFloatAsState(
        targetValue = if (isSelected) 1.12f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "PillIconScale"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(22.dp))
            .background(animatedBgColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = if (isSelected) 14.dp else 10.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = animatedIconColor,
                modifier = Modifier
                    .size(22.dp)
                    .scale(iconScale)
            )

            AnimatedVisibility(
                visible = isSelected,
                enter = expandHorizontally(
                    animationSpec = tween(450, easing = IbmEasing)
                ) + fadeIn(tween(350)),
                exit = shrinkHorizontally(
                    animationSpec = tween(350, easing = IbmEasing)
                ) + fadeOut(tween(200))
            ) {
                Row {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = label,
                        color = activeColor,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.8.sp
                    )
                }
            }
        }
    }
}

/**
 * Floating Mini Player with IBM Blue/Purple harmonic line styling.
 * Fully transitional Play/Pause and Close buttons with haptic feedback.
 */
@Composable
fun AppleMusicMiniPlayer(
    visible: Boolean,
    title: String,
    subtitle: String,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    onClose: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = ThemeState.isDarkMode.value

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            animationSpec = tween(450, easing = IbmEasing)
        ) { it } + fadeIn(tween(350)) + scaleIn(initialScale = 0.94f, animationSpec = tween(450, easing = IbmEasing)),
        exit = slideOutVertically(
            animationSpec = tween(350, easing = IbmEasing)
        ) { it } + fadeOut(tween(250)) + scaleOut(targetScale = 0.92f, animationSpec = tween(300)),
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(22.dp))
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(22.dp),
            color = if (isDark) Color(0xD9161B26) else Color(0xF2FFFFFF),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = if (isDark) Color(0x38FFFFFF) else Color(0x1F000000)
            ),
            shadowElevation = if (isDark) 6.dp else 12.dp
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(11.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(IbmBlue, IbmPurple)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        AnimatedContent(
                            targetState = title,
                            transitionSpec = { fadeIn(tween(250)) togetherWith fadeOut(tween(150)) },
                            label = "MiniPlayerTitleTransition"
                        ) { targetTitle ->
                            Text(
                                text = targetTitle,
                                color = RippleColors.textPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 0.5.sp,
                                maxLines = 1
                            )
                        }
                        AnimatedContent(
                            targetState = subtitle,
                            transitionSpec = { fadeIn(tween(250)) togetherWith fadeOut(tween(150)) },
                            label = "MiniPlayerSubtitleTransition"
                        ) { targetSub ->
                            Text(
                                text = targetSub,
                                color = RippleColors.textSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Light,
                                letterSpacing = 0.4.sp,
                                maxLines = 1
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Transitional Play/Pause Button
                        var isPlayTapped by remember { mutableStateOf(false) }
                        val playButtonScale by animateFloatAsState(
                            targetValue = if (isPlayTapped) 0.82f else 1.0f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            ),
                            label = "PlayButtonScale"
                        )

                        Surface(
                            shape = CircleShape,
                            color = Color.Transparent,
                            modifier = Modifier
                                .size(38.dp)
                                .scale(playButtonScale)
                                .clickable {
                                    isPlayTapped = true
                                    onTogglePlay()
                                }
                        ) {
                            LaunchedEffect(isPlayTapped) {
                                if (isPlayTapped) {
                                    delay(120)
                                    isPlayTapped = false
                                }
                            }
                            Box(contentAlignment = Alignment.Center) {
                                AnimatedContent(
                                    targetState = isPlaying,
                                    transitionSpec = {
                                        (scaleIn(initialScale = 0.4f, animationSpec = spring(Spring.DampingRatioMediumBouncy)) +
                                                fadeIn(tween(180)))
                                            .togetherWith(
                                                scaleOut(targetScale = 0.4f, animationSpec = tween(120)) +
                                                        fadeOut(tween(120))
                                            )
                                    },
                                    label = "PlayPauseMorph"
                                ) { playing ->
                                    Icon(
                                        imageVector = if (playing) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = if (playing) "Pause" else "Play",
                                        tint = RippleColors.textPrimary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }

                        // Transitional Close Button
                        var isCloseTapped by remember { mutableStateOf(false) }
                        val closeButtonScale by animateFloatAsState(
                            targetValue = if (isCloseTapped) 0.78f else 1.0f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            ),
                            label = "CloseButtonScale"
                        )
                        val closeIconRotate by animateFloatAsState(
                            targetValue = if (isCloseTapped) 90f else 0f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            ),
                            label = "CloseIconRotate"
                        )

                        Surface(
                            shape = CircleShape,
                            color = Color.Transparent,
                            modifier = Modifier
                                .size(36.dp)
                                .scale(closeButtonScale)
                                .clickable {
                                    isCloseTapped = true
                                    onClose()
                                }
                        ) {
                            LaunchedEffect(isCloseTapped) {
                                if (isCloseTapped) {
                                    delay(150)
                                    isCloseTapped = false
                                }
                            }
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss Player",
                                    tint = RippleColors.textSecondary,
                                    modifier = Modifier
                                        .size(19.dp)
                                        .rotate(closeIconRotate)
                                )
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(IbmBlue, IbmPurple)
                            )
                        )
                )
            }
        }
    }
}
