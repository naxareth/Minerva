package com.example.minerva_10.fragments

import ParentAdapter
import android.os.Bundle
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
                // Fetch data from both endpoints concurrently
                val topAiringDeferred = async { RetrofitClient.animeApiService.getTopAiringAnimes() }
                val recentEpisodesDeferred = async { RetrofitClient.animeApiService.getRecentEpisodes() }

                // Wait for both responses
                val topAiringAnimes = topAiringDeferred.await()
                val recentEpisodes = recentEpisodesDeferred.await()

                // Create categories for both
                val categories = listOf(
                    Category("Top Airing", topAiringAnimes.results.map { Item(it.title, it.image, it.id) }),
                    Category("Recent Episodes", recentEpisodes.results.map { Item(it.title, it.image, it.id) })
                )

                // Set the adapter for the RecyclerView
                parentRecyclerView.adapter = ParentAdapter(categories, requireActivity()) { item ->
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
}