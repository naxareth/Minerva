package com.example.minerva_10.fragments

import android.content.Intent
import android.os.Bundle
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
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private lateinit var tvName: TextView
    private lateinit var btLogout: Button

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
                val topAiringAnimes = RetrofitClient.animeApiService.getTopAiringAnimes(1).results
                val recentEpisodes = RetrofitClient.animeApiService.getRecentEpisodes(1).results

                val allAnimes = topAiringAnimes + recentEpisodes

                // Add all anime items to ViewModel
                sharedViewModel.setAnimeList(allAnimes.map { Item(it.title, it.image, it.id) })
                sharedViewModel.setTopAiringList(topAiringAnimes.map { Item(it.title, it.image, it.id) })
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val parentRecyclerView: RecyclerView = view.findViewById(R.id.parentRecyclerView)
            parentRecyclerView.layoutManager = LinearLayoutManager(requireContext())

            // Use Coroutines to fetch data from both endpoints
            lifecycleScope.launch {
                try {
                    // Fetch data from both endpoints concurrently
                    val topAiringDeferred = async { RetrofitClient.animeApiService.getTopAiringAnimes(1) }
                    val recentEpisodesDeferred = async { RetrofitClient.animeApiService.getRecentEpisodes(1) }

                    // Wait for both responses
                    val topAiringAnimes = topAiringDeferred.await()
                    val recentEpisodes = recentEpisodesDeferred.await()

                    // Create categories for both
                    val categories = listOf(
                        Category("TOP AIRING", topAiringAnimes.results.map { Item(it.title, it.image, it.id) }),
                        Category("RECENT EPISODES", recentEpisodes.results.map { Item(it.title, it.image, it.id) })
                    )

                    // Set the adapter for the RecyclerView
                    parentRecyclerView.adapter = AnimeParentAdapter(categories, requireActivity()) { item ->
                        // Create a bundle to pass the anime item
                        val bundle = Bundle()
                        bundle.putString("anime_id", item.id) // Pass the item's ID

                        // Navigate to the AnimeInfoFragment
                        val animeInfoFragment = AnimeInfoFragment()
                        animeInfoFragment.arguments = bundle
                        fragmentManager?.beginTransaction()?.replace(R.id.fragment_container, animeInfoFragment)?.commit()
                    }

                } catch (e: Exception) {
                    // Handle the error
                    e.printStackTrace()
                }
            }
        }
    }
}