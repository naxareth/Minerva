package com.example.minerva_10.fragments

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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoriteFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FavoriteParentAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(context, 1)

        adapter = FavoriteParentAdapter(emptyList(), requireActivity()) { favoriteResource ->
            val bundle = Bundle()
            bundle.putString("anime_id", favoriteResource.anime_id)
            val animeInfoFragment = AnimeInfoFragment()
            animeInfoFragment.arguments = bundle
            fragmentManager?.beginTransaction()?.replace(R.id.fragment_container, animeInfoFragment)?.commit()
        }

        // Get the token from the previous activity
        val token = arguments?.getString("token")

        if (token != null) {
            RetrofitClient.api.getFavorites("Bearer $token").enqueue(object : Callback<FavoriteResponse> {
                override fun onResponse(call: Call<FavoriteResponse>, response: Response<FavoriteResponse>) {
                    if (response.isSuccessful) {
                        val favoriteResponse = response.body()
                        if (favoriteResponse != null && favoriteResponse.data.isNotEmpty()) {
                            adapter.favoriteResources = favoriteResponse.data
                            adapter.notifyDataSetChanged()
                        } else {
                            Toast.makeText(context, "No favorites found", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Failed to fetch favorites", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<FavoriteResponse>, t: Throwable) {
                    Toast.makeText(context, "Failed to fetch favorites", Toast.LENGTH_SHORT).show()
                }
            })
        }

        recyclerView.adapter = adapter

        return view
    }
}