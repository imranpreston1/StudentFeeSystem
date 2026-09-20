package com.example.studentfeesystem.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FeeViewModelFactory(
    private val application: Application,
    private val studentId: Int
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FeeViewModel::class.java)) {
            return FeeViewModel(application, studentId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
