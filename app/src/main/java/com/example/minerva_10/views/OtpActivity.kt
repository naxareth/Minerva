package com.example.minerva_10.views

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.minerva_10.R
import com.example.minerva_10.api.RetrofitClient
import com.example.minerva_10.api.responses.OtpRequest
import com.example.minerva_10.api.responses.VerifyOtpRequest
import com.example.minerva_10.api.responses.ChangePasswordRequest
import com.example.minerva_10.api.responses.ChangePasswordResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class OtpActivity : AppCompatActivity() {
    private lateinit var etEmail: EditText
    private lateinit var etOtp: EditText
    private lateinit var etNewPassword: EditText
    private lateinit var etConfirmNewPassword: EditText
    private lateinit var btSendOtp: Button
    private lateinit var btChangePassword: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        etEmail = findViewById(R.id.etEmail)
        etOtp = findViewById(R.id.etOtp)
        etNewPassword = findViewById(R.id.etNewPassword)
        etConfirmNewPassword = findViewById(R.id.etConfirmNewPassword)
        btSendOtp = findViewById(R.id.btSendOtp)
        btChangePassword = findViewById(R.id.btChangePassword)

        btSendOtp.setOnClickListener {
            val email = etEmail.text.toString()

            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val otpRequest = OtpRequest(email)
            sendOtp(otpRequest)
        }

        btChangePassword.setOnClickListener {
            val email = etEmail.text.toString()
            val otp = etOtp.text.toString()
            val newPassword = etNewPassword.text.toString()
            val confirmNewPassword = etConfirmNewPassword.text.toString()

            if (email.isEmpty() || otp.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword != confirmNewPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val verifyOtpRequest = VerifyOtpRequest(email, otp)
            verifyOtp(verifyOtpRequest, newPassword, confirmNewPassword)
        }
    }

    private fun sendOtp(otpRequest: OtpRequest) {
        RetrofitClient.api.sendOtp(otpRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@OtpActivity, "OTP sent successfully", Toast.LENGTH_SHORT).show()
                } else {
                    handleErrorResponse(response.code())
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                handleFailureError(t)
            }
        })
    }

    private fun verifyOtp(verifyOtpRequest: VerifyOtpRequest, newPassword: String, confirmNewPassword: String) {
        RetrofitClient.api.verifyOtp(verifyOtpRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    val changePasswordRequest = ChangePasswordRequest(verifyOtpRequest.email, verifyOtpRequest.otp, newPassword, confirmNewPassword)
                    changePassword(changePasswordRequest)
                } else {
                    handleErrorResponse(response.code())
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                handleFailureError(t)
            }
        })
    }

    private fun changePassword(changePasswordRequest: ChangePasswordRequest) {
        RetrofitClient.api.changePassword(changePasswordRequest).enqueue(object : Callback<ChangePasswordResponse> {
            override fun onResponse(call: Call<ChangePasswordResponse>, response: Response<ChangePasswordResponse>) {
                if (response.isSuccessful) {
                    val changePasswordResponse = response.body()
                    val message = changePasswordResponse?.message

                    if (message != null) {
                        Toast.makeText(this@OtpActivity, message, Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@OtpActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@OtpActivity, "Invalid response", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    handleErrorResponse(response.code())
                }
            }

            override fun onFailure(call: Call<ChangePasswordResponse>, t: Throwable) {
                handleFailureError(t)
            }
        })
    }

    private fun handleErrorResponse(code: Int) {
        when (code) {
            404 -> {
                Toast .makeText(this, "Account not found", Toast.LENGTH_SHORT).show()
            }
            401 -> {
                Toast.makeText(this, "Invalid email or OTP", Toast.LENGTH_SHORT).show()
            }
            422 -> {
                Toast.makeText(this, "Invalid email or OTP", Toast.LENGTH_SHORT).show()
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