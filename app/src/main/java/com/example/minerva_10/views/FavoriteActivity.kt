package com.example.minerva_10.views

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.minerva_10.R
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.minerva_10.FavoriteAdapter
import com.example.minerva_10.api.RetrofitClient
import com.example.minerva_10.api.responses.FavoriteResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FavoriteActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FavoriteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = FavoriteAdapter()
        recyclerView.adapter = adapter

        // Get the token from the previous activity
        val token = intent.getStringExtra("token")

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
                                    Toast.makeText(this@FavoriteActivity, "No favorites found", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                // Handle null favoriteResources
                                Toast.makeText(this@FavoriteActivity, "No favorites found", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        // Handle error response
                        Log.d("FavoriteActivity", "Error response: ${response.errorBody()}")
                    }
                }

                override fun onFailure(call: Call<FavoriteResponse>, t: Throwable) {
                    // Handle failure error
                    Log.d("FavoriteActivity", "Error: ${t.message}")
                }
            })
        }
    }
}