package com.example.game.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.game.model.Difficulty

/**
 * Exact "Level Complete!" Dialog matching Card 3 of the design sheet:
 * - Floating golden crown with sparkle confetti
 * - "Level Complete!" blue ribbon
 * - Level number, moves taken, and live timer
 * - Big chunky green "Next Level" button
 * - Circular Home, Replay, and Share action buttons
 */
@Composable
fun LevelCompleteDialog3D(
    levelNumber: Int,
    movesTaken: Int,
    timeFormatted: String,
    starsEarned: Int,
    coinsEarned: Int,
    onNextLevel: () -> Unit,
    onReplay: () -> Unit,
    onHome: () -> Unit,
    onShare: () -> Unit
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 36.dp)
                .testTag("level_complete_dialog"),
            contentAlignment = Alignment.TopCenter
        ) {
            // Main Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .border(3.dp, Color(0xFFE50914), RoundedCornerShape(32.dp)),
                color = Color(0xFF161622),
                shape = RoundedCornerShape(32.dp),
                shadowElevation = 16.dp
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(28.dp))

                    // "Level Complete!" red ribbon banner
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(24.dp)),
                        color = Color(0xFFE50914)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Level Complete!",
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Level $levelNumber",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Stars row
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (i in 1..3) {
                            val isLit = i <= starsEarned
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (isLit) Color(0xFFFFB703) else Color(0xFF333342),
                                modifier = Modifier
                                    .size(36.dp)
                                    .padding(horizontal = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stats: Moves and Time
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF222230))
                            .border(1.dp, Color(0xFF333342), RoundedCornerShape(16.dp))
                            .padding(vertical = 12.dp, horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.NearMe,
                                contentDescription = null,
                                tint = Color(0xFFFF334B),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Moves: $movesTaken",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 15.sp
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = Color(0xFFFF334B),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Time: $timeFormatted",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Coins earned pill
                    Text(
                        text = "+$coinsEarned Coins Earned! ★",
                        color = Color(0xFFFFCC00),
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Chunky Red Next Level Button
                    GlossyButton3D(
                        text = "Next Level",
                        icon = Icons.Default.PlayArrow,
                        color = Button3DColor.RED,
                        onClick = onNextLevel,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "next_level_button"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Bottom circular action buttons: Home, Replay, Share
                    Row(
                        modifier = Modifier.fillMaxWidth(0.85f),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularButton3D(
                                icon = Icons.Default.Home,
                                onClick = onHome,
                                size = 48.dp,
                                color = Button3DColor.BLACK
                            )
                            Text("Home", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFA0A5B5))
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularButton3D(
                                icon = Icons.Default.Refresh,
                                onClick = onReplay,
                                size = 48.dp,
                                color = Button3DColor.BLACK
                            )
                            Text("Replay", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFA0A5B5))
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularButton3D(
                                icon = Icons.Default.Share,
                                onClick = onShare,
                                size = 48.dp,
                                color = Button3DColor.BLACK
                            )
                            Text("Share", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFA0A5B5))
                        }
                    }
                }
            }

            // Top Floating Golden Crown
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .offset(y = (-8).dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFFFFDD33), Color(0xFFFF9900))
                        )
                    )
                    .border(2.5.dp, Color(0xFFB36B00), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Crown",
                    tint = Color.White,
                    modifier = Modifier.size(38.dp)
                )
            }
        }
    }
}

/**
 * Exact "Need a Hint?" Dialog matching Card 4 of the design sheet:
 * - Floating lightbulb emblem
 * - "Watch a video to get a hint!" or spend 100 coins
 * - Gold/Orange "Watch Ad / 100 Coins" button
 * - "No, Thanks" dismiss link
 */
@Composable
fun NeedAHintDialog3D(
    coins: Int,
    onWatchAd: () -> Unit,
    onUseCoins: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
                .testTag("need_a_hint_dialog"),
            contentAlignment = Alignment.TopCenter
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp)
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
                    // Top close button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }

                    // Header red ribbon
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(46.dp)
                            .clip(RoundedCornerShape(23.dp)),
                        color = Color(0xFFE50914)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "Need a Hint?",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Hints highlight an unblocked arrow ready to escape the maze!",
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        color = Color(0xFFA0A5B5)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Gold "Watch Ad" Button
                    GlossyButton3D(
                        text = "Watch Ad (+1 Hint)",
                        color = Button3DColor.GOLD,
                        onClick = onWatchAd,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Use 100 Coins button if player has enough
                    if (coins >= 100) {
                        GlossyButton3D(
                            text = "Buy Hint (100 Coins)",
                            color = Button3DColor.RED,
                            onClick = onUseCoins,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    Text(
                        text = "No, Thanks",
                        color = Color(0xFFA0A5B5),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        modifier = Modifier
                            .clickable { onDismiss() }
                            .padding(8.dp)
                    )
                }
            }

            // Floating Lightbulb Top Emblem
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .offset(y = (-6).dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFFFFDD33), Color(0xFFFF9900))
                        )
                    )
                    .border(2.5.dp, Color(0xFFB36B00), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(34.dp)
                )
            }
        }
    }
}

/**
 * Exact "No Lives!" Dialog matching Card 7 of the design sheet:
 * - 3 red hearts topper
 * - "No Lives!"
 * - "Watch a video to get more lives!"
 * - Gold "Watch Ad (+3 Lives)" button
 * - "No, Thanks"
 */
