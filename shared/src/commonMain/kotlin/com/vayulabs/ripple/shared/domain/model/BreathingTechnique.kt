package com.vayulabs.ripple.shared.domain.model

enum class BreathingPhase(val label: String, val instruction: String) {
    IDLE("Stillness", "Rest your hands and soften your shoulders"),
    INHALE("Inhale", "Breathe in deeply through your nose"),
    HOLD_AFTER_INHALE("Hold", "Pause gently, feeling the expansion"),
    EXHALE("Exhale", "Release slowly through your mouth"),
    HOLD_AFTER_EXHALE("Rest", "Stay empty, grounded in this moment")
}

data class BreathingStep(
    val phase: BreathingPhase,
    val durationMillis: Long
)

enum class BreathingTechnique(
    val title: String,
    val subtitle: String,
    val description: String,
    val therapeuticBenefit: String,
    val steps: List<BreathingStep>
) {
    PHYSIOLOGICAL_SIGH(
        title = "Physiological Sigh",
        subtitle = "Fastest neurobiological de-escalation",
        description = "Two rapid inhales through your nose, followed by one long, slow sigh out through your mouth.",
        therapeuticBenefit = "Pioneered by neuroscientists to quickly re-expand collapsed lung alveoli and discharge carbon dioxide, dropping autonomic arousal in seconds.",
        steps = listOf(
            BreathingStep(BreathingPhase.INHALE, 2500L),
            BreathingStep(BreathingPhase.HOLD_AFTER_INHALE, 500L), // Quick second inhale top-up
            BreathingStep(BreathingPhase.EXHALE, 5000L),
            BreathingStep(BreathingPhase.HOLD_AFTER_EXHALE, 1000L)
        )
    ),
    BOX_BREATHING(
        title = "Box Breathing (4-4-4-4)",
        subtitle = "Balance & steady focus",
        description = "Equal intervals of inhale, hold, exhale, and hold.",
        therapeuticBenefit = "Stabilizes the autonomic nervous system. Used by first responders and tactical professionals to clear adrenaline surges.",
        steps = listOf(
            BreathingStep(BreathingPhase.INHALE, 4000L),
            BreathingStep(BreathingPhase.HOLD_AFTER_INHALE, 4000L),
            BreathingStep(BreathingPhase.EXHALE, 4000L),
            BreathingStep(BreathingPhase.HOLD_AFTER_EXHALE, 4000L)
        )
    ),
    RELAXING_4_7_8(
        title = "4-7-8 Deep Calm",
        subtitle = "Natural nervous system sedative",
        description = "Inhale for 4 seconds, hold for 7 seconds, exhale slowly for 8 seconds.",
        therapeuticBenefit = "Strongly activates the vagus nerve, inducing parasympathetic tone. Highly effective for acute bedtime rumination.",
        steps = listOf(
            BreathingStep(BreathingPhase.INHALE, 4000L),
            BreathingStep(BreathingPhase.HOLD_AFTER_INHALE, 7000L),
            BreathingStep(BreathingPhase.EXHALE, 8000L),
            BreathingStep(BreathingPhase.HOLD_AFTER_EXHALE, 1000L)
        )
    ),
    RESONANT_COHERENCE(
        title = "Resonant Coherence",
        subtitle = "Cardiovascular synchronization",
        description = "Inhale for 5.5 seconds, exhale for 5.5 seconds (6 breaths per minute).",
        therapeuticBenefit = "Aligns heart rate variability (HRV) with respiratory oscillations, maximizing inner tranquility.",
        steps = listOf(
            BreathingStep(BreathingPhase.INHALE, 5500L),
            BreathingStep(BreathingPhase.EXHALE, 5500L)
        )
    );

    val totalCycleDurationMillis: Long
        get() = steps.sumOf { it.durationMillis }
}
