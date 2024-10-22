package com.example.minerva_10.api.responses

data class DownloadItem(
    val animeId: String, // Include the anime ID
    val animeTitle: String,
    val episodeNumber: Int,
    val coverImageUrl: String,
    var progress: Int
)