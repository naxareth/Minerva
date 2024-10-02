package com.example.minerva_10

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.minerva_10.api.RetrofitClient
import com.example.minerva_10.api.responses.LoginRequest
import com.example.minerva_10.api.responses.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btLogin: Button
    private lateinit var btSignUp: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btLogin = findViewById(R.id.btLogin)
        btSignUp = findViewById(R.id.btSignUpNow)

        btLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            val loginRequest = LoginRequest(email, password)
            RetrofitClient.api.login(loginRequest).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        val token = loginResponse?.token
                        val message = loginResponse?.message

                        // Use the token and message variables to do something
                        Toast.makeText(this@LoginActivity, "Login successful: $message", Toast.LENGTH_SHORT).show()
                    } else {
                        // Handle the error
                        Toast.makeText(this@LoginActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    // Handle the error
                    Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
        btSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}