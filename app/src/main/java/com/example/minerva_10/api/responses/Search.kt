package com.example.minerva_10.api.responses

data class Search(
    val currentPage: Int,
    val hasNextPage: Boolean,
    val results: List<SearchResult>
)