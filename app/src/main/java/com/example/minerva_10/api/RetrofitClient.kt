package com.example.minerva_10.api

import android.util.Log
import com.example.minerva_10.api.interfaces.AnimeApiService
import com.example.minerva_10.api.interfaces.ApiInterface
import com.example.minerva_10.api.responses.AnimeResponse
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://192.168.68.55:8000/api/"
    private const val ANIME_API_URL = "https://animetize-api.vercel.app/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ApiInterface by lazy {
        retrofit.create(ApiInterface::class.java)
    }

    val animeApiService: AnimeApiService by lazy {
        retrofit.newBuilder()
            .baseUrl(ANIME_API_URL)
            .build()
            .create(AnimeApiService::class.java)
    }

    suspend fun fetchAnimeData(currentPage: Int): Pair<AnimeResponse, AnimeResponse> {
        // Launch a coroutine to fetch both data concurrently
        return kotlinx.coroutines.coroutineScope {
            val topAiringDeferred: Deferred<AnimeResponse> = async {
                Log.d("RetrofitClient", "Fetching top airing animes...")
                animeApiService.getTopAiringAnimes(currentPage)
            }

            val recentEpisodesDeferred: Deferred<AnimeResponse> = async {
                Log.d("RetrofitClient", "Fetching recent episodes...")
                animeApiService.getRecentEpisodes(currentPage)
            }

            // Wait for both responses
            val topAiringResponse = topAiringDeferred.await()
            val recentEpisodesResponse = recentEpisodesDeferred.await()

            Pair(topAiringResponse, recentEpisodesResponse)
        }

    }
}