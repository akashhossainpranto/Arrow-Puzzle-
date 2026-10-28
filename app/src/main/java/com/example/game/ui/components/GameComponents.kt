package com.example.game.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class Button3DColor(
    val topFaceStart: Color,
    val topFaceEnd: Color,
    val bottomBevel: Color,
    val highlight: Color,
    val textColor: Color = Color.White
) {
    RED(
        topFaceStart = Color(0xFFFF334B),
        topFaceEnd = Color(0xFFE50914),
        bottomBevel = Color(0xFF8B0000),
        highlight = Color(0x66FFFFFF)
    ),
    BLACK(
        topFaceStart = Color(0xFF282834),
        topFaceEnd = Color(0xFF161620),
        bottomBevel = Color(0xFF0A0A0E),
        highlight = Color(0x44FFFFFF)
    ),
    BLUE( // Ruby Red accent for legacy blue references
        topFaceStart = Color(0xFFD61A3C),
        topFaceEnd = Color(0xFF9E0B24),
        bottomBevel = Color(0xFF5A000A),
        highlight = Color(0x66FFFFFF)
    ),
    GREEN(
        topFaceStart = Color(0xFF2EDB62),
        topFaceEnd = Color(0xFF1CB049),
        bottomBevel = Color(0xFF147A34),
        highlight = Color(0x66FFFFFF)
    ),
    GOLD(
        topFaceStart = Color(0xFFFFCC00),
        topFaceEnd = Color(0xFFFFA500),
        bottomBevel = Color(0xFFB36B00),
        highlight = Color(0x77FFFFFF)
    ),
    GREY(
        topFaceStart = Color(0xFF3A3A48),
        topFaceEnd = Color(0xFF252530),
        bottomBevel = Color(0xFF14141C),
        highlight = Color(0x33FFFFFF)
    )
}

/**
 * 3D Flat-Glossy Action Button matching the design sheet.
 * Features a distinct bottom bevel and upper specular reflection highlight.
 */
@Composable
fun GlossyButton3D(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    color: Button3DColor = Button3DColor.GREEN,
    height: Dp = 56.dp,
    fontSize: TextUnit = 18.sp,
    testTag: String = "button_3d"
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val bevelHeight = 5.dp
    val pressOffset = if (isPressed) bevelHeight else 0.dp

    Box(
        modifier = modifier
            .height(height)
            .testTag(testTag)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.TopCenter
    ) {
        // Bottom bevel layer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(height / 2))
                .background(color.bottomBevel)
        )

        // Top glossy face
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height - bevelHeight)
                .offset(y = pressOffset)
                .clip(RoundedCornerShape(height / 2))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(color.topFaceStart, color.topFaceEnd)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // Upper specular reflection arc
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .height((height - bevelHeight) * 0.45f)
                    .align(Alignment.TopCenter)
                    .offset(y = 2.dp)
                    .clip(RoundedCornerShape(height / 2))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(color.highlight, Color.Transparent)
                        )
                    )
            )

            // Button content
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color.textColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    color = color.textColor,
                    fontSize = fontSize,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

/**
 * 3D Flat-Glossy Circular Button (Home, Settings, Restart, Hint, Grid, etc.)
 */
