package com.vayulabs.ripple.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf

object UserPreferences {
    private const val PREFS_NAME = "ripple_user_prefs"
    private const val KEY_USER_NAME = "key_user_name"
    private const val KEY_FIRST_LAUNCH = "key_first_launch"

    val currentUserName = mutableStateOf<String?>(null)
    val isFirstLaunch = mutableStateOf(true)

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedName = prefs?.getString(KEY_USER_NAME, null)
        val firstLaunch = prefs?.getBoolean(KEY_FIRST_LAUNCH, true) ?: true

        currentUserName.value = savedName
        isFirstLaunch.value = firstLaunch
    }

    fun setUserName(name: String) {
        val trimmed = name.trim()
        currentUserName.value = trimmed
        isFirstLaunch.value = false

        prefs?.edit()?.apply {
            putString(KEY_USER_NAME, trimmed)
            putBoolean(KEY_FIRST_LAUNCH, false)
            apply()
        }
    }

    fun getUserNameOrDefault(default: String = "Friend"): String {
        return currentUserName.value?.takeIf { it.isNotBlank() } ?: default
    }
}
