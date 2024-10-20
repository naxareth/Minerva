package com.example.minerva_10.fragments

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.minerva_10.R
import com.example.minerva_10.adapter.AnimeAdapter2
import com.example.minerva_10.api.RetrofitClient
import com.example.minerva_10.api.responses.Search
import com.example.minerva_10.api.responses.SearchResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var animeAdapter: AnimeAdapter2
    private lateinit var searchEditText: EditText
    private var animeList: MutableList<SearchResult> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        // Initialize views
        searchEditText = view.findViewById(R.id.searchEditText)
        recyclerView = view.findViewById(R.id.recommendedAnimeRecyclerView)

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        animeAdapter = AnimeAdapter2(animeList)
        recyclerView.adapter = animeAdapter

        // Call API when user enters a search term
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = searchEditText.text.toString()
                if (query.isNotBlank()) {
                    searchAnime(query)
                } else {
                    animeList.clear()
                    animeAdapter.notifyDataSetChanged()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        return view
    }

    private fun searchAnime(query: String) {
        RetrofitClient.animeApiService.searchAnime(query).enqueue(object : Callback<Search> {
            override fun onResponse(call: Call<Search>, response: Response<Search>) {
                if (response.isSuccessful) {
                    val results = response.body()?.results ?: emptyList()
                    animeList.clear()
                    animeList.addAll(results)
                    animeAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(context, "Failed to retrieve data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Search>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}