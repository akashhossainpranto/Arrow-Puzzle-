package com.example.game.engine

import com.example.game.model.Arrow
import com.example.game.model.Direction
import com.example.game.model.GridPoint

/**
 * Deterministic collision and raycast engine for arrow movement.
 */
object PuzzlePhysics {

    /**
     * Determines whether [arrow] can freely slide forward out of the board in its pointing direction
     * without colliding with any other arrow in [allArrows].
     */
    fun canArrowExit(
        arrow: Arrow,
        allArrows: List<Arrow>,
        gridWidth: Int,
        gridHeight: Int
    ): Boolean {
        return findBlockingArrow(arrow, allArrows, gridWidth, gridHeight) == null
    }

    /**
     * Finds the first arrow blocking [arrow]'s forward exit path, or null if the path is clear.
     */
    fun findBlockingArrow(
        arrow: Arrow,
        allArrows: List<Arrow>,
        gridWidth: Int,
        gridHeight: Int
    ): Arrow? {
        // Fast lookup map of cell -> Arrow (excluding the candidate arrow itself)
        val cellToArrowMap = mutableMapOf<GridPoint, Arrow>()
        for (other in allArrows) {
            if (other.id != arrow.id) {
                for (cell in other.occupiedCells) {
                    cellToArrowMap[cell] = other
                }
            }
        }

        // Trace ray from tip forward in direction until outside grid bounds
        val tip = arrow.tipPoint
        val dir = arrow.direction
        var step = 1

        while (true) {
            val rayCell = GridPoint(tip.x + dir.dx * step, tip.y + dir.dy * step)
            // If the ray has escaped outside the board boundaries, the path is completely clear
            if (rayCell.x < 0 || rayCell.x >= gridWidth || rayCell.y < 0 || rayCell.y >= gridHeight) {
                return null
            }
            // Check if ray hits any other arrow
            val blocker = cellToArrowMap[rayCell]
            if (blocker != null) {
                return blocker
            }
            step++
        }
    }

    /**
     * Returns all arrows in [allArrows] that currently have a completely clear exit path.
     */
    fun findMovableArrows(
        allArrows: List<Arrow>,
        gridWidth: Int,
        gridHeight: Int
    ): List<Arrow> {
        val movable = mutableListOf<Arrow>()
        for (arrow in allArrows) {
            if (canArrowExit(arrow, allArrows, gridWidth, gridHeight)) {
                movable.add(arrow)
            }
        }
        return movable
    }
}
