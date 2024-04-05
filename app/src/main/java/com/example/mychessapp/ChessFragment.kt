package com.example.mychessapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.mychessapp.databinding.FragmentChessBinding

class ChessFragment : Fragment() {
    private lateinit var viewBinding: FragmentChessBinding

    private val chessViewModel: ChessViewModel by viewModels()

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
        setUpResetButton()
        observeSelectedPosition()
        observeBoardSize()
        observeMoves()
        observePaths()
        observeStartOver()
    }

    private fun setUpChessView() {
        viewBinding.chessView.apply {
            chessViewEventHandler = chessViewModel
        }
    }

    private fun setUpEditTexts() {
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

    private fun setUpResetButton() {
        viewBinding.resetButton.setOnClickListener {
            viewBinding.chessView.apply {
                drawRow = null
                drawCol = null
                defaultChessboardSize = MIN_BOARD_SIZE
                positionsToHighlight = null
                invalidate()
            }
            chessViewModel.resetSelectedPosition()
            chessViewModel.setMoves(DEFAULT_NUMBER_OF_MOVES)
            chessViewModel.setChessBoardSize(MIN_BOARD_SIZE)
            viewBinding.chessBoardSizeContainer.editText.setText(DEFAULT_BOARD_SIZE)
            viewBinding.movesContainer.editText.setText(DEFAULT_MOVES)
        }
    }

    private fun observeSelectedPosition() {
        chessViewModel.selectedStartingPosition.observe(viewLifecycleOwner) { position ->
            if (position != null) {
                viewBinding.selectPositionInfoText.text = getString(R.string.select_ending_position)
                viewBinding.chessView.apply {
                    if (drawRow == null && drawCol == null) {
                        drawRow = position.row
                        drawCol = position.col
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
                defaultChessboardSize = size
                drawRow = null
                drawCol = null
                positionsToHighlight = null
                invalidate()
            }
            chessViewModel.resetSelectedPosition()
        }
    }

    private fun observeMoves() {
        chessViewModel.moves.observe(viewLifecycleOwner) {
            viewBinding.chessView.apply {
                drawRow = null
                drawCol = null
                positionsToHighlight = null
                invalidate()
            }
            chessViewModel.resetSelectedPosition()
        }
    }

    private fun observePaths() {
        chessViewModel.paths.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                viewBinding.chessView.apply {
                    positionsToHighlight = it
                    invalidate()
                }
            } else {
                Toast.makeText(
                    activity,
                    getString(R.string.no_paths),
                    Toast.LENGTH_SHORT
                ).show()
                viewBinding.chessView.apply {
                    drawRow = null
                    drawCol = null
                    invalidate()
                }
                chessViewModel.resetSelectedPosition()
            }
        }
    }

    private fun observeStartOver() {
        chessViewModel.startOver.observe(viewLifecycleOwner) {
            if (it) {
                viewBinding.chessView.apply {
                    positionsToHighlight = null
                    drawRow = null
                    drawCol = null
                    invalidate()
                }
                chessViewModel.resetSelectedPosition()
            }
        }
    }

    companion object {
        private const val DEFAULT_BOARD_SIZE = "6"
        const val MIN_BOARD_SIZE = 6
        private const val MAX_BOARD_SIZE = 16
        private const val DEFAULT_MOVES = "3"
        private const val MIN_NUMBER_OF_MOVES = 1
        private const val MAX_NUMBER_OF_MOVES = 6
        const val DEFAULT_NUMBER_OF_MOVES = 3
    }
}