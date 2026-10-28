package com.example.game.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LevelProgressDao {
    @Query("SELECT * FROM level_progress ORDER BY levelNumber ASC")
    fun getAllProgress(): Flow<List<LevelProgressEntity>>

    @Query("SELECT * FROM level_progress WHERE levelNumber = :levelNumber LIMIT 1")
    suspend fun getProgress(levelNumber: Int): LevelProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: LevelProgressEntity)

    @Query("UPDATE level_progress SET unlocked = 1 WHERE levelNumber = :levelNumber")
    suspend fun unlockLevel(levelNumber: Int)

    @Query("SELECT MAX(levelNumber) FROM level_progress WHERE unlocked = 1")
    suspend fun getHighestUnlockedLevel(): Int?

    @Query("DELETE FROM level_progress")
    suspend fun clearAll()
}
