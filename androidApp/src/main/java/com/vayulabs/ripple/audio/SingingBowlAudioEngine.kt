package com.vayulabs.ripple.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.SoundPool
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * Procedural Singing Bowl Acoustic Engine.
 * Provides real-time singing drone with rotational velocity volume modulation
 * and high-resonance metallic gong strike acoustics.
 */
class SingingBowlAudioEngine(private val context: Context) {
    private val sampleRate = 44100
    private var audioTrack: AudioTrack? = null
    private var droneJob: Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Default + Job())

    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(4)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private var strikeSoundId = 0
    @Volatile private var targetDroneVolume = 0.0f
    @Volatile private var currentDroneVolume = 0.0f
    @Volatile private var isRunning = false

    init {
        // Generate high-resolution metallic singing bowl gong strike in background
        coroutineScope.launch {
            try {
                val strikeWav = generateGongWav()
                val tempFile = File(context.cacheDir, "singing_bowl_strike.wav")
                FileOutputStream(tempFile).use { it.write(strikeWav) }
                strikeSoundId = soundPool.load(tempFile.path, 1)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        startContinuousDrone()
    }

    private fun startContinuousDrone() {
        if (isRunning) return
        isRunning = true

        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        ).coerceAtLeast(sampleRate / 4)

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
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize * 2)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        audioTrack?.play()

        droneJob = coroutineScope.launch {
            val chunk = 1024
            val pcm = ShortArray(chunk)
            var phase1 = 0.0
            var phase2 = 0.0
            var phase3 = 0.0

            val f1 = 432.0 // Warm Earth fundamental
            val f2 = 864.0 // Octave
            val f3 = 1296.0 // Harmonic shimmer

            val step1 = 2.0 * PI * f1 / sampleRate
            val step2 = 2.0 * PI * f2 / sampleRate
            val step3 = 2.0 * PI * f3 / sampleRate

            while (isActive && isRunning) {
                // Smooth envelope damping toward target volume
                currentDroneVolume += (targetDroneVolume - currentDroneVolume) * 0.05f

                for (i in 0 until chunk) {
                    phase1 += step1
                    if (phase1 > 2.0 * PI) phase1 -= 2.0 * PI
                    phase2 += step2
                    if (phase2 > 2.0 * PI) phase2 -= 2.0 * PI
                    phase3 += step3
                    if (phase3 > 2.0 * PI) phase3 -= 2.0 * PI

                    // Harmonic bowl resonance
                    val sample = (sin(phase1) * 0.65 + sin(phase2) * 0.25 + sin(phase3) * 0.10) * currentDroneVolume
                    pcm[i] = (sample * 32767.0).coerceIn(-32768.0, 32767.0).toInt().toShort()
                }

                audioTrack?.write(pcm, 0, chunk)
            }
        }
    }

    /**
     * Updates rotational energy from user tracing rim (0.0 to 1.0).
     */
    fun setRimIntensity(intensity: Float) {
        targetDroneVolume = intensity.coerceIn(0.0f, 0.75f)
    }

    /**
     * Plays a resonant strike with rich gong decay.
     */
    fun playStrike() {
        if (strikeSoundId != 0) {
            soundPool.play(strikeSoundId, 0.65f, 0.65f, 1, 0, 1.0f)
        }
    }

    fun release() {
        isRunning = false
        droneJob?.cancel()
        audioTrack?.stop()
        audioTrack?.release()
        soundPool.release()
    }

    private fun generateGongWav(): ByteArray {
        val durationMs = 2800
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val pcmData = ShortArray(numSamples)

        val durationSec = durationMs / 1000.0
        val f1 = 432.0
        val f2 = 864.0
        val f3 = 1296.0

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val envelope = exp(-t * 2.2) // Exponential metallic decay

            // Multi-frequency bronze gong harmonics
            val sample = (
                sin(2.0 * PI * f1 * t) * 0.60 +
                sin(2.0 * PI * f2 * t) * 0.28 +
                sin(2.0 * PI * f3 * t) * 0.12
            ) * envelope

            pcmData[i] = (sample * 32767.0).coerceIn(-32768.0, 32767.0).toInt().toShort()
        }

        val totalDataLen = numSamples * 2
        val totalFileLen = totalDataLen + 36
        val buffer = ByteBuffer.allocate(44 + totalDataLen).order(ByteOrder.LITTLE_ENDIAN)

        buffer.put("RIFF".toByteArray())
        buffer.putInt(totalFileLen)
        buffer.put("WAVE".toByteArray())
        buffer.put("fmt ".toByteArray())
        buffer.putInt(16)
        buffer.putShort(1.toShort()) // PCM
        buffer.putShort(1.toShort()) // Mono
        buffer.putInt(sampleRate)
        buffer.putInt(sampleRate * 2)
        buffer.putShort(2.toShort())
        buffer.putShort(16.toShort())
        buffer.put("data".toByteArray())
        buffer.putInt(totalDataLen)

        for (sample in pcmData) {
            buffer.putShort(sample)
        }

        return buffer.array()
    }
}
