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

            if (email.isEmpty() || name.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 8) {
                Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show()
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
                    // Handle the error
                    Toast.makeText(this@SignUpActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}