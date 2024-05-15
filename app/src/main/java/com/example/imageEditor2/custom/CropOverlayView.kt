package com.example.imageEditor.custom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class CropOverlayView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private var mCropRect: RectF? = null
    private val mPaint =
        Paint().apply {
            color = Color.TRANSPARENT
            style = Paint.Style.STROKE
            strokeWidth = 5f
            isAntiAlias = true
        }
    private var mCropSuccessCallback: CropSuccessCallback? = null
    private lateinit var mStartPoint: PointF
    private lateinit var mEndPoint: PointF

    fun setCropSuccessCallback(callback: CropSuccessCallback) {
        mCropSuccessCallback = callback
        mPaint.color = Color.WHITE
    }

    fun removeCropCallback() {
        mCropSuccessCallback = null
        mPaint.color = Color.TRANSPARENT
        mCropRect = RectF(0f, 0f, 0f, 0f)
        invalidate()
    }

    fun hasInstanceListener() = mCropSuccessCallback != null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.apply {
            mCropRect?.let { drawRect(it, mPaint) }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                mStartPoint = PointF(event.x, event.y)
                mEndPoint = PointF(event.x, event.y)
                mCropRect = RectF(mStartPoint.x, mStartPoint.y, mEndPoint.x, mEndPoint.y)
                invalidate()
            }

            MotionEvent.ACTION_MOVE -> {
                mEndPoint.set(event.x, event.y)
                mCropRect?.set(
                    mStartPoint.x,
                    mStartPoint.y,
                    mEndPoint.x,
                    mEndPoint.y,
                )
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                mEndPoint.set(event.x, event.y)
                mCropRect?.set(
                    mStartPoint.x,
                    mStartPoint.y,
                    mEndPoint.x,
                    mEndPoint.y,
                )
                invalidate()
                mCropRect?.let {
                    if (it.width() > 0 && it.height() > 0) {
                        mCropSuccessCallback?.onCropSuccess(
                            mStartPoint,
                            mEndPoint,
                        )
                    }
                }
            }
        }
        return true
    }
}
