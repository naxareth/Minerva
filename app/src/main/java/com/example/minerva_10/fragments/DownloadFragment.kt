package com.example.minerva_10.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.minerva_10.R
import com.example.minerva_10.adapters.DownloadAdapter
import com.example.minerva_10.api.responses.DownloadItem
import com.example.minerva_10.viewmodels.SharedViewModel
import androidx.fragment.app.activityViewModels

class DownloadFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var downloadAdapter: DownloadAdapter
    private val downloadItems = mutableListOf<DownloadItem>()
    private val sharedViewModel: SharedViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_download, container, false)

        // Retrieve the arguments from the Bundle
        arguments?.let {
            val animeTitle = it.getString("ANIME_TITLE")
            val coverImageUrl = it.getString("COVER_IMAGE_URL")
            val episodeNumber = it.getInt("EPISODE_NUMBER")

            // Use this data to update your UI or add a download item
            if (animeTitle != null && coverImageUrl != null) {
                val downloadItem = DownloadItem(animeTitle, episodeNumber, coverImageUrl)
                addDownloadItem(downloadItem) // Add this item to the RecyclerView
            }
        }

        // Existing RecyclerView setup code...
        recyclerView = view.findViewById(R.id.recycler_view_downloads)
        downloadAdapter = DownloadAdapter(downloadItems)
        recyclerView.adapter = downloadAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Observe the shared ViewModel for any new download items
        sharedViewModel.downloadItem.observe(viewLifecycleOwner) { downloadItem ->
            addDownloadItem(downloadItem)
        }

        return view
    }

    fun addDownloadItem(downloadItem: DownloadItem) {
        downloadItems.add(downloadItem)
        downloadAdapter.notifyItemInserted(downloadItems.size - 1)
    }

    fun updateDownloadProgress(position: Int, progress: Int) {
        if (position in downloadItems.indices) {
            downloadItems[position].progress = progress
            downloadAdapter.notifyItemChanged(position)
        }
    }

    fun getDownloadItemCount(): Int {
        return downloadItems.size
    }
}