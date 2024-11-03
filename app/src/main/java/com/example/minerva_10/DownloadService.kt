package com.example.minerva_10.services

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.minerva_10.R
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class DownloadService : Service() {

    private lateinit var client: OkHttpClient
    private val notificationId = 1 // Unique ID for the notification
    private lateinit var notificationManager: NotificationManagerCompat

    override fun onCreate() {
        super.onCreate()
        client = OkHttpClient()
        notificationManager = NotificationManagerCompat.from(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val url = intent?.getStringExtra("url")
        val filePath = intent?.getStringExtra("filePath")

        // Start foreground service
        val notification = NotificationCompat.Builder(this, "anime_download_channel")
            .setContentTitle("Downloading Anime")
            .setContentText("Download in progress...")
            .setSmallIcon(R.drawable.download)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .build()

        startForeground(notificationId, notification)

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
                // Optionally, notify the user about the failure
                updateNotification("Download failed", false)
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
                                updateNotification("Downloading Anime", true, progress)
                            }
                        }
                    }

                    // Notify the user about the download completion
                    updateNotification("Download complete", false)
                }
            }
        })
    }

    private fun updateNotification(contentText: String, ongoing: Boolean, progress: Int = 0) {
        val notification = NotificationCompat.Builder(this, "anime_download_channel")
            .setContentTitle("Downloading Anime")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.download)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(ongoing)
            .setProgress(100, progress, false) // Update progress
            .build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notificationManager.notify(notificationId, notification)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}