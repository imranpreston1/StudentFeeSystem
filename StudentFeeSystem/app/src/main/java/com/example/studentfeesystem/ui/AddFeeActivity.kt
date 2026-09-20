package com.example.studentfeesystem.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.studentfeesystem.data.Fee
import com.example.studentfeesystem.databinding.ActivityAddFeeBinding
import com.example.studentfeesystem.viewmodel.FeeViewModel
import com.example.studentfeesystem.viewmodel.FeeViewModelFactory
import java.util.Date

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

        binding.buttonSaveFee.setOnClickListener {
            saveFee()
        }
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
