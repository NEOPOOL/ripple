package com.vayulabs.ripple.shared.domain.model

enum class QuoteCategory(val displayName: String, val description: String) {
    PANIC_DEESCALATION("Panic & Overwhelm", "Short, grounding anchors for when adrenaline spikes"),
    SELF_COMPASSION("Self-Compassion", "Gentle permission to feel without judgment"),
    LETTING_GO("Release & Acceptance", "Releasing what is outside your circle of control"),
    INNER_STRENGTH("Quiet Strength", "Reminders of resilience through hard moments")
}

data class Quote(
    val id: String,
    val text: String,
    val author: String? = null,
    val category: QuoteCategory,
    val isFavorite: Boolean = false
)
