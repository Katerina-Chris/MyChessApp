package com.example.mychessapp

class Position(val col: Int, val row: Int) {

    fun nextPositions(): List<Position> {
        val nextPositions = mutableListOf<Position>()
        for (direction in Direction.entries) {
            when (direction) {
                Direction.UP -> {
                    nextPositions.add(Position(col + 1, row + 2))
                    nextPositions.add(Position(col - 1, row + 2))
                }

                Direction.DOWN -> {
                    nextPositions.add(Position(col + 1, row - 2))
                    nextPositions.add(Position(col - 1, row - 2))
                }

                Direction.RIGHT -> {
                    nextPositions.add(Position(col + 2, row + 1))
                    nextPositions.add(Position(col + 2, row - 1))
                }

                Direction.LEFT -> {
                    nextPositions.add(Position(col - 2, row + 1))
                    nextPositions.add(Position(col - 2, row - 1))
                }
            }
        }
        return nextPositions.filter {
            it.isValidMove(20)
        }.toList()
    }

    // Checks if the position is a valid move on the chessboard
    fun isValidMove(chessBoardDimension: Int): Boolean {
        val validRow = row >= 0 && row <= (chessBoardDimension - 1)
        val validCol = col >= 0 && col <= (chessBoardDimension - 1)
        return validCol && validRow
    }

    // Overrides the equals() function to compare two Position objects
    override fun equals(other: Any?): Boolean {
        val equalRows = row == (other as Position).row
        val equalCols = col == (other as Position).col
        return equalCols && equalRows
    }

    override fun hashCode(): Int {
        return col + row
    }
}

// Enum class for different directions of the knight's movement
enum class Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT;
}