package com.example.game.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.engine.LevelCatalog
import com.example.game.ui.components.Button3DColor
import com.example.game.ui.components.CircularButton3D
import com.example.game.ui.components.CoinPill3D
import com.example.game.ui.components.LevelSelectTile3D

@Composable
fun LevelSelectScreen(
    viewModel: GameViewModel,
    onBack: () -> Unit,
    onShowInterstitialAd: (onClosed: () -> Unit) -> Unit = { it() }
) {
    BackHandler {
        onBack()
    }

    val progressMap by viewModel.levelProgressMap.collectAsState()
    val coinsCount by viewModel.coinsCount.collectAsState()

    val pageSize = 20
    val totalPages = (LevelCatalog.TOTAL_LEVELS + pageSize - 1) / pageSize
    var currentPage by remember { mutableStateOf(0) }

    val startLevel = currentPage * pageSize + 1
    val endLevel = minOf((currentPage + 1) * pageSize, LevelCatalog.TOTAL_LEVELS)
    val pageLevels = (startLevel..endLevel).toList()

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
                // Red circular Back button
                CircularButton3D(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    onClick = onBack,
                    size = 46.dp,
                    color = Button3DColor.RED,
                    testTag = "level_select_back_btn"
                )

                Text(
                    text = "Select Level",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )

                // Coin pill
                CoinPill3D(
                    coins = coinsCount,
                    onAddCoins = { viewModel.addCoins(150) },
                    modifier = Modifier.testTag("level_select_coin_pill")
                )
            }

            // --- 4x5 Grid of 3D Numbered Level Tiles ---
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .testTag("level_select_grid")
            ) {
                items(pageLevels) { levelNum ->
                    val progress = progressMap[levelNum]
                    val isUnlocked = progress?.unlocked == true || levelNum == 1
                    val isCompleted = progress?.completed == true
                    val stars = progress?.stars ?: 0

                    LevelSelectTile3D(
                        levelNumber = levelNum,
                        isUnlocked = isUnlocked,
                        isCompleted = isCompleted,
                        stars = stars,
                        onClick = {
                            if (isUnlocked) {
                                onShowInterstitialAd {
                                    viewModel.loadLevel(levelNum)
                                    viewModel.navigateTo(ScreenState.GAMEPLAY)
                                }
                            }
                        }
                    )
                }
            }

            // --- Bottom Pagination Controls ---
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularButton3D(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    onClick = {
                        if (currentPage > 0) currentPage--
                    },
                    size = 46.dp,
                    color = if (currentPage > 0) Button3DColor.RED else Button3DColor.GREY
                )

                Text(
                    text = "${currentPage + 1} / $totalPages",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )

                CircularButton3D(
                    icon = Icons.AutoMirrored.Filled.ArrowForward,
                    onClick = {
                        if (currentPage < totalPages - 1) currentPage++
                    },
                    size = 46.dp,
                    color = if (currentPage < totalPages - 1) Button3DColor.RED else Button3DColor.GREY
                )
            }
        }
    }
}
