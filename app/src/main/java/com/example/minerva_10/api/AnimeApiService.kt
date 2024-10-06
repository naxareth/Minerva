package com.example.minerva_10.api

import retrofit2.http.GET

interface AnimeApiService {
    @GET("top-airing")
    suspend fun getTopAiringAnimes(): AnimeResponse

    @GET("recent-episodes")
    suspend fun getRecentEpisodes(): AnimeResponse
}

