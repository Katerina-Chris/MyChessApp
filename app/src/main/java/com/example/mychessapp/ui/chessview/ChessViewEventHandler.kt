package com.example.mychessapp.ui.chessview

interface ChessViewEventHandler {
    fun selectedPosition(row: Int, col: Int)
    fun resetStartingPosition()
}