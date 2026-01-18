package com.construction.expense

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.construction.expense.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 * MainActivity - The main entry point and container for the app
 *
 * This activity hosts a Navigation Component with bottom navigation for:
 * - Dashboard (home screen with overview)
 * - Expenses (list and manage expenses)
 * - Projects (project management)
 * - Reports (view and generate reports)
 * - Settings (app configuration)
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Timber.d("MainActivity created")

        setupNavigation()
    }

    /**
     * Sets up the Navigation Component with bottom navigation
     */
    private fun setupNavigation() {
        // Get the NavHostFragment and NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Setup bottom navigation with NavController
        val bottomNav: BottomNavigationView = binding.bottomNavigation
        bottomNav.setupWithNavController(navController)

        // Center FAB -> Add Expense screen (modern flow, like the mock)
        binding.fabAdd.setOnClickListener {
            val current = navController.currentDestination?.id
            if (current != R.id.add_edit_expense) {
                navController.navigate(R.id.add_edit_expense)
            }
        }

        Timber.d("Navigation setup complete")
    }

    override fun onSupportNavigateUp(): Boolean = navController.navigateUp() || super.onSupportNavigateUp()
}
