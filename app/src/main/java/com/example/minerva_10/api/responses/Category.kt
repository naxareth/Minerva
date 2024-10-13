package com.example.minerva_10.api.responses

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class Category(val title: String, val items: List<Item>)
@Parcelize
data class Item(val title: String, val image: String, val id: String) : Parcelable

