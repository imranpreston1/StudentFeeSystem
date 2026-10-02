package com.example.studentfeesystem.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.studentfeesystem.databinding.ActivityLoginBinding
import com.example.studentfeesystem.util.PrefsHelper

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val schoolName = PrefsHelper.getSchoolName(this)
        if (schoolName.isNotBlank()) {
            binding.textSchoolName.text = schoolName
        }

        binding.buttonLogin.setOnClickListener {
            val username = binding.editLoginUsername.text.toString().trim()
            val password = binding.editLoginPassword.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (PrefsHelper.verifyLogin(this, username, password)) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
