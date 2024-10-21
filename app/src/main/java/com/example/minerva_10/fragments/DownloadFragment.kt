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

class DownloadFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var downloadAdapter: DownloadAdapter
    private val downloadItems = mutableListOf<DownloadItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_download, container, false)

        recyclerView = view.findViewById(R.id.recycler_view_downloads)
        downloadAdapter = DownloadAdapter(downloadItems)
        recyclerView.adapter = downloadAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)

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
