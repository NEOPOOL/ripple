package com.vayulabs.ripple.presentation.quotes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vayulabs.ripple.presentation.components.AppleLargeHeader
import com.vayulabs.ripple.presentation.components.IbmBlueLight
import com.vayulabs.ripple.theme.*
import com.vayulabs.ripple.shared.domain.model.QuoteCategory
import com.vayulabs.ripple.shared.repository.QuotesRepository

@Composable
fun QuotesScreen(
    quotesRepository: QuotesRepository,
    onNavigateBack: () -> Unit
) {
    val quotes by quotesRepository.quotes.collectAsState()
    var selectedCategory by remember { mutableStateOf<QuoteCategory?>(null) }
    val isDark = ThemeState.isDarkMode.value

    val filteredQuotes = remember(quotes, selectedCategory) {
        if (selectedCategory == null) quotes
        else quotes.filter { it.category == selectedCategory }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            AppleLargeHeader(
                eyebrow = "Truths",
                title = "Library"
            )

            // Category Filter Pills (Minimal & Thin)
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    val isAll = selectedCategory == null
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = if (isAll) IbmBlueLight else RippleColors.surface,
                        border = if (isAll) null else androidx.compose.foundation.BorderStroke(0.5.dp, RippleColors.cardBorder),
                        modifier = Modifier.clickable { selectedCategory = null }
                    ) {
                        Text(
                            text = "All",
                            color = if (isAll) Color.White else RippleColors.textSecondary,
                            fontWeight = if (isAll) FontWeight.Medium else FontWeight.Light,
                            fontSize = 13.sp,
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }

                items(QuoteCategory.values()) { category ->
                    val isSelected = selectedCategory == category
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = if (isSelected) IbmBlueLight else RippleColors.surface,
                        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(0.5.dp, RippleColors.cardBorder),
                        modifier = Modifier.clickable { selectedCategory = category }
                    ) {
                        Text(
                            text = category.displayName,
                            color = if (isSelected) Color.White else RippleColors.textSecondary,
                            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Light,
                            fontSize = 13.sp,
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Affirmation Cards List with Thin Typography
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredQuotes, key = { it.id }) { quote ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp)),
                        color = RippleColors.surface,
                        border = androidx.compose.foundation.BorderStroke(0.8.dp, RippleColors.cardBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = quote.category.displayName.uppercase(),
                                    color = IbmBlueLight,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Normal,
                                    letterSpacing = 2.sp
                                )

                                IconButton(
                                    onClick = { quotesRepository.toggleFavorite(quote.id) },
                                    modifier = Modifier.size(22.dp)
                                ) {
                                    Icon(
                                        imageVector = if (quote.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                        contentDescription = null,
                                        tint = if (quote.isFavorite) IbmBlueLight else RippleColors.textTertiary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "“${quote.text}”",
                                color = RippleColors.textPrimary,
                                fontSize = 15.sp,
                                lineHeight = 22.sp,
                                letterSpacing = 0.4.sp,
                                fontWeight = FontWeight.Light // Thin & clean
                            )

                            if (quote.author != null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "— ${quote.author}",
                                    color = RippleColors.textSecondary,
                                    fontSize = 11.sp,
                                    fontStyle = FontStyle.Italic,
                                    fontWeight = FontWeight.Light,
                                    letterSpacing = 0.3.sp,
                                    modifier = Modifier.align(Alignment.End)
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(130.dp))
                }
            }
        }
    }
}
