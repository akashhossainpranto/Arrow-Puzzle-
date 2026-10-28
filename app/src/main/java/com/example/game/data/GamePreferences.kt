package com.example.game.data

import android.content.Context
import android.content.SharedPreferences

class GamePreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("arrow_main_prefs", Context.MODE_PRIVATE)

    var isSoundEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND, true)
        set(value) = prefs.edit().putBoolean(KEY_SOUND, value).apply()

    var isHapticsEnabled: Boolean
        get() = prefs.getBoolean(KEY_HAPTICS, true)
        set(value) = prefs.edit().putBoolean(KEY_HAPTICS, value).apply()

    var hintsCount: Int
        get() = prefs.getInt(KEY_HINTS, 5) // Starts with 5 free hints
        set(value) = prefs.edit().putInt(KEY_HINTS, value).apply()

    var isDarkTheme: Boolean
        get() = prefs.getBoolean(KEY_DARK_THEME, true)
        set(value) = prefs.edit().putBoolean(KEY_DARK_THEME, value).apply()

    var coinsCount: Int
        get() = prefs.getInt(KEY_COINS, 150) // Starts with 150 coins as in design
        set(value) = prefs.edit().putInt(KEY_COINS, value).apply()

    var isMusicEnabled: Boolean
        get() = prefs.getBoolean(KEY_MUSIC, true)
        set(value) = prefs.edit().putBoolean(KEY_MUSIC, value).apply()

    var isGridVisible: Boolean
        get() = prefs.getBoolean(KEY_GRID_VISIBLE, true)
        set(value) = prefs.edit().putBoolean(KEY_GRID_VISIBLE, value).apply()

    fun resetPreferences() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val KEY_SOUND = "key_sound"
        private const val KEY_MUSIC = "key_music"
        private const val KEY_HAPTICS = "key_haptics"
        private const val KEY_HINTS = "key_hints"
        private const val KEY_DARK_THEME = "key_dark_theme"
        private const val KEY_COINS = "key_coins"
        private const val KEY_GRID_VISIBLE = "key_grid_visible"
    }
}
