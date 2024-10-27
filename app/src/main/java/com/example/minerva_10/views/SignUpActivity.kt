package com.example.minerva_10.views

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.minerva_10.R
import com.example.minerva_10.api.RetrofitClient
import com.example.minerva_10.api.responses.RegisterRequest
import com.example.minerva_10.api.responses.RegisterResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {
    private lateinit var etSignEmail: EditText
    private lateinit var etSignName: EditText
    private lateinit var etSignPass: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btSignRegister: Button
    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        etSignEmail = findViewById(R.id.etSignEmail)
        etSignName = findViewById(R.id.etSignName)
        etSignPass = findViewById(R.id.etSignPass)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btSignRegister = findViewById(R.id.btSignRegister)
        backButton = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            finish()
        }

        btSignRegister.setOnClickListener {
            val email = etSignEmail.text.toString()
            val name = etSignName.text.toString()
            val password = etSignPass.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            if (!isValidInput(email, password) || name.isEmpty() || confirmPassword.isEmpty()) {
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val registerRequest = RegisterRequest(name, email, password, confirmPassword)
            RetrofitClient.api.register(registerRequest).enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                    if (response.isSuccessful) {
                        val registerResponse = response.body()
                        val token = registerResponse?.token
                        val message = registerResponse?.message

                        val sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("token", token)
                        editor.apply()

                        Toast.makeText(this@SignUpActivity, "Register successful: $message", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        when (response.code()) {
                            422 -> {
                                Toast.makeText(this@SignUpActivity, "Account already exists", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                try {
                                    val errorResponse = response.errorBody()?.string()
                                    val jsonObject = JSONObject(errorResponse)
                                    val message = jsonObject.getString("message")
                                    Toast.makeText(this@SignUpActivity, "Error: $message", Toast.LENGTH_SHORT).show()
                                } catch (e: Exception) {
                                    Toast.makeText(this@SignUpActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    Toast.makeText(this@SignUpActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun isValidInput(email: String, password: String): Boolean {
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
}