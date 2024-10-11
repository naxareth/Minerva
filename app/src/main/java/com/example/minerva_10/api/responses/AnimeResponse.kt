package com.example.minerva_10.api.responses

data class AnimeResponse(
    val currentPage: Int,
    val hasNextPage: Boolean,
    val results: List<Result>
)

data class Result(
    val genres: List<String>,
    val id: String,
    val image: String,
    val title: String,
    val url: String,
    val episodeId: String,
    val episodeNumber: Int,
)
