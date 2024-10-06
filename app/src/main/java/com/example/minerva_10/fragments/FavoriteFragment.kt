package com.example.minerva_10.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.minerva_10.R
import com.example.minerva_10.FavoriteAdapter
import com.example.minerva_10.api.RetrofitClient
import com.example.minerva_10.api.responses.FavoriteResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoriteFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FavoriteAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = FavoriteAdapter()
        recyclerView.adapter = adapter

        // Get the token from the previous activity
        val token = arguments?.getString("token")

        if (token != null) {
            // Use the token to fetch data from the API
            RetrofitClient.api.getFavorites("Bearer $token").enqueue(object : Callback<FavoriteResponse> {
                override fun onResponse(call: Call<FavoriteResponse>, response: Response<FavoriteResponse>) {
                    if (response.isSuccessful) {
                        val favoriteResponse = response.body()
                        if (favoriteResponse != null) {
                            val favoriteResources = favoriteResponse.data
                            if (favoriteResources != null) {
                                if (favoriteResources.isNotEmpty()) {
                                    adapter.submitList(favoriteResources)
                                } else {
                                    // Handle empty list
                                    Toast.makeText(context, "No favorites found", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                // Handle null favoriteResources
                                Toast.makeText(context, "No favorites found", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        // Handle error response
                        Log.d("FavoriteFragment", "Error response: ${response.errorBody()}")
                    }
                }

                override fun onFailure(call: Call<FavoriteResponse>, t: Throwable) {
                    // Handle failure error
                    Log.d("FavoriteFragment", "Error: ${t.message}")
                }
            })
        }

        return view
    }
}