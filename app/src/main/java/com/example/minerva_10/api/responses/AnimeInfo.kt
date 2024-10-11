package com.example.minerva_10.api.responses

import android.os.Parcel
import android.os.Parcelable

data class AnimeInfo(
    val description: String,
    val episodes: List<Episode>,
    val genres: List<String>,
    val id: String,
    val image: String,
    val otherName: String,
    val releaseDate: String,
    val status: String,
    val subOrDub: String,
    val title: String,
    val totalEpisodes: Int,
    val type: String,
    val url: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.createTypedArrayList(Episode.CREATOR) ?: emptyList(),
        parcel.createStringArrayList() ?: emptyList(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(description)
        parcel.writeTypedList(episodes)
        parcel.writeStringList(genres)
        parcel.writeString(id)
        parcel.writeString(image)
        parcel.writeString(otherName)
        parcel.writeString(releaseDate)
        parcel.writeString(status)
        parcel.writeString(subOrDub)
        parcel.writeString(title)
        parcel.writeInt(totalEpisodes)
        parcel.writeString(type)
        parcel.writeString(url)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AnimeInfo> {
        override fun createFromParcel(parcel: Parcel): AnimeInfo {
            return AnimeInfo(parcel)
        }

        override fun newArray(size: Int): Array<AnimeInfo?> {
            return arrayOfNulls(size)
        }
    }
}
