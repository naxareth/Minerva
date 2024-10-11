package com.example.minerva_10.api.interfaces

import com.example.minerva_10.api.responses.AnimeResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface AnimeApiService {
    @GET("top-airing")
    suspend fun getTopAiringAnimes(@Query("page") page: Int): AnimeResponse

    @GET("recent-episodes")
    suspend fun getRecentEpisodes(@Query("page") page: Int): AnimeResponse
}


