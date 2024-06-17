package com.example.volcanoseason3

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.volcanoseason3.databinding.ActivityMainBinding
import com.example.volcanoseason3.databinding.FragmentHomeBinding
import com.example.volcanoseason3.ui.home.HomeFragment
import com.example.volcanoseason3.ui.home.HomeViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action !!!!!", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            showAddLinkDialog()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_checklist
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Add a destination listener to control FAB visibility.
        navController.addOnDestinationChangedListener { _, dest, _ ->
            if (dest.id == R.id.nav_home) {
                binding.appBarMain.fab.show()
            } else {
                binding.appBarMain.fab.hide()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun showAddLinkDialog(): Unit {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_link, null)
        val editTextName = dialogView.findViewById<EditText>(R.id.edit_text_name)
        val editTextLink = dialogView.findViewById<EditText>(R.id.edit_text_link)

        AlertDialog.Builder(this)
            .setTitle("Add a mountain")
            .setView(dialogView)
            .setPositiveButton("Add") { dialog, _ ->
                val name = editTextName.text.toString()
                val link = editTextLink.text.toString()
                addLinkToHomeFragment(name, link)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun addLinkToHomeFragment(name: String, link: String) {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val homeFragment =
            navHostFragment.childFragmentManager.fragments.find { it is HomeFragment } as? HomeFragment
        homeFragment?.addLink(name, link)
    }
}