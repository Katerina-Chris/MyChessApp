package com.example.mychessapp

interface ChessViewEventHandler {

    fun selectedPosition(row: Int, col: Int)
    fun resetStartingPosition()
}