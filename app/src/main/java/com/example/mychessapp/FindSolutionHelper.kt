package com.example.mychessapp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FindSolutionHelper @Inject constructor() : FindSolution {
    override suspend fun calculateSolution(
        startingPosition: Position,
        endingPosition: Position,
        userInputMoves: Int,
        chessBoardSize: Int
    ): Solution = withContext(Dispatchers.IO) {
        var paths = mutableListOf<MutableList<Position>>()

        val queue = ArrayDeque<List<Position>>()
        queue.addLast(listOf(startingPosition))

        var currentMoves = 1

        while (queue.isNotEmpty() && currentMoves <= userInputMoves + 1) {
            val queueSize = queue.size

            repeat(queueSize) {
                val currentPath = queue.removeFirst()
                val currentPosition = currentPath.last()

                if (currentPosition == endingPosition) {
                    paths.add(currentPath.toMutableList())
                } else {
                    val path = possiblePath(currentPosition.col, currentPosition.row)

                    for (move in path.moves) {
                        if (move !in currentPath && isValid(move.row, move.col, chessBoardSize)) {
                            val newPath = currentPath.toMutableList()
                            newPath.add(move)
                            queue.addLast(newPath)
                        }
                    }
                }
            }

            currentMoves++
        }

        paths.forEach {
            it.removeAt(0)
            if (it.size < userInputMoves) it.clear()
        }
        paths = paths.filter { it.isNotEmpty() }.toMutableList()

        val pathList = paths.map { Path(it) }

        return@withContext Solution(pathList)
    }

    fun possiblePath(col: Int, row: Int): Path {
        val positions = mutableListOf<Position>()
        for (direction in Direction.entries) {
            when (direction) {
                Direction.UP -> {
                    positions.add(Position(col + 1, row + 2))
                    positions.add(Position(col - 1, row + 2))
                }

                Direction.DOWN -> {
                    positions.add(Position(col + 1, row - 2))
                    positions.add(Position(col - 1, row - 2))
                }

                Direction.RIGHT -> {
                    positions.add(Position(col + 2, row + 1))
                    positions.add(Position(col + 2, row - 1))
                }

                Direction.LEFT -> {
                    positions.add(Position(col - 2, row + 1))
                    positions.add(Position(col - 2, row - 1))
                }
            }
        }

        return Path(positions.filter {
            isValid(it.row, it.col, 20)
        }.toList())
    }

    fun isValid(row: Int, col: Int, size: Int): Boolean {
        val validRow = row >= 0 && row <= (size - 1)
        val validCol = col >= 0 && col <= (size - 1)
        return validCol && validRow
    }
}