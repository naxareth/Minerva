// MainActivity.kt
package com.example.minerva_10.views

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.minerva_10.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Splash screen
        Handler(Looper.getMainLooper()).postDelayed({
            // Navigate to LayoutActivity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, 3000) // 3 seconds delay


        }
    }

