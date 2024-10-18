package com.example.minerva_10.views

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.minerva_10.R
import com.example.minerva_10.api.RetrofitClient
import com.example.minerva_10.api.responses.EpisodeInfo
import com.example.minerva_10.api.responses.StreamingResponse
import com.example.minerva_10.api.interfaces.AnimeApiService // Make sure to import your ApiService
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import kotlinx.coroutines.launch
import retrofit2.HttpException

class VideoPlayerActivity : AppCompatActivity() {

    private lateinit var player: SimpleExoPlayer
    private lateinit var playerView: PlayerView
    private lateinit var episodeInfo: EpisodeInfo
    private lateinit var apiService: AnimeApiService // Initialize your ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        playerView = findViewById(R.id.player_view)
        episodeInfo = intent.getParcelableExtra("EPISODE_INFO") ?: return // Retrieve episode info

        // Initialize ExoPlayer
        player = SimpleExoPlayer.Builder(this).build()
        playerView.player = player

        // Initialize your ApiService
        apiService = RetrofitClient.animeApiService // Ensure the ApiService is initialized

        // Directly fetch streaming links and play video
        fetchAndPlayVideo(episodeInfo.id)
    }

    private fun fetchAndPlayVideo(episodeId: String) {
        lifecycleScope.launch {
            try {
                // Define the server name you want to use
                val serverName = "gogocdn" // Change this to the desired server name if necessary

                Log.d("VideoPlayerActivity", "Fetching streaming links for Episode ID: $episodeId, Server: $serverName")
                val streamingResponse: StreamingResponse = apiService.getStreamingLinks(episodeId, serverName)

                Log.d("VideoPlayerActivity", "Streaming Response: $streamingResponse")

                // Get the complete URL from the streaming response
                val completeVideoUrl = streamingResponse.sources.first().url
                playVideo(completeVideoUrl)
            } catch (e: HttpException) {
                Log.e("VideoPlayerActivity", "Error fetching streaming links: ${e.response()?.errorBody()?.string()}")
                Log.e("VideoPlayerActivity", "Error code: ${e.code()}")
                Log.e("VideoPlayerActivity", "Error message: ${e.message()}")
            } catch (e: Exception) {
                Log.e("VideoPlayerActivity", "Error fetching streaming links: $e")
            }
        }
    }

    private fun playVideo(videoUrl: String) {
        val mediaItem = MediaItem.fromUri(videoUrl)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    override fun onStop() {
        super.onStop()
        player.release()
    }
}