package com.example.minerva_10.views

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.minerva_10.R
import com.example.minerva_10.api.RetrofitClient
import com.example.minerva_10.api.responses.LoginRequest
import com.example.minerva_10.api.responses.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

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

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val loginRequest = LoginRequest(email, password)
            login(loginRequest)
        }

        btSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    // ...

    private fun login(loginRequest: LoginRequest) {
        RetrofitClient.api.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    val token = loginResponse?.token
                    val message = loginResponse?.message

                    if (token != null && message != null) {
                        // Store the token securely
                        // ...

                        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                        intent.putExtra("token", token)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@LoginActivity, "Invalid response", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    handleErrorResponse(response.code())
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                handleFailureError(t)
            }
        })
    }

    private fun handleErrorResponse(code: Int) {
        when (code) {
            404 -> {
                Toast.makeText(this, "Account not found", Toast.LENGTH_SHORT).show()
            }
            401 -> {
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
            }
            422 -> {
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "Error: $code", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleFailureError(t: Throwable) {
        if (t is IOException) {
            Toast.makeText(this, "Account not found", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
        }
    }
}