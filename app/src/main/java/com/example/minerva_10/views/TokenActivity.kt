package com.example.minerva_10.views

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.minerva_10.R
import com.example.minerva_10.LoginActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.minerva_10.FavoriteAdapter
import com.example.minerva_10.api.RetrofitClient
import com.example.minerva_10.api.responses.FavoriteResource
import com.example.minerva_10.api.responses.FavoriteResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TokenActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FavoriteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_token)

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
                    Log.d("TokenActivity", "Response code: ${response.code()}")
                    Log.d("TokenActivity", "Response message: ${response.message()}")
                    if (response.isSuccessful) {
                        val favoriteResponse = response.body()
                        if (favoriteResponse != null) {
                            val favoriteResources = favoriteResponse.data
                            if (favoriteResources != null) {
                                Log.d("TokenActivity", "Received ${favoriteResources.size} favorite resources")
                                Log.d("TokenActivity", "Favorite resources: $favoriteResources")
                                if (favoriteResources.isNotEmpty()) {
                                    adapter.submitList(favoriteResources)
                                } else {
                                    // Handle empty list
                                    Toast.makeText(this@TokenActivity, "No favorites found", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                // Handle null favoriteResources
                                Toast.makeText(this@TokenActivity, "No favorites found", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        // Handle error response
                        Log.d("TokenActivity", "Error response: ${response.errorBody()}")
                    }
                }

                override fun onFailure(call: Call<FavoriteResponse>, t: Throwable) {
                    // Handle failure error
                    Log.d("TokenActivity", "Error: ${t.message}")
                }
            })
        }
    }
}