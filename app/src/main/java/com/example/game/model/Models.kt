package com.example.game.model

import kotlin.math.max
import kotlin.math.min

/**
 * Direction in which an arrow tip points or travels.
 */
enum class Direction(val dx: Int, val dy: Int, val angleDegrees: Float) {
    UP(0, -1, 270f),
    DOWN(0, 1, 90f),
    LEFT(-1, 0, 180f),
    RIGHT(1, 0, 0f);

    companion object {
        fun fromDelta(dx: Int, dy: Int): Direction {
            return when {
                dx > 0 -> RIGHT
                dx < 0 -> LEFT
                dy > 0 -> DOWN
                else -> UP
            }
        }
    }
}

/**
 * Discrete grid coordinate.
 */
data class GridPoint(val x: Int, val y: Int) {
    fun offset(direction: Direction, distance: Int = 1): GridPoint =
        GridPoint(x + direction.dx * distance, y + direction.dy * distance)
}

/**
 * Represents a single arrow consisting of a path on the grid ending at a directional tip.
 * [points] is the sequence of vertices from tail to tip.
 * The tip is always [points.last()].
 * The arrowhead points in [direction].
 */
data class Arrow(
    val id: String,
    val points: List<GridPoint>,
    val direction: Direction,
    val colorHex: Long = 0xFF121E36L
) {
    val tipPoint: GridPoint
        get() = points.last()

    /**
     * Set of all discrete grid cells occupied by the arrow's polyline body.
     */
    val occupiedCells: Set<GridPoint> by lazy {
        val cells = mutableSetOf<GridPoint>()
        if (points.size == 1) {
            cells.add(points.first())
            return@lazy cells
        }
        for (i in 0 until points.size - 1) {
            val p1 = points[i]
            val p2 = points[i + 1]
            val minX = min(p1.x, p2.x)
            val maxX = max(p1.x, p2.x)
            val minY = min(p1.y, p2.y)
            val maxY = max(p1.y, p2.y)
            for (x in minX..maxX) {
                for (y in minY..maxY) {
                    cells.add(GridPoint(x, y))
                }
            }
        }
        cells
    }

    /**
     * Segments between consecutive vertices.
     */
    val segments: List<Pair<GridPoint, GridPoint>> by lazy {
        val list = mutableListOf<Pair<GridPoint, GridPoint>>()
        for (i in 0 until points.size - 1) {
            list.add(Pair(points[i], points[i + 1]))
        }
        list
    }
}

enum class Difficulty(val displayName: String, val colorHex: Long) {
    BEGINNER("Beginner", 0xFF22C55E),
    EASY("Easy", 0xFF10B981),
    MEDIUM("Medium", 0xFF0EA5E9),
    HARD("Hard", 0xFF3B82F6),
    EXPERT("Expert", 0xFF8B5CF6),
    MASTER("Master", 0xFFA855F7),
    EXTREME("Extreme", 0xFFEC4899)
}

data class LevelData(
    val levelNumber: Int,
    val gridWidth: Int,
    val gridHeight: Int,
    val arrows: List<Arrow>,
    val parMoves: Int,
    val difficulty: Difficulty
)
