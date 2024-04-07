package com.example.mychessapp

import com.example.mychessapp.helpers.implementation.FindSolutionHelper
import com.example.mychessapp.ui.Position
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Test

class FindSolutionHelperTest {

    @Test
    fun `verify isValid returns true for valid position within chessboard bounds`() {
        val findSolutionHelper = FindSolutionHelper()
        val row = 3
        val col = 2
        val size = 5

        assertEquals(true, findSolutionHelper.isValid(row, col, size))
    }

    @Test
    fun `verify isValid returns false for position outside chessboard bounds`() {
        val findSolutionHelper = FindSolutionHelper()
        val row = -1
        val col = 6
        val size = 5

        assertEquals(false, findSolutionHelper.isValid(row, col, size))
    }

    @Test
    fun `verify possiblePath returns correct positions`() {
        val findSolutionHelper = FindSolutionHelper()
        val col = 1
        val row = 1

        val expectedPositions = listOf(
            Position(2, 3),
            Position(0, 3),
            Position(3, 2),
            Position(3, 0)
        )

        val actualPath = findSolutionHelper.possiblePath(col, row)
        assertEquals(expectedPositions, actualPath.moves)
    }

    @Test
    fun `verify calculateSolution returns 1 for input that has 1 path`() {
        val findSolutionHelper = FindSolutionHelper()

        val startingPosition = Position(0, 0)
        val endingPosition = Position(1, 2)
        val userInputMoves = 1
        val chessBoardSize = 8

        val solution = runBlocking {
            findSolutionHelper.calculateSolution(
                startingPosition,
                endingPosition,
                userInputMoves,
                chessBoardSize
            )
        }

        assertEquals(1, solution.paths.size)
    }

    @Test
    fun `verify calculateSolution returns 0 for input with no paths`() = runBlocking {
        val findSolutionHelper = FindSolutionHelper()

        val startingPosition = Position(0, 0)
        val endingPosition = Position(1, 0)

        val userInputMoves = 1
        val chessBoardSize = 6

        val solution = findSolutionHelper.calculateSolution(
            startingPosition,
            endingPosition,
            userInputMoves,
            chessBoardSize
        )

        assertEquals(0, solution.paths.size)
    }
}