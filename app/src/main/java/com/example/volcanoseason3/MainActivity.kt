package com.example.volcanoseason3

import android.os.Bundle
import android.provider.MediaStore.Audio.Radio
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
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
        val radioGroupOptions = dialogView.findViewById<RadioGroup>(R.id.radioGroupOptions)
        val radioButtonVolcano = dialogView.findViewById<RadioButton>(R.id.radio_button_volcano)
        val radioButtonRegion = dialogView.findViewById<RadioButton>(R.id.radio_button_region)

        AlertDialog.Builder(this)
            .setTitle("Add a mountain")
            .setView(dialogView)
            .setPositiveButton("Add") { dialog, _ ->
                val name = editTextName.text.toString()
                val link = editTextLink.text.toString()
                val selectedRadio = when (radioGroupOptions.checkedRadioButtonId) {
                    R.id.radio_button_volcano -> getString(R.string.emoji_volcano)
                    R.id.radio_button_region -> getString(R.string.emoji_region)
                    else -> ""
                }
                val combinedName = "$selectedRadio $name"
                if (isValidUrl(link)) {
                    addLinkToHomeFragment(combinedName, link)
                } else {
                    Snackbar.make(binding.root, "Invalid URL. Please try again", Snackbar.LENGTH_LONG).show()
                }
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

    private fun isValidUrl(url: String): Boolean {
        return android.util.Patterns.WEB_URL.matcher(url).matches()
    }
}