package com.example.minerva_10.fragments

import android.content.Context.MODE_PRIVATE
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAnimeinfoBinding.inflate(inflater, container, false)
        animeApiService = RetrofitClient.animeApiService // Use the correct instance

        // Initialize episodeAdapter and episodeList here
        episodeAdapter = EpisodeAdapter(emptyList())
        binding.episodeList.layoutManager = LinearLayoutManager(context)
        binding.episodeList.adapter = episodeAdapter
        binding.episodeList.isNestedScrollingEnabled = true

        val sharedPreferences = context?.getSharedPreferences("token_prefs", MODE_PRIVATE)
        token = sharedPreferences?.getString("token", null)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the anime ID from the arguments
        val animeId = arguments?.getString("anime_id") ?: ""

        // Fetch anime information
        fetchAnimeInfo(animeId)

        // Set up the toggle button listener
        binding.addToFavoritesButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Anime is being added to favorites
                addAnimeToFavorites(animeId)
            } else {
                // Anime is being removed from favorites
                removeAnimeFromFavorites(animeId)
            }
        }

        // Check if the anime is already a favorite
        checkIfAnimeIsFavorite()
    }

    private fun checkIfAnimeIsFavorite() {
        val animeId = arguments?.getString("anime_id") ?: ""
        val sharedPreferences = context?.getSharedPreferences("token_prefs", MODE_PRIVATE)
        val token = sharedPreferences?.getString("token", "") ?: ""

        RetrofitClient.api.getFavorites(token).enqueue(object : Callback<FavoriteResponse> {
            override fun onResponse(call: Call<FavoriteResponse>, response: Response<FavoriteResponse>) {
                if (response.isSuccessful) {
                    val favoriteResponse = response.body()
                    val favoriteResources = favoriteResponse?.data ?: emptyList()

                    val isFavorite = favoriteResources.any { it.`id` == animeId }

                    binding.addToFavoritesButton.isChecked = isFavorite
                } else {
                    // Handle error
                }
            }

            override fun onFailure(call: Call<FavoriteResponse>, t: Throwable) {
                // Handle error
            }
        })
    }

    private fun addAnimeToFavorites(animeId: String) {
        val sharedPreferences = context?.getSharedPreferences("token_prefs", MODE_PRIVATE)
        val token = sharedPreferences?.getString("token", "") ?: ""

        // Fetch the anime API ID
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val animeInfo: AnimeInfo = animeApiService.getAnimeInfo(animeId)
                val animeApiId = animeInfo.id // Fetch the ID from the anime API

                // Log the image URL to check if it's fetched successfully
                Log.d("Favorite", "Fetched anime image URL: ${animeInfo.image}")

                withContext(Dispatchers.Main) {
                    // Create a new Favorite instance with the actual anime API ID
                    val favorite = Favorite(
                        id = animeApiId, // Use the actual anime API ID
                        title = binding.animeTitle.text.toString(),
                        image = animeInfo.image, // Use the fetched image URL
                        user_id = 0
                    )

                    // Add the favorite to the database
                    RetrofitClient.api.createFavorite("Bearer $token", favorite).enqueue(object : Callback<FavoriteResource> {
                        override fun onResponse(call: Call<FavoriteResource>, response: Response<FavoriteResource>) {
                            if (response.isSuccessful) {
                                val favoriteResource = response.body()
                                if (favoriteResource != null) {
                                    Log.d("Favorite", "Favorite created with anime-id: ${favoriteResource.`id`}")
                                    Log.d("Favorite", "Favorite created with title: ${favoriteResource.title}")
                                    Log.d("Favorite", "Favorite created with image: ${favoriteResource.image}")
                                    Log.d("Favorite", "Favorite created with user_id: ${favoriteResource.user_id}")
                                }
                            } else {
                                Log.e("Favorite", "Error adding anime to favorites: ${response.code()}")
                                val errorBody = response.errorBody()
                                if (errorBody != null) {
                                    Log.e("Favorite", "Error body: ${errorBody.string()}")
                                }
                            }
                        }

                        override fun onFailure(call: Call<FavoriteResource>, t: Throwable) {
                            Log.e("Favorite", "Error adding anime to favorites: $t")
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
        val sharedPreferences = context?.getSharedPreferences("token_prefs", MODE_PRIVATE)
        val token = sharedPreferences?.getString("token", "") ?: ""

        // Call the API to remove the favorite
        RetrofitClient.api.deleteFavorite("Bearer $token", animeId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // Anime removed from favorites successfully
                    Log.d("Favorite", "Anime removed from favorites successfully")
                } else {
                    // Handle error
                    Log.e("Favorite", "Error removing anime from favorites: ${response.code()}")
                    val errorBody = response.errorBody()
                    if (errorBody != null) {
                        Log.e("Favorite", "Error body: ${errorBody.string()}")
                    }
                    // Show error message to the user
                    Toast.makeText(context, "Error removing anime from favorites", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Handle error
                Log.e("Favorite", "Error removing anime from favorites: $t")
                // Show error message to the user
                Toast.makeText(context, "Error removing anime from favorites", Toast.LENGTH_SHORT).show()
            }
        })
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}