package com.example.imageEditor2.custom

import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.ImageView
import kotlin.math.abs

class ImageListener :
    GestureDetector.SimpleOnGestureListener() {
    private var mImageView: ImageView? = null
    private var isZoomIn = false
        set(value) {
            field = value
            mImageView?.let {
                if (isZoomIn) {
                    it.animate().scaleX(2.0f).scaleY(2.0f).setDuration(300).start()
                } else {
                    it.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).start()
                    it.translationX = 0f
                    it.translationY = 0f
                }
            }
        }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float,
    ): Boolean {
        if (isZoomIn) {
            mImageView?.let {
                if (abs(it.translationX - distanceX) <= it.width / 2) it.translationX -= distanceX
                if (abs(it.translationY - distanceY) <= it.height / 2) it.translationY -= distanceY
            }
        }
        return true
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        isZoomIn = !isZoomIn
        return true
    }

    fun addListenerOnImageView(imageView: ImageView) {
        this.mImageView = imageView
    }

    fun removeListener() {
        mImageView = null
    }

    fun hasListener() = mImageView != null
}
