package com.example.minerva_10.views

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.minerva_10.R
import com.example.minerva_10.adapter.EpisodeAdapter
import com.example.minerva_10.api.RetrofitClient
import com.example.minerva_10.api.interfaces.AnimeApiService
import com.example.minerva_10.api.responses.AnimeInfo
import com.example.minerva_10.api.responses.EpisodeInfo
import com.example.minerva_10.api.responses.Favorite
import com.example.minerva_10.api.responses.FavoriteResource
import com.example.minerva_10.databinding.ActivityAnimeInfoBinding
import com.example.minerva_10.views.VideoPlayerActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class AnimeInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnimeInfoBinding
    private lateinit var animeApiService: AnimeApiService
    private lateinit var episodeAdapter: EpisodeAdapter
    private var token: String? = null
    private lateinit var sharedPreferences: SharedPreferences
    private var animeId: String? = null
    private lateinit var animeInfo: AnimeInfo // Store the AnimeInfo object

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnimeInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        animeApiService = RetrofitClient.animeApiService
        episodeAdapter = EpisodeAdapter(emptyList()) { episode -> onEpisodeClicked(episode) }

        binding.episodeList.layoutManager = LinearLayoutManager(this)
        binding.episodeList.adapter = episodeAdapter

        val sharedPreferencesToken = getSharedPreferences("token_prefs", MODE_PRIVATE)
        token = sharedPreferencesToken.getString("token", null)

        sharedPreferences = getSharedPreferences("favorites_prefs_${token ?: "default"}", MODE_PRIVATE)

        binding.backButton.setOnClickListener { finish() } // Navigate back to the previous activity

        animeId = intent.getStringExtra("anime_id")
        Log.d("AnimeInfoActivity", "Anime ID: $animeId") // Log the anime ID
        fetchAnimeInfo(animeId ?: "")

        binding.playButton.setOnClickListener { playFirstEpisode() } // Call the method to play the first episode

        binding.addToFavoritesButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                addAnimeToFavorites(animeId ?: "")
            } else {
                removeAnimeFromFavorites(animeId ?: "")
            }
        }

        checkIfAnimeIsFavorite(animeId ?: "")
    }

    private fun playFirstEpisode() {
        // Check if animeInfo is initialized and has episodes
        if (this::animeInfo.isInitialized && animeInfo.episodes.isNotEmpty()) {
            val firstEpisode = animeInfo.episodes[0]
            val intent = Intent(this, VideoPlayerActivity::class.java).apply {
                putExtra("EPISODE_INFO", firstEpisode) // Pass the first episode object
                putExtra("ANIME_INFO", animeInfo) // Pass the anime info object
            }
            startActivity(intent)
        } else {
            Log.e("AnimeInfoActivity", "No episodes available for this anime.")
        }
    }

    private fun checkIfAnimeIsFavorite(animeId: String) {
        val favoriteIds = sharedPreferences.getStringSet("favorite_ids", emptySet())
        val isFavorite = favoriteIds?.contains(animeId) ?: false
        binding.addToFavoritesButton.isChecked = isFavorite
    }

    private fun addAnimeToFavorites(animeId: String) {
        val sharedPreferencesToken = getSharedPreferences("token_prefs", MODE_PRIVATE)
        val token = sharedPreferencesToken.getString("token", "") ?: ""

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val animeInfo: AnimeInfo = animeApiService.getAnimeInfo(animeId)
                val animeApiId = animeInfo.id

                withContext(Dispatchers.Main) {
                    val favorite = Favorite(
                        anime_id = animeApiId,
                        title = binding.animeTitle.text.toString(),
                        image = animeInfo.image,
                        user_id = 0
                    )

                    RetrofitClient.api.createFavorite("Bearer $token", favorite).enqueue(object : Callback<FavoriteResource> {
                        override fun onResponse(call: Call<FavoriteResource>, response: Response<FavoriteResource>) {
                            if (response.isSuccessful) {
                                Log.d("Favorite", "Added to favorites")
                                // Update shared preferences
                                updateFavoriteIds(animeId, add = true)
                            } else {
                                Log.e("Favorite", "Error adding to favorites: ${response.code()}")
                            }
                        }

                        override fun onFailure(call: Call<FavoriteResource>, t: Throwable) {
                            Log.e("Favorite", "Error adding to favorites: $t")
                        }
                    })
                }
            } catch (e: IOException) {
                Log.e("Fetch Data", "Error fetching anime information: $e")
            } catch (e: HttpException) {
                Log.e("Fetch Data", "Error fetching anime information: $e")
            }
        }
    }

    private fun removeAnimeFromFavorites(animeId: String) {
        val sharedPreferencesToken = getSharedPreferences("token_prefs", MODE_PRIVATE)
        val token = sharedPreferencesToken.getString("token", "") ?: ""

        RetrofitClient.api.deleteFavorite("Bearer $token", animeId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("Favorite", "Removed from favorites")
                    // Update shared preferences
                    updateFavoriteIds(animeId, add = false)
                } else {
                    Log.e("Favorite", "Error removing from favorites: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("Favorite", "Error removing from favorites: $t")
            }
        })
    }

    private fun updateFavoriteIds(animeId: String, add: Boolean) {
        val favoriteIds = sharedPreferences.getStringSet("favorite_ids", emptySet())?.toMutableSet() ?: mutableSetOf()
        if (add) {
            favoriteIds.add(animeId)
        } else {
            favoriteIds.remove(animeId)
        }
        sharedPreferences.edit().putStringSet("favorite_ids", favoriteIds).apply()
    }
    private fun fetchAnimeInfo(animeId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val animeInfo: AnimeInfo = animeApiService.getAnimeInfo(animeId)
                Log.d("Fetch Data", "Anime Information: ${animeInfo.title}")

                // Store the animeInfo for later use
                this@AnimeInfoActivity.animeInfo = animeInfo // Store the animeInfo

                withContext(Dispatchers.Main) {
                    // Update the UI with the anime information
                    updateAnimeInfoUI(animeInfo)
                    // Update the episode list
                    episodeAdapter.updateEpisodes(animeInfo.episodes)
                }
            } catch (e: IOException) {
                Log.e("Fetch Data", "Error fetching anime information: $e")
            } catch (e: HttpException) {
                Log.e("Fetch Data", "Error fetching anime information: $e")
            }
        }
    }

    private fun updateAnimeInfoUI(animeInfo: AnimeInfo) {
        // Update the anime title
        binding.animeTitle.text = animeInfo.title

        // Load the anime image
        Glide.with(this)
            .load(animeInfo.image)
            .into(binding.animeImage)

        Glide.with(this)
            .load(animeInfo.image)
            .into(binding.ivImage)

        // Update the anime release date
        binding.animeReleaseDate.text = animeInfo.releaseDate
        binding.animeReleaseDateLabel.text = "Release Date:"

        // Update the anime description
        binding.animeDescription.text = animeInfo.description

        // Update the anime genres
        binding.animeGenres.text = animeInfo.genres.joinToString(", ")

        // Update the anime sub or dub
        //binding.animeSubOrDub.text = animeInfo.subOrDub
        //binding.animeSubOrDubLabel.text = "Sub or Dub:"

        // Update the anime type
        binding.animeType.text = animeInfo.type

        // Update the anime status
        binding.animeStatus.text = animeInfo.status

        // Update the anime other name
        //binding.animeOtherName.text = animeInfo.otherName
        //binding.animeOtherNameLabel.text = "Other Name:"
    }

    private fun onEpisodeClicked(episode: EpisodeInfo) {
        // Start VideoPlayerActivity and pass the episode and anime info
        val intent = Intent(this, VideoPlayerActivity::class.java).apply {
            putExtra("EPISODE_INFO", episode) // Pass the episode object
            putExtra("ANIME_INFO", animeInfo) // Pass the anime info object
        }
        startActivity(intent)
    }
}