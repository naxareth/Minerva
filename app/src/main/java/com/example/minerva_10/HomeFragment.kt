package com.example.minerva_10

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.minerva_10.api.RetrofitClient
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
                // Fetch data from both endpoints concurrently
                val topAiringDeferred = async { RetrofitClient.animeApiService.getTopAiringAnimes() }
                val recentEpisodesDeferred = async { RetrofitClient.animeApiService.getRecentEpisodes() }

                // Wait for both responses
                val topAiringAnimes = topAiringDeferred.await()
                val recentEpisodes = recentEpisodesDeferred.await()

                // Create categories for both
                val categories = listOf(
                    Category("Top Airing", topAiringAnimes.results.map { Item(it.title, it.image) }),
                    Category("Recent Episodes", recentEpisodes.results.map { Item(it.title, it.image) })
                )

                // Set the adapter for the RecyclerView with both categories
                parentRecyclerView.adapter = ParentAdapter(categories)

            } catch (e: Exception) {
                // Handle the error
            }
        }
    }
}





