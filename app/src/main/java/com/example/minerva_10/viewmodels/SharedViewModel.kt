package com.example.minerva_10.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.minerva_10.api.responses.DownloadItem

class SharedAnimeViewModel : ViewModel() {
    private val _animeDetails = MutableLiveData<DownloadItem>()
    val animeDetails: LiveData<DownloadItem> get() = _animeDetails

    fun setAnimeDetails(item: DownloadItem) {
        _animeDetails.value = item
    }
}