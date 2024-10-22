package com.example.minerva_10.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.minerva_10.api.responses.DownloadItem

class SharedAnimeViewModel : ViewModel() {
    private val _downloadItems = MutableLiveData<List<DownloadItem>>(emptyList())
    val downloadItems: LiveData<List<DownloadItem>> get() = _downloadItems

    fun addDownloadItem(downloadItem: DownloadItem) {
        val currentList = _downloadItems.value ?: emptyList()
        _downloadItems.value = currentList + downloadItem
        Log.d("SharedAnimeViewModel", "Added download item: $downloadItem, Current list: ${_downloadItems.value}")
    }

    fun updateDownloadProgress(animeId: String, progress: Int) {
        val currentList = _downloadItems.value ?: return
        val updatedList = currentList.map { item ->
            if (item.animeId == animeId) {
                item.copy(progress = progress)
            } else {
                item
            }
        }
        _downloadItems.postValue(updatedList)
    }
}