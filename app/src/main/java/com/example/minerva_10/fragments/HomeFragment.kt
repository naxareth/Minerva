package com.example.minerva_10.fragments

import ParentAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.minerva_10.R
import com.example.minerva_10.api.RetrofitClient
import com.example.minerva_10.api.responses.Category
import com.example.minerva_10.api.responses.Item
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val parentRecyclerView: RecyclerView = view.findViewById(R.id.parentRecyclerView)
        parentRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Use Coroutines to fetch data from both endpoints
        lifecycleScope.launch {
            try {
                Log.d("HomeFragment", "Starting to fetch anime data...")

                // Fetch data from both endpoints concurrently
                val startTime = System.currentTimeMillis()

                val topAiringDeferred = async {
                    Log.d("HomeFragment", "Fetching top airing animes...")
                    RetrofitClient.animeApiService.getTopAiringAnimes()
                }
                val recentEpisodesDeferred = async {
                    Log.d("HomeFragment", "Fetching recent episodes...")
                    RetrofitClient.animeApiService.getRecentEpisodes()
                }

                // Wait for both responses
                val topAiringAnimes = topAiringDeferred.await()
                Log.d("HomeFragment", "Fetched top airing animes: ${topAiringAnimes.results.size} results")

                val recentEpisodes = recentEpisodesDeferred.await()
                Log.d("HomeFragment", "Fetched recent episodes: ${recentEpisodes.results.size} results")

                val endTime = System.currentTimeMillis()
                Log.d("HomeFragment", "Fetching completed in ${endTime - startTime} ms")

                // Create categories for both
                val categories = listOf(
                    Category("Top Airing", topAiringAnimes.results.map { Item(it.title, it.image) }),
                    Category("Recent Episodes", recentEpisodes.results.map { Item(it.title, it.image) })
                )

                // Set the adapter for the RecyclerView with both categories
                parentRecyclerView.adapter = ParentAdapter(categories, requireActivity())
                Log.d("HomeFragment", "Adapter set with ${categories.size} categories.")

            } catch (e: Exception) {
                Log.e("HomeFragment", "Error fetching anime data: ${e.message}")
                // Handle the error
            }
        }
    }
}