@Composable
fun CircularButton3D(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 54.dp,
    color: Button3DColor = Button3DColor.BLUE,
    badgeCount: Int? = null,
    testTag: String = "circle_button_3d"
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val bevelHeight = 4.dp
    val pressOffset = if (isPressed) bevelHeight else 0.dp

    Box(
        modifier = modifier
            .size(size)
            .testTag(testTag)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.TopCenter
    ) {
        // Bottom bevel shadow
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(color.bottomBevel)
        )

        // Top glossy face
        Box(
            modifier = Modifier
                .size(size - bevelHeight)
                .offset(y = pressOffset)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(color.topFaceStart, color.topFaceEnd)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // Specular sheen
            Box(
                modifier = Modifier
                    .size(size * 0.75f, size * 0.35f)
                    .align(Alignment.TopCenter)
                    .offset(y = 2.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(color.highlight, Color.Transparent)
                        )
                    )
            )

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color.textColor,
                modifier = Modifier.size(size * 0.52f)
            )
        }

        // Optional red notification badge
        if (badgeCount != null && badgeCount > 0) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 2.dp, y = (-2).dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF2A2A))
                    .border(1.5.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$badgeCount",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * 3D Puffy Glossy Heart.
 * Renders full red heart with specular shine, or soft slate gray when empty.
 */
@Composable
fun PuffyHeart3D(
    isFull: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 26.dp
) {
    Canvas(
        modifier = modifier
            .size(size)
            .padding(2.dp)
    ) {
        val w = this.size.width
        val h = this.size.height

        val heartPath = Path().apply {
            moveTo(w * 0.5f, h * 0.85f)
            cubicTo(w * 0.15f, h * 0.60f, 0f, h * 0.35f, 0f, h * 0.22f)
            cubicTo(0f, h * 0.05f, w * 0.22f, 0f, w * 0.5f, h * 0.22f)
            cubicTo(w * 0.78f, 0f, w, h * 0.05f, w, h * 0.22f)
            cubicTo(w, h * 0.35f, w * 0.85f, h * 0.60f, w * 0.5f, h * 0.85f)
            close()
        }

        if (isFull) {
            // Bottom bevel
            drawPath(
                path = heartPath,
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFFF334B), Color(0xFFC00A24))
                ),
                style = Fill
            )
            // Specular shine dot
            drawCircle(
                color = Color(0x99FFFFFF),
                radius = w * 0.14f,
                center = Offset(w * 0.32f, h * 0.26f)
            )
        } else {
            // Depleted heart
            drawPath(
                path = heartPath,
                color = Color(0xFF2A2A38),
                style = Fill
            )
        }
    }
}

/**
 * Gold Coin Pill `[★] 150 [+]` styled for Red & Black theme.
 */
@Composable
fun CoinPill3D(
    coins: Int,
    onAddCoins: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .border(2.dp, Color(0xFFE50914), RoundedCornerShape(20.dp)),
        color = Color(0xFF161620),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Gold Star Medallion
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFFFFE066), Color(0xFFFF9900))
                        )
                    )
                    .border(1.5.dp, Color(0xFFB36B00), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "$coins",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(modifier = Modifier.width(6.dp))

            // Crimson red plus button
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE50914))
                    .clickable { onAddCoins() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Coins",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/**
 * 3D Flat-Beveled Numbered Level Tile (4x5 grid on Level Select screen).
 * Styled in high-contrast Red & Black.
 */
@Composable
fun LevelSelectTile3D(
    levelNumber: Int,
    isUnlocked: Boolean,
    isCompleted: Boolean,
    stars: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val bevelHeight = 4.dp
    val pressOffset = if (isPressed) bevelHeight else 0.dp

    val (topColorStart, topColorEnd, bottomBevel) = when {
        !isUnlocked -> Triple(Color(0xFF282834), Color(0xFF181822), Color(0xFF0C0C12))
        isCompleted -> Triple(Color(0xFFE50914), Color(0xFFB71C1C), Color(0xFF700810))
        else -> Triple(Color(0xFFFF334B), Color(0xFFE50914), Color(0xFF8A0A16)) // Current active
    }

    Box(
        modifier = modifier
            .size(64.dp)
            .testTag("level_tile_$levelNumber")
            .clickable(
                enabled = isUnlocked,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.TopCenter
    ) {
        // Bottom bevel
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
                .background(bottomBevel)
        )

        // Top glossy face
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp - bevelHeight)
                .offset(y = pressOffset)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(topColorStart, topColorEnd)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            if (!isUnlocked) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = Color(0xFFCBD5E1),
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "$levelNumber",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )

                    // Stars row
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (i in 1..2) {
                            val isLit = i <= stars
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (isLit) Color(0xFFFFCC00) else Color(0x66FFFFFF),
                                modifier = Modifier.size(11.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
