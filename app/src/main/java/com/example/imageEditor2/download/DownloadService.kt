package com.example.imageEditor2.download

import android.graphics.Bitmap

interface DownloadService {
    suspend fun downloadImage(url: String)

    suspend fun saveImage(bitmap: Bitmap)
}
