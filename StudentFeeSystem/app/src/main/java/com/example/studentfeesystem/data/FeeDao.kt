package com.example.studentfeesystem.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FeeDao {

    @Insert
    suspend fun insert(fee: Fee)

    @Update
    suspend fun update(fee: Fee)

    @Delete
    suspend fun delete(fee: Fee)

    @Query("SELECT * FROM fees WHERE studentId = :studentId ORDER BY id DESC")
    fun getFeesForStudent(studentId: Int): LiveData<List<Fee>>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM fees WHERE status = 'Paid'")
    fun getTotalCollected(): LiveData<Double>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM fees WHERE status = 'Pending'")
    fun getTotalPending(): LiveData<Double>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM fees WHERE status = 'Paid' AND paidDate = :date")
    fun getTodayCollected(date: String): LiveData<Double>
}
