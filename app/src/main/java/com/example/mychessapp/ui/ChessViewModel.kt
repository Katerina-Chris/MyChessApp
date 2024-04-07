package com.example.mychessapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mychessapp.ui.ChessFragment.Companion.DEFAULT_NUMBER_OF_MOVES
import com.example.mychessapp.ui.ChessFragment.Companion.MIN_BOARD_SIZE
import com.example.mychessapp.helpers.implementation.FindSolutionHelper
import com.example.mychessapp.helpers.implementation.PreferencesHelper
import com.example.mychessapp.ui.chessview.ChessViewEventHandler
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChessViewModel @Inject constructor(
    private val preferencesHelper: PreferencesHelper,
    private val findSolutionHelper: FindSolutionHelper
) : ViewModel(),
    ChessViewEventHandler {

    private val _selectedPosition = MutableLiveData<Position?>()
    val selectedPosition: LiveData<Position?> = _selectedPosition

    private val _chessBoardSize = MutableLiveData(MIN_BOARD_SIZE)
    val chessBoardSize: LiveData<Int> = _chessBoardSize

    private val _moves = MutableLiveData(DEFAULT_NUMBER_OF_MOVES)
    val moves: LiveData<Int> = _moves

    private val _solution = MutableLiveData<Solution>()
    val solution: LiveData<Solution> = _solution

    private val _startOver = MutableLiveData<Boolean>()
    val startOver: LiveData<Boolean> = _startOver

    private val _loadingState = MutableLiveData<State>()
    val loadingState: LiveData<State> = _loadingState

    // position retrieved from SharedPreferences
    var position: Position? = null

    // Handles user's selected position from chessView
    override fun selectedPosition(row: Int, col: Int) {
        if (_selectedPosition.value == null) {
            _selectedPosition.value = Position(col, row)
            preferencesHelper.saveString(
                SELECTED_STARTING_POSITION_KEY,
                Gson().toJson(Position(col, row))
            )
        } else {
            calculateMoves(Position(col, row))
        }
    }

    // When user taps on ChessView after a solution was found,
    // resetting the starting position and the highlighted positions
    override fun resetStartingPosition() {
        setStartOver()
    }

    init {
        // Retrieve preferences
        val savedChessBoardSize = preferencesHelper.loadInt(SELECTED_SIZE_KEY, MIN_BOARD_SIZE)
        val savedMoves = preferencesHelper.loadInt(SELECTED_MOVES_KEY, DEFAULT_NUMBER_OF_MOVES)
        val paths = preferencesHelper.loadString(SELECTED_PATHS_KEY, "")
        val startingPosition = preferencesHelper.loadString(SELECTED_STARTING_POSITION_KEY, "")

        // Update LiveData with retrieved preferences
        if (savedChessBoardSize != _chessBoardSize.value || savedMoves != _moves.value) {
            _chessBoardSize.value = savedChessBoardSize
            _moves.value = savedMoves
        }

        if (paths != "") {
            val selectedSolution = Gson().fromJson(paths, Solution::class.java)
            _solution.value = selectedSolution
        }
        if (startingPosition != "") {
            position = Gson().fromJson(startingPosition, Position::class.java)
        }
    }

    fun setChessBoardSize(size: Int) {
        _chessBoardSize.value = size
        preferencesHelper.saveInt(SELECTED_SIZE_KEY, size)
        setStartOver()
    }

    fun setMoves(moves: Int) {
        _moves.value = moves
        preferencesHelper.saveInt(SELECTED_MOVES_KEY, moves)
        setStartOver()
    }

    fun resetSelectedPosition() {
        _selectedPosition.value = null
    }

    fun resetAllPreferences() {
        preferencesHelper.removePreference(SELECTED_SIZE_KEY)
        preferencesHelper.removePreference(SELECTED_MOVES_KEY)
        resetPositionAndPathsPreferences()
    }

    fun resetPositionAndPathsPreferences() {
        preferencesHelper.removePreference(SELECTED_STARTING_POSITION_KEY)
        preferencesHelper.removePreference(SELECTED_PATHS_KEY)
    }

    fun setStartOver() {
        _startOver.value = true
    }

    // Calculate the solution
    private fun calculateMoves(endingPosition: Position) {
        val start = selectedPosition.value ?: return
        _loadingState.value = State.LoadingState
        viewModelScope.launch {
            val paths =
                findSolutionHelper.calculateSolution(
                    start,
                    endingPosition,
                    moves.value ?: DEFAULT_NUMBER_OF_MOVES,
                    _chessBoardSize.value ?: MIN_BOARD_SIZE
                )
            _loadingState.value = State.Content
            _solution.value = paths
            if (paths.paths.isNotEmpty()) {
                preferencesHelper.saveString(SELECTED_PATHS_KEY, Gson().toJson(paths))
            }
        }
    }

    sealed class State {
        data object LoadingState : State()
        data object Content : State()
    }

    companion object {
        // Keys for storing preferences
        private const val SELECTED_SIZE_KEY = "SELECTED_SIZE_KEY"
        private const val SELECTED_MOVES_KEY = "SELECTED_MOVES_KEY"
        private const val SELECTED_PATHS_KEY = "SELECTED_PATHS_KEY"
        private const val SELECTED_STARTING_POSITION_KEY = "SELECTED_STARTING_POSITION_KEY"
    }
}