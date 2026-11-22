package com.example.myapplication

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.SweepGradient
import android.util.AttributeSet
import android.widget.ProgressBar
import androidx.core.content.ContextCompat

class CircularProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.progressBarStyle
) : ProgressBar(context, attrs, defStyleAttr) {

    private val strokeWidthPx = 32f

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = strokeWidthPx
        color = ContextCompat.getColor(context, R.color.progress_bg_track)
        strokeCap = Paint.Cap.ROUND
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = strokeWidthPx
        strokeCap = Paint.Cap.ROUND
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.indigo_600)
        textSize = 80f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    private val rectF = RectF()
    private var indeterminateAngle = 0f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val margin = strokeWidthPx / 2f + 16f
        val diameter = Math.min(w, h)
        val left = (w - diameter) / 2f + margin
        val top = (h - diameter) / 2f + margin
        val right = left + diameter - 2 * margin
        val bottom = top + diameter - 2 * margin
        rectF.set(left, top, right, bottom)

        val cyan = ContextCompat.getColor(context, R.color.progress_cyan)
        val purple = ContextCompat.getColor(context, R.color.progress_purple)
        val sweepGradient = SweepGradient(w / 2f, h / 2f, intArrayOf(cyan, purple, cyan), floatArrayOf(0f, 0.75f, 1f))
        val matrix = Matrix()
        matrix.setRotate(-90f, w / 2f, h / 2f)
        sweepGradient.setLocalMatrix(matrix)
        progressPaint.shader = sweepGradient
    }

    @Synchronized
    override fun onDraw(canvas: Canvas) {
        // Draw background circle
        canvas.drawOval(rectF, bgPaint)

        if (isIndeterminate) {
            // Draw spinning arc for indeterminate mode
            canvas.drawArc(rectF, indeterminateAngle, 100f, false, progressPaint)
            indeterminateAngle = (indeterminateAngle + 6f) % 360f
            postInvalidateDelayed(16)
        } else {
            // Draw progress arc
            val maxVal = if (max > 0) max else 100
            val sweepAngle = (progress.toFloat() / maxVal.toFloat()) * 360f
            if (sweepAngle > 0) {
                canvas.drawArc(rectF, -90f, sweepAngle, false, progressPaint)
            }

            // Draw percentage text in center
            val text = "$progress%"
            val textY = rectF.centerY() - ((textPaint.descent() + textPaint.ascent()) / 2)
            canvas.drawText(text, rectF.centerX(), textY, textPaint)
        }
    }
}