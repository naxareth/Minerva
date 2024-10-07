package com.example.minerva_10.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.minerva_10.R
import com.example.minerva_10.api.RetrofitClient
import com.example.minerva_10.api.responses.LogoutRequest
import com.example.minerva_10.api.responses.LogoutResponse
import com.example.minerva_10.api.responses.ProfileResponse
import com.example.minerva_10.api.responses.User
import com.example.minerva_10.views.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingsFragment : Fragment() {

    private lateinit var tvName: TextView
    private lateinit var btLogout: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvName = view.findViewById(R.id.tvName)
        btLogout = view.findViewById(R.id.btLogout)

        // Get the token from shared preferences
        val sharedPreferences = activity?.getSharedPreferences("token_prefs", 0)
        val token = sharedPreferences?.getString("token", "")

        // Get the user data from API
        RetrofitClient.api.profile("Bearer $token").enqueue(object : Callback<ProfileResponse> {
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

        // Logout button click listener
        btLogout.setOnClickListener {
            // Get the token from shared preferences
            val sharedPreferences = activity?.getSharedPreferences("token_prefs", 0)
            val token = sharedPreferences?.getString("token", "")

            if (token != null) {
                // Logout API request
                RetrofitClient.api.logout("Bearer $token").enqueue(object : Callback<LogoutResponse> {
                    override fun onResponse(call: Call<LogoutResponse>, response: Response<LogoutResponse>) {
                        if (response.isSuccessful) {
                            // Clear the token from shared preferences
                            val editor = sharedPreferences?.edit()
                            editor?.clear()
                            editor?.apply()

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
            } else {
                // Handle error
            }
        }
    }
}