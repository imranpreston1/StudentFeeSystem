package com.example.studentfeesystem.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.studentfeesystem.R
import com.example.studentfeesystem.databinding.ActivityMainBinding
import com.example.studentfeesystem.ui.fragments.DashboardFragment
import com.example.studentfeesystem.ui.fragments.SettingsFragment
import com.example.studentfeesystem.ui.fragments.StudentsFragment
import com.example.studentfeesystem.util.PrefsHelper

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        val schoolName = PrefsHelper.getSchoolName(this)
        if (schoolName.isNotBlank()) {
            supportActionBar?.subtitle = schoolName
        }

        if (savedInstanceState == null) {
            showFragment(DashboardFragment())
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    showFragment(DashboardFragment())
                    true
                }
                R.id.nav_students -> {
                    showFragment(StudentsFragment())
                    true
                }
                R.id.nav_settings -> {
                    showFragment(SettingsFragment())
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh the subtitle in case the school name was just changed in Settings.
        val schoolName = PrefsHelper.getSchoolName(this)
        supportActionBar?.subtitle = if (schoolName.isNotBlank()) schoolName else null
    }

    /** Called by fragments (e.g. Dashboard quick actions) to switch tabs programmatically. */
    fun selectTab(itemId: Int) {
        binding.bottomNavigation.selectedItemId = itemId
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
