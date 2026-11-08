package com.vayulabs.ripple.shared.domain.model

enum class SensoryType {
    FLUID_CANVAS,
    WORRY_STONE,
    BUBBLE_POP,
    ZEN_SAND,
    SINGING_BOWL,
    KALEIDOSCOPE,
    COLOR_SORT
}

data class SensoryTool(
    val type: SensoryType,
    val title: String,
    val subtitle: String,
    val description: String,
    val iconName: String,
    val isEnabled: Boolean = true
)

object SensoryCatalog {
    val tools = listOf(
        SensoryTool(
            type = SensoryType.FLUID_CANVAS,
            title = "Liquid Silk",
            subtitle = "Fluid velocity simulation",
            description = "Touch and swirl slow-moving pastel smoke ripples across deep darkness.",
            iconName = "water",
            isEnabled = true
        ),
        SensoryTool(
            type = SensoryType.WORRY_STONE,
            title = "Tactile Stone",
            subtitle = "Haptic friction grounding",
            description = "Rub your thumb rhythmically against a polished digital pebble with dynamic micro-vibrations.",
            iconName = "lens",
            isEnabled = true
        ),
        SensoryTool(
            type = SensoryType.BUBBLE_POP,
            title = "Tactile Bubbles",
            subtitle = "Floating organic bubbles",
            description = "Pop translucent floating bubbles with organic acoustic snaps and tactile feedback.",
            iconName = "bubble_chart",
            isEnabled = true
        ),
        SensoryTool(
            type = SensoryType.ZEN_SAND,
            title = "Zen Sand Garden",
            subtitle = "Kinetic rake grooves",
            description = "Rake smooth geometric and organic grooves through fine digital sand with tactile feedback.",
            iconName = "grain",
            isEnabled = false
        ),
        SensoryTool(
            type = SensoryType.SINGING_BOWL,
            title = "Singing Bowl",
            subtitle = "Acoustic resonance tuning",
            description = "Trace circular motions around a Tibetan bronze rim to build harmonic 432 Hz resonance.",
            iconName = "album",
            isEnabled = false
        ),
        SensoryTool(
            type = SensoryType.KALEIDOSCOPE,
            title = "Mandala Drift",
            subtitle = "Symmetrical generative art",
            description = "Draw multi-axis symmetrical trails that slowly dissolve into peaceful emptiness.",
            iconName = "flare",
            isEnabled = false
        ),
        SensoryTool(
            type = SensoryType.COLOR_SORT,
            title = "Gradient Flow",
            subtitle = "Cognitive sensory reset",
            description = "Arrange soothing muted pastel swatches into a seamless continuous spectrum.",
            iconName = "gradient",
            isEnabled = false
        )
    )
}
