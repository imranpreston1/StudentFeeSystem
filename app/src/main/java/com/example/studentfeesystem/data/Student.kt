package com.example.studentfeesystem.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val rollNumber: String,
    val studentClass: String,
    val contact: String,
    val admissionDate: String,
    val fatherName: String = "",
    val address: String = "",
    val dateOfBirth: String = "",
    val gender: String = "",
    val group: String = "",
    val monthlyFee: Double = 0.0,
    val photoUri: String = ""
)
