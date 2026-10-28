package com.example.game.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import com.example.game.model.Arrow
import com.example.game.model.Direction
import com.example.game.model.GridPoint
import com.example.game.model.LevelData
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

@Composable
fun ArrowBoard(
    level: LevelData,
    activeArrows: List<Arrow>,
    movingArrows: Map<String, MovingArrowState>,
    shakingArrowId: String?,
    hintArrowId: String?,
    isDarkTheme: Boolean,
    isGridVisible: Boolean = true,
    onArrowTapped: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Continuous smooth transition for hint pulsing
    val infiniteTransition = rememberInfiniteTransition(label = "boardPulse")
    val hintPulse by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hintPulseAnim"
    )

    // Shake animation phase
    val shakePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.283f,
        animationSpec = infiniteRepeatable(
            animation = tween(70, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shakeAnim"
    )

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .testTag("arrow_puzzle_board")
            .pointerInput(level.levelNumber, activeArrows) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val boardMetrics = calculateBoardMetrics(size.width.toFloat(), size.height.toFloat(), level)
                    val tappedArrowId = findTappedArrow(down.position, activeArrows, boardMetrics)
                    if (tappedArrowId != null) {
                        down.consume()
                        onArrowTapped(tappedArrowId)
                    }
                }
            }
    ) {
        val metrics = calculateBoardMetrics(size.width, size.height, level)

        // Draw elevated board card background
        drawBoardBackground(metrics, isDarkTheme)

        // Draw subtle grid dots if enabled
        if (isGridVisible) {
            drawGridDots(metrics, level, isDarkTheme)
        }

        // Draw stationary active arrows
        for (arrow in activeArrows) {
            val movingState = movingArrows[arrow.id]
            if (movingState != null) continue // Drawn separately with exit motion

            val isShaking = arrow.id == shakingArrowId
            val isHinted = arrow.id == hintArrowId

            val shakeOffset = if (isShaking) {
                Offset(sin(shakePhase * 3f) * 8f, 0f)
            } else {
                Offset.Zero
            }

            val baseColor = when {
                isShaking -> Color(0xFFFF1744) // Intense warning red flash
                isHinted -> Color(0xFFFFD700)  // Radiant gold hint
                else -> Color(0xFFFF2A3C)      // Radiant Crimson Red on Obsidian Black
            }

            drawSmoothArrow(
                arrow = arrow,
                metrics = metrics,
                color = baseColor,
                extraOffset = shakeOffset,
                isHinted = isHinted,
                hintPulse = hintPulse,
                isDarkTheme = isDarkTheme
            )
        }

        // Draw exiting arrows with high-speed punchy acceleration, motion blur streaks, and spark particles
        for ((_, movingState) in movingArrows) {
            val arrow = movingState.arrow
            val progress = movingState.progress

            // Punchy FastOutLinearIn acceleration curve
            val easeProgress = progress * progress * (1.8f - 0.8f * progress)

            // Initial pop: expands to 1.15x on tap, then rockets forward
            val popScale = if (progress < 0.22f) {
                1.0f + (progress / 0.22f) * 0.15f
            } else {
                1.15f - ((progress - 0.22f) / 0.78f) * 0.10f
            }

            val travelDistance = max(level.gridWidth, level.gridHeight) * 2.3f * metrics.cellSize
            val forwardVec = Offset(arrow.direction.dx.toFloat(), arrow.direction.dy.toFloat())
            val moveOffset = forwardVec * (travelDistance * easeProgress)

            // 1. Draw luminous motion blur streaks (3 trailing ghost layers in fiery red)
            for (trailIdx in 3 downTo 1) {
                val lagDist = metrics.cellSize * 0.32f * trailIdx * (1f + progress * 0.8f)
                val trailOffset = moveOffset - forwardVec * lagDist
                val trailAlpha = (0.32f / trailIdx) * (1f - progress * 0.4f)
                val trailColor = Color(0xFFFF2A3C).copy(alpha = trailAlpha)

                drawSmoothArrow(
                    arrow = arrow,
                    metrics = metrics,
                    color = trailColor,
                    extraOffset = trailOffset,
                    isHinted = false,
                    hintPulse = 0f,
                    isDarkTheme = isDarkTheme,
                    scale = popScale * (1f - trailIdx * 0.05f)
                )
            }

            // 2. Draw main rocket arrow with fiery crimson glow
            val movingColor = Color(0xFFFF3B56)
            drawSmoothArrow(
                arrow = arrow,
                metrics = metrics,
                color = movingColor,
                extraOffset = moveOffset,
                isHinted = true,
                hintPulse = 1f,
                isDarkTheme = isDarkTheme,
                scale = popScale
            )

            // 3. Draw luminous particle sparks bursting around tip and exit trail
            val tipCanvas = metrics.gridToCanvas(arrow.tipPoint) + moveOffset
            val sparkCount = 8
            val hash = arrow.id.hashCode()
            val sparkFade = (1f - progress).coerceIn(0f, 1f)

            for (p in 0 until sparkCount) {
                val angle = ((hash + p * 47) % 360) * (3.14159f / 180f)
                val speed = metrics.cellSize * (0.4f + ((hash * (p + 1)) % 100) / 140f) * progress
                val sparkX = tipCanvas.x - forwardVec.x * speed * 0.7f + kotlin.math.cos(angle) * (speed * 0.5f)
                val sparkY = tipCanvas.y - forwardVec.y * speed * 0.7f + kotlin.math.sin(angle) * (speed * 0.5f)

                val sparkRadius = metrics.cellSize * (0.055f * sparkFade)
                val sparkColor = if (p % 2 == 0) {
                    Color(0xFFFF2A3C).copy(alpha = 0.85f * sparkFade)
                } else {
                    Color(0xFFFFD700).copy(alpha = 0.85f * sparkFade)
                }

                drawCircle(
                    color = sparkColor,
                    radius = sparkRadius,
                    center = Offset(sparkX, sparkY)
                )
            }
        }
    }
}

