package com.example.minerva_10.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.minerva_10.adapter.EpisodeAdapter
import com.example.minerva_10.api.responses.AnimeInfo
import com.example.minerva_10.api.RetrofitClient
import com.example.minerva_10.api.interfaces.AnimeApiService
import com.example.minerva_10.databinding.FragmentAnimeinfoBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException


class AnimeInfoFragment : Fragment() {

    private lateinit var binding: FragmentAnimeinfoBinding
    private lateinit var animeApiService: AnimeApiService
    private lateinit var episodeAdapter: EpisodeAdapter

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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the anime ID from the arguments
        val animeId = arguments?.getString("anime_id") ?: ""

        // Fetch anime information
        fetchAnimeInfo(animeId)
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