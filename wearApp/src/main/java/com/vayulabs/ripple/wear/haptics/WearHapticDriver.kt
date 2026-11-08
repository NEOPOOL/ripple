package com.vayulabs.ripple.wear.haptics

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import com.vayulabs.ripple.shared.domain.model.BreathingPhase

class WearHapticDriver(context: Context) {
    private val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator

    fun onPhaseChanged(phase: BreathingPhase) {
        if (vibrator == null || !vibrator.hasVibrator()) return

        when (phase) {
            BreathingPhase.INHALE -> {
                // Rising vibration ramp on wrist
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val timings = longArrayOf(0, 50, 50, 50)
                    val amplitudes = intArrayOf(0, 60, 0, 120)
                    vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(60)
                }
            }
            BreathingPhase.HOLD_AFTER_INHALE, BreathingPhase.HOLD_AFTER_EXHALE -> {
                // Double tap
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK))
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(25, 100))
                }
            }
            BreathingPhase.EXHALE -> {
                // Gentle falling pulse
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val timings = longArrayOf(0, 80, 50, 30)
                    val amplitudes = intArrayOf(0, 100, 0, 40)
                    vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(40)
                }
            }
            BreathingPhase.IDLE -> {
                vibrator.cancel()
            }
        }
    }

    fun stop() {
        vibrator?.cancel()
    }
}
