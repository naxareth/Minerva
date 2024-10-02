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
import com.example.minerva_10.api.responses.RegisterRequest
import com.example.minerva_10.api.responses.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {
    private lateinit var etSignEmail: EditText
    private lateinit var etSignName: EditText
    private lateinit var etSignPass: EditText
    private lateinit var btSignRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        etSignEmail = findViewById(R.id.etSignEmail)
        etSignName = findViewById(R.id.etSignName)
        etSignPass = findViewById(R.id.etSignPass)
        btSignRegister = findViewById(R.id.btSignRegister)

        btSignRegister.setOnClickListener {
            val email = etSignEmail.text.toString()
            val name = etSignName.text.toString()
            val password = etSignPass.text.toString()

            val registerRequest = RegisterRequest(name, email, password)
            RetrofitClient.api.register(registerRequest).enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                    if (response.isSuccessful) {
                        val registerResponse = response.body()
                        val token = registerResponse?.token
                        val message = registerResponse?.message

                        // Use the token and message variables to do something
                        Toast.makeText(this@SignUpActivity, "Register successful: $message", Toast.LENGTH_SHORT).show()
                    } else {
                        // Handle the error
                        Toast.makeText(this@SignUpActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
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