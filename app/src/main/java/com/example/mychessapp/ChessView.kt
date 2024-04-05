package com.example.mychessapp

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class ChessView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private val paint = Paint()

    var chessViewEventHandler: ChessViewEventHandler? = null
    var drawCol: Int? = null
    var drawRow: Int? = null

    private var cellSide = 0f
    private var dimensionX = 0f
    private var dimensionY = 0f
    var defaultChessboardSize: Int = 6
    var positionsToHighlight: List<List<Position>?>? = null

    override fun onDraw(canvas: Canvas) {
        this.layoutParams.height = width
        this.layoutParams.width = width
        this.requestLayout()
        val chessBoardSide = width * 0.95f
        cellSide = chessBoardSide / defaultChessboardSize.toFloat()
        dimensionX = (width - chessBoardSide) / 2f
        dimensionY = (height - chessBoardSide) / 2f

        drawChessBoard(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val col = ((event.x - dimensionX) / cellSide).toInt()
        val row = ((event.y - dimensionY) / cellSide).toInt()
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
        canvas.drawRect(
            0f,
            (height - width) / 2f,
            width.toFloat(),
            (height - width) / 2f + width,
            paint
        )
        for (row in 0 until defaultChessboardSize) {
            for (col in 0 until defaultChessboardSize) {

                drawChessRect(canvas, row, col)

                drawCol?.let { drawCol ->
                    drawRow?.let { drawRow ->
                        if (drawRow == row && drawCol == col) {
                            drawHorse(canvas, drawRow, drawCol)
                        }
                    }
                }

            }
        }
    }

    private fun drawChessRect(canvas: Canvas, row: Int, col: Int) {
        paint.color = if ((row + col) % 2 == 1) Color.DKGRAY else Color.LTGRAY

        val rect = RectF(
            dimensionX + row * cellSide,
            dimensionY + col * cellSide,
            dimensionX + (row + 1) * cellSide,
            dimensionY + (col + 1) * cellSide
        )
        canvas.drawRect(rect, paint)

        val paintText = Paint()
        paintText.color = Color.DKGRAY
        paintText.textSize = 50F
        paintText.textAlign = Paint.Align.CENTER

        // check if rect need to be highlighted
        positionsToHighlight?.let {
            it.forEach { positions ->
                positions?.let {
                    for (position in positions.indices) {
                        if (positions[position].col == col && positions[position].row == row) {
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

    private fun drawHorse(canvas: Canvas, col: Int, row: Int) {
        val piece = BitmapFactory.decodeResource(resources, R.drawable.white_horse)
        canvas.drawBitmap(
            piece, null, RectF(
                dimensionX + col * cellSide,
                dimensionY + row * cellSide,
                dimensionX + (col + 1) * cellSide,
                dimensionY + (row + 1) * cellSide
            ), paint
        )
    }
}