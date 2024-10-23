package com.example.minerva_10.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.minerva_10.R
import com.example.minerva_10.adapters.DownloadAdapter
import com.example.minerva_10.viewmodels.SharedAnimeViewModel

class DownloadFragment : Fragment() {
    private val viewModel: SharedAnimeViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DownloadAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("DownloadFragment", "onCreateView called")
        val view = inflater.inflate(R.layout.fragment_download, container, false)
        recyclerView = view.findViewById(R.id.recycler_view_downloads)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Initialize the adapter
        adapter = DownloadAdapter()
        recyclerView.adapter = adapter

        // Observe the download items
        viewModel.downloadItems.observe(viewLifecycleOwner) { items ->
            Log.d("DownloadFragment", "Observed download items: $items") // Log the items
            adapter.submitList(items) // Update the adapter with new data
            if (items.isEmpty()) {
                Log.d("DownloadFragment", "No download items available.")
            } else {
                Log.d("DownloadFragment", "Download items updated: ${items.size} items.")
            }
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        Log.d("DownloadFragment", "Fragment is visible and active")
    }

    override fun onStop() {
        super.onStop()
        Log.d("DownloadFragment", "Fragment is no longer visible")
    }
}