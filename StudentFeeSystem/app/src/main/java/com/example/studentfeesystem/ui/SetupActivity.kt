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

        // If setup is already done and this isn't an explicit "edit" request,
        // skip straight to MainActivity (this is the launcher activity).
        if (!isEditMode && PrefsHelper.isSetupDone(this)) {
            goToMain()
            return
        }

        binding = ActivitySetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (isEditMode) {
            binding.editSchoolName.setText(PrefsHelper.getSchoolName(this))
            binding.buttonSaveSetup.text = "Update"
        }

        binding.buttonSaveSetup.setOnClickListener {
            val name = binding.editSchoolName.text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter your school name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            PrefsHelper.setSchoolName(this, name)
            if (isEditMode) {
                Toast.makeText(this, "School name updated", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                goToMain()
            }
        }
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
