package com.example.game.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.ui.components.Button3DColor
import com.example.game.ui.components.CircularButton3D
import com.example.game.ui.components.DifficultyDialog3D
import com.example.game.ui.components.LevelCompleteDialog3D
import com.example.game.ui.components.NeedAHintDialog3D
import com.example.game.ui.components.NoLivesDialog3D
import com.example.game.ui.components.PausedDialog3D
import com.example.game.ui.components.PuffyHeart3D
import com.example.game.ui.components.SettingsDialog3D

@Composable
fun GamePlayScreen(
    viewModel: GameViewModel,
    onBack: () -> Unit,
    onShowInterstitialAd: (onClosed: () -> Unit) -> Unit = { it() }
) {
    BackHandler {
        onBack()
    }

    val currentLevelNumber by viewModel.currentLevelNumber.collectAsState()
    val level by viewModel.currentLevel.collectAsState()
    val activeArrows by viewModel.activeArrows.collectAsState()
    val movingArrows by viewModel.movingArrows.collectAsState()
    val shakingArrowId by viewModel.shakingArrowId.collectAsState()
    val hintArrowId by viewModel.hintArrowId.collectAsState()
    val hearts by viewModel.hearts.collectAsState()
    val movesCount by viewModel.movesCount.collectAsState()
    val elapsedSeconds by viewModel.elapsedSeconds.collectAsState()
    val playState by viewModel.playState.collectAsState()
    val starsEarned by viewModel.starsEarned.collectAsState()
    val hintsCount by viewModel.hintsCount.collectAsState()
    val coinsCount by viewModel.coinsCount.collectAsState()
    val isGridVisible by viewModel.isGridVisible.collectAsState()

    var showPauseDialog by remember { mutableStateOf(false) }
    var showHintDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showDifficultyDialog by remember { mutableStateOf(false) }

    val formattedTime = remember(elapsedSeconds) {
        val m = elapsedSeconds / 60
        val s = elapsedSeconds % 60
        String.format("%02d:%02d", m, s)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A0A0E),
                        Color(0xFF160A0E),
                        Color(0xFF0D0D12)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // --- Top Bar (Red and Black Header) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Carbon Black / Red circular Home button
                CircularButton3D(
                    icon = Icons.Default.Home,
                    onClick = onBack,
                    size = 46.dp,
                    color = Button3DColor.BLACK,
                    testTag = "game_home_btn"
                )

                // Level title and Puffy Hearts
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Level $currentLevelNumber",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    // 3 Puffy Hearts in glowing Crimson Red
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        for (i in 1..3) {
                            PuffyHeart3D(isFull = i <= hearts, size = 24.dp)
                            if (i < 3) Spacer(modifier = Modifier.width(4.dp))
                        }
                    }
                }

                // Difficulty pill & Settings gear
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.5.dp, Color(0xFFE50914), RoundedCornerShape(12.dp))
                            .clickable { showDifficultyDialog = true },
                        color = Color(0xFF161622)
                    ) {
                        Text(
                            text = level.difficulty.displayName,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Circular Settings Gear Button
                    CircularButton3D(
                        icon = Icons.Default.Settings,
                        onClick = { showPauseDialog = true },
                        size = 46.dp,
                        color = Button3DColor.BLACK,
                        testTag = "game_settings_btn"
                    )
                }
            }

            // --- Stats Bar: Moves (Airplane) and Timer ---
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF161622))
                    .border(2.dp, Color(0xFF8B0000), RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.NearMe,
                        contentDescription = "Moves",
                        tint = Color(0xFFFF334B),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$movesCount",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "Timer",
                        tint = Color(0xFFFF334B),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = formattedTime,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // --- Center Elevated Board Container ---
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                ArrowBoard(
                    level = level,
                    activeArrows = activeArrows,
                    movingArrows = movingArrows,
                    shakingArrowId = shakingArrowId,
                    hintArrowId = hintArrowId,
                    isDarkTheme = true,
                    isGridVisible = isGridVisible,
                    onArrowTapped = { arrowId -> viewModel.onArrowTapped(arrowId) }
                )
            }

            // --- Exact Bottom Control Bar ---
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Restart Button
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularButton3D(
                        icon = Icons.Default.Refresh,
                        onClick = { viewModel.restartCurrentLevel() },
                        size = 56.dp,
                        color = Button3DColor.BLACK,
                        testTag = "game_restart_btn"
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Restart",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFA0A5B5)
                    )
                }

                // Hint Button (with red notification badge)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularButton3D(
                        icon = Icons.Default.Lightbulb,
                        onClick = {
                            if (hintsCount > 0) {
                                viewModel.useHint()
                            } else {
                                showHintDialog = true
                            }
                        },
                        size = 56.dp,
                        color = Button3DColor.RED,
                        badgeCount = hintsCount,
                        testTag = "game_hint_btn"
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Hint",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFA0A5B5)
                    )
                }

                // Grid Button (#)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularButton3D(
                        icon = Icons.Default.Tag,
                        onClick = { viewModel.toggleGrid() },
                        size = 56.dp,
                        color = Button3DColor.BLACK,
                        testTag = "game_grid_btn"
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Grid",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFA0A5B5)
                    )
                }
            }
        }

        // --- Modals ---
        // 1. Level Complete (Crown + Confetti)
        if (playState == PlayState.LEVEL_CLEARED) {
            LevelCompleteDialog3D(
                levelNumber = currentLevelNumber,
                movesTaken = movesCount,
                timeFormatted = formattedTime,
                starsEarned = starsEarned,
                coinsEarned = when (starsEarned) { 3 -> 50; 2 -> 35; else -> 25 },
                onNextLevel = {
                    onShowInterstitialAd {
                        viewModel.nextLevel()
                    }
                },
                onReplay = { viewModel.restartCurrentLevel() },
                onHome = {
                    onShowInterstitialAd {
                        onBack()
                    }
                },
                onShare = { /* Share celebration */ }
            )
        }

        // 2. Out of Hearts / No Lives
        if (playState == PlayState.GAME_OVER) {
            NoLivesDialog3D(
                onWatchAd = {
                    onShowInterstitialAd {
                        viewModel.rewardAdLives()
                    }
                },
                onDismiss = { viewModel.restartCurrentLevel() }
            )
        }

        // 3. Need a Hint Modal
        if (showHintDialog) {
            NeedAHintDialog3D(
                coins = coinsCount,
                onWatchAd = {
                    onShowInterstitialAd {
                        viewModel.rewardAdHint()
                    }
                    showHintDialog = false
                },
                onUseCoins = {
                    if (viewModel.buyHintWithCoins()) {
                        showHintDialog = false
                    }
                },
                onDismiss = { showHintDialog = false }
            )
        }

        // 4. Paused Modal
        if (showPauseDialog) {
            PausedDialog3D(
                onResume = { showPauseDialog = false },
                onRestart = {
                    showPauseDialog = false
                    viewModel.restartCurrentLevel()
                },
                onHome = {
                    showPauseDialog = false
                    onShowInterstitialAd {
                        onBack()
                    }
                },
                onExit = {
                    showPauseDialog = false
                    onShowInterstitialAd {
                        onBack()
                    }
                }
            )
        }

        // 5. Difficulty Modal
        if (showDifficultyDialog) {
            DifficultyDialog3D(
                currentDifficulty = level.difficulty,
                onSelectDifficulty = { diff ->
                    // Load appropriate level for difficulty
                    val targetLevel = when (diff) {
                        com.example.game.model.Difficulty.EASY -> 11
                        com.example.game.model.Difficulty.MEDIUM -> 26
                        com.example.game.model.Difficulty.HARD -> 51
                        com.example.game.model.Difficulty.EXPERT -> 76
                        else -> 1
                    }
                    viewModel.loadLevel(targetLevel)
                },
                onDismiss = { showDifficultyDialog = false }
            )
        }
    }
}
