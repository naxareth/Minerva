package com.example.minerva_10.api.responses

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class AnimeResponse(
    val results: List<Result>
)


data class Result(
    val id: String,
    val image: String,
    val releaseDate: String,
    val subOrDub: String,
    val title: String
)
@Parcelize
data class AnimeInfo(
    val id: String,
    val title: String,
    val url: String,
    val image: String,
    val releaseDate: String? = null,
    val description: String? = null,
    val genres: List<String>,
    val subOrDub: String,
    val type: String? = null,
    val status: String,
    val otherName: String? = null,
    val totalEpisodes: Int,
    val episodes: List<EpisodeInfo>,
) : Parcelable

