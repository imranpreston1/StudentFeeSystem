package com.example.studentfeesystem.ui

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentfeesystem.data.Fee
import com.example.studentfeesystem.data.Student
import com.example.studentfeesystem.databinding.ActivityStudentDetailBinding
import com.example.studentfeesystem.printer.PrinterHelper
import com.example.studentfeesystem.util.PrefsHelper
import com.example.studentfeesystem.viewmodel.FeeViewModel
import com.example.studentfeesystem.viewmodel.FeeViewModelFactory
import com.example.studentfeesystem.viewmodel.StudentViewModel
import kotlinx.coroutines.launch

class StudentDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudentDetailBinding
    private lateinit var studentViewModel: StudentViewModel
    private lateinit var feeViewModel: FeeViewModel
    private lateinit var feeAdapter: FeeAdapter
    private var studentId: Int = -1
    private var currentStudent: Student? = null
    private var pendingPrintFee: Fee? = null

    private val bluetoothPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            val fee = pendingPrintFee
            pendingPrintFee = null
            if (granted && fee != null) {
                showPrinterSelectionDialog(fee)
            } else if (!granted) {
                Toast.makeText(this, "Bluetooth permission is needed to print", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        studentId = intent.getIntExtra("studentId", -1)
        if (studentId == -1) {
            finish()
            return
        }

        studentViewModel = ViewModelProvider(this)[StudentViewModel::class.java]
        feeViewModel = ViewModelProvider(
            this,
            FeeViewModelFactory(application, studentId)
        )[FeeViewModel::class.java]

        loadStudentInfo()

        feeAdapter = FeeAdapter(
            onToggleStatus = { fee ->
                val newStatus = if (fee.status == "Paid") "Pending" else "Paid"
                val newPaidDate = if (newStatus == "Paid") android.text.format.DateFormat.format("yyyy-MM-dd", java.util.Date()).toString() else null
                feeViewModel.update(fee.copy(status = newStatus, paidDate = newPaidDate))
            },
            onPrint = { fee ->
                printFeeReceipt(fee)
            }
        )

        binding.recyclerViewFees.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewFees.adapter = feeAdapter

        feeViewModel.feesForStudent.observe(this) { fees ->
            feeAdapter.submitList(fees)
            binding.textEmptyFees.visibility = if (fees.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE

            val totalPaid = fees.filter { it.status == "Paid" }.sumOf { it.amount }
            val totalPending = fees.filter { it.status == "Pending" }.sumOf { it.amount }
            binding.textPaidTotal.text = "Rs. ${formatFee(totalPaid)}"
            binding.textBalance.text = "Rs. ${formatFee(totalPending)}"
        }

        binding.fabAddFee.setOnClickListener {
            val intent = Intent(this, AddFeeActivity::class.java)
            intent.putExtra("studentId", studentId)
            startActivity(intent)
        }

        binding.buttonEditStudent.setOnClickListener {
            val intent = Intent(this, AddEditStudentActivity::class.java)
            intent.putExtra("studentId", studentId)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadStudentInfo()
    }

    private fun loadStudentInfo() {
        lifecycleScope.launch {
            val student = studentViewModel.getStudentById(studentId)
            student?.let {
                currentStudent = it
                binding.textDetailName.text = it.name
                val classGroup = if (it.group.isNotBlank()) "${it.studentClass} - ${it.group}" else it.studentClass
                binding.textDetailInfo.text = "Roll No: ${it.rollNumber} | Class: $classGroup"
                binding.textDetailExtra.text =
                    "Father: ${it.fatherName.ifBlank { "-" }}\n" +
                    "Contact: ${it.contact.ifBlank { "-" }}\n" +
                    "Address: ${it.address.ifBlank { "-" }}"
                binding.textMonthlyFee.text = "Rs. ${formatFee(it.monthlyFee)}"
                binding.textDetailAvatarInitials.text = getInitials(it.name)

                if (it.photoUri.isNotBlank()) {
                    try {
                        binding.imageDetailAvatar.setImageURI(Uri.parse(it.photoUri))
                        binding.imageDetailAvatar.visibility = View.VISIBLE
                        binding.textDetailAvatarInitials.visibility = View.GONE
                    } catch (e: Exception) {
                        binding.imageDetailAvatar.visibility = View.GONE
                        binding.textDetailAvatarInitials.visibility = View.VISIBLE
                    }
                } else {
                    binding.imageDetailAvatar.visibility = View.GONE
                    binding.textDetailAvatarInitials.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun formatFee(value: Double): String {
        return if (value == value.toLong().toDouble()) value.toLong().toString() else value.toString()
    }

    private fun getInitials(name: String): String {
        val parts = name.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
        return when {
            parts.isEmpty() -> "?"
            parts.size == 1 -> parts[0].take(2).uppercase()
            else -> (parts[0].take(1) + parts[1].take(1)).uppercase()
        }
    }

    private fun printFeeReceipt(fee: Fee) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                != PackageManager.PERMISSION_GRANTED
            ) {
                pendingPrintFee = fee
                bluetoothPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
                return
            }
        }
        showPrinterSelectionDialog(fee)
    }

    private fun showPrinterSelectionDialog(fee: Fee) {
        val devices = PrinterHelper.getPairedDevices()
        if (devices.isEmpty()) {
            Toast.makeText(
                this,
                "No paired Bluetooth printer found. Pair your thermal printer in phone Bluetooth settings first.",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        val names = devices.map { it.name ?: it.address }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("Select Printer")
            .setItems(names) { _, which ->
                connectAndPrint(devices[which], fee)
            }
            .show()
    }

    private fun connectAndPrint(device: BluetoothDevice, fee: Fee) {
        Toast.makeText(this, "Connecting to printer...", Toast.LENGTH_SHORT).show()
        val student = currentStudent
        Thread {
            val connected = PrinterHelper.connect(device)
            val success = if (connected) {
                PrinterHelper.printReceipt(
                    schoolName = PrefsHelper.getSchoolName(this@StudentDetailActivity).ifBlank { "School" },
                    studentName = student?.name ?: "",
                    rollNumber = student?.rollNumber ?: "",
                    studentClass = student?.studentClass ?: "",
                    fatherName = student?.fatherName?.ifBlank { "-" } ?: "-",
                    month = fee.month,
                    amount = fee.amount,
                    status = fee.status,
                    date = fee.paidDate ?: "-"
                )
            } else {
                false
            }
            PrinterHelper.disconnect()
            runOnUiThread {
                val message = when {
                    !connected -> "Could not connect to printer"
                    !success -> "Print failed"
                    else -> "Receipt printed"
                }
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }.start()
    }
}
