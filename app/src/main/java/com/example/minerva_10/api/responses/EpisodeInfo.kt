package com.example.minerva_10.api.responses

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EpisodeInfo(
    val id: String,  // Add this line to include the episode ID
    val number: Int,
    val url: String
) : Parcelable

@Parcelize
data class StreamingResponse(
    val headers: Headers,
    val sources: List<Source>,
    val download: String // No longer working
) : Parcelable

@Parcelize
data class Headers(
    val referer: String,
    val watchsb: String?, // Nullable
    val userAgent: String? // Nullable
) : Parcelable

@Parcelize
data class Source(
    val url: String,
    val quality: String,
    val isM3U8: Boolean
) : Parcelable

@Parcelize
data class Server(
    val name: String,
    val url: String
) : Parcelable