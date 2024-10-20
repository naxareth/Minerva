package com.example.minerva_10.fragments

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
import com.example.minerva_10.adapter.AnimeParentAdapter
import com.example.minerva_10.api.RetrofitClient
import com.example.minerva_10.api.responses.Category
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var parentRecyclerView: RecyclerView
    private lateinit var adapter: AnimeParentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentRecyclerView = view.findViewById(R.id.parentRecyclerView)
        parentRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize adapter with an empty list
        adapter = AnimeParentAdapter(emptyList(), requireActivity()) { item ->
            val bundle = Bundle().apply {
                putString("anime_id", item.id) // Pass the item's ID
            }
            val animeInfoFragment = AnimeInfoFragment().apply {
                arguments = bundle
            }
            fragmentManager?.beginTransaction()?.replace(R.id.fragment_container, animeInfoFragment)?.commit()
        }
        parentRecyclerView.adapter = adapter

        // Load initial data
        loadMoreItems()
    }

    private fun loadMoreItems() {
        lifecycleScope.launch {
            try {
                val topAiringDeferred = async { RetrofitClient.animeApiService.getTopAiringAnimes(1) }
                val recentEpisodesDeferred = async { RetrofitClient.animeApiService.getRecentEpisodes(1) }

                val topAiringAnimes = topAiringDeferred.await()
                val recentEpisodes = recentEpisodesDeferred.await()

                Log.d("HomeFragment", "Top Airing: ${topAiringAnimes.results.size}, Recent Episodes: ${recentEpisodes.results.size}")

                // Create categories for both
                val categories = listOf(
                    Category("TOP AIRING", topAiringAnimes.results),
                    Category("RECENT EPISODES", recentEpisodes.results)
                )

                // Update the adapter with new categories
                adapter.updateCategories(categories)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}