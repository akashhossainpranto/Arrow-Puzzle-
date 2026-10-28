package com.example.game.ui

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.game.engine.LevelCatalog
import com.example.game.ui.components.Button3DColor
import com.example.game.ui.components.CircularButton3D
import com.example.game.ui.components.CoinPill3D
import com.example.game.ui.components.GlossyButton3D
import com.example.game.ui.components.SettingsDialog3D

@Composable
fun HomeScreen(
    viewModel: GameViewModel,
    onPlay: () -> Unit,
    onSelectLevel: () -> Unit,
    onShowInterstitialAd: (onClosed: () -> Unit) -> Unit = { it() }
) {
    val context = LocalContext.current
    BackHandler {
        onShowInterstitialAd {
            (context as? Activity)?.finish()
        }
    }
    val progressMap by viewModel.levelProgressMap.collectAsState()
    val coinsCount by viewModel.coinsCount.collectAsState()
    val isSoundEnabled = viewModel.preferences.isSoundEnabled
    val isMusicEnabled by viewModel.isMusicEnabled.collectAsState()
    val isHapticsEnabled = viewModel.preferences.isHapticsEnabled

    var showSettingsDialog by remember { mutableStateOf(false) }
    var showStatsDialog by remember { mutableStateOf(false) }
    var showShopDialog by remember { mutableStateOf(false) }

    val highestUnlocked = progressMap.filter { it.value.unlocked }.keys.maxOrNull() ?: 1
    val completedCount = progressMap.count { it.value.completed }
    val totalStars = progressMap.values.sumOf { it.stars }

    // Preview level for the home card (uses Level 1)
    val previewLevel = remember { LevelCatalog.getLevel(1) }

    val infiniteTransition = rememberInfiniteTransition(label = "titlePulse")
    val titleScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "titleScale"
    )

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
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // --- Top Coin Bar ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CoinPill3D(
                    coins = coinsCount,
                    onAddCoins = { showShopDialog = true },
                    modifier = Modifier.testTag("home_coin_pill")
                )
            }

            // --- 3D Cartoon Bubble Title (Red and Black Edition) ---
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.scale(titleScale)
            ) {
                // Top line: "TAP AWAY" in deep carbon black with crimson border
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .border(2.5.dp, Color(0xFFE50914), RoundedCornerShape(20.dp)),
                    color = Color(0xFF161622)
                ) {
                    Text(
                        text = "TAP AWAY",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Bottom line: "ARROWS" in radiant Crimson Red with black border
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(22.dp))
                        .border(3.dp, Color(0xFF0A0A0E), RoundedCornerShape(22.dp)),
                    color = Color(0xFFE50914)
                ) {
                    Text(
                        text = "ARROWS",
                        color = Color.White,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 3.sp,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                    )
                }
            }

            // --- Center Preview Maze Card (Sleek Obsidian & Crimson Frame) ---
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(32.dp))
                    .border(3.5.dp, Color(0xFFE50914), RoundedCornerShape(32.dp)),
                color = Color(0xFF121218),
                shape = RoundedCornerShape(32.dp),
                shadowElevation = 12.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ArrowBoard(
                        level = previewLevel,
                        activeArrows = previewLevel.arrows,
                        movingArrows = emptyMap(),
                        shakingArrowId = null,
                        hintArrowId = previewLevel.arrows.firstOrNull()?.id,
                        isDarkTheme = true,
                        isGridVisible = true,
                        onArrowTapped = {
                            onShowInterstitialAd {
                                viewModel.loadLevel(highestUnlocked)
                                onPlay()
                            }
                        }
                    )
                }
            }

            // --- Action Buttons ---
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Big Chunky 3D "PLAY" Button in Brilliant Crimson Red
                GlossyButton3D(
                    text = "PLAY",
                    icon = Icons.Default.PlayArrow,
                    color = Button3DColor.RED,
                    height = 64.dp,
                    fontSize = 24.sp,
                    onClick = {
                        onShowInterstitialAd {
                            viewModel.loadLevel(highestUnlocked)
                            onPlay()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(0.92f),
                    testTag = "home_play_button"
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Bottom 3 Circular Buttons: Settings, Stats, Shop in Carbon Black
                Row(
                    modifier = Modifier.fillMaxWidth(0.78f),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularButton3D(
                        icon = Icons.Default.Settings,
                        onClick = { showSettingsDialog = true },
                        size = 54.dp,
                        color = Button3DColor.BLACK,
                        testTag = "home_settings_btn"
                    )

                    CircularButton3D(
                        icon = Icons.Default.BarChart,
                        onClick = { showStatsDialog = true },
                        size = 54.dp,
                        color = Button3DColor.BLACK,
                        testTag = "home_stats_btn"
                    )

                    CircularButton3D(
                        icon = Icons.Default.ShoppingCart,
                        onClick = { showShopDialog = true },
                        size = 54.dp,
                        color = Button3DColor.BLACK,
                        testTag = "home_shop_btn"
                    )
                }
            }
        }

        // Settings Dialog
        if (showSettingsDialog) {
            SettingsDialog3D(
                soundEnabled = isSoundEnabled,
                musicEnabled = isMusicEnabled,
                vibrationEnabled = isHapticsEnabled,
                onToggleSound = { viewModel.toggleSound() },
                onToggleMusic = { viewModel.toggleMusic() },
                onToggleVibration = { viewModel.toggleHaptics() },
                onResetProgress = { viewModel.resetAllProgress() },
                onDismiss = { showSettingsDialog = false }
            )
        }

        // Stats Dialog
        if (showStatsDialog) {
            Dialog(onDismissRequest = { showStatsDialog = false }) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(32.dp))
                        .border(3.dp, Color(0xFFE50914), RoundedCornerShape(32.dp)),
                    color = Color(0xFF161622),
                    shape = RoundedCornerShape(32.dp),
                    shadowElevation = 16.dp
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Player Statistics",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        StatRow("Levels Completed", "$completedCount / ${LevelCatalog.TOTAL_LEVELS}")
                        StatRow("Total Stars", "$totalStars ★")
                        StatRow("Highest Level", "Level $highestUnlocked")
                        StatRow("Coins Balance", "$coinsCount ★")

                        Spacer(modifier = Modifier.height(24.dp))

                        GlossyButton3D(
                            text = "Level Selection",
                            color = Button3DColor.RED,
                            onClick = {
                                showStatsDialog = false
                                onSelectLevel()
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // Shop Dialog
        if (showShopDialog) {
            Dialog(onDismissRequest = { showShopDialog = false }) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(32.dp))
                        .border(3.dp, Color(0xFFE50914), RoundedCornerShape(32.dp)),
                    color = Color(0xFF161622),
                    shape = RoundedCornerShape(32.dp),
                    shadowElevation = 16.dp
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Coin & Hint Shop",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Current Balance: $coinsCount Coins ★",
                            color = Color(0xFFFFCC00),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        GlossyButton3D(
                            text = "Free Reward (+150 Coins)",
                            color = Button3DColor.GOLD,
                            onClick = {
                                viewModel.addCoins(150)
                                showShopDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        GlossyButton3D(
                            text = "Buy 3 Hints (200 Coins)",
                            color = Button3DColor.RED,
                            onClick = {
                                if (coinsCount >= 200) {
                                    viewModel.addCoins(-200)
                                    viewModel.rewardAdHint()
                                    viewModel.rewardAdHint()
                                    viewModel.rewardAdHint()
                                    showShopDialog = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        GlossyButton3D(
                            text = "Close",
                            color = Button3DColor.BLACK,
                            onClick = { showShopDialog = false },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF222230))
            .border(1.dp, Color(0xFF333342), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color.White, fontWeight = FontWeight.SemiBold)
        Text(value, color = Color(0xFFFF334B), fontWeight = FontWeight.Black, fontSize = 16.sp)
    }
}
