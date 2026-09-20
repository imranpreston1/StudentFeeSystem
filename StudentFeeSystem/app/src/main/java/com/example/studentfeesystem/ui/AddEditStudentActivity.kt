package com.example.studentfeesystem.ui

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.studentfeesystem.data.Student
import com.example.studentfeesystem.databinding.ActivityAddEditStudentBinding
import com.example.studentfeesystem.viewmodel.StudentViewModel
import kotlinx.coroutines.launch
import java.util.Calendar

class AddEditStudentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditStudentBinding
    private lateinit var studentViewModel: StudentViewModel
    private var editingStudent: Student? = null
    private var selectedPhotoUri: String = ""

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {
                // Some providers don't support persistable permissions - image may not
                // survive an app restart in that case, but still works for this session.
            }
            selectedPhotoUri = uri.toString()
            binding.imagePhoto.setImageURI(uri)
            binding.imagePhoto.scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
            binding.imagePhoto.setPadding(0, 0, 0, 0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        studentViewModel = ViewModelProvider(this)[StudentViewModel::class.java]

        binding.buttonAddPhoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.editDob.setOnClickListener { showDatePicker() }

        val studentId = intent.getIntExtra("studentId", -1)

        if (studentId != -1) {
            lifecycleScope.launch {
                val student = studentViewModel.getStudentById(studentId)
                student?.let {
                    editingStudent = it
                    binding.editName.setText(it.name)
                    binding.editRollNumber.setText(it.rollNumber)
                    binding.editClass.setText(it.studentClass)
                    binding.editGroup.setText(it.group)
                    binding.editContact.setText(it.contact)
                    binding.editAdmissionDate.setText(it.admissionDate)
                    binding.editFatherName.setText(it.fatherName)
                    binding.editAddress.setText(it.address)
                    binding.editDob.setText(it.dateOfBirth)
                    if (it.monthlyFee > 0) {
                        binding.editMonthlyFee.setText(formatFee(it.monthlyFee))
                    }
                    if (it.gender == "Female") {
                        binding.radioFemale.isChecked = true
                    } else {
                        binding.radioMale.isChecked = true
                    }
                    if (it.photoUri.isNotBlank()) {
                        selectedPhotoUri = it.photoUri
                        try {
                            binding.imagePhoto.setImageURI(Uri.parse(it.photoUri))
                            binding.imagePhoto.scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
                            binding.imagePhoto.setPadding(0, 0, 0, 0)
                        } catch (e: Exception) {
                            // photo may no longer be accessible - keep the placeholder icon
                        }
                    }
                    binding.buttonDelete.visibility = android.view.View.VISIBLE
                }
            }
        }

        binding.buttonSave.setOnClickListener {
            saveStudent()
        }

        binding.buttonDelete.setOnClickListener {
            editingStudent?.let {
                studentViewModel.delete(it)
                Toast.makeText(this, "Student deleted", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun formatFee(value: Double): String {
        return if (value == value.toLong().toDouble()) value.toLong().toString() else value.toString()
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                val formatted = String.format("%02d/%02d/%04d", day, month + 1, year)
                binding.editDob.setText(formatted)
            },
            calendar.get(Calendar.YEAR) - 15,
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun saveStudent() {
        val name = binding.editName.text.toString().trim()
        val rollNumber = binding.editRollNumber.text.toString().trim()
        val studentClass = binding.editClass.text.toString().trim()
        val group = binding.editGroup.text.toString().trim()
        val contact = binding.editContact.text.toString().trim()
        val admissionDate = binding.editAdmissionDate.text.toString().trim()
        val fatherName = binding.editFatherName.text.toString().trim()
        val address = binding.editAddress.text.toString().trim()
        val dob = binding.editDob.text.toString().trim()
        val monthlyFeeText = binding.editMonthlyFee.text.toString().trim()
        val monthlyFee = monthlyFeeText.toDoubleOrNull() ?: 0.0
        val gender = if (binding.radioFemale.isChecked) "Female" else "Male"

        if (name.isEmpty() || rollNumber.isEmpty()) {
            Toast.makeText(this, "Name and Roll Number are required", Toast.LENGTH_SHORT).show()
            return
        }

        val current = editingStudent
        if (current != null) {
            val updated = current.copy(
                name = name,
                rollNumber = rollNumber,
                studentClass = studentClass,
                group = group,
                contact = contact,
                admissionDate = admissionDate,
                fatherName = fatherName,
                address = address,
                dateOfBirth = dob,
                gender = gender,
                monthlyFee = monthlyFee,
                photoUri = selectedPhotoUri
            )
            studentViewModel.update(updated)
        } else {
            val newStudent = Student(
                name = name,
                rollNumber = rollNumber,
                studentClass = studentClass,
                group = group,
                contact = contact,
                admissionDate = admissionDate,
                fatherName = fatherName,
                address = address,
                dateOfBirth = dob,
                gender = gender,
                monthlyFee = monthlyFee,
                photoUri = selectedPhotoUri
            )
            studentViewModel.insert(newStudent)
        }

        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
        finish()
    }
}
