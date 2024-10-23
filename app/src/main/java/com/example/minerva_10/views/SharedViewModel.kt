package com.example.minerva_10.views

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.minerva_10.api.responses.Item

class SharedViewModel : ViewModel() {
    private val _animeList = MutableLiveData<List<Item>>()
    val animeList: LiveData<List<Item>> get() = _animeList

    private val _topAiringList = MutableLiveData<List<Item>>()
    val topAiringList: LiveData<List<Item>> get() = _topAiringList

    private val _recentEpisodesList = MutableLiveData<List<Item>>()
    val recentEpisodesList: LiveData<List<Item>> get() = _recentEpisodesList

    fun setAnimeList(anime: List<Item>) {
        _animeList.value = anime
    }

    fun setTopAiringList(anime: List<Item>) {
        _topAiringList.value = anime
    }

    fun setRecentEpisodesList(anime: List<Item>) {
        _recentEpisodesList.value = anime
    }
}

