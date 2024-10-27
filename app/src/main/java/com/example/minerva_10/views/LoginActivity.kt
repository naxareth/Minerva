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
    private lateinit var btForgotPassword: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val sharedPreferences = getSharedPreferences("token_prefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        if (token != null) {
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("token", token)
            startActivity(intent)
            finish()
        } else {
            etEmail = findViewById(R.id.etEmail)
            etPassword = findViewById(R.id.etPassword)
            btLogin = findViewById(R.id.btLogin)
            btSignUp = findViewById(R.id.btSignUpNow)
            btForgotPassword = findViewById(R.id.forgotPassword)

            btLogin.setOnClickListener {
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()

                if (!isValidLoginInput(email, password)) {
                    return@setOnClickListener
                }

                val loginRequest = LoginRequest(email, password)
                login(loginRequest)
            }

            btSignUp.setOnClickListener {
                val intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
            }

            btForgotPassword.setOnClickListener {
                val intent = Intent(this, OtpActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun login(loginRequest: LoginRequest) {
        RetrofitClient.api.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    val token = loginResponse?.token
                    val userId = loginResponse?.userId
                    val message = loginResponse?.message

                    if (token != null && message != null && userId != null) {
                        val sharedPreferences = getSharedPreferences("token_prefs", MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("token", token)
                        editor.putInt("user_id", userId)
                        editor.apply()

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

    private fun isValidLoginInput(email: String, password: String): Boolean {
        val emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$".toRegex()
        val passwordRegex = "^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]*$".toRegex() // Allow specific special characters

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Fields cannot be empty or contain only spaces", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!emailRegex.matches(email)) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!passwordRegex.matches(password)) {
            Toast.makeText(this, "Password can only contain letters, numbers, and specific special characters", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
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