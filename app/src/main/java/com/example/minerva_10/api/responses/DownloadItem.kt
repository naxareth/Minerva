package com.example.minerva_10.api.responses

data class DownloadItem(
    val animeTitle: String, //AnimeInfo's title
    val episodeNumber: Int, //EpisodeInfo's number
    val coverImageUrl: String, //AnimeInfo's image
    var progress: Int = 0 // Progress can be updated as the download proceeds
)