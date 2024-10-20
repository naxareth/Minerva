package com.example.minerva_10.views

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.minerva_10.R
import com.example.minerva_10.api.RetrofitClient
import com.example.minerva_10.api.responses.EpisodeInfo
import com.example.minerva_10.api.responses.StreamingResponse
import com.example.minerva_10.api.interfaces.AnimeApiService
import com.example.minerva_10.viewmodels.VideoPlayerViewModel
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import kotlinx.coroutines.launch
import retrofit2.HttpException

class VideoPlayerActivity : AppCompatActivity() {

    private lateinit var playerView: PlayerView
    private lateinit var episodeInfo: EpisodeInfo
    private lateinit var apiService: AnimeApiService
    private val viewModel: VideoPlayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        playerView = findViewById(R.id.player_view)
        episodeInfo = intent.getParcelableExtra("EPISODE_INFO") ?: return

        // Initialize your ApiService
        apiService = RetrofitClient.animeApiService

        // Initialize ExoPlayer
        if (viewModel.player == null) {
            viewModel.player = SimpleExoPlayer.Builder(this).build()
            playerView.player = viewModel.player
        } else {
            playerView.player = viewModel.player
            viewModel.player?.seekTo(viewModel.playbackPosition)
        }

        // Fetch and play the video
        fetchAndPlayVideo(episodeInfo.id)
    }

    private fun fetchAndPlayVideo(episodeId: String) {
        lifecycleScope.launch {
            try {
                val serverName = "gogocdn"
                Log.d("VideoPlayerActivity", "Fetching streaming links for Episode ID: $episodeId, Server: $serverName")
                val streamingResponse: StreamingResponse = apiService.getStreamingLinks(episodeId, serverName)
                Log.d("VideoPlayerActivity", "Streaming Response: $streamingResponse")

                val completeVideoUrl = streamingResponse.sources.first().url
                playVideo(completeVideoUrl)
            } catch (e: HttpException) {
                Log.e("VideoPlayerActivity", "Error fetching streaming links: ${e.response()?.errorBody()?.string()}")
            } catch (e: Exception) {
                Log.e("VideoPlayerActivity", "Error fetching streaming links: $e")
            }
        }
    }

    private fun playVideo(videoUrl: String) {
        val mediaItem = MediaItem.fromUri(videoUrl)
        viewModel.player?.setMediaItem(mediaItem)
        viewModel.player?.prepare()
        viewModel.player?.play()
    }

    override fun onStop() {
        super.onStop()
        // Save the current playback position
        viewModel.playbackPosition = viewModel.player?.currentPosition ?: 0
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release the player only if it was created in this activity
        if (viewModel.player != null) {
            viewModel.player?.release()
            viewModel.player = null
        }
    }
}