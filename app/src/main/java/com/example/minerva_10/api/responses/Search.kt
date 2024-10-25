package com.example.minerva_10.api.responses

data class Search(
    val currentPage: Int,
    val hasNextPage: Boolean,
    val results: List<SearchResult>
)

data class SearchResult(
    val id: String,
    val title: String,
    val image: String,
    val releaseDate: String?,
    val subOrDub: String?
)