package com.example

import com.example.game.engine.LevelCatalog
import com.example.game.engine.PuzzlePhysics
import com.example.game.engine.PuzzleSolver
import com.example.game.model.Arrow
import com.example.game.model.Direction
import com.example.game.model.GridPoint
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ArrowPuzzleTest {

    @Test
    fun testUnblockedArrowCanExit() {
        // Arrow pointing right at edge of grid (x = 2 to 4 in 5x5 grid)
        val freeArrow = Arrow(
            id = "free",
            points = listOf(GridPoint(2, 2), GridPoint(4, 2)),
            direction = Direction.RIGHT
        )
        val canExit = PuzzlePhysics.canArrowExit(freeArrow, listOf(freeArrow), 5, 5)
        assertTrue(canExit)
    }

    @Test
    fun testBlockedArrowCannotExit() {
        // a1 points right toward (4, 2)
        val a1 = Arrow(
            id = "a1",
            points = listOf(GridPoint(1, 2), GridPoint(3, 2)),
            direction = Direction.RIGHT
        )
        // a2 occupies (4, 2)
        val a2 = Arrow(
            id = "a2",
            points = listOf(GridPoint(4, 1), GridPoint(4, 3)),
            direction = Direction.DOWN
        )
        val a1CanExit = PuzzlePhysics.canArrowExit(a1, listOf(a1, a2), 5, 5)
        assertFalse(a1CanExit)
        val blocker = PuzzlePhysics.findBlockingArrow(a1, listOf(a1, a2), 5, 5)
        assertEquals("a2", blocker?.id)
    }

    @Test
    fun testLevel1IsSolvable() {
        val level1 = LevelCatalog.getLevel(1)
        val solution = PuzzleSolver.solve(level1)
        assertNotNull(solution)
        assertEquals(level1.arrows.size, solution?.size)
    }

    @Test
    fun testLevel2IsSolvable() {
        val level2 = LevelCatalog.getLevel(2)
        val solution = PuzzleSolver.solve(level2)
        assertNotNull(solution)
        assertEquals(level2.arrows.size, solution?.size)
    }

    @Test
    fun testLevel3IsSolvable() {
        val level3 = LevelCatalog.getLevel(3)
        val solution = PuzzleSolver.solve(level3)
        assertNotNull(solution)
        assertEquals(level3.arrows.size, solution?.size)
    }

    @Test
    fun testProceduralLevelsAreSolvable() {
        val level10 = LevelCatalog.getLevel(10)
        assertTrue(PuzzleSolver.isSolvable(level10))

        val level50 = LevelCatalog.getLevel(50)
        assertTrue(PuzzleSolver.isSolvable(level50))

        val level150 = LevelCatalog.getLevel(150)
        assertTrue(PuzzleSolver.isSolvable(level150))

        val level200 = LevelCatalog.getLevel(200)
        assertTrue(PuzzleSolver.isSolvable(level200))
    }

    @Test
    fun testTotalLevelsCount() {
        assertEquals(200, LevelCatalog.TOTAL_LEVELS)
    }
}
