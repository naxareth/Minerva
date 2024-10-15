package com.example.minerva_10.fragments

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.minerva_10.adapter.EpisodeAdapter
import com.example.minerva_10.api.responses.AnimeInfo
import com.example.minerva_10.api.RetrofitClient
import com.example.minerva_10.api.interfaces.AnimeApiService
import com.example.minerva_10.api.responses.Favorite
import com.example.minerva_10.api.responses.FavoriteResource
import com.example.minerva_10.api.responses.FavoriteResponse
import com.example.minerva_10.databinding.FragmentAnimeinfoBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class AnimeInfoFragment : Fragment() {

    private lateinit var binding: FragmentAnimeinfoBinding
    private lateinit var animeApiService: AnimeApiService
    private lateinit var episodeAdapter: EpisodeAdapter
    private var token: String? = null
    private var userId: Int? = null
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAnimeinfoBinding.inflate(inflater, container, false)
        animeApiService = RetrofitClient.animeApiService

        episodeAdapter = EpisodeAdapter(emptyList())
        binding.episodeList.layoutManager = LinearLayoutManager(context)
        binding.episodeList.adapter = episodeAdapter
        binding.episodeList.isNestedScrollingEnabled = true

        val sharedPreferencesToken = context?.getSharedPreferences("token_prefs", MODE_PRIVATE)
        token = sharedPreferencesToken?.getString("token", null)
        userId = sharedPreferencesToken?.getInt("user_id", 0)

        // Use the token to create a user-specific SharedPreferences for favorites
        sharedPreferences = context?.getSharedPreferences("favorites_prefs_${token ?: "default"}", MODE_PRIVATE) ?: return null

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateUserId()

        val sharedPreferencesToken = context?.getSharedPreferences("token_prefs", MODE_PRIVATE)
        token = sharedPreferencesToken?.getString("token", null)
        userId = sharedPreferencesToken?.getInt("user_id", 0)

        // Use the token to create a user-specific SharedPreferences for favorites
        sharedPreferences = context?.getSharedPreferences("favorites_prefs_${token ?: "default"}", MODE_PRIVATE) ?: return

        val animeId = arguments?.getString("anime_id") ?: ""
        fetchAnimeInfo(animeId)

        binding.addToFavoritesButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                addAnimeToFavorites(animeId)
            } else {
                removeAnimeFromFavorites(animeId)
            }
        }

        checkIfAnimeIsFavorite(animeId)
    }

    private fun checkIfAnimeIsFavorite(animeId: String) {
        val sharedPreferencesToken = context?.getSharedPreferences("token_prefs", MODE_PRIVATE)
        val token = sharedPreferencesToken?.getString("token", null)
        val userId = sharedPreferencesToken?.getInt("user_id", 0) ?: 0

        val sharedPreferences = context?.getSharedPreferences("favorites_prefs_${token ?: "default"}", MODE_PRIVATE) ?: return

        val favoriteIdsKey = "favorite_ids_$userId"
        val favoriteIds = sharedPreferences.getStringSet(favoriteIdsKey, emptySet())
        val isFavorite = favoriteIds?.contains(animeId) ?: false
        binding.addToFavoritesButton.isChecked = isFavorite
    }

    private fun addAnimeToFavorites(animeId: String) {
        val sharedPreferencesToken = context?.getSharedPreferences("token_prefs", MODE_PRIVATE)
        token = sharedPreferencesToken?.getString("token", "") ?: return
        userId = sharedPreferencesToken?.getInt("user_id", 0) ?: return

        val sharedPreferences = context?.getSharedPreferences("favorites_prefs_${token}", MODE_PRIVATE) ?: return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val animeInfo: AnimeInfo = animeApiService.getAnimeInfo(animeId)
                val animeApiId = animeInfo.id

                withContext(Dispatchers.Main) {
                    val favorite = Favorite(
                        id = animeApiId,
                        title = binding.animeTitle.text.toString(),
                        image = animeInfo.image,
                        user_id = userId!!
                    )

                    RetrofitClient.api.createFavorite("Bearer $token", favorite).enqueue(object : Callback<FavoriteResource> {
                        override fun onResponse(call: Call<FavoriteResource>, response: Response<FavoriteResource>) {
                            if (response.isSuccessful) {
                                Log.d("Favorite", "Added to favorites")
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
                Log.e("Fetch Data", "Error fetching anime information: $ e")
            } catch (e: HttpException) {
                Log.e("Fetch Data", "Error fetching anime information: $e")
            }
        }

        // Store favorite IDs separately for each user based on user ID
        val favoriteIdsKey = "favorite_ids_$userId"
        val favoriteIds = sharedPreferences.getStringSet(favoriteIdsKey, emptySet())
        val newFavoriteIds = favoriteIds?.toSet()?.plus(animeId) ?: setOf(animeId)
        sharedPreferences.edit().putStringSet(favoriteIdsKey, newFavoriteIds).apply()

        // Update the UI to reflect the new favorite status
        binding.addToFavoritesButton.isChecked = true
    }

    private fun removeAnimeFromFavorites(animeId: String) {
        val sharedPreferencesToken = context?.getSharedPreferences("token_prefs", MODE_PRIVATE)
        token = sharedPreferencesToken?.getString("token", "") ?: return
        userId = sharedPreferencesToken?.getInt("user_id", 0) ?: return

        RetrofitClient.api.deleteFavorite("Bearer $token", animeId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("Favorite", "Removed from favorites")
                } else {
                    Log.e("Favorite", "Error removing from favorites: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("Favorite", "Error removing from favorites: $t")
            }
        })

        // Remove from favorites using the unique key
        val sharedPreferences = context?.getSharedPreferences("favorites_prefs_${token}", MODE_PRIVATE) ?: return
        val favoriteIdsKey = "favorite_ids_$userId"
        val favoriteIds = sharedPreferences.getStringSet(favoriteIdsKey, emptySet())
        val newFavoriteIds = favoriteIds?.toSet()?.minus(animeId) ?: emptySet()
        sharedPreferences.edit().putStringSet(favoriteIdsKey, newFavoriteIds).apply()

        // Update the UI to reflect the new favorite status
        binding.addToFavoritesButton.isChecked = false
    }

    private fun fetchAnimeInfo(animeId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val animeInfo: AnimeInfo = animeApiService.getAnimeInfo(animeId)
                Log.d("Fetch Data", "Anime Information: ${animeInfo.title}")
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

        // Update the anime URL
        //binding.animeUrl.text = animeInfo.url

        // Update the anime release date
        binding.animeReleaseDate.text = animeInfo.releaseDate
        binding.animeReleaseDateLabel.text = "Release Date:"

        // Update the anime description
        binding.animeDescription.text = animeInfo.description
        binding.animeDescriptionLabel.text = "Description:"

        // Update the anime genres
        binding.animeGenres.text = animeInfo.genres.joinToString(", ")
        binding.animeGenresLabel.text = "Genres:"

        // Update the anime sub or dub
        binding.animeSubOrDub.text = animeInfo.subOrDub
        binding.animeSubOrDubLabel.text = "Sub or Dub:"

        // Update the anime type
        binding.animeType.text = animeInfo.type
        binding.animeTypeLabel.text = "Type:"

        // Update the anime status
        binding.animeStatus.text = animeInfo.status
        binding.animeStatusLabel.text = "Status:"

        // Update the anime other name
        binding.animeOtherName.text = animeInfo.otherName
        binding.animeOtherNameLabel.text = "Other Name:"
    }

    private fun updateUserId() {
        val sharedPreferencesToken = context?.getSharedPreferences("token_prefs", MODE_PRIVATE)
        userId = sharedPreferencesToken?.getInt("user_id", 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}