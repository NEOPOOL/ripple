package com.vayulabs.ripple.presentation.sensory

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vayulabs.ripple.theme.*

data class ColorBlock(
    val id: Int,
    val originalOrder: Int,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorSortScreen(onNavigateBack: () -> Unit) {
    val isDark = ThemeState.isDarkMode.value

    val initialPalette = if (isDark) listOf(
        Color(0xFF2E3E4B),
        Color(0xFF3C5364),
        Color(0xFF506C7E),
        Color(0xFF678598),
        Color(0xFF82A0B3),
        Color(0xFF90A89A),
        Color(0xFFA5B8A8),
        Color(0xFFC4BEB2)
    ) else listOf(
        Color(0xFF4C6D85),
        Color(0xFF62849C),
        Color(0xFF7E9EB4),
        Color(0xFF9CB7CB),
        Color(0xFFB5CCE0),
        Color(0xFFB6D4C3),
        Color(0xFFCCE4D5),
        Color(0xFFE4DFD5)
    )

    var blocks by remember(isDark) {
        mutableStateOf(
            initialPalette.mapIndexed { index, color ->
                ColorBlock(id = index, originalOrder = index, color = color)
            }.shuffled()
        )
    }

    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    val isSolved = remember(blocks) {
        blocks.map { it.originalOrder } == blocks.indices.toList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gradient Flow", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = RippleColors.textPrimary) },
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
                    IconButton(onClick = {
                        blocks = blocks.shuffled()
                        selectedIndex = null
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Shuffle", tint = RippleColors.textPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RippleColors.background)
            )
        },
        containerColor = RippleColors.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "CHROMATIC SPECTRUM HARMONY",
                    color = RippleColors.orbCore,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Tap two swatches to swap them into a smooth gradient",
                    color = RippleColors.textSecondary,
                    fontSize = 13.sp
                )
            }

            // Color Tiles Column
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                blocks.forEachIndexed { index, block ->
                    val isSelected = selectedIndex == index
                    val animatedBorder by animateColorAsState(
                        targetValue = if (isSelected) (if (isDark) Color.White else Color(0xFF0F172A)) else Color.Transparent,
                        label = "BorderAnimation"
                    )

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                val currentSelected = selectedIndex
                                if (currentSelected == null) {
                                    selectedIndex = index
                                } else if (currentSelected == index) {
                                    selectedIndex = null
                                } else {
                                    // Swap
                                    val mutable = blocks.toMutableList()
                                    val temp = mutable[currentSelected]
                                    mutable[currentSelected] = mutable[index]
                                    mutable[index] = temp
                                    blocks = mutable
                                    selectedIndex = null
                                }
                            },
                        color = block.color,
                        border = androidx.compose.foundation.BorderStroke(2.5.dp, animatedBorder),
                        shadowElevation = if (isSelected) 4.dp else 0.dp
                    ) {}
                }
            }

            // Success feedback
            if (isSolved) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AccentSage)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Harmony Restored • Mind Centered",
                        color = AccentSage,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            } else {
                Text(
                    text = "No timer. No pressure. Move at your own pace.",
                    color = RippleColors.textSecondary.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }
    }
}
