package com.example.mychessapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.mychessapp.R
import com.example.mychessapp.databinding.FragmentChessBinding
import com.example.mychessapp.utilities.DialogUtils
import com.example.mychessapp.utilities.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChessFragment : Fragment() {
    private lateinit var viewBinding: FragmentChessBinding

    private val chessViewModel: ChessViewModel by viewModels()
    private val dialogUtils by lazy { DialogUtils() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentChessBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpChessView()
        setUpEditTexts()
        setUpResetAllButton()
        observeSelectedPosition()
        observeBoardSize()
        observeMoves()
        observePaths()
        observeStartOver()
        observeLoadingState()
    }

    private fun observeLoadingState() {
        chessViewModel.loadingState.observe(viewLifecycleOwner) {
            when (it) {
                ChessViewModel.State.LoadingState -> dialogUtils.showLoading(requireContext())
                ChessViewModel.State.Content -> dialogUtils.hideLoading()
            }
        }
    }

    private fun setUpChessView() {
        viewBinding.chessView.apply {
            chessViewEventHandler = chessViewModel
        }
    }

    private fun setUpEditTexts() {
        // Set up editText for board size
        viewBinding.chessBoardSizeContainer.apply {
            labelText.text = getString(R.string.desired_chess_board_size)
            editText.hint = getString(R.string.enter_chess_board_size_hint)
            editText.setText(DEFAULT_BOARD_SIZE)
            editText.setOnEditorActionListener { _, actionId, _ ->
                hideKeyboard()

                val stringInput = viewBinding.chessBoardSizeContainer.editText.text.toString()
                if (stringInput.isNotEmpty()) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        if (stringInput.toInt() in MIN_BOARD_SIZE..MAX_BOARD_SIZE) {
                            chessViewModel.setChessBoardSize(stringInput.toInt())
                        } else {
                            Toast.makeText(
                                activity,
                                getString(R.string.enter_chess_board_size_hint),
                                Toast.LENGTH_LONG
                            ).show()
                            viewBinding.chessBoardSizeContainer.editText.text = null
                        }
                    }
                }
                true
            }
        }

        // Set up editText for number of moves
        viewBinding.movesContainer.apply {
            labelText.text = getString(R.string.desired_number_of_moves)
            editText.hint = getString(R.string.enter_number_of_moves_hint)
            editText.setText(DEFAULT_MOVES)
            editText.setOnEditorActionListener { _, actionId, _ ->
                hideKeyboard()
                val stringInput = viewBinding.movesContainer.editText.text.toString()
                if (stringInput.isNotEmpty()) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        if (stringInput.toInt() in MIN_NUMBER_OF_MOVES..MAX_NUMBER_OF_MOVES) {
                            chessViewModel.setMoves(stringInput.toInt())
                        } else {
                            Toast.makeText(
                                activity,
                                getString(R.string.enter_number_of_moves_hint),
                                Toast.LENGTH_LONG
                            ).show()
                            viewBinding.movesContainer.editText.text = null
                        }
                    }
                }
                true
            }
        }
    }

    // Reset chessView and preferences
    private fun setUpResetAllButton() {
        viewBinding.resetButton.setOnClickListener {
            viewBinding.chessView.apply {
                startingPosition = null
                minBoardSize = MIN_BOARD_SIZE
                positionsToHighlight = null
                invalidate()
            }
            chessViewModel.resetSelectedPosition()
            chessViewModel.setMoves(DEFAULT_NUMBER_OF_MOVES)
            chessViewModel.setChessBoardSize(MIN_BOARD_SIZE)
            chessViewModel.resetAllPreferences()
            viewBinding.chessBoardSizeContainer.editText.setText(DEFAULT_BOARD_SIZE)
            viewBinding.movesContainer.editText.setText(DEFAULT_MOVES)
        }
    }

    private fun observeSelectedPosition() {
        chessViewModel.selectedPosition.observe(viewLifecycleOwner) { position ->
            if (position != null) {
                viewBinding.selectPositionInfoText.text = getString(R.string.select_ending_position)
                viewBinding.chessView.apply {
                    if (startingPosition == null) {
                        startingPosition = position
                        invalidate()
                    }
                }
            } else {
                viewBinding.selectPositionInfoText.text =
                    getString(R.string.select_starting_position)
            }
        }
    }

    private fun observeBoardSize() {
        chessViewModel.chessBoardSize.observe(viewLifecycleOwner) { size ->
            viewBinding.chessView.apply {
                minBoardSize = size
                startingPosition = null
                positionsToHighlight = null
                invalidate()
            }
            viewBinding.chessBoardSizeContainer.editText.setText(size.toString())
            chessViewModel.resetSelectedPosition()
        }
    }

    private fun observeMoves() {
        chessViewModel.moves.observe(viewLifecycleOwner) {
            viewBinding.chessView.apply {
                startingPosition = null
                positionsToHighlight = null
                invalidate()
            }
            viewBinding.movesContainer.editText.setText(it.toString())
            chessViewModel.resetSelectedPosition()
        }
    }

    private fun observePaths() {
        chessViewModel.solution.observe(viewLifecycleOwner) { paths ->
            if (paths.paths.isNotEmpty()) {
                viewBinding.chessView.apply {
                    positionsToHighlight = paths
                    startingPosition =
                        if (chessViewModel.selectedPosition.value == null) chessViewModel.position
                        else chessViewModel.selectedPosition.value
                    invalidate()
                }
            } else {
                // Show toast and start over
                Toast.makeText(
                    activity,
                    getString(R.string.no_paths),
                    Toast.LENGTH_SHORT
                ).show()
                viewBinding.chessView.apply {
                    startingPosition = null

                    invalidate()
                }
                chessViewModel.setStartOver()
            }
        }
    }

    private fun observeStartOver() {
        chessViewModel.startOver.observe(viewLifecycleOwner) {
            if (it) {
                viewBinding.chessView.apply {
                    positionsToHighlight = null
                    startingPosition = null
                    invalidate()
                }
                chessViewModel.resetSelectedPosition()
                chessViewModel.resetPositionAndPathsPreferences()
            }
        }
    }

    companion object {
        private const val DEFAULT_BOARD_SIZE = "6"
        private const val DEFAULT_MOVES = "3"
        private const val MIN_NUMBER_OF_MOVES = 1

        // Due to complex calculations of possible paths, especially for max board size(16)
        private const val MAX_NUMBER_OF_MOVES = 7

        private const val MAX_BOARD_SIZE = 16
        const val MIN_BOARD_SIZE = 6
        const val DEFAULT_NUMBER_OF_MOVES = 3
    }
}