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
import com.example.minerva_10.adapter.PageAdapter
import com.example.minerva_10.api.RetrofitClient
import com.example.minerva_10.api.responses.Category
import com.example.minerva_10.api.responses.Item
import kotlinx.coroutines.async
import com.example.minerva_10.api.responses.AnimeResponse
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var currentPage = 1
    private var totalPages =
        1 // Initialize to 1, will update with actual total pages from the response.
    private lateinit var parentRecyclerView: RecyclerView
    private lateinit var pageRecyclerView: RecyclerView
    private lateinit var parentAdapter: ParentAdapter


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
        pageRecyclerView = view.findViewById(R.id.pageRecyclerView)
        parentRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        pageRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        // Load the first page
        loadPage(currentPage)


        // Use Coroutines to fetch data from both endpoints
        lifecycleScope.launch {
            try {
                Log.d("HomeFragment", "Starting to fetch anime data...")

                // Fetch data from both endpoints concurrently
                val startTime = System.currentTimeMillis()

                val topAiringDeferred = async {
                    Log.d("HomeFragment", "Fetching top airing animes...")
                    RetrofitClient.animeApiService.getTopAiringAnimes(currentPage)
                }
                val recentEpisodesDeferred = async {
                    Log.d("HomeFragment", "Fetching recent episodes...")
                    RetrofitClient.animeApiService.getRecentEpisodes(currentPage)
                }

                // Wait for both responses
                val topAiringAnimes = topAiringDeferred.await()
                Log.d(
                    "HomeFragment",
                    "Fetched top airing animes: ${topAiringAnimes.results.size} results"
                )

                val recentEpisodes = recentEpisodesDeferred.await()
                Log.d(
                    "HomeFragment",
                    "Fetched recent episodes: ${recentEpisodes.results.size} results"
                )

                val endTime = System.currentTimeMillis()
                Log.d("HomeFragment", "Fetching completed in ${endTime - startTime} ms")

                // Create categories for both
                val categories = listOf(
                    Category(
                        "Top Airing",
                        topAiringAnimes.results.map { Item(it.title, it.image) }),
                    Category(
                        "Recent Episodes",
                        recentEpisodes.results.map { Item(it.title, it.image) })
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

    private fun loadPage(page: Int) {
        lifecycleScope.launch {
            try {
                // Fetch data from both the top-airing and recent-episodes endpoints
                val topAiringResponse: AnimeResponse =
                    RetrofitClient.animeApiService.getTopAiringAnimes(page)
                val recentEpisodesResponse: AnimeResponse =
                    RetrofitClient.animeApiService.getRecentEpisodes(page)

                // Map the API results to your Category and Item model
                val categories = listOf(
                    Category(
                        "Top Airing",
                        topAiringResponse.results.map { Item(it.title, it.image) }),
                    Category(
                        "Recent Episodes",
                        recentEpisodesResponse.results.map { Item(it.title, it.image) })
                )

                // Set the adapter for the parent RecyclerView with the fetched categories
                parentAdapter = ParentAdapter(categories, requireActivity())
                parentRecyclerView.adapter = parentAdapter

                // Set the total pages based on the API response (increment if more data exists)
                if (topAiringResponse.hasNextPage || recentEpisodesResponse.hasNextPage) {
                    totalPages = page + 1 // Adjust based on your API's pagination logic
                }

                // Set the PageAdapter to display page numbers
                pageRecyclerView.adapter = PageAdapter(totalPages) { selectedPage ->
                    loadPage(selectedPage) // Trigger page load on number click
                }

            } catch (e: Exception) {
                // Handle API error
                e.printStackTrace()
            }
        }
    }
}
