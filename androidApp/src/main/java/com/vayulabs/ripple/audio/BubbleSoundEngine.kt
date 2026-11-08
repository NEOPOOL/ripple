package com.vayulabs.ripple.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

/**
 * High-performance, zero-latency procedural bubble pop synthesizer for somatic feedback.
 * Generates organic cavity resonance pop wave variations in-memory and streams
 * polyphonically via SoundPool with pitch modulation.
 */
class BubbleSoundEngine(private val context: Context) {
    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(14)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private val soundIds = mutableListOf<Int>()
    @Volatile
    var isReady = false
        private set

    init {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Generate 3 authentic plastic bubble wrap micro-snaps:
                // Characterized by a sharp, tiny plastic transient (1800-2400Hz down to 800Hz)
                // and rapid exponential damping in ~20-25ms.
                val specs = listOf(
                    // Crisp delicate bubble wrap snap
                    PopSpec(startFreq = 2100.0, endFreq = 980.0, durationMs = 24, decay = 185.0),
                    // Soft muted plastic pop
                    PopSpec(startFreq = 1850.0, endFreq = 840.0, durationMs = 26, decay = 165.0),
                    // High-pitch micro-bubble snap
                    PopSpec(startFreq = 2350.0, endFreq = 1120.0, durationMs = 21, decay = 210.0)
                )

                specs.forEachIndexed { index, spec ->
                    val wavBytes = generateWav(spec)
                    val tempFile = File(context.cacheDir, "bubble_wrap_pop_$index.wav")
                    FileOutputStream(tempFile).use { it.write(wavBytes) }
                    val soundId = soundPool.load(tempFile.path, 1)
                    if (soundId != 0) {
                        synchronized(soundIds) {
                            soundIds.add(soundId)
                        }
                    }
                }
                isReady = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Plays a small, quiet, satisfying plastic bubble wrap pop with size-dependent acoustic pitch.
     */
    fun playPop(pitchModifier: Float = 1.0f) {
        val ids = synchronized(soundIds) { soundIds.toList() }
        if (ids.isEmpty()) return
        val soundId = ids.random()
        val pitch = ((0.96f + Random.nextFloat() * 0.10f) * pitchModifier).coerceIn(0.65f, 1.85f)
        // Soft, understated volume (0.34) mimicking delicate bubble membrane snap
        soundPool.play(soundId, 0.34f, 0.34f, 1, 0, pitch)
    }

    fun release() {
        soundPool.release()
    }

    private data class PopSpec(
        val startFreq: Double,
        val endFreq: Double,
        val durationMs: Int,
        val decay: Double
    )

    private fun generateWav(spec: PopSpec): ByteArray {
        val sampleRate = 44100
        val numSamples = (sampleRate * (spec.durationMs / 1000.0)).toInt()
        val pcmData = ShortArray(numSamples)

        val durationSec = spec.durationMs / 1000.0
        val chirpRate = (spec.endFreq - spec.startFreq) / durationSec

        for (n in 0 until numSamples) {
            val t = n.toDouble() / sampleRate
            val env = exp(-t * spec.decay)
            val phase = 2.0 * PI * (spec.startFreq * t + 0.5 * chirpRate * t * t)

            // Realistic plastic bubble wrap snap:
            // High-frequency plastic membrane snap (3200Hz for ~1.5ms) + rapid cavity drop
            val fundamental = sin(phase)
            val plasticSnap = if (n < (sampleRate * 0.0015)) {
                sin(2.0 * PI * 3200.0 * t) * (1.0 - n / (sampleRate * 0.0015)) * 0.55
            } else 0.0

            val sample = (fundamental * 0.70 + plasticSnap) * env * 0.80
            pcmData[n] = (sample.coerceIn(-1.0, 1.0) * 32767.0).toInt().toShort()
        }

        // Build 16-bit PCM RIFF WAV
        val byteDataSize = numSamples * 2
        val totalSize = 36 + byteDataSize
        val buffer = ByteBuffer.allocate(44 + byteDataSize).order(ByteOrder.LITTLE_ENDIAN)

        buffer.put("RIFF".toByteArray())
        buffer.putInt(totalSize)
        buffer.put("WAVE".toByteArray())
        buffer.put("fmt ".toByteArray())
        buffer.putInt(16) // Subchunk1Size
        buffer.putShort(1) // PCM format
        buffer.putShort(1) // Mono
        buffer.putInt(sampleRate)
        buffer.putInt(sampleRate * 2) // ByteRate
        buffer.putShort(2) // BlockAlign
        buffer.putShort(16) // BitsPerSample
        buffer.put("data".toByteArray())
        buffer.putInt(byteDataSize)

        for (sample in pcmData) {
            buffer.putShort(sample)
        }

        return buffer.array()
    }
}
