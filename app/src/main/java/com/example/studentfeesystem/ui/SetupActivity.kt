package com.example.studentfeesystem.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.studentfeesystem.databinding.ActivitySetupBinding
import com.example.studentfeesystem.util.PrefsHelper

class SetupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetupBinding
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isEditMode = intent.getBooleanExtra("editMode", false)

        if (!isEditMode) {
            if (PrefsHelper.isSetupDone(this)) {
                // First-run already completed -> require login instead of showing setup again.
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                return
            }
        }

        binding = ActivitySetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (isEditMode) {
            // Editing from Settings: only the school name is changed here.
            binding.textSubtitle.text = "Update your school / institute name"
            binding.editSchoolName.setText(PrefsHelper.getSchoolName(this))
            binding.loginFieldsGroup.visibility = android.view.View.GONE
            binding.buttonSaveSetup.text = "Update"
        }

        binding.buttonSaveSetup.setOnClickListener {
            if (isEditMode) {
                saveSchoolNameOnly()
            } else {
                saveFirstRunSetup()
            }
        }
    }

    private fun saveSchoolNameOnly() {
        val name = binding.editSchoolName.text.toString().trim()
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter your school name", Toast.LENGTH_SHORT).show()
            return
        }
        PrefsHelper.setSchoolName(this, name)
        Toast.makeText(this, "School name updated", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun saveFirstRunSetup() {
        val name = binding.editSchoolName.text.toString().trim()
        val username = binding.editUsername.text.toString().trim()
        val password = binding.editPassword.text.toString()
        val confirmPassword = binding.editConfirmPassword.text.toString()

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter your school name", Toast.LENGTH_SHORT).show()
            return
        }
        if (username.isEmpty()) {
            Toast.makeText(this, "Please choose an admin username", Toast.LENGTH_SHORT).show()
            return
        }
        if (password.length < 4) {
            Toast.makeText(this, "Password must be at least 4 characters", Toast.LENGTH_SHORT).show()
            return
        }
        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        PrefsHelper.setSchoolName(this, name)
        PrefsHelper.setAdminCredentials(this, username, password)

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
