package com.example.minerva_10.views

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.minerva_10.api.responses.Item

class SharedViewModel : ViewModel() {
    private val _animeList = MutableLiveData<List<Item>>()
    val animeList: LiveData<List<Item>> get() = _animeList

    // Add a new LiveData property to store the "TOP AIRING" animes
    private val _topAiringList = MutableLiveData<List<Item>>()
    val topAiringList: LiveData<List<Item>> get() = _topAiringList

    fun setAnimeList(anime: List<Item>) {
        _animeList.value = anime
    }

    // Function to set "TOP AIRING" animes
    fun setTopAiringList(anime: List<Item>) {
        _topAiringList.value = anime
    }
}
