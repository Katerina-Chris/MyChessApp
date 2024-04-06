package com.example.mychessapp

interface FindSolution {
    suspend fun calculateSolution(
        startingPosition: Position,
        endingPosition: Position,
        userInputMoves: Int,
        chessBoardSize: Int
    ): Solution
}