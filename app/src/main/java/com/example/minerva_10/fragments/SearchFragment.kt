package com.example.minerva_10.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.minerva_10.R
import com.example.minerva_10.adapter.AnimeAdapter2
import com.example.minerva_10.api.RetrofitClient
import com.example.minerva_10.api.responses.SearchResult
import com.example.minerva_10.views.SharedViewModel
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var animeAdapter: AnimeAdapter2
    private lateinit var searchEditText: EditText
    private lateinit var recommendedTitleTextView: TextView  // Add a TextView for "RECOMMENDED"
    private var animeList: MutableList<SearchResult> = mutableListOf()

    private var currentPage = 1
    private var hasNextPage = true
    private var isLoading = false

    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        searchEditText = view.findViewById(R.id.searchEditText)
        recyclerView = view.findViewById(R.id.recommendedAnimeRecyclerView)

        recyclerView.layoutManager = LinearLayoutManager(context)
        animeAdapter = AnimeAdapter2(animeList)
        recyclerView.adapter = animeAdapter

        recyclerView.adapter = animeAdapter

        setupSearch()

        // Load recommended anime on fragment creation
        loadRecommendedAnime()

// Add scroll listener for pagination
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && hasNextPage) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                        // Load next page
                        fetchPaginatedSearchResults(searchEditText.text.toString())
                    }
                }
            }
        })


        setupSearch()

        return view
    }

    private fun setupSearch() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().lowercase().trim()
                if (query.isNotBlank()) {
                    currentPage = 1
                    animeList.clear()
                    fetchPaginatedSearchResults(query)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun loadRecommendedAnime() {
        lifecycleScope.launch {
            try {
                val topAiringResults = RetrofitClient.animeApiService.getTopAiringAnimes(currentPage)
                val recommendedAnimeList = topAiringResults.results.map {
                    SearchResult(
                        id = it.id,
                        image = it.image,
                        releaseDate = it.releaseDate ?: "Unknown",
                        subOrDub = it.subOrDub ?: "Unknown",
                        title = it.title
                    )
                }
                animeList.addAll(recommendedAnimeList)
                animeAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun fetchPaginatedSearchResults(query: String) {
        if (isLoading || !hasNextPage) return
        isLoading = true

        lifecycleScope.launch {
            try {
                // Fetch paginated data from both categories
                val topAiringResults = RetrofitClient.animeApiService.getTopAiringAnimes(currentPage)
                val recentEpisodesResults = RetrofitClient.animeApiService.getRecentEpisodes(currentPage)

                // Filter and map top airing results
                val filteredTopAiring = topAiringResults.results.filter { it.title.contains(query, ignoreCase = true) }
                val filteredRecentEpisodes = recentEpisodesResults.results.filter { it.title.contains(query, ignoreCase = true) }

                // Map filtered results to SearchResult objects with release date and subOrDub
                val combinedResults = (filteredTopAiring + filteredRecentEpisodes).map {
                    SearchResult(
                        id = it.id,
                        image = it.image,  // Assuming 'it.image' contains the URL for the anime image
                        releaseDate = it.releaseDate ?: "Unknown",  // Provide a fallback if releaseDate is null
                        subOrDub = it.subOrDub ?: "Unknown",  // Provide a fallback if subOrDub is null
                        title = it.title
                    )
                }

                animeList.addAll(combinedResults)
                animeAdapter.notifyDataSetChanged()

                // Determine if there are more pages to load
                hasNextPage = topAiringResults.hasNextPage && recentEpisodesResults.hasNextPage
                currentPage++

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }



}
