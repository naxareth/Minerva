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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.minerva_10.R
import com.example.minerva_10.adapter.AnimeAdapter2
import com.example.minerva_10.api.responses.SearchResult
import com.example.minerva_10.views.SharedViewModel

class SearchFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var animeAdapter: AnimeAdapter2
    private lateinit var searchEditText: EditText
    private lateinit var recommendedLabel: TextView // Reference to the "RECOMMENDED" TextView
    private var animeList: MutableList<SearchResult> = mutableListOf()

    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        // Initialize views
        searchEditText = view.findViewById(R.id.searchEditText)
        recyclerView = view.findViewById(R.id.recommendedAnimeRecyclerView)
        recommendedLabel = view.findViewById(R.id.recommendedLabel) // Initialize the "RECOMMENDED" label

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        animeAdapter = AnimeAdapter2(animeList)
        recyclerView.adapter = animeAdapter

        // Observe the anime list from ViewModel (this will handle your top airing anime)
        sharedViewModel.animeList.observe(viewLifecycleOwner) { topAiringAnimeList ->
            if (topAiringAnimeList.isNotEmpty()) {
                // Initially show top airing anime as "RECOMMENDED"
                val recommendedAnime = topAiringAnimeList.map { item ->
                    SearchResult(
                        title = item.title,
                        image = item.image,
                        id = item.id,
                        releaseDate = "", // Set release date if available
                        subOrDub = "" // Set sub/dub if available
                    )
                }

                animeList.clear()
                animeList.addAll(recommendedAnime)
                animeAdapter.notifyDataSetChanged()

                // Show the "RECOMMENDED" label and anime list
                recommendedLabel.visibility = View.VISIBLE
            } else {
                // Hide the "RECOMMENDED" label if no data
                recommendedLabel.visibility = View.GONE
            }
        }

        // Set up search functionality
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().lowercase().trim()
                if (query.isNotBlank()) {
                    // Filter anime list based on search query
                    val filteredList = sharedViewModel.animeList.value?.filter {
                        it.title.lowercase().contains(query)
                    } ?: emptyList()

                    // Convert filtered items to SearchResult objects
                    val searchResults = filteredList.map { item ->
                        SearchResult(
                            title = item.title,
                            image = item.image,
                            id = item.id,
                            releaseDate = "", // Set release date if available
                            subOrDub = "" // Set sub/dub if available
                        )
                    }

                    // Update the list and hide "RECOMMENDED" label when searching
                    animeList.clear()
                    animeList.addAll(searchResults)
                    animeAdapter.notifyDataSetChanged()

                    recommendedLabel.visibility = View.GONE
                } else {
                    // Restore "RECOMMENDED" when query is empty
                    val topAiringAnime = sharedViewModel.animeList.value?.map { item ->
                        SearchResult(
                            title = item.title,
                            image = item.image,
                            id = item.id,
                            releaseDate = "",
                            subOrDub = ""
                        )
                    } ?: emptyList()

                    animeList.clear()
                    animeList.addAll(topAiringAnime)
                    animeAdapter.notifyDataSetChanged()

                    recommendedLabel.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        return view
    }
}
