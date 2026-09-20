package com.example.studentfeesystem.ui

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.studentfeesystem.data.Student
import com.example.studentfeesystem.databinding.ItemStudentBinding

class StudentAdapter(
    private val onClick: (Student) -> Unit
) : ListAdapter<Student, StudentAdapter.StudentViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = ItemStudentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StudentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class StudentViewHolder(private val binding: ItemStudentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(student: Student) {
            binding.textName.text = student.name
            binding.textRollClass.text = "Roll No: ${student.rollNumber} | Class: ${student.studentClass}"
            binding.textContact.text = "Contact: ${student.contact}"
            binding.textFather.text = "Father: ${student.fatherName.ifBlank { "-" }}"
            binding.textAvatarInitials.text = getInitials(student.name)

            if (student.photoUri.isNotBlank()) {
                try {
                    binding.imageAvatarPhoto.setImageURI(Uri.parse(student.photoUri))
                    binding.imageAvatarPhoto.visibility = View.VISIBLE
                    binding.textAvatarInitials.visibility = View.GONE
                } catch (e: Exception) {
                    binding.imageAvatarPhoto.visibility = View.GONE
                    binding.textAvatarInitials.visibility = View.VISIBLE
                }
            } else {
                binding.imageAvatarPhoto.visibility = View.GONE
                binding.textAvatarInitials.visibility = View.VISIBLE
            }

            binding.root.setOnClickListener { onClick(student) }
        }

        private fun getInitials(name: String): String {
            val parts = name.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
            return when {
                parts.isEmpty() -> "?"
                parts.size == 1 -> parts[0].take(2).uppercase()
                else -> (parts[0].take(1) + parts[1].take(1)).uppercase()
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Student>() {
        override fun areItemsTheSame(oldItem: Student, newItem: Student) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Student, newItem: Student) = oldItem == newItem
    }
}
