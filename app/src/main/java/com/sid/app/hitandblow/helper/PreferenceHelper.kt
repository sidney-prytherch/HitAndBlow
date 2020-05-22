package com.sid.app.hitandblow.helper

import android.content.SharedPreferences

class PreferenceHelper(private val sharedPreferences: SharedPreferences) {

    fun getBool(preferenceKey: String): Boolean {
        return sharedPreferences.getBoolean(preferenceKey, true)
    }

    fun getInt(preferenceKey: String, defValue: Int):Int {
        return sharedPreferences.getInt(preferenceKey, defValue)
    }

    fun setBool(preferenceKey: String, value: Boolean) {
        sharedPreferences.edit()
            .putBoolean(preferenceKey, value)
            .apply()
    }

    fun setInt(preferenceKey: String, value: Int) {
        sharedPreferences.edit()
            .putInt(preferenceKey, value)
            .apply()
    }

    companion object {
        const val RED_ENABLED = "red_enabled"
        const val ORANGE_ENABLED = "orange_enabled"
        const val YELLOW_ENABLED = "yellow_enabled"
        const val GREEN_ENABLED = "green_enabled"
        const val BLUEGREEN_ENABLED = "bluegreen_enabled"
        const val LIGHTBLUE_ENABLED = "lightblue_enabled"
        const val DARKBLUE_ENABLED = "darkblue_enabled"
        const val PURPLE_ENABLED = "purple_enabled"
        const val PINK_ENABLED = "pink_enabled"

        const val GUESSES = "guesses"
    }
}