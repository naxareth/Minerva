package com.example.minerva_10.views

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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
    private lateinit var tvCooldown: TextView
    private lateinit var backButton: Button

    private var countdownTimer: CountDownTimer? = null
    private var isCooldown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        etEmail = findViewById(R.id.etEmail)
        etOtp = findViewById(R.id.etOtp)
        etNewPassword = findViewById(R.id.etNewPassword)
        etConfirmNewPassword = findViewById(R.id.etConfirmNewPassword)
        btSendOtp = findViewById(R.id.btSendOtp)
        btChangePassword = findViewById(R.id.btChangePassword)
        tvCooldown = findViewById(R.id.tvCooldown)
        backButton = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            finish()
        }

        btSendOtp.setOnClickListener {
            val email = etEmail.text.toString()

            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isCooldown) {
                Toast.makeText(this, "Please wait before requesting a new OTP", Toast.LENGTH_SHORT).show()
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

            if (!isValidOtpInput(email, otp, newPassword)) {
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
                    startCooldown()
                } else {
                    handleErrorResponse(response.code())
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                handleFailureError(t)
            }
        })
    }

    private fun startCooldown() {
        isCooldown = true
        btSendOtp.isEnabled = false
        countdownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = (millisUntilFinished / 1000) % 60
                val minutes = (millisUntilFinished / 1000) / 60
                tvCooldown.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                isCooldown = false
                btSendOtp.isEnabled = true
                tvCooldown.text = ""
            }
        }.start()
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

    private fun isValidOtpInput(email: String, otp: String, newPassword: String): Boolean {
        val emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$".toRegex()
        val passwordRegex = "^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]*$".toRegex() // Allow specific special characters

        if (email.isEmpty() || otp.isEmpty() || newPassword.isEmpty() || etConfirmNewPassword.text.toString().isEmpty()) {
            Toast.makeText(this, "Fields cannot be empty or contain only spaces", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!emailRegex.matches(email)) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!passwordRegex.matches(newPassword)) {
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