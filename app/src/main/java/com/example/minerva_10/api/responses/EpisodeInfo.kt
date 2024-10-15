package com.example.minerva_10.api.responses

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EpisodeInfo(
    val number: Int,
    val url: String
) : Parcelable