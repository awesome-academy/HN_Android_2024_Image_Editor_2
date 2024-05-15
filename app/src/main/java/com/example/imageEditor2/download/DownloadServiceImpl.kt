package com.example.imageEditor2.download

import android.app.DownloadManager
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import com.example.imageEditor2.utils.FILE_TITLE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

class DownloadServiceImpl(private val context: Context) : DownloadService {
    override suspend fun downloadImage(url: String) {
        val request = DownloadManager.Request(Uri.parse(url))
        request.setTitle(FILE_TITLE)
        val fileName = "image/${UUID.randomUUID()}.jpg"
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
        val downloadManager =
            context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }

    override suspend fun saveImage(bitmap: Bitmap) {
        val fileName = "image/${UUID.randomUUID()}.jpg"
        val directory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!directory.exists()) {
            directory.mkdirs() // Tạo thư mục nếu nó chưa tồn tại
        }
        try {
            val file = File(directory, fileName)
            val outputStream =
                withContext(Dispatchers.IO) {
                    FileOutputStream(file)
                }
            // Nén và ghi bitmap vào tệp
            withContext(Dispatchers.IO) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.flush()
                outputStream.close()
            }
        } catch (e: IOException) {
            throw Throwable(e)
        }
    }
}
