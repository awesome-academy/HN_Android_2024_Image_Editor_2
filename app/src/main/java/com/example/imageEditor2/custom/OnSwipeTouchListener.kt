package com.example.imageEditor.custom

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView

class OnSwipeTouchListener(private val imageView: ImageView) : View.OnTouchListener {
    private var mLastX = 0f
    private var mLastY = 0f

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(
        view: View?,
        event: MotionEvent?,
    ): Boolean {
        when (event?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                mLastX = event.x
                mLastY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaX = (event.x - mLastX) / 2
                val deltaY = (event.y - mLastY) / 2
                imageView.translationX += deltaX
                imageView.translationY += deltaY
                mLastX = event.x
                mLastY = event.y
            }
        }
        view?.performClick()
        return true
    }
}
