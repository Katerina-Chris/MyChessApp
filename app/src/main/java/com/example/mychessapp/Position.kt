package com.example.mychessapp

import java.io.Serializable

class Position(val col: Int, val row: Int) : Serializable {
    override fun equals(other: Any?): Boolean {
        val equalRows = row == (other as Position).row
        val equalCols = col == other.col
        return equalCols && equalRows
    }

    override fun hashCode(): Int {
        return col + row
    }
}

data class Solution(
    val paths: List<Path>
) : Serializable

data class Path(
    val moves: List<Position>
) : Serializable

enum class Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT;
}