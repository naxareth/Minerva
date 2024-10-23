package com.example.minerva_10.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class DownloadService : Service() {

    private lateinit var client: OkHttpClient

    override fun onCreate() {
        super.onCreate()
        client = OkHttpClient()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val url = intent?.getStringExtra("url")
        val filePath = intent?.getStringExtra("filePath")

        if (url != null && filePath != null) {
            downloadFile(url, filePath)
        }

        return START_NOT_STICKY
    }

    private fun downloadFile(url: String, filePath: String) {
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace() // Handle the error
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val inputStream: InputStream? = response.body?.byteStream()
                    val file = File(filePath)
                    val outputStream = FileOutputStream(file)

                    // Notify the user about the download progress
                    val totalBytes = response.body?.contentLength() ?: 0
                    var bytesRead: Long = 0
                    val buffer = ByteArray(4096)
                    var read: Int

                    inputStream?.use { input ->
                        outputStream.use { output ->
                            while (input.read(buffer).also { read = it } != -1) {
                                output.write(buffer, 0, read)
                                bytesRead += read
                                val progress = (bytesRead * 100 / totalBytes).toInt()
                                // Update the UI or notification with progress
                                // You can send a broadcast or use a notification to inform the user
                            }
                        }
                    }

                    // Notify the user about the download completion
                }
            }
        })
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}