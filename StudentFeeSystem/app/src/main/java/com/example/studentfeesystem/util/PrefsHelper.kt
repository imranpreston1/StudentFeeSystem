package com.example.studentfeesystem.util

import android.content.Context

object PrefsHelper {
    private const val PREFS_NAME = "app_prefs"
    private const val KEY_SCHOOL_NAME = "school_name"

    fun isSetupDone(context: Context): Boolean {
        return getSchoolName(context).isNotBlank()
    }

    fun getSchoolName(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_SCHOOL_NAME, "") ?: ""
    }

    fun setSchoolName(context: Context, name: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_SCHOOL_NAME, name).apply()
    }
}
