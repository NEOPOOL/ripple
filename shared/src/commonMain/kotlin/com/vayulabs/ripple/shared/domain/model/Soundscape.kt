package com.vayulabs.ripple.shared.domain.model

enum class SoundCategory {
    NATURE,
    NOISE,
    AMBIENT
}

data class SoundTrack(
    val id: String,
    val title: String,
    val description: String,
    val category: SoundCategory,
    val defaultVolume: Float = 0.5f,
    val isPlaying: Boolean = false
)

object SoundscapeCatalog {
    val tracks = listOf(
        SoundTrack(
            id = "deep_brown_noise",
            title = "Deep Brown Noise",
            description = "Low-frequency rumble that mutes intrusive thoughts and softens hypervigilance",
            category = SoundCategory.NOISE,
            defaultVolume = 0.6f
        ),
        SoundTrack(
            id = "soft_rain",
            title = "Rain on Skylight",
            description = "Gentle continuous rainfall against glass",
            category = SoundCategory.NATURE,
            defaultVolume = 0.7f
        ),
        SoundTrack(
            id = "ocean_swell",
            title = "Ocean Tide",
            description = "Slow, rhythmic waves breaking softly on distant shore",
            category = SoundCategory.NATURE,
            defaultVolume = 0.5f
        ),
        SoundTrack(
            id = "forest_wind",
            title = "Forest Breeze",
            description = "Wind whispering through high pines and distant leaves",
            category = SoundCategory.NATURE,
            defaultVolume = 0.4f
        ),
        SoundTrack(
            id = "theta_drone_432hz",
            title = "432 Hz Warm Drone",
            description = "Harmonic calming synthesizer resonance tuned to deep relaxation",
            category = SoundCategory.AMBIENT,
            defaultVolume = 0.35f
        )
    )
}
