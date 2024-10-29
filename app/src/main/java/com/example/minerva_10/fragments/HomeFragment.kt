package com.example.minerva_10.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.minerva_10.R
import com.example.minerva_10.adapter.AnimeParentAdapter
import com.example.minerva_10.api.RetrofitClient
import com.example.minerva_10.api.responses.Category
import com.example.minerva_10.api.responses.Item
import com.example.minerva_10.api.responses.ProfileResponse
import com.example.minerva_10.api.responses.LogoutResponse
import com.example.minerva_10.views.LoginActivity
import com.example.minerva_10.views.SharedViewModel
import com.example.minerva_10.views.AnimeInfoActivity
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private lateinit var tvName: TextView
    private lateinit var btLogout: Button
    private lateinit var loadingAnimation: View // Declare a variable for the loading animation

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvName = view.findViewById(R.id.tvName)
        btLogout = view.findViewById(R.id.btLogout)
        loadingAnimation = view.findViewById(R.id.loadingAnimation) // Initialize the loading animation

        // Get the token from shared preferences
        val sharedPreferences = activity?.getSharedPreferences("token_prefs", 0)
        val token = sharedPreferences?.getString("token", "")

        // Get the user data from API
        token?.let {
            RetrofitClient.api.profile("Bearer $it").enqueue(object : Callback<ProfileResponse> {
                override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                    if (response.isSuccessful) {
                        val profileResponse = response.body()
                        val userData = profileResponse?.data
                        // Display the user data
                        tvName.text = "Hello, ${userData?.name}"
                    } else {
                        // Handle error
                    }
                }

                override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                    // Handle error
                }
            })
        }

        // Logout button click listener
        btLogout.setOnClickListener {
            token?.let {
                // Logout API request
                RetrofitClient.api.logout("Bearer $it").enqueue(object : Callback<LogoutResponse> {
                    override fun onResponse(call: Call<LogoutResponse>, response: Response<LogoutResponse>) {
                        if (response.isSuccessful) {
                            // Clear the token from shared preferences
                            sharedPreferences?.edit()?.clear()?.apply()

                            // Navigate to the login activity
                            val intent = Intent(activity, LoginActivity::class.java)
                            startActivity(intent)
                            activity?.finish()
                        } else {
                            // Handle error
                        }
                    }

                    override fun onFailure(call: Call<LogoutResponse>, t: Throwable) {
                        // Handle error
                    }
                })
            } ?: run {
                // Handle error
            }
        }

        val sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        // Fetch anime data using coroutines
        lifecycleScope.launch {
            try {
                // Show the loading animation
                loadingAnimation.visibility = View.VISIBLE

                // Use async to fetch data from both endpoints concurrently
                val topAiringDeferred = async { RetrofitClient.animeApiService.getTopAiringAnimes(1) }
                val recentEpisodesDeferred = async { RetrofitClient.animeApiService.getRecentEpisodes(1) }

                // Wait for both responses
                val topAiringAnimes = topAiringDeferred.await()
                val recentEpisodes = recentEpisodesDeferred.await()

                // Create categories for both
                val categories = listOf(
                    Category("TOP AIRING", topAiringAnimes.results.map { Item(it.title, it.image, it.id, it.releaseDate, it.subOrDub) }),
                    Category("RECENT EPISODES", recentEpisodes.results.map { Item(it.title, it.image, it.id, it.releaseDate, it.subOrDub) })
                )

                // Set the adapter for the RecyclerView
                val parentRecyclerView: RecyclerView = view.findViewById(R.id.parentRecyclerView)
                parentRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                parentRecyclerView.adapter = AnimeParentAdapter(categories, requireActivity()) { item ->
                    // Create an Intent to navigate to AnimeInfoActivity
                    val intent = Intent(requireContext(), AnimeInfoActivity::class.java).apply {
                        putExtra("anime_id", item.id) // Pass the item's ID
                    }
                    startActivity(intent) // Start the AnimeInfoActivity
                }

                // Add a delay before hiding the loading animation
                Handler().postDelayed({
                    loadingAnimation.visibility = View.GONE
                }, 500) // Adjust the delay as needed

            } catch (e: Exception) {
                // Handle the error
                e.printStackTrace()
            }
        }
    }
}