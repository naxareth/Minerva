package com.example.minerva_10.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.minerva_10.R
import com.example.minerva_10.views.DownloadActivity
import com.example.minerva_10.fragments.FavoriteFragment
import com.example.minerva_10.fragments.HomeFragment
import com.example.minerva_10.fragments.SearchFragment
import com.example.minerva_10.viewmodels.SharedAnimeViewModel

class HomeActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var sharedViewModel: SharedAnimeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize the Shared ViewModel
        sharedViewModel = ViewModelProvider(this).get(SharedAnimeViewModel::class.java)

        // Observe download items
        sharedViewModel.downloadItems.observe(this, Observer { items ->
            Log.d("HomeActivity", "Observed download items: $items")
            // Here you can update the UI or notify the user about changes
            // For example, you might want to show a notification or update a badge count
        })

        bottomNavigationView = findViewById(R.id.bottom_nav_bar)

        // Check if fragment is already added to avoid re-adding on rotation.
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .addToBackStack(null)
                .commit()
        }

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, HomeFragment())
                        .addToBackStack(null)
                        .commit()
                    true
                }
                R.id.search -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, SearchFragment())
                        .addToBackStack(null)
                        .commit()
                    true
                }
                R.id.favorites -> {
                    val token = intent.getStringExtra("token")
                    val favoriteFragment = FavoriteFragment()
                    val bundle = Bundle()
                    bundle.putString("token", token)
                    favoriteFragment.arguments = bundle
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, favoriteFragment)
                        .addToBackStack(null)
                        .commit()
                    true
                }
                R.id.downloads -> {
                    // Start DownloadActivity
                    val intent = Intent(this, DownloadActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}