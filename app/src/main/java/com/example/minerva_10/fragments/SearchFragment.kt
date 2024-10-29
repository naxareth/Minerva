package com.example.minerva_10.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.minerva_10.R
import com.example.minerva_10.adapter.SearchAdapter
import com.example.minerva_10.api.RetrofitClient
import com.example.minerva_10.api.responses.SearchResult
import com.example.minerva_10.views.AnimeInfoActivity // Import the AnimeInfoActivity
import kotlinx.coroutines.launch
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.widget.TextView

class SearchFragment : Fragment(), SearchAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var animeAdapter: SearchAdapter
    private lateinit var searchEditText: EditText
    private var animeList: MutableList<SearchResult> = mutableListOf()
    private var recommendedAnimeList: MutableList<SearchResult> = mutableListOf() // New list for recommended animes

    private var currentPage = 1
    private var hasNextPage = true
    private var isLoading = false

    private lateinit var recommendedTitleTextView: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        searchEditText = view.findViewById(R.id.searchEditText)
        recyclerView = view.findViewById(R.id.recommendedAnimeRecyclerView)
        recommendedTitleTextView = view.findViewById(R.id.recommendedTitleTextView)

        recyclerView?.layoutManager = LinearLayoutManager(context)

        // Create the adapter and set the listener
        animeAdapter = SearchAdapter(animeList, this)
        recyclerView?.adapter = animeAdapter

        setupSearch()
        setupSearchActionListener() // Add this to listen to the Search action on the keyboard

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

    private val searchHandler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    private fun setupSearch() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            private var searchQuery = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().lowercase().trim()

                recommendedTitleTextView.visibility = if (query.isNotEmpty()) View.GONE else View.VISIBLE

                // Show the clear icon when there is text, else show the search icon
                val icon = if (query.isNotEmpty()) R.drawable.clear_24px else R.drawable.search_24px
                searchEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, icon, 0)

                // Handle search execution with delay
                searchRunnable?.let { searchHandler.removeCallbacks(it) }
                searchRunnable = Runnable {
                    if (query != searchQuery) {
                        searchQuery = query
                        currentPage = 1
                        animeList.clear()
                        animeAdapter.notifyDataSetChanged()

                        if (query.isNotBlank()) {
                            fetchPaginatedSearchResults(query)
                        } else {
                            animeList.clear()
                            animeList.addAll(recommendedAnimeList)
                            animeAdapter.notifyDataSetChanged()
                        }
                    }
                }
                searchHandler.postDelayed(searchRunnable!!, 500)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Handle the clear button click by checking if it's the clear icon, then clearing text
        searchEditText.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val clearIcon = R.drawable.clear_24px
                val drawableEnd = searchEditText.compoundDrawables[2]

                if (drawableEnd != null && event.rawX >= (searchEditText.right - drawableEnd.bounds.width())) {
                    // Clear text if clear icon is clicked
                    searchEditText.text.clear()
                    currentPage = 1
                    animeList.clear()
                    animeAdapter.notifyDataSetChanged()

                    animeList.addAll(recommendedAnimeList) // Reload recommended animes
                    animeAdapter.notifyDataSetChanged()
                    true
                } else {
                    false
                }
            } else {
                false
            }
        }
    }




    private fun loadRecommendedAnime() {
        lifecycleScope.launch {
            try {
                // Use the new endpoint to get AnimeResponse
                val topAiringResults = RetrofitClient.animeApiService.getTopAiringAnimesResponse(currentPage)
                recommendedAnimeList = topAiringResults.results.map {
                    SearchResult( //please fix this, AnimeResponse does not call the <List> SearchResult from my data class at all, should be Result
                        id = it.id,
                        title = it.title,
                        image = it.image,
                        releaseDate = it.releaseDate ?: "Unknown Release Date", // Default to a more informative string
                        subOrDub = it.subOrDub ?: "Unknown" // Default to a more informative string
                    )
                }.toMutableList() // Store in the new list

                // Clear the animeList before adding recommended animes
                animeList.clear()
                animeList.addAll(recommendedAnimeList)
                animeAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun fetchPaginatedSearchResults(query: String) {
        if (isLoading || !hasNextPage) return  // Prevents concurrent modifications
        isLoading = true

        lifecycleScope.launch {
            try {
                val searchResults = RetrofitClient.animeApiService.searchAnime(query, currentPage)
                animeList.addAll(searchResults.results) // Add results
                animeAdapter.notifyDataSetChanged() // Refresh adapter
                hasNextPage = searchResults.hasNextPage
                if (!hasNextPage) {
                    recyclerView.clearOnScrollListeners() // Stop further pagination calls
                }
                currentPage++

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false // Reset loading status
            }
        }
    }



    // Handle the click event
    override fun onItemClick(anime: SearchResult) {
        // Create an Intent to navigate to AnimeInfoActivity
        val intent = Intent(requireContext(), AnimeInfoActivity::class.java).apply {
            putExtra("anime_id", anime.id.toString()) // Pass the anime ID
        }
        startActivity(intent) // Start the AnimeInfoActivity
    }

    private fun setupSearchActionListener() {
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = searchEditText.text.toString().lowercase().trim()
                if (query.isNotBlank()) {
                    currentPage = 1  // Reset the page for new search
                    animeList.clear()  // Clear the existing results
                    animeAdapter.notifyDataSetChanged()  // Notify adapter to refresh view
                    fetchPaginatedSearchResults(query)  // Call search function with query
                }
                true  // Return true to indicate the search action was handled
            } else {
                false  // If the action isn't search, ignore it
            }
        }
    }

}