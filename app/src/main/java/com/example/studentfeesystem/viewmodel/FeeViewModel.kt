package com.example.studentfeesystem.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.studentfeesystem.data.AppDatabase
import com.example.studentfeesystem.data.Fee
import kotlinx.coroutines.launch

class FeeViewModel(application: Application, private val studentId: Int) : AndroidViewModel(application) {

    private val feeDao = AppDatabase.getDatabase(application).feeDao()

    val feesForStudent: LiveData<List<Fee>> = feeDao.getFeesForStudent(studentId)

    fun insert(fee: Fee) = viewModelScope.launch {
        feeDao.insert(fee)
    }

    fun update(fee: Fee) = viewModelScope.launch {
        feeDao.update(fee)
    }

    fun delete(fee: Fee) = viewModelScope.launch {
        feeDao.delete(fee)
    }
}
