package com.example.studentfeesystem.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.studentfeesystem.R
import com.example.studentfeesystem.databinding.FragmentDashboardBinding
import com.example.studentfeesystem.ui.AddEditStudentActivity
import com.example.studentfeesystem.ui.MainActivity
import com.example.studentfeesystem.viewmodel.StudentViewModel

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var studentViewModel: StudentViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        studentViewModel = ViewModelProvider(this)[StudentViewModel::class.java]

        studentViewModel.totalStudents.observe(viewLifecycleOwner) { count ->
            binding.textTotalStudents.text = count.toString()
        }
        studentViewModel.totalCollected.observe(viewLifecycleOwner) { total ->
            binding.textTotalCollected.text = "Rs. ${formatAmount(total)}"
        }
        studentViewModel.totalPending.observe(viewLifecycleOwner) { total ->
            binding.textTotalPending.text = "Rs. ${formatAmount(total)}"
        }
        studentViewModel.todayCollected.observe(viewLifecycleOwner) { total ->
            binding.textTodayCollected.text = "Rs. ${formatAmount(total)}"
        }

        binding.cardAddStudent.setOnClickListener {
            startActivity(Intent(requireContext(), AddEditStudentActivity::class.java))
        }

        binding.cardSearchStudent.setOnClickListener {
            (activity as? MainActivity)?.selectTab(R.id.nav_students)
        }

        binding.cardBackup.setOnClickListener {
            (activity as? MainActivity)?.selectTab(R.id.nav_settings)
        }
    }

    private fun formatAmount(value: Double): String {
        return if (value == value.toLong().toDouble()) value.toLong().toString() else value.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
