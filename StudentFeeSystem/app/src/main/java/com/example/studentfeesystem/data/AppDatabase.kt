package com.example.studentfeesystem.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Student::class, Fee::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun studentDao(): StudentDao
    abstract fun feeDao(): FeeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "student_fee_database"
                )
                    // Simplest upgrade path for the new fatherName/address columns.
                    // NOTE: this wipes existing local data on upgrade from version 1.
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        /** Call after directly replacing the underlying .db file (backup/restore) so the next
         * getDatabase() call opens a brand new connection instead of reusing a stale one. */
        fun resetInstance() {
            synchronized(this) {
                INSTANCE = null
            }
        }
    }
}
