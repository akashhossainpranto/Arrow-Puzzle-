package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.game.ads.AdMobManager
import com.example.game.ui.GamePlayScreen
import com.example.game.ui.HomeScreen
import com.example.game.ui.LevelSelectScreen
import com.example.game.ui.ScreenState
import com.example.game.ui.GameViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Google AdMob with real app ID and preload interstitial
        AdMobManager.initialize(this)

        setContent {
            val isDarkTheme = viewModel.preferences.isDarkTheme
            val screenState by viewModel.screenState.collectAsState()

            val showInterstitial: ((() -> Unit) -> Unit) = { onClosed ->
                AdMobManager.showInterstitial(this, onClosed)
            }

            MyApplicationTheme(darkTheme = isDarkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    when (screenState) {
                        ScreenState.HOME -> {
                            HomeScreen(
                                viewModel = viewModel,
                                onPlay = {
                                    viewModel.navigateTo(ScreenState.GAMEPLAY)
                                },
                                onSelectLevel = {
                                    viewModel.navigateTo(ScreenState.LEVEL_SELECT)
                                },
                                onShowInterstitialAd = showInterstitial
                            )
                        }
                        ScreenState.LEVEL_SELECT -> {
                            LevelSelectScreen(
                                viewModel = viewModel,
                                onBack = {
                                    viewModel.navigateTo(ScreenState.HOME)
                                },
                                onShowInterstitialAd = showInterstitial
                            )
                        }
                        ScreenState.GAMEPLAY -> {
                            GamePlayScreen(
                                viewModel = viewModel,
                                onBack = {
                                    viewModel.navigateTo(ScreenState.LEVEL_SELECT)
                                },
                                onShowInterstitialAd = showInterstitial
                            )
                        }
                    }
                }
            }
        }
    }
}
