package com.example.minerva_10.api.interfaces

import com.example.minerva_10.api.responses.AnimeResponse
import retrofit2.http.GET

interface AnimeApiService {
    @GET("top-airing")
    suspend fun getTopAiringAnimes(): AnimeResponse

    @GET("recent-episodes")
    suspend fun getRecentEpisodes(): AnimeResponse
}

