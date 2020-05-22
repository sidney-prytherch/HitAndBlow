package com.sid.app.hitandblow

import android.os.Bundle
import androidx.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.sid.app.hitandblow.helper.PreferenceHelper
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var preferenceManager: PreferenceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        }
        preferenceManager = PreferenceHelper(PreferenceManager.getDefaultSharedPreferences(this))
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            super.onRestoreInstanceState(savedInstanceState)
        }
        setupBottomNavigationBar()
    }

    private fun setupBottomNavigationBar() {
        val navController = Navigation.findNavController(this, R.id.nav_host_container)
        setupWithNavController(bottom_nav, navController)
        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    fun getColorsEnabledArray(): BooleanArray {
        return booleanArrayOf(
            preferenceManager.getBool(PreferenceHelper.RED_ENABLED),
            preferenceManager.getBool(PreferenceHelper.ORANGE_ENABLED),
            preferenceManager.getBool(PreferenceHelper.YELLOW_ENABLED),
            preferenceManager.getBool(PreferenceHelper.GREEN_ENABLED),
            preferenceManager.getBool(PreferenceHelper.BLUEGREEN_ENABLED),
            preferenceManager.getBool(PreferenceHelper.LIGHTBLUE_ENABLED),
            preferenceManager.getBool(PreferenceHelper.DARKBLUE_ENABLED),
            preferenceManager.getBool(PreferenceHelper.PURPLE_ENABLED),
            preferenceManager.getBool(PreferenceHelper.PINK_ENABLED)
        )
    }

    fun setColorBoolean(key: String, isEnabled: Boolean) {
        preferenceManager.setBool(key, isEnabled)
    }

    fun getGuessLimitOption(): Int {
        return preferenceManager.getInt(PreferenceHelper.GUESSES, 3)
    }

    fun setGuessLimitOption(guessLimit: Int) {
        preferenceManager.setInt(PreferenceHelper.GUESSES, guessLimit)
    }
}
