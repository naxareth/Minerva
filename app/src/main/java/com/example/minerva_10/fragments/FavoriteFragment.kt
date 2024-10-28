package com.example.minerva_10.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.minerva_10.R
import com.example.minerva_10.adapter.FavoriteParentAdapter
import com.example.minerva_10.api.RetrofitClient
import com.example.minerva_10.api.responses.FavoriteResponse
import com.example.minerva_10.views.AnimeInfoActivity // Import the AnimeInfoActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoriteFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FavoriteParentAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(context, 1) // Set column count to 1

        // Initialize Adapter
        adapter = FavoriteParentAdapter(emptyList(), requireActivity()) { favoriteResource ->
            // Create an Intent to navigate to the AnimeInfoActivity
            val intent = Intent(requireContext(), AnimeInfoActivity::class.java).apply {
                putExtra("anime_id", favoriteResource.anime_id) // Pass the item's ID
            }
            startActivity(intent) // Start the AnimeInfoActivity
        }

        // Set the adapter to the RecyclerView
        recyclerView.adapter = adapter

        // Get the token from the arguments
        val token = arguments?.getString("token")

        // Fetch favorites if token is available
        if (token != null) {
            fetchFavorites(token)
        } else {
            Toast.makeText(context, "Token is missing", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun fetchFavorites(token: String) {
        RetrofitClient.api.getFavorites("Bearer $token").enqueue(object : Callback<FavoriteResponse> {
            override fun onResponse(call: Call<FavoriteResponse>, response: Response<FavoriteResponse>) {
                if (response.isSuccessful) {
                    val favoriteResponse = response.body()
                    favoriteResponse?.data?.let { favoriteResources ->
                        if (favoriteResources.isNotEmpty()) {
                            adapter.favoriteResources = favoriteResources
                            adapter.notifyDataSetChanged()
                        } else {
                            Toast.makeText(context, "No favorites found", Toast.LENGTH_SHORT).show()
                        }
                    } ?: run {
                        Toast.makeText(context, "No favorites found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Failed to fetch favorites: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<FavoriteResponse>, t: Throwable) {
                Toast.makeText(context, "Failed to fetch favorites: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("FavoriteFragment", "Error fetching favorites", t)
            }
        })
    }
}