private data class BoardMetrics(
    val boardLeft: Float,
    val boardTop: Float,
    val boardWidth: Float,
    val boardHeight: Float,
    val cellSize: Float
) {
    fun gridToCanvas(p: GridPoint): Offset {
        return Offset(
            x = boardLeft + (p.x + 0.5f) * cellSize,
            y = boardTop + (p.y + 0.5f) * cellSize
        )
    }
}

private fun calculateBoardMetrics(canvasWidth: Float, canvasHeight: Float, level: LevelData): BoardMetrics {
    val paddingPx = 32f
    val availW = canvasWidth - 2 * paddingPx
    val availH = canvasHeight - 2 * paddingPx

    val cellSize = min(availW / level.gridWidth, availH / level.gridHeight)
    val boardW = level.gridWidth * cellSize
    val boardH = level.gridHeight * cellSize
    val left = (canvasWidth - boardW) / 2f
    val top = (canvasHeight - boardH) / 2f

    return BoardMetrics(left, top, boardW, boardH, cellSize)
}

private fun DrawScope.drawBoardBackground(metrics: BoardMetrics, isDarkTheme: Boolean) {
    val margin = metrics.cellSize * 0.42f
    val bgRect = androidx.compose.ui.geometry.Rect(
        left = metrics.boardLeft - margin,
        top = metrics.boardTop - margin,
        right = metrics.boardLeft + metrics.boardWidth + margin,
        bottom = metrics.boardTop + metrics.boardHeight + margin
    )

    // Soft ambient shadow
    drawRoundRect(
        color = Color(0x66000000),
        topLeft = Offset(bgRect.left + 1f, bgRect.top + 4f),
        size = Size(bgRect.width, bgRect.height),
        cornerRadius = CornerRadius(28f, 28f)
    )

    // Card face: Deep Carbon Black
    drawRoundRect(
        color = Color(0xFF14141B),
        topLeft = Offset(bgRect.left, bgRect.top),
        size = Size(bgRect.width, bgRect.height),
        cornerRadius = CornerRadius(28f, 28f)
    )

    // Crimson red board border
    drawRoundRect(
        color = Color(0xFFE50914),
        topLeft = Offset(bgRect.left, bgRect.top),
        size = Size(bgRect.width, bgRect.height),
        cornerRadius = CornerRadius(28f, 28f),
        style = Stroke(width = 2.4f)
    )
}

private fun DrawScope.drawGridDots(metrics: BoardMetrics, level: LevelData, isDarkTheme: Boolean) {
    val dotColor = Color(0xFF2E161C)
    val dotRadius = metrics.cellSize * 0.04f

    for (x in 0 until level.gridWidth) {
        for (y in 0 until level.gridHeight) {
            val center = metrics.gridToCanvas(GridPoint(x, y))
            drawCircle(
                color = dotColor,
                radius = dotRadius,
                center = center
            )
        }
    }
}

/**
 * Builds a smooth, filleted vector path with generous rounded corner arcs using Bézier curves.
 */
private fun buildSmoothFilletedPath(canvasPoints: List<Offset>, cornerRadius: Float): Path {
    val path = Path()
    if (canvasPoints.isEmpty()) return path
    if (canvasPoints.size <= 2) {
        path.moveTo(canvasPoints.first().x, canvasPoints.first().y)
        for (i in 1 until canvasPoints.size) {
            path.lineTo(canvasPoints[i].x, canvasPoints[i].y)
        }
        return path
    }

    path.moveTo(canvasPoints.first().x, canvasPoints.first().y)

    for (i in 1 until canvasPoints.size - 1) {
        val pPrev = canvasPoints[i - 1]
        val pCurr = canvasPoints[i]
        val pNext = canvasPoints[i + 1]

        val d1 = pCurr - pPrev
        val len1 = hypot(d1.x, d1.y)
        val d2 = pNext - pCurr
        val len2 = hypot(d2.x, d2.y)

        if (len1 > 0f && len2 > 0f) {
            val u1 = Offset(d1.x / len1, d1.y / len1)
            val u2 = Offset(d2.x / len2, d2.y / len2)

            // Clamp radius so it doesn't exceed half the incoming/outgoing segment lengths
            val r = min(cornerRadius, min(len1 / 2.2f, len2 / 2.2f))

            val arcStart = pCurr - u1 * r
            val arcEnd = pCurr + u2 * r

            path.lineTo(arcStart.x, arcStart.y)
            path.quadraticBezierTo(pCurr.x, pCurr.y, arcEnd.x, arcEnd.y)
        } else {
            path.lineTo(pCurr.x, pCurr.y)
        }
    }

    val last = canvasPoints.last()
    path.lineTo(last.x, last.y)
    return path
}

