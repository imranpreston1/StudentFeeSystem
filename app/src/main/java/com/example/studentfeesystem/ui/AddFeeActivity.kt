package com.example.studentfeesystem.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.studentfeesystem.data.Fee
import com.example.studentfeesystem.databinding.ActivityAddFeeBinding
import com.example.studentfeesystem.viewmodel.FeeViewModel
import com.example.studentfeesystem.viewmodel.FeeViewModelFactory
import com.example.studentfeesystem.viewmodel.StudentViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddFeeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddFeeBinding
    private lateinit var feeViewModel: FeeViewModel
    private var studentId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFeeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        studentId = intent.getIntExtra("studentId", -1)
        if (studentId == -1) {
            finish()
            return
        }

        feeViewModel = ViewModelProvider(
            this,
            FeeViewModelFactory(application, studentId)
        )[FeeViewModel::class.java]

        // Default the month to the current month, and fetch the student's monthly fee
        // to pre-fill the amount, so the user usually just has to confirm & save.
        binding.editMonth.setText(SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date()))

        val studentViewModel = ViewModelProvider(this)[StudentViewModel::class.java]
        lifecycleScope.launch {
            val student = studentViewModel.getStudentById(studentId)
            if (student != null && student.monthlyFee > 0) {
                binding.editAmount.setText(formatFee(student.monthlyFee))
            }
        }

        binding.buttonSaveFee.setOnClickListener {
            saveFee()
        }
    }

    private fun formatFee(value: Double): String {
        return if (value == value.toLong().toDouble()) value.toLong().toString() else value.toString()
    }

    private fun saveFee() {
        val month = binding.editMonth.text.toString().trim()
        val amountText = binding.editAmount.text.toString().trim()

        if (month.isEmpty() || amountText.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountText.toDoubleOrNull()
        if (amount == null) {
            Toast.makeText(this, "Enter a valid amount", Toast.LENGTH_SHORT).show()
            return
        }

        val isPaid = binding.switchPaid.isChecked
        val status = if (isPaid) "Paid" else "Pending"
        val paidDate = if (isPaid) {
            android.text.format.DateFormat.format("yyyy-MM-dd", Date()).toString()
        } else {
            null
        }

        val fee = Fee(
            studentId = studentId,
            month = month,
            amount = amount,
            paidDate = paidDate,
            status = status
        )

        feeViewModel.insert(fee)
        Toast.makeText(this, "Fee record saved", Toast.LENGTH_SHORT).show()
        finish()
    }
}
