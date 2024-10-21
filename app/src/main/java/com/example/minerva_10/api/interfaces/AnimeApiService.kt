package com.example.minerva_10.api.interfaces

import com.example.minerva_10.api.responses.AnimeInfo
import com.example.minerva_10.api.responses.AnimeResponse
import com.example.minerva_10.api.responses.EpisodeInfo
import com.example.minerva_10.api.responses.Search
import retrofit2.Call
import com.example.minerva_10.api.responses.PaginatedResponse
import com.example.minerva_10.api.responses.Server
import com.example.minerva_10.api.responses.StreamingResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AnimeApiService {
    @GET("top-airing")
    suspend fun getTopAiringAnimes(@Query("page") page: Int): PaginatedResponse

    @GET("recent-episodes")
    suspend fun getRecentEpisodes(@Query("page") page: Int): PaginatedResponse

    @GET("info/{id}")
    suspend fun getAnimeInfo(@Path("id") id: String): AnimeInfo

    @GET("info/{id}/episodes")
    suspend fun getAnimeEpisodes(@Path("id") id: String): List<EpisodeInfo>

    @GET("search")  // Replace with the correct endpoint
    fun searchAnime(
        @Query("query") query: String
    ): Call<Search>

    @GET("servers/{episodeId}")
    suspend fun getServers(@Path("episodeId") episodeId: String): List<Server>

    @GET("watch/{episodeId}")
    suspend fun getStreamingLinks(
        @Path("episodeId") episodeId: String,
        @Query("server") serverName: String
    ): StreamingResponse
}
