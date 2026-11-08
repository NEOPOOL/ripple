package com.vayulabs.ripple.shared.repository

import com.vayulabs.ripple.shared.domain.model.Quote
import com.vayulabs.ripple.shared.domain.model.QuoteCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class QuotesRepository {
    private val allQuotes = listOf(
        // Panic & Overwhelm
        Quote(
            id = "panic-1",
            text = "This feeling is intense, but it is not dangerous. It will peak, soften, and pass.",
            category = QuoteCategory.PANIC_DEESCALATION
        ),
        Quote(
            id = "panic-2",
            text = "Your heart is beating fast to protect you, not harm you. You are physically safe right here.",
            category = QuoteCategory.PANIC_DEESCALATION
        ),
        Quote(
            id = "panic-3",
            text = "You don't need to fight this wave. Place your feet flat on the floor and let it roll past you.",
            category = QuoteCategory.PANIC_DEESCALATION
        ),
        Quote(
            id = "panic-4",
            text = "Notice 3 solid objects around you right now. You are grounded in this room, not in your fear.",
            category = QuoteCategory.PANIC_DEESCALATION
        ),
        Quote(
            id = "panic-5",
            text = "Breathe out a little longer than you breathe in. Your nervous system knows how to settle.",
            category = QuoteCategory.PANIC_DEESCALATION
        ),

        // Self-Compassion
        Quote(
            id = "comp-1",
            text = "You do not have to have everything figured out right in this very second.",
            category = QuoteCategory.SELF_COMPASSION
        ),
        Quote(
            id = "comp-2",
            text = "Give yourself permission to pause. Rest is not something you must earn through exhaustion.",
            category = QuoteCategory.SELF_COMPASSION
        ),
        Quote(
            id = "comp-3",
            text = "Talk to yourself today with the tenderness you would offer a tired friend.",
            category = QuoteCategory.SELF_COMPASSION
        ),
        Quote(
            id = "comp-4",
            text = "It is okay if all you did today was breathe and get through.",
            category = QuoteCategory.SELF_COMPASSION
        ),

        // Letting Go & Acceptance
        Quote(
            id = "let-1",
            text = "Thoughts are just visitors in your mind, not permanent tenants or undeniable facts.",
            category = QuoteCategory.LETTING_GO
        ),
        Quote(
            id = "let-2",
            text = "You cannot control the waves, but you can learn to float.",
            author = "Jon Kabat-Zinn",
            category = QuoteCategory.LETTING_GO
        ),
        Quote(
            id = "let-3",
            text = "Whatever you are holding with clenched fists, open your hands and let it rest.",
            category = QuoteCategory.LETTING_GO
        ),
        Quote(
            id = "let-4",
            text = "Worry does not empty tomorrow of its sorrow, it only empties today of its strength.",
            author = "Corrie ten Boom",
            category = QuoteCategory.LETTING_GO
        ),

        // Quiet Strength
        Quote(
            id = "str-1",
            text = "You have survived 100% of your most anxious days so far. Your track record is unbroken.",
            category = QuoteCategory.INNER_STRENGTH
        ),
        Quote(
            id = "str-2",
            text = "Peace is not the absence of chaos, but quiet confidence in the middle of it.",
            category = QuoteCategory.INNER_STRENGTH
        ),
        Quote(
            id = "str-3",
            text = "Small, slow steps forward are still steps forward.",
            category = QuoteCategory.INNER_STRENGTH
        )
    )

    private val favoriteIds = MutableStateFlow<Set<String>>(emptySet())

    private val _quotes = MutableStateFlow(allQuotes)
    val quotes: StateFlow<List<Quote>> = _quotes.asStateFlow()

    fun toggleFavorite(quoteId: String) {
        val currentFavs = favoriteIds.value.toMutableSet()
        if (currentFavs.contains(quoteId)) {
            currentFavs.remove(quoteId)
        } else {
            currentFavs.add(quoteId)
        }
        favoriteIds.value = currentFavs

        _quotes.value = allQuotes.map { quote ->
            quote.copy(isFavorite = currentFavs.contains(quote.id))
        }
    }

    fun getDailyQuote(): Quote {
        val index = (kotlin.math.abs(System.currentTimeMillis().hashCode()) % allQuotes.size)
        return allQuotes[index]
    }

    fun getQuotesByCategory(category: QuoteCategory): List<Quote> {
        val favs = favoriteIds.value
        return allQuotes.filter { it.category == category }.map {
            it.copy(isFavorite = favs.contains(it.id))
        }
    }
}
