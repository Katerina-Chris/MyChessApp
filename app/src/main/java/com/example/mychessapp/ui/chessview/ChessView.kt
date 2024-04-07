package com.example.mychessapp.ui.chessview

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.mychessapp.R
import com.example.mychessapp.ui.ChessFragment.Companion.MIN_BOARD_SIZE
import com.example.mychessapp.ui.Position
import com.example.mychessapp.ui.Solution

// Custom view representing the chessboard
class ChessView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private val paint = Paint()

    private var cellSide = 0f
    private var canvasX = 0f
    private var canvasY = 0f

    // Event handler for chess board interactions
    var chessViewEventHandler: ChessViewEventHandler? = null
    var startingPosition: Position? = null
    var minBoardSize: Int = MIN_BOARD_SIZE

    // Containing paths to highlight
    var positionsToHighlight: Solution? = null

    override fun onDraw(canvas: Canvas) {
        this.layoutParams.height = width
        this.layoutParams.width = width
        this.requestLayout()

        val chessBoardSide = width * 0.95f
        cellSide = chessBoardSide / minBoardSize.toFloat()
        canvasX = (width - chessBoardSide) / 2f
        canvasY = (height - chessBoardSide) / 2f

        drawChessBoard(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Calculate the touched cell
        val col = ((event.x - canvasX) / cellSide).toInt()
        val row = ((event.y - canvasY) / cellSide).toInt()

        if (event.action == MotionEvent.ACTION_DOWN) {
            if (positionsToHighlight == null) {
                chessViewEventHandler?.selectedPosition(col, row)
            } else {
                chessViewEventHandler?.resetStartingPosition()
            }
        }
        return true
    }

    private fun drawChessBoard(canvas: Canvas) {
        paint.color = Color.BLACK
        // Draw the chessboard background
        canvas.drawRect(
            0f,
            (height - width) / 2f,
            width.toFloat(),
            (height - width) / 2f + width,
            paint
        )

        for (row in 0 until minBoardSize) {
            for (col in 0 until minBoardSize) {
                drawChessRect(canvas, row, col)
                // Draw the knight piece if the position is the starting position
                startingPosition?.let {
                    if (it.row == row && it.col == col) {
                        drawKnightPiece(canvas, it.row, it.col)
                    }
                }
            }
        }
    }

    // Draw each cell
    private fun drawChessRect(canvas: Canvas, row: Int, col: Int) {
        paint.color = if ((row + col) % 2 == 1) Color.DKGRAY else Color.LTGRAY

        val rect = RectF(
            canvasX + row * cellSide,
            canvasY + col * cellSide,
            canvasX + (row + 1) * cellSide,
            canvasY + (col + 1) * cellSide
        )
        canvas.drawRect(rect, paint)

        val paintText = Paint()
        paintText.color = Color.DKGRAY
        paintText.textSize = 36F
        paintText.textAlign = Paint.Align.CENTER

        // Highlight the cell
        positionsToHighlight?.let {
            it.paths.forEach { positions ->
                positions.let {
                    for (position in positions.moves.indices) {
                        if (positions.moves[position].col == col && positions.moves[position].row == row) {
                            paint.color = Color.RED
                            canvas.drawRect(
                                rect,
                                paint
                            )
                            val text = (position + 1).toString()
                            canvas.drawText(text, rect.centerX(), rect.centerY(), paintText)
                        }
                    }
                }
            }
        }
    }

    private fun drawKnightPiece(canvas: Canvas, col: Int, row: Int) {
        val piece = BitmapFactory.decodeResource(resources, R.drawable.white_horse)
        canvas.drawBitmap(
            piece, null, RectF(
                canvasX + col * cellSide,
                canvasY + row * cellSide,
                canvasX + (col + 1) * cellSide,
                canvasY + (row + 1) * cellSide
            ), paint
        )
    }
}