package com.example.minerva_10.api

data class AnimeResponse(
    val results: List<Result>
)

data class Result(
    val title: String,
    val image: String
)
