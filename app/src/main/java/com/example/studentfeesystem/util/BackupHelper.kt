package com.example.studentfeesystem.util

import android.content.Context
import android.net.Uri
import com.example.studentfeesystem.data.AppDatabase
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object BackupHelper {

    private const val DB_NAME = "student_fee_database"

    fun getDbFile(context: Context): File = context.getDatabasePath(DB_NAME)

    /** Flushes any pending writes to the main db file, then closes and releases the singleton. */
    private fun checkpointAndClose(context: Context) {
        val db = AppDatabase.getDatabase(context)
        try {
            db.openHelper.writableDatabase.execSQL("PRAGMA wal_checkpoint(FULL)")
        } catch (e: Exception) {
            // ignore - not fatal, worst case a few very last writes might be missed
        }
        db.close()
        AppDatabase.resetInstance()
    }

    /** Blocking call - run on a background thread. Copies the current database to destUri. */
    fun backupTo(context: Context, destUri: Uri): Boolean {
        return try {
            checkpointAndClose(context)
            val dbFile = getDbFile(context)
            context.contentResolver.openOutputStream(destUri)?.use { out ->
                FileInputStream(dbFile).use { input ->
                    input.copyTo(out)
                }
            } ?: return false
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /** Blocking call - run on a background thread. Replaces the current database with sourceUri's contents. */
    fun restoreFrom(context: Context, sourceUri: Uri): Boolean {
        return try {
            checkpointAndClose(context)
            val dbFile = getDbFile(context)

            // Remove any leftover WAL/SHM files so old data can't leak back in
            File(dbFile.path + "-wal").delete()
            File(dbFile.path + "-shm").delete()

            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                FileOutputStream(dbFile).use { output ->
                    input.copyTo(output)
                }
            } ?: return false
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
