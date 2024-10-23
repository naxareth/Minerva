package com.example.minerva_10.api.responses

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaginatedResponse(
    val currentPage: Int,
    val hasNextPage: Boolean,
    val results: List<Item>
) : Parcelable

@Parcelize
data class Category(val title: String, val items: List<Item>) : Parcelable

@Parcelize
data class Item(
    val id: String,
    val title: String,
    val image: String,
) : Parcelable