package com.example.minerva_10.api.interfaces

import com.example.minerva_10.api.responses.AnimeInfo
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

    @GET("top-airing")
    suspend fun getRecommendedAnime(@Query("page") page: Int): PaginatedResponse


    @GET("recent-episodes")
    suspend fun getRecentEpisodes(@Query("page") page: Int): PaginatedResponse

    @GET("info/{id}")
    suspend fun getAnimeInfo(@Path("id") id: String): AnimeInfo

    @GET("info/{id}/episodes")
    suspend fun getAnimeEpisodes(@Path("id") id: String): List<EpisodeInfo>

    // Suspend function for searching anime
    @GET("{query}")
    suspend fun searchAnime(
        @Path("query") query: String,  // Use @Path for the query parameter
        @Query("page") page: Int       // Keep page as a query parameter
    ): Search  // Return the Search response directly


    @GET("servers/{episodeId}")
    suspend fun getServers(@Path("episodeId") episodeId: String): List<Server>

    @GET("watch/{episodeId}")
    suspend fun getStreamingLinks(
        @Path("episodeId") episodeId: String,
        @Query("server") serverName: String
    ): StreamingResponse
}
