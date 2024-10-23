package com.example.minerva_10.views

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.lifecycle.lifecycleScope
import com.example.minerva_10.R
import com.example.minerva_10.api.RetrofitClient
import com.example.minerva_10.api.responses.EpisodeInfo
import com.example.minerva_10.api.interfaces.AnimeApiService
import com.example.minerva_10.api.responses.AnimeInfo
import com.example.minerva_10.api.responses.DownloadItem
import com.example.minerva_10.api.responses.StreamingResponse
import com.example.minerva_10.viewmodels.SharedAnimeViewModel
import com.example.minerva_10.viewmodels.VideoPlayerViewModel
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.HttpException
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit

class VideoPlayerActivity : AppCompatActivity() {

    private lateinit var playerView: PlayerView
    private lateinit var animeTitleTextView: TextView
    private lateinit var qualitySpinner: Spinner
    private lateinit var episodeInfo: EpisodeInfo
    private lateinit var apiService: AnimeApiService
    private val viewModel: VideoPlayerViewModel by viewModels()
    private val sharedViewModel: SharedAnimeViewModel by viewModels()
    private var availableQualities: List<String> = emptyList()
    private lateinit var downloadButton: Button
    private lateinit var animeInfo: AnimeInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        checkNotificationPermission() // Check for notification permissions
        createNotificationChannel() // Create notification channel

        playerView = findViewById(R.id.player_view)
        animeTitleTextView = findViewById(R.id.anime_title)
        qualitySpinner = findViewById(R.id.quality_spinner)
        downloadButton = findViewById(R.id.download_button)

        animeInfo = intent.getParcelableExtra("ANIME_INFO") ?: return
        Log.d("VideoPlayerActivity", "Retrieved AnimeInfo: $animeInfo")
        updateAnimeTitle()

        episodeInfo = intent.getParcelableExtra("EPISODE_INFO") ?: return
        apiService = RetrofitClient.animeApiService

        if (viewModel.player == null) {
            viewModel.player = SimpleExoPlayer.Builder(this).build()
            playerView.player = viewModel.player
        } else {
            playerView.player = viewModel.player
            viewModel.player?.seekTo(viewModel.playbackPosition)
        }

        playerView.setControllerVisibilityListener { visibility ->
            qualitySpinner.visibility = if (visibility == View.VISIBLE) View.VISIBLE else View.GONE
            downloadButton.visibility = if (visibility == View.VISIBLE) View.VISIBLE else View.GONE
            animeTitleTextView.visibility = if (visibility == View.VISIBLE) View.VISIBLE else View.GONE
        }

        fetchStreamingLinks(episodeInfo.id)

