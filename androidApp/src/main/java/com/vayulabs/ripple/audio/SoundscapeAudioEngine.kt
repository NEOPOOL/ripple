package com.vayulabs.ripple.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.*
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

enum class BinauralMode(val label: String, val freqDiff: Double, val somaticDesc: String) {
    OFF("Off", 0.0, "Natural ambient soundscape"),
    THETA("Theta • 6 Hz", 6.0, "Deep somatic relaxation & hypervigilance reduction"),
    ALPHA("Alpha • 10 Hz", 10.0, "Calm presence & reduced cognitive chatter")
}

/**
 * Real-time Procedural Audio Synthesizer for Ripple.
 * Generates high-fidelity Brown Noise, Rainfall, Ocean Waves, and 432Hz Ambient Drones
 * directly on device with zero external MP3 downloads or lag.
 *
 * Fully reactive to Jetpack Compose with observable playback states.
 */
class SoundscapeAudioEngine {
    private val sampleRate = 44100
    private val bufferSize = AudioTrack.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_OUT_STEREO,
        AudioFormat.ENCODING_PCM_16BIT
    ).coerceAtLeast(sampleRate / 4)

    private var audioTrack: AudioTrack? = null
    private var synthesisJob: Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Default + Job())

    // Observable Compose states for UI reactivity
    var isPlaying by mutableStateOf(false)
        private set

    var hasActiveAudio by mutableStateOf(false)
        private set

    var activeTrackTitle by mutableStateOf("Deep Brown Noise")
        private set

    var activeTrackSubtitle by mutableStateOf("Acoustic Shielding • Ambient Flow")
        private set

    // Track volume states observable by Compose sliders and cards
    var brownNoiseVolume by mutableFloatStateOf(0.0f)
        private set
    var rainVolume by mutableFloatStateOf(0.0f)
        private set
    var oceanVolume by mutableFloatStateOf(0.0f)
        private set
    var windVolume by mutableFloatStateOf(0.0f)
        private set
    var droneVolume by mutableFloatStateOf(0.0f)
        private set

    // Brainwave entrainment & breath coupling states
    var binauralMode by mutableStateOf(BinauralMode.OFF)
        private set
    var isBreathCoupled by mutableStateOf(false)
        private set

    // Real-time volatile volume registers for audio thread (zero snapshot overhead)
    @Volatile private var synthBrownVol = 0.0f
    @Volatile private var synthRainVol = 0.0f
    @Volatile private var synthOceanVol = 0.0f
    @Volatile private var synthWindVol = 0.0f
    @Volatile private var synthDroneVol = 0.0f
    @Volatile private var synthBinauralDiff = 0.0
    @Volatile private var synthBreathCoupled = false

    @Volatile private var isSynthRunning = false

    fun start() {
        if (isSynthRunning) return
        isSynthRunning = true
        isPlaying = true

        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize * 2)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        audioTrack?.play()

        synthesisJob = coroutineScope.launch {
            val chunkSamples = 2048
            val pcmData = ShortArray(chunkSamples * 2) // Stereo (L, R)

            // Brown noise state
            var brownL = 0.0
            var brownR = 0.0

            // Rain noise filter state
            var rainFilterL = 0.0
            var rainFilterR = 0.0

            // Ocean LFO state (cycles every ~7.5 seconds)
            var oceanPhase = 0.0
            val oceanPhaseStep = (2.0 * PI) / (sampleRate * 7.5)

            // Wind LFO state
            var windPhase = 0.0
            val windPhaseStep = (2.0 * PI) / (sampleRate * 5.0)

            // 432 Hz Sine Drone state
            var dronePhase432 = 0.0
            val droneStep432 = (2.0 * PI * 432.0) / sampleRate
            var dronePhase438 = 0.0
            val droneStep438 = (2.0 * PI * 438.0) / sampleRate

            // Dedicated Binaural Beats Carrier state (216 Hz carrier)
            var binauralPhaseL = 0.0
            var binauralPhaseR = 0.0
            val carrierFreq = 216.0
            val binauralStepL = (2.0 * PI * carrierFreq) / sampleRate

            // Breath Pacing LFO: 10-second respiratory cycle (4s Inhale, 6s Exhale)
            var breathPhase = 0.0
            val breathPhaseStep = (2.0 * PI) / (sampleRate * 10.0)

            while (isActive && isSynthRunning) {
                val bVol = synthBrownVol
                val rVol = synthRainVol
                val oVol = synthOceanVol
                val wVol = synthWindVol
                val dVol = synthDroneVol
                val bDiff = synthBinauralDiff
                val isBreath = synthBreathCoupled

                val anyPlaying = bVol > 0.01f || rVol > 0.01f || oVol > 0.01f || wVol > 0.01f || dVol > 0.01f || bDiff > 0.01

                for (i in 0 until chunkSamples) {
                    var sampleL = 0.0
                    var sampleR = 0.0

                    if (anyPlaying) {
                        // Breath Modulation Factor
                        val breathFactor = if (isBreath) {
                            breathPhase += breathPhaseStep
                            if (breathPhase > 2.0 * PI) breathPhase -= 2.0 * PI
                            0.65 + 0.35 * ((sin(breathPhase) + 1.0) / 2.0)
                        } else 1.0

                        // 1. Deep Brown Noise (1/f² integration - muted deep rumble)
                        if (bVol > 0.01f) {
                            val whiteL = (Random.nextDouble() * 2.0 - 1.0)
                            val whiteR = (Random.nextDouble() * 2.0 - 1.0)
                            brownL = (brownL + (0.025 * whiteL)) / 1.025
                            brownR = (brownR + (0.025 * whiteR)) / 1.025
                            sampleL += brownL * 0.45 * bVol * breathFactor
                            sampleR += brownR * 0.45 * bVol * breathFactor
                        }

                        // 2. Skylight Rain (high-frequency soft drops)
                        if (rVol > 0.01f) {
                            val whiteL = (Random.nextDouble() * 2.0 - 1.0)
                            val whiteR = (Random.nextDouble() * 2.0 - 1.0)
                            rainFilterL = rainFilterL * 0.85 + whiteL * 0.15
                            rainFilterR = rainFilterR * 0.85 + whiteR * 0.15
                            val drop = if (Random.nextInt(1000) < 5) Random.nextDouble() * 0.25 else 0.0
                            sampleL += (rainFilterL * 0.25 + drop) * rVol * breathFactor
                            sampleR += (rainFilterR * 0.25 + drop) * rVol * breathFactor
                        }

                        // 3. Ocean Waves (LFO swell and recede)
                        if (oVol > 0.01f) {
                            oceanPhase += oceanPhaseStep
                            if (oceanPhase > 2.0 * PI) oceanPhase -= 2.0 * PI
                            val swell = ((sin(oceanPhase) + 1.0) / 2.0).coerceIn(0.05, 1.0) * breathFactor
                            val waveNoiseL = (Random.nextDouble() * 2.0 - 1.0) * swell
                            val waveNoiseR = (Random.nextDouble() * 2.0 - 1.0) * swell
                            sampleL += waveNoiseL * 0.3 * oVol
                            sampleR += waveNoiseR * 0.3 * oVol
                        }

                        // 4. Forest Wind (resonant whispering breeze)
                        if (wVol > 0.01f) {
                            windPhase += windPhaseStep
                            if (windPhase > 2.0 * PI) windPhase -= 2.0 * PI
                            val windMod = ((sin(windPhase) * 0.4) + 0.6) * breathFactor
                            val whiteL = (Random.nextDouble() * 2.0 - 1.0) * windMod
                            val whiteR = (Random.nextDouble() * 2.0 - 1.0) * windMod
                            sampleL += whiteL * 0.22 * wVol
                            sampleR += whiteR * 0.22 * wVol
                        }

                        // 5. 432 Hz Warm Harmonic Drone
                        if (dVol > 0.01f) {
                            dronePhase432 += droneStep432
                            if (dronePhase432 > 2.0 * PI) dronePhase432 -= 2.0 * PI
                            dronePhase438 += droneStep438
                            if (dronePhase438 > 2.0 * PI) dronePhase438 -= 2.0 * PI

                            val sine432 = sin(dronePhase432)
                            val sine438 = sin(dronePhase438)
                            sampleL += sine432 * 0.18 * dVol
                            sampleR += sine438 * 0.18 * dVol
                        }

                        // 6. Dedicated Brainwave Binaural Beats (Theta / Alpha)
                        if (bDiff > 0.01) {
                            val stepR = (2.0 * PI * (carrierFreq + bDiff)) / sampleRate
                            binauralPhaseL += binauralStepL
                            if (binauralPhaseL > 2.0 * PI) binauralPhaseL -= 2.0 * PI
                            binauralPhaseR += stepR
                            if (binauralPhaseR > 2.0 * PI) binauralPhaseR -= 2.0 * PI

                            sampleL += sin(binauralPhaseL) * 0.14
                            sampleR += sin(binauralPhaseR) * 0.14
                        }
                    }

                    // Soft clipping and conversion to 16-bit PCM
                    val finalL = (sampleL.coerceIn(-1.0, 1.0) * 32767.0).toInt().toShort()
                    val finalR = (sampleR.coerceIn(-1.0, 1.0) * 32767.0).toInt().toShort()

                    pcmData[i * 2] = finalL
                    pcmData[i * 2 + 1] = finalR
                }

                audioTrack?.write(pcmData, 0, pcmData.size)
            }
        }
    }

    /**
     * Toggles or adjusts the volume of an individual soundscape channel.
     */
    fun updateTrackVolume(trackId: String, play: Boolean, volume: Float) {
        val targetVol = if (play) volume else 0.0f
        when (trackId) {
            "deep_brown_noise" -> {
                brownNoiseVolume = targetVol
                synthBrownVol = targetVol
            }
            "soft_rain" -> {
                rainVolume = targetVol
                synthRainVol = targetVol
            }
            "ocean_swell" -> {
                oceanVolume = targetVol
                synthOceanVol = targetVol
            }
            "forest_wind" -> {
                windVolume = targetVol
                synthWindVol = targetVol
            }
            "theta_drone_432hz" -> {
                droneVolume = targetVol
                synthDroneVol = targetVol
            }
        }

        val anyActive = brownNoiseVolume > 0.01f ||
                rainVolume > 0.01f ||
                oceanVolume > 0.01f ||
                windVolume > 0.01f ||
                droneVolume > 0.01f ||
                synthBinauralDiff > 0.01

        hasActiveAudio = anyActive

        if (anyActive) {
            updateMetadata()
            if (!isSynthRunning) {
                start()
            }
        } else {
            if (isSynthRunning) {
                stop()
            }
        }
    }

    fun setBinaural(mode: BinauralMode) {
        binauralMode = mode
        synthBinauralDiff = mode.freqDiff
        if (mode != BinauralMode.OFF) {
            hasActiveAudio = true
            updateMetadata()
            if (!isSynthRunning) start()
        } else {
            val anyActive = brownNoiseVolume > 0.01f || rainVolume > 0.01f || oceanVolume > 0.01f || windVolume > 0.01f || droneVolume > 0.01f
            hasActiveAudio = anyActive
            if (!anyActive && isSynthRunning) stop()
        }
    }

    fun toggleBreathCoupling() {
        isBreathCoupled = !isBreathCoupled
        synthBreathCoupled = isBreathCoupled
    }

    private fun updateMetadata() {
        val (title, subtitle) = when {
            rainVolume > 0.01f && oceanVolume > 0.01f -> "Rain & Ocean Tide" to "Dual Layer Soundscape"
            rainVolume > 0.01f -> "Skylight Rain" to "Soft Raindrop Rhythm"
            brownNoiseVolume > 0.01f -> "Deep Brown Noise" to "Acoustic Shielding • Ambient Flow"
            oceanVolume > 0.01f -> "Ocean Waves" to "Rhythmic Tidal Swell"
            windVolume > 0.01f -> "Forest Wind" to "Whispering Breeze"
            droneVolume > 0.01f -> "432 Hz Drone" to "Theta Harmonic Resonance"
            else -> "Acoustic Soundscape" to "Calming Acoustic Shielding"
        }
        activeTrackTitle = title
        activeTrackSubtitle = subtitle
    }

    fun isAudioActive(): Boolean = isPlaying

    /**
     * Seamlessly pauses the audio synthesizer without clearing volume settings.
     */
    fun pause() {
        if (!isSynthRunning) return
        isSynthRunning = false
        isPlaying = false
        synthesisJob?.cancel()
        synthesisJob = null
        try {
            audioTrack?.pause()
        } catch (_: Exception) {}
    }

    /**
     * Resumes the audio synthesizer using the active channel volumes.
     */
    fun resume() {
        if (isSynthRunning) return
        val anyActive = synthBrownVol > 0.01f ||
                synthRainVol > 0.01f ||
                synthOceanVol > 0.01f ||
                synthWindVol > 0.01f ||
                synthDroneVol > 0.01f

        if (anyActive) {
            start()
        } else {
            // Default to Deep Brown Noise if resume called without active channels
            synthBrownVol = 0.5f
            brownNoiseVolume = 0.5f
            hasActiveAudio = true
            updateMetadata()
            start()
        }
    }

    /**
     * Stops the audio synthesizer, releases audio resources, and resets channel volumes.
     */
    fun stop() {
        isSynthRunning = false
        isPlaying = false
        hasActiveAudio = false
        synthesisJob?.cancel()
        synthesisJob = null
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (_: Exception) {}
        audioTrack = null

        brownNoiseVolume = 0.0f
        rainVolume = 0.0f
        oceanVolume = 0.0f
        windVolume = 0.0f
        droneVolume = 0.0f

        synthBrownVol = 0.0f
        synthRainVol = 0.0f
        synthOceanVol = 0.0f
        synthWindVol = 0.0f
        synthDroneVol = 0.0f
    }
}
