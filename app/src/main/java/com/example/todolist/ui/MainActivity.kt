package com.example.todolist.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.todolist.R
import com.example.todolist.utils.NetworkManager
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {
    @Inject
    internal lateinit var networkManager: NetworkManager

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_TodoList)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupActionBar()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                findNavController(R.id.nav_host_fragment).popBackStack()
                return true
            }
        }
        return true
    }

    override fun onStart() {
        super.onStart()
        networkManager.register()
    }

    override fun onResume() {
        super.onResume()
        networkManager.checkInternetConnection()
    }

    override fun onStop() {
        networkManager.unregister()
        super.onStop()
    }

    private fun setupActionBar() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        setupActionBarWithNavController(navController = navHostFragment.navController)
    }
}
