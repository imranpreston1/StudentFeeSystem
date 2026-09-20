package com.example.studentfeesystem.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.studentfeesystem.databinding.FragmentSettingsBinding
import com.example.studentfeesystem.ui.SetupActivity
import com.example.studentfeesystem.util.BackupHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val backupLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri: Uri? ->
        if (uri == null) return@registerForActivityResult
        val context = requireContext()
        Toast.makeText(context, "Backing up...", Toast.LENGTH_SHORT).show()
        Thread {
            val success = BackupHelper.backupTo(context, uri)
            activity?.runOnUiThread {
                if (success) {
                    Toast.makeText(context, "Backup saved successfully", Toast.LENGTH_LONG).show()
                    restartApp()
                } else {
                    Toast.makeText(context, "Backup failed", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }

    private val restoreLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri == null) return@registerForActivityResult
        AlertDialog.Builder(requireContext())
            .setTitle("Restore Data")
            .setMessage("This will REPLACE all current students and fee records on this phone with the data from the selected backup file. This cannot be undone. Continue?")
            .setPositiveButton("Restore") { _, _ ->
                val context = requireContext()
                Toast.makeText(context, "Restoring...", Toast.LENGTH_SHORT).show()
                Thread {
                    val success = BackupHelper.restoreFrom(context, uri)
                    activity?.runOnUiThread {
                        if (success) {
                            Toast.makeText(context, "Data restored successfully", Toast.LENGTH_LONG).show()
                            restartApp()
                        } else {
                            Toast.makeText(context, "Restore failed", Toast.LENGTH_LONG).show()
                        }
                    }
                }.start()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rowSchoolSettings.setOnClickListener {
            val intent = Intent(requireContext(), SetupActivity::class.java)
            intent.putExtra("editMode", true)
            startActivity(intent)
        }

        binding.rowBackup.setOnClickListener {
            val timestamp = SimpleDateFormat("yyyy-MM-dd_HHmm", Locale.getDefault()).format(Date())
            backupLauncher.launch("StudentFeeBackup_$timestamp.db")
        }

        binding.rowRestore.setOnClickListener {
            restoreLauncher.launch(arrayOf("*/*"))
        }
    }

    private fun restartApp() {
        val context = requireContext()
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        activity?.finishAffinity()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
