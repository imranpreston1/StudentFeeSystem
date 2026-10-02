package com.example.studentfeesystem.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentfeesystem.data.Student
import com.example.studentfeesystem.databinding.FragmentStudentsBinding
import com.example.studentfeesystem.ui.AddEditStudentActivity
import com.example.studentfeesystem.ui.StudentAdapter
import com.example.studentfeesystem.ui.StudentDetailActivity
import com.example.studentfeesystem.viewmodel.StudentViewModel

class StudentsFragment : Fragment() {

    private var _binding: FragmentStudentsBinding? = null
    private val binding get() = _binding!!
    private lateinit var studentViewModel: StudentViewModel
    private lateinit var adapter: StudentAdapter

    private var fullStudentList: List<Student> = emptyList()
    private var currentQuery: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        studentViewModel = ViewModelProvider(this)[StudentViewModel::class.java]

        adapter = StudentAdapter { student ->
            val intent = Intent(requireContext(), StudentDetailActivity::class.java)
            intent.putExtra("studentId", student.id)
            startActivity(intent)
        }

        binding.recyclerViewStudents.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewStudents.adapter = adapter

        studentViewModel.allStudents.observe(viewLifecycleOwner) { students ->
            fullStudentList = students
            applyFilter()
        }

        binding.editSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentQuery = s?.toString() ?: ""
                applyFilter()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.fabAddStudent.setOnClickListener {
            startActivity(Intent(requireContext(), AddEditStudentActivity::class.java))
        }
    }

    private fun applyFilter() {
        val filtered = if (currentQuery.isBlank()) {
            fullStudentList
        } else {
            fullStudentList.filter {
                it.name.contains(currentQuery, ignoreCase = true) ||
                    it.rollNumber.contains(currentQuery, ignoreCase = true)
            }
        }
        adapter.submitList(filtered)
        binding.textEmpty.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