private fun DrawScope.drawSmoothArrow(
    arrow: Arrow,
    metrics: BoardMetrics,
    color: Color,
    extraOffset: Offset = Offset.Zero,
    isHinted: Boolean = false,
    hintPulse: Float = 1f,
    isDarkTheme: Boolean = false,
    scale: Float = 1f
) {
    // Streamlined, medium-slim stroke width
    val strokeWidth = metrics.cellSize * 0.175f * scale
    val cornerRadius = metrics.cellSize * 0.36f * scale
    val tipSize = metrics.cellSize * 0.44f * scale

    val canvasPoints = arrow.points.map { pt ->
        metrics.gridToCanvas(pt) + extraOffset
    }

    if (canvasPoints.size < 2) return

    val smoothPath = buildSmoothFilletedPath(canvasPoints, cornerRadius)

    // 1. Radiant glow if hinted or moving
    if (isHinted) {
        val glowRadius = strokeWidth * 2.4f
        val glowColor = Color(0xFFFFD700).copy(alpha = 0.45f * hintPulse)
        drawPath(
            path = smoothPath,
            color = glowColor,
            style = Stroke(
                width = glowRadius,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }

    // 2. Soft, subtle shadow
    val shadowOffset = Offset(1.2f, 2.4f)
    val shadowPoints = canvasPoints.map { it + shadowOffset }
    val shadowPath = buildSmoothFilletedPath(shadowPoints, cornerRadius)
    drawPath(
        path = shadowPath,
        color = if (isDarkTheme) Color(0x1E000000) else Color(0x12000000),
        style = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )

    // 3. Arrow polyline body with smooth rounded corners
    drawPath(
        path = smoothPath,
        color = color,
        style = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )

    // 4. Sleek, beveled arrowhead at the tip
    val tip = canvasPoints.last()
    drawSleekArrowHead(tip, arrow.direction, tipSize, color)
}

/**
 * Renders a sleek, modern arrowhead with rounded vertex and subtle beveled rear flare.
 */
private fun DrawScope.drawSleekArrowHead(
    tipCenter: Offset,
    dir: Direction,
    size: Float,
    color: Color
) {
    val forward = Offset(dir.dx.toFloat(), dir.dy.toFloat())
    val normal = Offset(-forward.y, forward.x)

    val tipPoint = tipCenter + forward * (size * 0.55f)
    val baseCenter = tipCenter - forward * (size * 0.40f)
    val wingLeft = baseCenter + normal * (size * 0.50f)
    val wingRight = baseCenter - normal * (size * 0.50f)
    val innerNotch = baseCenter + forward * (size * 0.16f)

    val headPath = Path().apply {
        moveTo(tipPoint.x, tipPoint.y)
        lineTo(wingLeft.x, wingLeft.y)
        lineTo(innerNotch.x, innerNotch.y)
        lineTo(wingRight.x, wingRight.y)
        close()
    }

    drawPath(
        path = headPath,
        color = color,
        style = Fill
    )
}

/**
 * Finds the arrow closest to [tapOffset], within a comfortable touch radius.
 */
private fun findTappedArrow(
    tapOffset: Offset,
    arrows: List<Arrow>,
    metrics: BoardMetrics
): String? {
    var closestArrowId: String? = null
    var minDistance = Float.MAX_VALUE
    val touchThreshold = metrics.cellSize * 0.95f

    for (arrow in arrows) {
        val canvasPoints = arrow.points.map { metrics.gridToCanvas(it) }
        for (i in 0 until canvasPoints.size - 1) {
            val dist = distanceToSegment(tapOffset, canvasPoints[i], canvasPoints[i + 1])
            if (dist < minDistance && dist <= touchThreshold) {
                minDistance = dist
                closestArrowId = arrow.id
            }
        }
        // Also check distance to tip directly
        if (canvasPoints.isNotEmpty()) {
            val tipDist = (tapOffset - canvasPoints.last()).getDistance()
            if (tipDist < minDistance && tipDist <= touchThreshold) {
                minDistance = tipDist
                closestArrowId = arrow.id
            }
        }
    }

    return closestArrowId
}

private fun distanceToSegment(p: Offset, a: Offset, b: Offset): Float {
    val ab = b - a
    val ap = p - a
    val abLenSq = ab.x * ab.x + ab.y * ab.y
    if (abLenSq == 0f) return (p - a).getDistance()

    val t = ((ap.x * ab.x + ap.y * ab.y) / abLenSq).coerceIn(0f, 1f)
    val projection = a + ab * t
    return (p - projection).getDistance()
}
