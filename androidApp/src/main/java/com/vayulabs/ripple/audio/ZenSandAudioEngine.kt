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
 * Procedural acoustic synthesizer for Zen Sand Garden interactions.
 * Synthesizes realistic granular dry sand raking friction and gentle wind breeze.
 */
class ZenSandAudioEngine(private val context: Context) {
    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(8)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private val rakeSoundIds = mutableListOf<Int>()
    private var breezeSoundId: Int = 0

    @Volatile
    var isReady = false
        private set

    init {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Generate 3 variations of realistic sand scrape friction (dry granular silica)
                val rakeDurations = listOf(140, 180, 220)
                rakeDurations.forEachIndexed { idx, durMs ->
                    val wav = generateSandScrapeWav(durMs)
                    val temp = File(context.cacheDir, "sand_rake_$idx.wav")
                    FileOutputStream(temp).use { it.write(wav) }
                    val id = soundPool.load(temp.path, 1)
                    if (id != 0) {
                        synchronized(rakeSoundIds) { rakeSoundIds.add(id) }
                    }
                }

                // Generate gentle wind breeze sweep (for leveling sand)
                val breezeWav = generateBreezeWav(600)
                val breezeTemp = File(context.cacheDir, "sand_breeze.wav")
                FileOutputStream(breezeTemp).use { it.write(breezeWav) }
                breezeSoundId = soundPool.load(breezeTemp.path, 1)

                isReady = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun playRakeScrape(speedFactor: Float = 1.0f) {
        if (!isReady) return
        val id = synchronized(rakeSoundIds) {
            if (rakeSoundIds.isNotEmpty()) rakeSoundIds[Random.nextInt(rakeSoundIds.size)] else 0
        }
        if (id != 0) {
            val pitch = (0.85f + (speedFactor * 0.3f).coerceIn(0f, 0.5f)).coerceIn(0.7f, 1.4f)
            val vol = (0.35f + (speedFactor * 0.2f).coerceIn(0f, 0.45f)).coerceIn(0.2f, 0.8f)
            soundPool.play(id, vol, vol, 1, 0, pitch)
        }
    }

    fun playBreeze() {
        if (!isReady || breezeSoundId == 0) return
        soundPool.play(breezeSoundId, 0.55f, 0.55f, 2, 0, 1.0f)
    }

    fun release() {
        soundPool.release()
    }

    private fun generateSandScrapeWav(durationMs: Int): ByteArray {
        val sampleRate = 44100
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val pcm = ShortArray(numSamples)
        val random = Random(42 + durationMs)

        // Filter state for bandpass filtered granular noise (simulating sand grain friction ~1200-3400 Hz)
        var lowPass = 0.0
        var bandPass = 0.0

        for (i in 0 until numSamples) {
            val t = i.toDouble() / numSamples
            // Amplitude envelope: quick rise, sustaining granular rasp, soft tail
            val env = sin(t * PI).coerceIn(0.0, 1.0) * (1.0 - 0.2 * t)

            // Random granular noise
            val white = (random.nextDouble() * 2.0 - 1.0)
            
            // Subtle granular micro-clicks (individual quartz grains rolling)
            val grainClick = if (random.nextDouble() < 0.04) (random.nextDouble() * 2.0 - 1.0) * 1.5 else 0.0

            // 2-pole simple bandpass around 2200 Hz
            lowPass += 0.28 * (white + grainClick - lowPass)
            bandPass += 0.32 * (lowPass - bandPass)

            val sample = (bandPass * env * 14000.0).coerceIn(-32000.0, 32000.0)
            pcm[i] = sample.toInt().toShort()
        }

        return createWavFile(pcm, sampleRate)
    }

    private fun generateBreezeWav(durationMs: Int): ByteArray {
        val sampleRate = 44100
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val pcm = ShortArray(numSamples)
        val random = Random(101)

        var smoothNoise = 0.0

        for (i in 0 until numSamples) {
            val t = i.toDouble() / numSamples
            // Swell envelope: smooth bell curve
            val env = sin(t * PI)
            val white = (random.nextDouble() * 2.0 - 1.0)
            // Low-pass filter for smooth airy wind whoosh (~400-800Hz)
            smoothNoise += 0.08 * (white - smoothNoise)

            val sample = (smoothNoise * env * 18000.0).coerceIn(-32000.0, 32000.0)
            pcm[i] = sample.toInt().toShort()
        }

        return createWavFile(pcm, sampleRate)
    }

    private fun createWavFile(pcm: ShortArray, sampleRate: Int): ByteArray {
        val byteRate = sampleRate * 2
        val dataSize = pcm.size * 2
        val totalSize = 36 + dataSize

        val buffer = ByteBuffer.allocate(44 + dataSize)
        buffer.order(ByteOrder.LITTLE_ENDIAN)

        buffer.put("RIFF".toByteArray())
        buffer.putInt(totalSize)
        buffer.put("WAVE".toByteArray())

        buffer.put("fmt ".toByteArray())
        buffer.putInt(16)
        buffer.putShort(1.toShort()) // PCM
        buffer.putShort(1.toShort()) // Mono
        buffer.putInt(sampleRate)
        buffer.putInt(byteRate)
        buffer.putShort(2.toShort()) // block align
        buffer.putShort(16.toShort()) // bits per sample

        buffer.put("data".toByteArray())
        buffer.putInt(dataSize)

        for (sample in pcm) {
            buffer.putShort(sample)
        }

        return buffer.array()
    }
}
