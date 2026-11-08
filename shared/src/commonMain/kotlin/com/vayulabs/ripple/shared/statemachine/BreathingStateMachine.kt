package com.vayulabs.ripple.shared.statemachine

import com.vayulabs.ripple.shared.domain.model.BreathingPhase
import com.vayulabs.ripple.shared.domain.model.BreathingStep
import com.vayulabs.ripple.shared.domain.model.BreathingTechnique
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BreathingState(
    val isRunning: Boolean = false,
    val technique: BreathingTechnique = BreathingTechnique.PHYSIOLOGICAL_SIGH,
    val currentStepIndex: Int = 0,
    val currentPhase: BreathingPhase = BreathingPhase.IDLE,
    val currentStepDurationMillis: Long = 0L,
    val cycleCount: Int = 0,
    val elapsedSessionMillis: Long = 0L
)

class BreathingStateMachine(
    private val scope: CoroutineScope
) {
    private val _state = MutableStateFlow(BreathingState())
    val state: StateFlow<BreathingState> = _state.asStateFlow()

    private var sessionJob: Job? = null

    fun selectTechnique(technique: BreathingTechnique) {
        if (_state.value.isRunning) {
            stop()
        }
        _state.value = BreathingState(
            technique = technique,
            currentPhase = BreathingPhase.IDLE
        )
    }

    fun start() {
        if (_state.value.isRunning) return

        sessionJob?.cancel()
        sessionJob = scope.launch {
            val currentTech = _state.value.technique
            var currentCycle = 0
            var elapsed = 0L

            _state.value = _state.value.copy(
                isRunning = true,
                currentStepIndex = 0,
                cycleCount = 0,
                elapsedSessionMillis = 0L
            )

            while (true) {
                for (stepIndex in currentTech.steps.indices) {
                    val step: BreathingStep = currentTech.steps[stepIndex]
                    val duration = step.durationMillis

                    // Notify UI of phase transition and duration for hardware-synchronized 120Hz animation
                    _state.value = _state.value.copy(
                        currentStepIndex = stepIndex,
                        currentPhase = step.phase,
                        currentStepDurationMillis = duration,
                        cycleCount = currentCycle,
                        elapsedSessionMillis = elapsed
                    )

                    delay(duration)
                    elapsed += duration
                }
                currentCycle++
                _state.value = _state.value.copy(cycleCount = currentCycle)
            }
        }
    }

    fun stop() {
        sessionJob?.cancel()
        sessionJob = null
        _state.value = _state.value.copy(
            isRunning = false,
            currentPhase = BreathingPhase.IDLE,
            currentStepDurationMillis = 0L
        )
    }

    fun toggle() {
        if (_state.value.isRunning) {
            stop()
        } else {
            start()
        }
    }
}
