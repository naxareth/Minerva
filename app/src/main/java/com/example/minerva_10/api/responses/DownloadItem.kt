package com.example.minerva_10.api.responses

data class DownloadItem(
    val animeTitle: String,
    val episodeNumber: Int,
    val coverImageUrl: String,
    var progress: Int = 0 // Progress can be updated as the download proceeds
)