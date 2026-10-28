package com.example.game.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "level_progress")
data class LevelProgressEntity(
    @PrimaryKey val levelNumber: Int,
    val unlocked: Boolean = false,
    val completed: Boolean = false,
    val stars: Int = 0,
    val bestMoves: Int = 0
)
