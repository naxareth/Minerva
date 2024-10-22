package com.example.minerva_10.views

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.minerva_10.R
import com.example.minerva_10.adapters.DownloadAdapter
import com.example.minerva_10.api.responses.DownloadItem
import com.example.minerva_10.viewmodels.SharedAnimeViewModel
import com.bumptech.glide.Glide // Import Glide library

class DownloadActivity : AppCompatActivity() {

    private lateinit var sharedViewModel: SharedAnimeViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DownloadAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)

        // Initialize the ViewModel
        sharedViewModel = ViewModelProvider(this).get(SharedAnimeViewModel::class.java)

        // Set up observer immediately
        sharedViewModel.downloadItems.observe(this, Observer { items ->
            Log.d("DownloadActivity", "Observed download items: $items")
            adapter.submitList(items)
            if (items.isEmpty()) {
                Log.d("DownloadActivity", "No download items to display.")
            }
        })

        recyclerView = findViewById(R.id.recycler_view_downloads)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the adapter
        adapter = DownloadAdapter() // Make sure your adapter is initialized here
        recyclerView.adapter = adapter

        // Get the initial download items from the SharedViewModel
        // and submit them to the adapter to populate the RecyclerView
        sharedViewModel.downloadItems.value?.let { adapter.submitList(it) }
    }

    override fun onResume() {
        super.onResume()
        Log.d("DownloadActivity", "Observing download items")
    }

    override fun onPause() {
        super.onPause()
        Log.d("DownloadActivity", "Stopped observing download items")
    }
}