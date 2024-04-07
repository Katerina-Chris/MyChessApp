package com.example.mychessapp.helpers

import com.example.mychessapp.ui.Position
import com.example.mychessapp.ui.Solution

interface FindSolution {
    suspend fun calculateSolution(
        startingPosition: Position,
        endingPosition: Position,
        userInputMoves: Int,
        chessBoardSize: Int
    ): Solution
}