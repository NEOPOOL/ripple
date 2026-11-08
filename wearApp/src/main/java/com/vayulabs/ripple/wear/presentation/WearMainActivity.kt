package com.vayulabs.ripple.wear.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.wear.compose.material.MaterialTheme
import com.vayulabs.ripple.shared.statemachine.BreathingStateMachine
import com.vayulabs.ripple.wear.haptics.WearHapticDriver

class WearMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val hapticDriver = WearHapticDriver(applicationContext)

        setContent {
            MaterialTheme {
                val scope = rememberCoroutineScope()
                val stateMachine = remember { BreathingStateMachine(scope) }

                WearBreathingScreen(
                    stateMachine = stateMachine,
                    hapticDriver = hapticDriver
                )
            }
        }
    }
}
