package com.example.game.engine

import com.example.game.model.Difficulty
import com.example.game.model.LevelData

/**
 * Manages the 150 levels of ARROW MAIN with on-demand generation and in-memory caching.
 */
object LevelCatalog {
    const val TOTAL_LEVELS = 200

    private val cache = mutableMapOf<Int, LevelData>()

    fun getLevel(levelNumber: Int): LevelData {
        val clamped = levelNumber.coerceIn(1, TOTAL_LEVELS)
        return cache.getOrPut(clamped) {
            LevelGenerator.generateLevel(clamped)
        }
    }

    fun getDifficultyForLevel(levelNumber: Int): Difficulty {
        return when (levelNumber) {
            in 1..15 -> Difficulty.BEGINNER
            in 16..40 -> Difficulty.EASY
            in 41..80 -> Difficulty.MEDIUM
            in 81..120 -> Difficulty.HARD
            in 121..160 -> Difficulty.EXPERT
            in 161..185 -> Difficulty.MASTER
            else -> Difficulty.EXTREME
        }
    }
}
