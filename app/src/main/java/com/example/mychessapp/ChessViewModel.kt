package com.example.mychessapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mychessapp.ChessFragment.Companion.DEFAULT_NUMBER_OF_MOVES
import com.example.mychessapp.ChessFragment.Companion.MIN_BOARD_SIZE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChessViewModel : ViewModel(), ChessViewEventHandler {

    private val _selectedStartingPosition = MutableLiveData<Position?>()
    val selectedStartingPosition: LiveData<Position?> = _selectedStartingPosition

    private val _chessBoardSize = MutableLiveData(MIN_BOARD_SIZE)
    val chessBoardSize: LiveData<Int> = _chessBoardSize

    private val _moves = MutableLiveData(DEFAULT_NUMBER_OF_MOVES)
    val moves: LiveData<Int> = _moves

    private val _paths = MutableLiveData<List<List<Position>?>>()
    val paths: LiveData<List<List<Position>?>> = _paths

    private val _startOver = MutableLiveData<Boolean>()
    val startOver: LiveData<Boolean> = _startOver

    fun resetSelectedPosition() {
        _selectedStartingPosition.value = null
    }

    fun setChessBoardSize(size: Int) {
        _chessBoardSize.value = size
    }

    fun setMoves(moves: Int) {
        _moves.value = moves
    }

    override fun selectedPosition(row: Int, col: Int) {
        if (_selectedStartingPosition.value == null) {
            _selectedStartingPosition.value = Position(col, row)
        } else {
            calculateMoves(Position(col, row))
        }
    }

    override fun resetStartingPosition() {
        resetSelectedPosition()
        _startOver.value = true
    }

    private fun calculateMoves(endingPosition: Position) {
        val start = selectedStartingPosition.value ?: return

        viewModelScope.launch {
            val paths =
                findKnightPaths(start, endingPosition, moves.value ?: DEFAULT_NUMBER_OF_MOVES)
                _paths.value = paths
        }
    }

    private suspend fun findKnightPaths(
        start: Position,
        target: Position,
        moves: Int
    ): List<List<Position>> = withContext(Dispatchers.IO) {
        val paths = mutableListOf<MutableList<Position>>()

        val queue = ArrayDeque<List<Position>>()
        queue.addLast(listOf(start))

        var depth = 1

        while (queue.isNotEmpty() && depth <= moves + 1) {
            val levelSize = queue.size

            repeat(levelSize) {
                val currentPath = queue.removeFirst()
                val currentPos = currentPath.last()

                if (currentPos == target) {
                    // Found a path to the target
                    paths.add(currentPath.toMutableList())
                } else {
                    val nextMoves = currentPos.nextPositions()

                    for (move in nextMoves) {
                        if (move !in currentPath && chessBoardSize.value?.let { it1 ->
                                move.isValidMove(
                                    it1
                                )
                            } == true) {
                            val newPath = currentPath.toMutableList()
                            newPath.add(move)
                            queue.addLast(newPath)
                        }
                    }
                }
            }

            depth++
        }
        paths.forEach {
            it.removeAt(0)
            if (it.size < moves) it.clear()
        }

        return@withContext paths
    }
}