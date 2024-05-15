package com.example.imageEditor2.ui.home.adapter

interface OnClickImage {
    fun clickImage(url: String)

    fun doubleTapForLikeImage(id: String)

    fun clickLike(index: Int)
}