        downloadButton.setOnClickListener {
            val selectedQuality = availableQualities[qualitySpinner.selectedItemPosition]
            val filePath = "${externalCacheDir?.absolutePath}/${episodeInfo.number}.mp4"

            val downloadItem = DownloadItem(
                animeId = animeInfo.id,
                animeTitle = animeInfo.title,
                episodeNumber = episodeInfo.number,
                coverImageUrl = animeInfo.image,
                progress = 0
            )

            Log.d("VideoPlayerActivity", "Creating DownloadItem: $downloadItem")
            sharedViewModel.addDownloadItem(downloadItem)

            Log.d("VideoPlayerActivity", "Download button clicked, adding download item to Shared ViewModel: $downloadItem")
            fetchM3U8AndDownload(episodeInfo.id, selectedQuality, filePath)
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android .Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "anime_download_channel"
            val channelName = "Anime Download"
            val channelDescription = "Notifications for anime downloads"
            val importance = NotificationManager.IMPORTANCE_LOW

            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
            Log.d("VideoPlayerActivity", "Notification channel created: $channelId")
        }
    }

    private fun truncateTitle(title: String): String {
        val maxLength = if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 20 else 40
        return if (title.length > maxLength) {
            title.substring(0, maxLength) + "..."
        } else {
            title
        }
    }

    private fun updateAnimeTitle() {
        animeTitleTextView.text = truncateTitle(animeInfo.title)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        updateAnimeTitle()
    }

    private fun fetchStreamingLinks(episodeId: String) {
        lifecycleScope.launch {
            try {
                val serverName = "gogocdn"
                Log.d("VideoPlayerActivity", "Fetching streaming links for Episode ID: $episodeId, Server: $serverName")
                val streamingResponse: StreamingResponse = apiService.getStreamingLinks(episodeId, serverName)
                Log.d("VideoPlayerActivity", "Streaming Response: $streamingResponse ")

                availableQualities = streamingResponse.sources.map { it.quality }.distinct()
                setupQualitySpinner(availableQualities)

                val defaultQuality = availableQualities.firstOrNull() ?: "360p"
                fetchAndPlayVideo(episodeId, defaultQuality)
            } catch (e: HttpException) {
                Log.e(" VideoPlayerActivity ", "Error fetching streaming links: ${e.response()?.errorBody()?.string()}")
            } catch (e: Exception) {
                Log.e("VideoPlayerActivity", "Error fetching streaming links: $e")
            }
        }
    }

    private fun setupQualitySpinner(qualities: List<String>) {
        val adapter = ArrayAdapter(this, R.layout.spinner_item, qualities)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        qualitySpinner.adapter = adapter

        qualitySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedQuality = qualities[position]
                viewModel.player?.release()
                viewModel.player = null

                fetchAndPlayVideo(episodeInfo.id, selectedQuality)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun fetchAndPlayVideo(episodeId: String, quality: String) {
        lifecycleScope.launch {
            try {
                val serverName = "gogocdn"
                Log.d("VideoPlayerActivity", "Fetching streaming links for Episode ID: $episodeId, Server: $serverName, Quality: $quality")
                val streamingResponse: StreamingResponse = apiService.getStreamingLinks(episodeId, serverName)

                val selectedSource = streamingResponse.sources.find { it.quality == quality }
                if (selectedSource != null) {
                    val videoUrl = selectedSource.url
                    Log.d("VideoPlayerActivity", "Playing video with URL: $videoUrl")

                    viewModel.player = SimpleExoPlayer.Builder(this@VideoPlayerActivity).build()
                    playerView.player = viewModel.player

                    val mediaItem = MediaItem.fromUri(videoUrl)
                    viewModel.player?.setMediaItem(mediaItem)
                    viewModel.player?.prepare()
                    viewModel.player?.seekTo(viewModel.playbackPosition)
                } else {
                    Log.e("VideoPlayerActivity", "No source found for quality: $quality")
                }
            } catch (e: HttpException) {
                Log.e("VideoPlayerActivity", "Error fetching streaming links: ${e.response()?.errorBody()?.string()}")
            } catch (e: Exception) {
                Log.e("VideoPlayerActivity", "Error fetching streaming links: $e")
            }
        }
    }

    private fun fetchM3U8AndDownload(episodeId: String, quality: String, filePath: String) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val serverName = "gogocdn"
                    val streamingResponse: StreamingResponse = apiService.getStreamingLinks(episodeId, serverName)

                    val selectedSource = streamingResponse.sources.find { it.quality == quality }
                    if (selectedSource != null) {
                        val m3u8Url = selectedSource.url

                        // Create and display a notification for the download
                        val notificationId = 1
                        val notificationManager = getSystemService(NotificationManager::class.java)
                        val builder = NotificationCompat.Builder(this@VideoPlayerActivity, "anime_download_channel")
                            .setSmallIcon(R.drawable.download) // Replace with your download icon
                            .setContentTitle("Downloading Anime")
                            .setContentText("Download started...")
                            .setPriority(NotificationCompat.PRIORITY_LOW)
                            .setOngoing(true) // Makes the notification ongoing
                        notificationManager.notify(notificationId, builder.build())

                        val client = OkHttpClient()
                        val request = Request.Builder().url(m3u8Url).build()

                        client.newCall(request).execute().use { response ->
                            if (!response.isSuccessful) {
                                Log.e("VideoPlayerActivity", "Error fetching m3u8 file: ${response.code}")
                                return@withContext
                            }
                            val m3u8Content = response.body?.string() ?: return@withContext

                            val baseUrl = m3u8Url.substringBeforeLast("/")
                            val segmentUrls = parseM3U8(m3u8Content, baseUrl)

                            downloadSegments(segmentUrls, filePath, notificationManager, notificationId)
                        }
                    } else {
                        Log.e("VideoPlayerActivity", "No source found for quality: $quality")
                    }
                } catch (e: HttpException) {
                    Log.e("VideoPlayerActivity", "Error fetching streaming links: ${e.response()?.errorBody()?.string()}")
                } catch (e: Exception) {
                    Log.e("VideoPlayerActivity", "Error fetching streaming links: $e")
                }
            }
        }
    }

    private fun parseM3U8(m3u8Content: String, baseUrl: String): List<String> {
        val segmentUrls = mutableListOf<String>()
        val lines = m3u8Content.split("\n")
        for (line in lines) {
            if (line.endsWith(".ts")) {
                val fullUrl = if (line.startsWith("http://") || line.startsWith("https://")) {
                    line.trim()
                } else {
                    "$baseUrl/$line".trim() // Prepend base URL
                }
                segmentUrls.add(fullUrl)
                Log.d("VideoPlayerActivity", "Parsed segment URL: $fullUrl")
            }
        }
        return segmentUrls
    }

    private suspend fun downloadSegments(segmentUrls: List<String>, filePath: String, notificationManager: NotificationManager, notificationId: Int) {
        val outputFile = File(filePath)

        val client = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        FileOutputStream(outputFile).use { outputStream ->
            var totalBytesRead = 0
            val totalSegments = segmentUrls.size

            for ((index, url) in segmentUrls.withIndex()) {
                Log.d("VideoPlayerActivity", "Downloading segment: $url")
                val request = Request.Builder().url(url).build()

                try {
                    client.newCall(request).execute().use { response ->
                        if (!response.isSuccessful) {
                            Log.e("VideoPlayerActivity", "Error downloading segment: ${response.code} for URL: $url")
                        } else {
                            response.body?.byteStream()?.use { input ->
                                val buffer = ByteArray(1024)
                                var bytesRead: Int
                                while (input.read(buffer).also { bytesRead = it } != -1) {
                                    outputStream.write(buffer, 0, bytesRead)
                                    totalBytesRead += bytesRead

                                    // Calculate progress percentage
                                    val progressPercentage = ((index * 100 / totalSegments) + (bytesRead * 100 / outputFile.length()).toInt()).coerceAtMost(99)
                                    updateNotificationProgress(notificationManager, notificationId, progressPercentage)
                                }
                            }
                            Log.d("VideoPlayerActivity", "Finished downloading segment: $url")
                        }
                    }
                } catch (e: IOException) {
                    Log.e("VideoPlayerActivity", "IOException downloading segment: $url, Error: ${e.message}")
                } catch (e: Exception) {
                    Log.e("VideoPlayerActivity", "Exception downloading segment: $url, Error: ${e.message}")
                }
            }

            // Explicitly set progress to 100%
            updateNotificationProgress(notificationManager, notificationId, 100)

            Log.d("VideoPlayerActivity", "Download complete: $filePath")

            // Update notification to indicate download completion
            val builder = NotificationCompat.Builder(this@VideoPlayerActivity, "anime_download_channel")
                .setSmallIcon(R.drawable.download) // Replace with your download icon
                .setContentTitle("Anime Download Complete")
                .setContentText("Download finished successfully!")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(false) // Makes the notification non-ongoing
                .setProgress(0, 0, false) // Reset progress
            notificationManager.notify(notificationId, builder.build())
        }
    }

    private fun updateNotificationProgress(notificationManager: NotificationManager, notificationId: Int, progressPercentage: Int) {
        val builder = NotificationCompat.Builder(this@VideoPlayerActivity, "anime_download_channel")
            .setSmallIcon(R.drawable.download) // Replace with your download icon
            .setContentTitle("Downloading Anime")
            .setContentText("Download in progress... ($progressPercentage%)")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true) // Makes the notification ongoing
            .setProgress(100, progressPercentage, false) // Update progress
        notificationManager.notify(notificationId, builder.build())
    }

    override fun onPause() {
        super.onPause()
        viewModel.playbackPosition = viewModel.player?.currentPosition ?: 0
        viewModel.player?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.player?.release()
    }
}