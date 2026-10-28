package com.example.game.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.game.audio.HapticManager
import com.example.game.audio.SoundManager
import com.example.game.data.GameDatabase
import com.example.game.data.GamePreferences
import com.example.game.data.LevelProgressEntity
import com.example.game.engine.LevelCatalog
import com.example.game.engine.PuzzlePhysics
import com.example.game.engine.PuzzleSolver
import com.example.game.model.Arrow
import com.example.game.model.Difficulty
import com.example.game.model.LevelData
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class ScreenState {
    HOME,
    LEVEL_SELECT,
    GAMEPLAY
}

enum class PlayState {
    PLAYING,
    LEVEL_CLEARED,
    GAME_OVER
}

data class MovingArrowState(
    val arrow: Arrow,
    val progress: Float = 0f // 0f to 1f
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val db = GameDatabase.getDatabase(application)
    private val dao = db.levelProgressDao()
    val preferences = GamePreferences(application)
    val soundManager = SoundManager(application, preferences)
    val hapticManager = HapticManager(application, preferences)

    private val _screenState = MutableStateFlow(ScreenState.HOME)
    val screenState: StateFlow<ScreenState> = _screenState.asStateFlow()

    private val _currentLevelNumber = MutableStateFlow(1)
    val currentLevelNumber: StateFlow<Int> = _currentLevelNumber.asStateFlow()

    private val _currentLevel = MutableStateFlow(LevelCatalog.getLevel(1))
    val currentLevel: StateFlow<LevelData> = _currentLevel.asStateFlow()

    private val _activeArrows = MutableStateFlow<List<Arrow>>(emptyList())
    val activeArrows: StateFlow<List<Arrow>> = _activeArrows.asStateFlow()

    private val _movingArrows = MutableStateFlow<Map<String, MovingArrowState>>(emptyMap())
    val movingArrows: StateFlow<Map<String, MovingArrowState>> = _movingArrows.asStateFlow()

    private val _shakingArrowId = MutableStateFlow<String?>(null)
    val shakingArrowId: StateFlow<String?> = _shakingArrowId.asStateFlow()

    private val _hintArrowId = MutableStateFlow<String?>(null)
    val hintArrowId: StateFlow<String?> = _hintArrowId.asStateFlow()

    private val _hearts = MutableStateFlow(3)
    val hearts: StateFlow<Int> = _hearts.asStateFlow()

    private val _movesCount = MutableStateFlow(0)
    val movesCount: StateFlow<Int> = _movesCount.asStateFlow()

    private val _elapsedSeconds = MutableStateFlow(0)
    val elapsedSeconds: StateFlow<Int> = _elapsedSeconds.asStateFlow()

    private val _playState = MutableStateFlow(PlayState.PLAYING)
    val playState: StateFlow<PlayState> = _playState.asStateFlow()

    private val _starsEarned = MutableStateFlow(0)
    val starsEarned: StateFlow<Int> = _starsEarned.asStateFlow()

    private val _levelProgressMap = MutableStateFlow<Map<Int, LevelProgressEntity>>(emptyMap())
    val levelProgressMap: StateFlow<Map<Int, LevelProgressEntity>> = _levelProgressMap.asStateFlow()

    private val _hintsCount = MutableStateFlow(preferences.hintsCount)
    val hintsCount: StateFlow<Int> = _hintsCount.asStateFlow()

    private val _coinsCount = MutableStateFlow(preferences.coinsCount)
    val coinsCount: StateFlow<Int> = _coinsCount.asStateFlow()

    private val _isGridVisible = MutableStateFlow(preferences.isGridVisible)
    val isGridVisible: StateFlow<Boolean> = _isGridVisible.asStateFlow()

    private val _isMusicEnabled = MutableStateFlow(preferences.isMusicEnabled)
    val isMusicEnabled: StateFlow<Boolean> = _isMusicEnabled.asStateFlow()

    private var timerJob: Job? = null

    init {
        loadProgress()
        loadLevel(1)
    }

    private fun loadProgress() {
        viewModelScope.launch {
            dao.getAllProgress().collect { list ->
                val map = list.associateBy { it.levelNumber }.toMutableMap()
                if (!map.containsKey(1)) {
                    val initial = LevelProgressEntity(levelNumber = 1, unlocked = true)
                    dao.saveProgress(initial)
                    map[1] = initial
                }
                _levelProgressMap.value = map
            }
        }
    }

    fun navigateTo(screen: ScreenState) {
        _screenState.value = screen
    }

    fun loadLevel(levelNumber: Int) {
        val level = LevelCatalog.getLevel(levelNumber)
        _currentLevelNumber.value = levelNumber
        _currentLevel.value = level
        _activeArrows.value = level.arrows
        _movingArrows.value = emptyMap()
        _shakingArrowId.value = null
        _hintArrowId.value = null
        _hearts.value = 3
        _movesCount.value = 0
        _elapsedSeconds.value = 0
        _playState.value = PlayState.PLAYING
        _starsEarned.value = 0

        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_playState.value == PlayState.PLAYING) {
                delay(1000L)
                if (_playState.value == PlayState.PLAYING) {
                    _elapsedSeconds.value += 1
                }
            }
        }
    }

    fun onArrowTapped(arrowId: String) {
        if (_playState.value != PlayState.PLAYING) return
        val currentList = _activeArrows.value
        val arrow = currentList.find { it.id == arrowId } ?: return
        if (_movingArrows.value.containsKey(arrowId)) return
        if (_shakingArrowId.value == arrowId) return

        val level = _currentLevel.value
        val isMovable = PuzzlePhysics.canArrowExit(arrow, currentList, level.gridWidth, level.gridHeight)

        if (isMovable) {
            // Unblocked move!
            soundManager.playSlide()
            hapticManager.vibrateTap()
            _movesCount.value += 1
            if (_hintArrowId.value == arrowId) {
                _hintArrowId.value = null
            }

            // Decouple immediately for zero-latency consecutive multi-taps
            _activeArrows.value = currentList.filter { it.id != arrowId }
            animateArrowExit(arrow)
        } else {
            // Blocked move!
            soundManager.playBlocked()
            hapticManager.vibrateBlocked()
            _movesCount.value += 1
            triggerShake(arrowId)

            val newHearts = _hearts.value - 1
            _hearts.value = newHearts
            soundManager.playHeartLost()

            if (newHearts <= 0) {
                _playState.value = PlayState.GAME_OVER
                timerJob?.cancel()
            }
        }
    }

    private fun animateArrowExit(arrow: Arrow) {
        viewModelScope.launch {
            val durationMs = 140L
            val startTime = System.currentTimeMillis()

            while (true) {
                val elapsed = System.currentTimeMillis() - startTime
                val rawProgress = (elapsed.toFloat() / durationMs).coerceIn(0f, 1f)
                _movingArrows.value = _movingArrows.value + (arrow.id to MovingArrowState(arrow, rawProgress))
                if (rawProgress >= 1f) break
                delay(8L) // ~120 FPS
            }

            _movingArrows.value = _movingArrows.value - arrow.id

            if (_activeArrows.value.isEmpty() && _movingArrows.value.isEmpty() && _playState.value == PlayState.PLAYING) {
                onLevelCleared()
            }
        }
    }

    private fun triggerShake(arrowId: String) {
        viewModelScope.launch {
            _shakingArrowId.value = arrowId
            delay(260L)
            if (_shakingArrowId.value == arrowId) {
                _shakingArrowId.value = null
            }
        }
    }

    private fun onLevelCleared() {
        _playState.value = PlayState.LEVEL_CLEARED
        timerJob?.cancel()
        soundManager.playWin()
        hapticManager.vibrateVictory()

        val lvlNum = _currentLevelNumber.value
        val level = _currentLevel.value
        val moves = _movesCount.value
        val heartsLeft = _hearts.value

        val stars = when {
            heartsLeft == 3 && moves <= level.parMoves + 1 -> 3
            heartsLeft >= 2 -> 2
            else -> 1
        }
        _starsEarned.value = stars

        // Award Coins based on performance
        val coinsReward = when (stars) {
            3 -> 50
            2 -> 35
            else -> 25
        }
        val newCoins = _coinsCount.value + coinsReward
        _coinsCount.value = newCoins
        preferences.coinsCount = newCoins

        viewModelScope.launch {
            val existing = dao.getProgress(lvlNum)
            val bestMoves = if (existing != null && existing.bestMoves > 0) {
                minOf(existing.bestMoves, moves)
            } else {
                moves
            }
            val bestStars = maxOf(existing?.stars ?: 0, stars)

            dao.saveProgress(
                LevelProgressEntity(
                    levelNumber = lvlNum,
                    unlocked = true,
                    completed = true,
                    stars = bestStars,
                    bestMoves = bestMoves
                )
            )

            if (lvlNum < LevelCatalog.TOTAL_LEVELS) {
                val nextLvl = lvlNum + 1
                val nextExisting = dao.getProgress(nextLvl)
                if (nextExisting == null || !nextExisting.unlocked) {
                    dao.saveProgress(
                        LevelProgressEntity(
                            levelNumber = nextLvl,
                            unlocked = true,
                            completed = nextExisting?.completed ?: false,
                            stars = nextExisting?.stars ?: 0,
                            bestMoves = nextExisting?.bestMoves ?: 0
                        )
                    )
                }
            }
        }
    }

    fun useHint() {
        if (_playState.value != PlayState.PLAYING) return
        val current = _activeArrows.value
        if (current.isEmpty()) return

        if (_hintsCount.value > 0) {
            val newCount = _hintsCount.value - 1
            _hintsCount.value = newCount
            preferences.hintsCount = newCount

            val level = _currentLevel.value
            val hintArrow = PuzzleSolver.getHint(current, level.gridWidth, level.gridHeight)
            _hintArrowId.value = hintArrow?.id
            soundManager.playHint()
            hapticManager.vibrateTap()
        }
    }

    fun buyHintWithCoins(): Boolean {
        if (_coinsCount.value >= 100) {
            val newCoins = _coinsCount.value - 100
            _coinsCount.value = newCoins
            preferences.coinsCount = newCoins

            val newHints = _hintsCount.value + 1
            _hintsCount.value = newHints
            preferences.hintsCount = newHints
            useHint()
            return true
        }
        return false
    }

    fun rewardAdHint() {
        val newHints = _hintsCount.value + 1
        _hintsCount.value = newHints
        preferences.hintsCount = newHints
        useHint()
    }

    fun rewardAdLives() {
        _hearts.value = 3
        _playState.value = PlayState.PLAYING
        startTimer()
        soundManager.playWin()
    }

    fun addCoins(amount: Int = 150) {
        val newCount = _coinsCount.value + amount
        _coinsCount.value = newCount
        preferences.coinsCount = newCount
        soundManager.playWin()
    }

    fun toggleGrid(): Boolean {
        val newVal = !_isGridVisible.value
        _isGridVisible.value = newVal
        preferences.isGridVisible = newVal
        soundManager.playTap()
        return newVal
    }

    fun toggleMusic(): Boolean {
        val newVal = !_isMusicEnabled.value
        _isMusicEnabled.value = newVal
        preferences.isMusicEnabled = newVal
        return newVal
    }

    fun restartCurrentLevel() {
        soundManager.playTap()
        loadLevel(_currentLevelNumber.value)
    }

    fun nextLevel() {
        soundManager.playTap()
        val next = (_currentLevelNumber.value + 1).coerceAtMost(LevelCatalog.TOTAL_LEVELS)
        loadLevel(next)
    }

    fun toggleSound(): Boolean {
        val newVal = !preferences.isSoundEnabled
        preferences.isSoundEnabled = newVal
        return newVal
    }

    fun toggleHaptics(): Boolean {
        val newVal = !preferences.isHapticsEnabled
        preferences.isHapticsEnabled = newVal
        return newVal
    }

    fun toggleTheme(): Boolean {
        val newVal = !preferences.isDarkTheme
        preferences.isDarkTheme = newVal
        return newVal
    }

    fun resetAllProgress() {
        viewModelScope.launch {
            dao.clearAll()
            dao.saveProgress(LevelProgressEntity(levelNumber = 1, unlocked = true))
            preferences.resetPreferences()
            _hintsCount.value = 5
            _coinsCount.value = 150
            loadLevel(1)
            _screenState.value = ScreenState.HOME
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        soundManager.release()
    }
}