@Composable
fun NoLivesDialog3D(
    onWatchAd: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
                .testTag("no_lives_dialog"),
            contentAlignment = Alignment.TopCenter
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp)
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
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "No Lives!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Watch a video to get more lives and continue this level!",
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        color = Color(0xFFA0A5B5)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    GlossyButton3D(
                        text = "Watch Ad (+3 Lives)",
                        color = Button3DColor.GOLD,
                        onClick = onWatchAd,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "No, Thanks",
                        color = Color(0xFFA0A5B5),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        modifier = Modifier
                            .clickable { onDismiss() }
                            .padding(8.dp)
                    )
                }
            }

            // 3 Hearts topper
            Row(
                modifier = Modifier
                    .offset(y = (-6).dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF222230))
                    .border(1.5.dp, Color(0xFFE50914), RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                for (i in 1..3) {
                    PuffyHeart3D(isFull = true, size = 26.dp)
                    if (i < 3) Spacer(modifier = Modifier.width(4.dp))
                }
            }
        }
    }
}

/**
 * Exact "PAUSED" Dialog matching Card 6 of the design sheet:
 * - Resume (green)
 * - Restart (blue)
 * - Home (blue)
 * - Exit (red)
 */
@Composable
fun PausedDialog3D(
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onHome: () -> Unit,
    onExit: () -> Unit
) {
    Dialog(onDismissRequest = onResume) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .border(3.dp, Color(0xFFE50914), RoundedCornerShape(32.dp))
                .testTag("paused_dialog"),
            color = Color(0xFF161622),
            shape = RoundedCornerShape(32.dp),
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "PAUSED",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                GlossyButton3D(
                    text = "Resume",
                    color = Button3DColor.RED,
                    onClick = onResume,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "pause_resume_button"
                )

                Spacer(modifier = Modifier.height(12.dp))

                GlossyButton3D(
                    text = "Restart",
                    color = Button3DColor.BLACK,
                    onClick = onRestart,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "pause_restart_button"
                )

                Spacer(modifier = Modifier.height(12.dp))

                GlossyButton3D(
                    text = "Home",
                    color = Button3DColor.BLACK,
                    onClick = onHome,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "pause_home_button"
                )

                Spacer(modifier = Modifier.height(12.dp))

                GlossyButton3D(
                    text = "Exit",
                    color = Button3DColor.RED,
                    onClick = onExit,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "pause_exit_button"
                )
            }
        }
    }
}

/**
 * Exact "Settings" Dialog matching Card 8 of the design sheet:
 * - Sound toggle
 * - Music toggle
 * - Vibration toggle
 * - Language selection
 */
@Composable
fun SettingsDialog3D(
    soundEnabled: Boolean,
    musicEnabled: Boolean,
    vibrationEnabled: Boolean,
    onToggleSound: () -> Unit,
    onToggleMusic: () -> Unit,
    onToggleVibration: () -> Unit,
    onResetProgress: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .border(3.dp, Color(0xFFE50914), RoundedCornerShape(32.dp))
                .testTag("settings_dialog"),
            color = Color(0xFF161622),
            shape = RoundedCornerShape(32.dp),
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Settings",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sound
                SettingsToggleRow(
                    label = "Sound",
                    icon = Icons.Default.VolumeUp,
                    checked = soundEnabled,
                    onCheckedChange = { onToggleSound() }
                )

                // Music
                SettingsToggleRow(
                    label = "Music",
                    icon = Icons.Default.MusicNote,
                    checked = musicEnabled,
                    onCheckedChange = { onToggleMusic() }
                )

                // Vibration
                SettingsToggleRow(
                    label = "Vibration",
                    icon = Icons.Default.Vibration,
                    checked = vibrationEnabled,
                    onCheckedChange = { onToggleVibration() }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Language
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF222230))
                        .border(1.dp, Color(0xFF333342), RoundedCornerShape(14.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Language", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("English >", color = Color(0xFFFF334B), fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Reset Progress
                GlossyButton3D(
                    text = "Reset Progress",
                    color = Button3DColor.RED,
                    height = 46.dp,
                    fontSize = 14.sp,
                    onClick = {
                        onResetProgress()
                        onDismiss()
                    }
                )
            }
        }
    }
}

@Composable
private fun SettingsToggleRow(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF222230))
            .border(1.dp, Color(0xFF333342), RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(label, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFFE50914),
                uncheckedThumbColor = Color.LightGray,
                uncheckedTrackColor = Color(0xFF14141C)
            )
        )
    }
}

/**
 * "Select Difficulty" Modal matching Card 9 of the design sheet:
 * - Easy (black)
 * - Normal (red check)
 * - Hard (orange)
 * - Expert (crimson red)
 */
@Composable
fun DifficultyDialog3D(
    currentDifficulty: Difficulty,
    onSelectDifficulty: (Difficulty) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Select Difficulty",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                GlossyButton3D(
                    text = if (currentDifficulty == Difficulty.EASY) "Easy ✓" else "Easy",
                    color = Button3DColor.BLACK,
                    height = 50.dp,
                    onClick = {
                        onSelectDifficulty(Difficulty.EASY)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                GlossyButton3D(
                    text = if (currentDifficulty == Difficulty.MEDIUM) "Normal ✓" else "Normal",
                    color = Button3DColor.RED,
                    height = 50.dp,
                    onClick = {
                        onSelectDifficulty(Difficulty.MEDIUM)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                GlossyButton3D(
                    text = if (currentDifficulty == Difficulty.HARD) "Hard ✓" else "Hard",
                    color = Button3DColor.GOLD,
                    height = 50.dp,
                    onClick = {
                        onSelectDifficulty(Difficulty.HARD)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                GlossyButton3D(
                    text = if (currentDifficulty == Difficulty.EXPERT) "Expert ✓" else "Expert",
                    color = Button3DColor.RED,
                    height = 50.dp,
                    onClick = {
                        onSelectDifficulty(Difficulty.EXPERT)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
