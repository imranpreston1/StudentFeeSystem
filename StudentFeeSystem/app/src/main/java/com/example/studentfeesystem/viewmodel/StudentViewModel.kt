package com.example.studentfeesystem.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.studentfeesystem.data.AppDatabase
import com.example.studentfeesystem.data.Student
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StudentViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val studentDao = database.studentDao()
    private val feeDao = database.feeDao()

    val allStudents: LiveData<List<Student>> = studentDao.getAllStudents()
    val totalStudents: LiveData<Int> = studentDao.getStudentCount()
    val totalCollected: LiveData<Double> = feeDao.getTotalCollected()
    val totalPending: LiveData<Double> = feeDao.getTotalPending()

    val todayCollected: LiveData<Double> = feeDao.getTodayCollected(
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    )

    fun insert(student: Student) = viewModelScope.launch {
        studentDao.insert(student)
    }

    fun update(student: Student) = viewModelScope.launch {
        studentDao.update(student)
    }

    fun delete(student: Student) = viewModelScope.launch {
        studentDao.delete(student)
    }

    suspend fun getStudentById(id: Int): Student? {
        return studentDao.getStudentById(id)
    }
}
