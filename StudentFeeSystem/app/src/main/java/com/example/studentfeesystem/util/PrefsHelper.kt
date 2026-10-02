package com.example.studentfeesystem.util

import android.content.Context
import java.security.MessageDigest

object PrefsHelper {
    private const val PREFS_NAME = "app_prefs"
    private const val KEY_SCHOOL_NAME = "school_name"
    private const val KEY_ADMIN_USERNAME = "admin_username"
    private const val KEY_ADMIN_PASSWORD_HASH = "admin_password_hash"

    /** True once school name AND admin login have both been configured (first-run complete). */
    fun isSetupDone(context: Context): Boolean {
        return getSchoolName(context).isNotBlank() && getAdminUsername(context).isNotBlank()
    }

    fun getSchoolName(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_SCHOOL_NAME, "") ?: ""
    }

    fun setSchoolName(context: Context, name: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_SCHOOL_NAME, name).apply()
    }

    fun getAdminUsername(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_ADMIN_USERNAME, "") ?: ""
    }

    fun setAdminCredentials(context: Context, username: String, password: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_ADMIN_USERNAME, username)
            .putString(KEY_ADMIN_PASSWORD_HASH, hash(password))
            .apply()
    }

    fun changePassword(context: Context, newPassword: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_ADMIN_PASSWORD_HASH, hash(newPassword)).apply()
    }

    fun verifyLogin(context: Context, username: String, password: String): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val storedUsername = prefs.getString(KEY_ADMIN_USERNAME, "") ?: ""
        val storedHash = prefs.getString(KEY_ADMIN_PASSWORD_HASH, "") ?: ""
        return username == storedUsername && hash(password) == storedHash
    }

    private fun hash(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
