package com.example.minerva_10.views

import android.content.Intent
import android.os.Bundle
import android.util.Log // Import Log
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        etSignEmail = findViewById(R.id.etSignEmail)
        etSignName = findViewById(R.id.etSignName)
        etSignPass = findViewById(R.id.etSignPass)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btSignRegister = findViewById(R.id.btSignRegister)

        btSignRegister.setOnClickListener {
            val email = etSignEmail.text.toString()
            val name = etSignName.text.toString()
            val password = etSignPass.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            Log.d("SignUpActivity", "Button clicked. Inputs: email=$email, name=$name, password=$password, confirmPassword=$confirmPassword")

            if (email.isEmpty() || name.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Log.d("SignUpActivity", "Validation failed: Empty fields")
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 8) {
                Log.d("SignUpActivity", "Validation failed: Password too short")
                Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Log.d("SignUpActivity", "Validation failed: Passwords do not match")
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val registerRequest = RegisterRequest(name, email, password, confirmPassword)
            Log.d("SignUpActivity", "Sending register request with data: $registerRequest")

            RetrofitClient.api.register(registerRequest).enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                    if (response.isSuccessful) {
                        val registerResponse = response.body()
                        val token = registerResponse?.token
                        val message = registerResponse?.message
                        Log.d("SignUpActivity", "Register successful: token=$token, message=$message")

                        // Store the token in shared preferences
                        val sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("token", token)
                        editor.apply()

                        Toast.makeText(this@SignUpActivity, "Register successful: $message", Toast.LENGTH_SHORT).show()

                        // Navigate back to LoginActivity
                        val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Log.d("SignUpActivity", "Register failed: Code=${response.code()}")

                        // Get the error body as a string
                        val errorBody = response.errorBody()?.string()

                        // Check if the response is HTML (likely a server error page)
                        if (errorBody?.startsWith("<!DOCTYPE") == true) {
                            Log.d("SignUpActivity", "HTML response received instead of JSON")
                            Toast.makeText(this@SignUpActivity, "Server Error: Please try again later.", Toast.LENGTH_SHORT).show()
                        } else {
                            try {
                                // Parse the error body as JSON if it's not HTML
                                val jsonObject = JSONObject(errorBody)
                                val message = jsonObject.getString("message")
                                Log.d("SignUpActivity", "Error response from server: $message")
                                Toast.makeText(this@SignUpActivity, "Error: $message", Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                // Handle parsing exceptions or other unexpected errors
                                Log.d("SignUpActivity", "Exception: ${e.message}")
                                Toast.makeText(this@SignUpActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    Log.d("SignUpActivity", "Request failed: ${t.message}")
                    // Handle the error
                    Toast.makeText(this@SignUpActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
