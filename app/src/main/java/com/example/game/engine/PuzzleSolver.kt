package com.example.game.engine

import com.example.game.model.Arrow
import com.example.game.model.LevelData

/**
 * Intelligent puzzle solver for verification and hint generation.
 */
object PuzzleSolver {

    /**
     * Solves the given level, returning an ordered list of arrow IDs to tap to complete the level,
     * or null if the level has no valid solution (deadlock).
     */
    fun solve(level: LevelData): List<String>? {
        return solveRecursive(level.arrows, level.gridWidth, level.gridHeight, mutableListOf())
    }

    private fun solveRecursive(
        remaining: List<Arrow>,
        gridWidth: Int,
        gridHeight: Int,
        solutionAcc: MutableList<String>
    ): List<String>? {
        if (remaining.isEmpty()) {
            return solutionAcc
        }

        val movable = PuzzlePhysics.findMovableArrows(remaining, gridWidth, gridHeight)
        if (movable.isEmpty()) {
            return null // Deadlock reached on this branch
        }

        // Try movable candidates (heuristic: try arrow with smallest remaining dependency footprint)
        for (candidate in movable) {
            solutionAcc.add(candidate.id)
            val nextRemaining = remaining.filter { it.id != candidate.id }
            val result = solveRecursive(nextRemaining, gridWidth, gridHeight, solutionAcc)
            if (result != null) {
                return result
            }
            solutionAcc.removeAt(solutionAcc.lastIndex)
        }

        return null
    }

    /**
     * Finds the best next arrow to suggest as a hint from the current board state.
     */
    fun getHint(
        currentArrows: List<Arrow>,
        gridWidth: Int,
        gridHeight: Int
    ): Arrow? {
        val movable = PuzzlePhysics.findMovableArrows(currentArrows, gridWidth, gridHeight)
        if (movable.isEmpty()) return null

        // Pick candidate that leads to a winning branch
        for (candidate in movable) {
            val remaining = currentArrows.filter { it.id != candidate.id }
            val dummyLevel = LevelData(
                levelNumber = 0,
                gridWidth = gridWidth,
                gridHeight = gridHeight,
                arrows = remaining,
                parMoves = remaining.size,
                difficulty = com.example.game.model.Difficulty.BEGINNER
            )
            if (solve(dummyLevel) != null) {
                return candidate
            }
        }

        // Fallback to first movable arrow
        return movable.firstOrNull()
    }

    /**
     * Quick check if a level configuration is solvable.
     */
    fun isSolvable(level: LevelData): Boolean {
        return solve(level) != null
    }
}
