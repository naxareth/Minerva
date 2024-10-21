package com.example.minerva_10.fragments

import AnimeParentAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.minerva_10.R
import com.example.minerva_10.api.RetrofitClient
import com.example.minerva_10.api.responses.Category
import com.example.minerva_10.api.responses.Item
import com.example.minerva_10.views.SharedViewModel
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

        val sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        // Fetch anime data using coroutines as already done
        lifecycleScope.launch {
            try {
                val topAiringAnimes = RetrofitClient.animeApiService.getTopAiringAnimes().results
                val recentEpisodes = RetrofitClient.animeApiService.getRecentEpisodes().results

                val allAnimes = topAiringAnimes + recentEpisodes

                // Add all anime items to ViewModel
                sharedViewModel.setAnimeList(allAnimes.map { Item(it.title, it.image, it.id) })
                sharedViewModel.setTopAiringList(topAiringAnimes.map { Item(it.title, it.image, it.id) })
            } catch (e: Exception) {
                e.printStackTrace()
            }

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
                    Category("TOP AIRING", topAiringAnimes.results.map { Item(it.title, it.image, it.id) }),
                    Category("RECENT EPISODES", recentEpisodes.results.map { Item(it.title, it.image, it.id) })
                )

                // Set the adapter for the RecyclerView
                parentRecyclerView.adapter = AnimeParentAdapter(categories, requireActivity()) { item ->
                    // Create a bundle to pass the anime item
                    val bundle = Bundle()
                    bundle.putString("anime_id", item.id) // Pass the item's ID

                    // Navigate to the AnimeInfoFragment
                    val animeInfoFragment = AnimeInfoFragment()
                    animeInfoFragment.arguments = bundle
                    fragmentManager?.beginTransaction()?.replace(R.id.fragment_container, animeInfoFragment)?.commit()
                }

            } catch (e: Exception) {
                // Handle the error
                e.printStackTrace()
            }
        }
    }
}}