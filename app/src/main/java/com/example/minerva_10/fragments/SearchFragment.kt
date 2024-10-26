package com.example.minerva_10.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.minerva_10.R
import com.example.minerva_10.adapter.SearchAdapter
import com.example.minerva_10.api.RetrofitClient
import com.example.minerva_10.api.responses.SearchResult
import kotlinx.coroutines.launch

class SearchFragment : Fragment(), SearchAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var animeAdapter: SearchAdapter
    private lateinit var searchEditText: EditText
    private var animeList: MutableList<SearchResult> = mutableListOf()
    private var recommendedAnimeList: MutableList<SearchResult> = mutableListOf() // New list for recommended animes

    private var currentPage = 1
    private var hasNextPage = true
    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        searchEditText = view.findViewById(R.id.searchEditText)
        recyclerView = view.findViewById(R.id.recommendedAnimeRecyclerView)

        recyclerView.layoutManager = LinearLayoutManager(context)

        // Create the adapter and set the listener
        animeAdapter = SearchAdapter(animeList, this)
        recyclerView.adapter = animeAdapter

        setupSearch()

        // Load recommended anime initially (from Top Airing)
        loadRecommendedAnime()

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && hasNextPage) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                        fetchPaginatedSearchResults(searchEditText.text.toString())
                    }
                }
            }
        })

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
                } else {
                    // If the query is empty, reset to recommended animes
                    animeList.clear()
                    animeList.addAll(recommendedAnimeList) // Reset to recommended animes
                    animeAdapter.notifyDataSetChanged()
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun loadRecommendedAnime() {
        lifecycleScope.launch {
            try {
                val topAiringResults = RetrofitClient.animeApiService.getTopAiringAnimes(currentPage)
                recommendedAnimeList = topAiringResults.results.map {
                    SearchResult(
                        id = it.id,
                        title = it.title,
                        image = it.image,
                        releaseDate = it.releaseDate ?: "Unknown",
                        subOrDub = it.subOrDub ?: "Unknown"
                    )
                }.toMutableList() // Store in the new list
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
                // Call the searchAnime API for search results
                val searchResults = RetrofitClient.animeApiService.searchAnime(query, currentPage)

                // Add the fetched results to the animeList
                animeList.addAll(searchResults.results)
                animeAdapter.notifyDataSetChanged()

                // Update pagination data
                hasNextPage = searchResults.hasNextPage
                currentPage++

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    // Handle the click event
    override fun onItemClick(anime: SearchResult) {
        // Create a new instance of AnimeInfoFragment
        val animeInfoFragment = AnimeInfoFragment()
        val bundle = Bundle().apply {
            putString("anime_id", anime.id.toString()) // Pass the anime ID
        }
        animeInfoFragment.arguments = bundle

        // Replace the current fragment with AnimeInfoFragment
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, animeInfoFragment) // Use the correct container ID
            .addToBackStack(null)
            .commit()
    }
}