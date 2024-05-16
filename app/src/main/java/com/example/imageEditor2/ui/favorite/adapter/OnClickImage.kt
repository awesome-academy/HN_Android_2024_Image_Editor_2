package com.example.imageEditor2.ui.favorite.adapter

interface OnClickImage {
    fun likeImage(id: String)

    fun dislikeImage(id: String)

    fun clickDetailImage(url: String)
}
