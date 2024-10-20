package com.example.minerva_10.views

import android.content.Intent
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
import androidx.lifecycle.lifecycleScope
import com.example.minerva_10.R
import com.example.minerva_10.api.RetrofitClient
import com.example.minerva_10.api.responses.EpisodeInfo
import com.example.minerva_10.api.responses.StreamingResponse
import com.example.minerva_10.api.interfaces.AnimeApiService
import com.example.minerva_10.api.responses.AnimeInfo
import com.example.minerva_10.viewmodels.VideoPlayerViewModel
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import kotlinx.coroutines.launch
import retrofit2.HttpException

class VideoPlayerActivity : AppCompatActivity() {

    private lateinit var playerView: PlayerView
    private lateinit var animeTitleTextView: TextView
    private lateinit var qualitySpinner: Spinner
    private lateinit var episodeInfo: EpisodeInfo
    private lateinit var apiService: AnimeApiService
    private val viewModel: VideoPlayerViewModel by viewModels()
    private var availableQualities: List<String> = emptyList() // To hold available qualities

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        playerView = findViewById(R.id.player_view)
        animeTitleTextView = findViewById(R.id.anime_title)
        qualitySpinner = findViewById(R.id.quality_spinner)

        // Retrieve the AnimeInfo object from the intent
        val animeInfo = intent.getParcelableExtra<AnimeInfo>("ANIME_INFO")
        animeTitleTextView.text = animeInfo?.title // Set the anime title

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

        // Set controller visibility listener
        playerView.setControllerVisibilityListener { visibility ->
            if (visibility == View.VISIBLE) {
                qualitySpinner.visibility = View.VISIBLE
                findViewById<Button>(R.id.download_button).visibility = View.VISIBLE
            } else {
                qualitySpinner.visibility = View.GONE
                findViewById<Button>(R.id.download_button).visibility = View.GONE
            }
        }

        // Fetch streaming links to set up the quality spinner
        fetchStreamingLinks(episodeInfo.id)
    }

    private fun fetchStreamingLinks(episodeId: String) {
        lifecycleScope.launch {
            try {
                val serverName = "gogocdn"
                Log.d("VideoPlayerActivity", "Fetching streaming links for Episode ID: $episodeId, Server: $serverName")
                val streamingResponse: StreamingResponse = apiService.getStreamingLinks(episodeId, serverName)
                Log.d("VideoPlayerActivity", "Streaming Response: $streamingResponse")

                // Extract available qualities from the sources
                availableQualities = streamingResponse.sources.map { it.quality }.distinct()
                setupQualitySpinner(availableQualities)

                // Play the video with the default quality
                val defaultQuality = availableQualities.firstOrNull() ?: "360p" // Fallback to 360p if no qualities are available
                fetchAndPlayVideo(episodeId, defaultQuality)
            } catch (e: HttpException) {
                Log.e("VideoPlayerActivity", "Error fetching streaming links: ${e.response()?.errorBody()?.string()}")
            } catch (e: Exception) {
                Log.e("VideoPlayerActivity", "Error fetching streaming links: $e")
            }
        }
    }

    private fun setupQualitySpinner(qualities: List<String>) {
        // Use the custom layout for spinner items
        val adapter = ArrayAdapter(this, R.layout.spinner_item, qualities)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        qualitySpinner.adapter = adapter

        qualitySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedQuality = qualities[position]
                // Release previous player instance
                viewModel.player?.release()
                viewModel.player = null

                // Fetch and play the video with the selected quality
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

                // Find the source with the selected quality
                val selectedSource = streamingResponse.sources.find { it.quality == quality }
                if (selectedSource != null) {
                    val videoUrl = selectedSource.url
                    Log.d("VideoPlayerActivity", "Playing video with URL: $videoUrl")

                    // Initialize ExoPlayer
                    viewModel.player = SimpleExoPlayer.Builder(this@VideoPlayerActivity).build()
                    playerView.player = viewModel.player

                    // Prepare the video for playback
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