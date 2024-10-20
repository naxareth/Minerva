package com.example.minerva_10.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.minerva_10.fragments.HomeFragment
import com.example.minerva_10.R
import com.example.minerva_10.fragments.FavoriteFragment
import com.example.minerva_10.fragments.SearchFragment
import com.example.minerva_10.fragments.SettingsFragment

class HomeActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

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
                R.id.settings -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, SettingsFragment())
                        .addToBackStack(null)
                        .commit()
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