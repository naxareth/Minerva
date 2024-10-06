package com.example.minerva_10.api.responses

data class Category(val title: String, val items: List<Item>)
data class Item(val title: String, val image: String)

