package com.example.volcanoseason3.ui.home

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.volcanoseason3.R
import com.example.volcanoseason3.data.gallery.ForecastLink
import com.example.volcanoseason3.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: ForecastLinksViewModel by viewModels()

    private lateinit var forecastLinks: RecyclerView
    private lateinit var adapter: ForecastLinkAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Call to activate the actions menu
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        Log.d("HomeFragment", "Called onCreateOptionsMenu; $menu")
        inflater.inflate(R.menu.menu_home, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings_defaults -> {
                populateDefaultForecastLinks()
                true
            }
            R.id.action_settings_organize_list -> {
                showOrganizeSettingsDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("HomeFragment", "onViewCreated")

        forecastLinks = view.findViewById(R.id.rv_forecast_list)
        forecastLinks.layoutManager = LinearLayoutManager(requireContext())
        forecastLinks.setHasFixedSize(true)

        adapter = ForecastLinkAdapter(::onForecastLinkClicked, ::onForecastLinkLongPressed)
        forecastLinks.adapter = adapter

        viewModel.forecastLinks.observe(viewLifecycleOwner) { links ->
            Log.d("HomeFragment", "links: $links")
            adapter.updateForecastLinks(links.toMutableList())
            forecastLinks.scrollToPosition(0)
        }
    }

    fun addLink(name: String, link: String, emoji: String) {
        Log.d("HomeFragment", "Adding link for forecast: $name, $link")
        val newForecastLink = ForecastLink(name, link, emoji)
        viewModel.addForecastLink(newForecastLink)
        adapter.notifyDataSetChanged()
    }

    private fun onForecastLinkClicked(link: ForecastLink) {
        Log.d("HomeFragment", "Clicked on ForecastLink: $link")
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link.url))
        startActivity(intent)
    }

    private fun onForecastLinkLongPressed(link: ForecastLink): Boolean {
        Log.d("HomeFragment", "Long pressed on ForecastLink: $link")
        showConfirmationDialog(link)
        return true
    }

    private fun showConfirmationDialog(link: ForecastLink) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Remove Forecast Link")
        builder.setMessage("Are you sure you want to remove\n${link.name}?")

        builder.setPositiveButton("OK") { dialog, _ ->
            viewModel.removeForecastLink(link)
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun populateDefaultForecastLinks() {
        val linkNames : Array<String> = resources.getStringArray(R.array.forecast_link_names)
        val links : Array<String> = resources.getStringArray(R.array.forecast_links)
        val volcanoEmoji : String = getString(R.string.emoji_volcano)
        val regionEmoji: String = getString(R.string.emoji_region)
        val defaultForecastLinks = ArrayList(
            linkNames.zip(links) { name, link ->
                if (name.contains("NOAA")) {
                    ForecastLink(name, link, regionEmoji)
                } else {
                    ForecastLink(name, link, volcanoEmoji)
                }
            }.toList()
        )
        defaultForecastLinks.forEach { link ->
            viewModel.addForecastLink(link)
        }
    }

    private fun showOrganizeSettingsDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_organize_links, null)
        val spinnerSortBy = dialogView.findViewById<Spinner>(R.id.spinner_sort_by)
        val spinnerOrder = dialogView.findViewById<Spinner>(R.id.spinner_order)
        val spinnerSeparation = dialogView.findViewById<Spinner>(R.id.spinner_separation)

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Organize Forecast Links")
            .setView(dialogView)
            .setPositiveButton("OK") { dialog, _ ->
                val sortBy = spinnerSortBy.selectedItem.toString()
                val order = spinnerOrder.selectedItem.toString()
                val separation = spinnerSeparation.selectedItem.toString()

                // Handle the selected options here
                when (sortBy) {
                    "Alphabetically" -> organizeLinksAlphabetically(order, separation)
                    "By Longitude" -> organizeLinksByLongitude(order, separation)
                    // Add more conditions if needed
                }

                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun organizeLinksAlphabetically(order: String, separation: String) {
        // Implement the logic to organize links alphabetically
    }

    private fun organizeLinksByLongitude(order: String, separation: String) {
        // Implement the logic to organize links by longitude
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d("HomeFragment", "onDestroyView")
    }
}