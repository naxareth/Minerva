package com.example.minerva_10.api.interfaces

import com.example.minerva_10.api.responses.AnimeInfo
import com.example.minerva_10.api.responses.AnimeResponse
import com.example.minerva_10.api.responses.EpisodeInfo
import com.example.minerva_10.api.responses.Search
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AnimeApiService {
    @GET("top-airing")
    suspend fun getTopAiringAnimes(): AnimeResponse

    @GET("recent-episodes")
    suspend fun getRecentEpisodes(): AnimeResponse

    @GET("info/{id}")
    suspend fun getAnimeInfo(@Path("id") id: String): AnimeInfo

    @GET("info/{id}/episodes")
    suspend fun getAnimeEpisodes(@Path("id") id: String): List<EpisodeInfo>

    @GET("search")  // Replace with the correct endpoint
    fun searchAnime(
        @Query("query") query: String
    ): Call<Search>
}