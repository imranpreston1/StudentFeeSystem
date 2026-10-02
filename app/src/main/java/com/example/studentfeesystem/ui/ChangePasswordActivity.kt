package com.example.studentfeesystem.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.studentfeesystem.databinding.ActivityChangePasswordBinding
import com.example.studentfeesystem.util.PrefsHelper

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonChangePassword.setOnClickListener {
            val current = binding.editCurrentPassword.text.toString()
            val newPassword = binding.editNewPassword.text.toString()
            val confirm = binding.editConfirmNewPassword.text.toString()

            val username = PrefsHelper.getAdminUsername(this)

            if (!PrefsHelper.verifyLogin(this, username, current)) {
                Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (newPassword.length < 4) {
                Toast.makeText(this, "New password must be at least 4 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (newPassword != confirm) {
                Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            PrefsHelper.changePassword(this, newPassword)
            Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
