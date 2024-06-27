package com.example.volcanoseason3.ui.home

import android.app.AlertDialog
import android.content.Context
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
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.volcanoseason3.R
import com.example.volcanoseason3.data.gallery.ForecastLink
import com.example.volcanoseason3.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar
import java.time.LocalTime

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
            R.id.action_settings_display_size -> {
                Snackbar.make(binding.root, "Add the display settings action here.", Snackbar.LENGTH_LONG).show()
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

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val forecastLink = adapter.getItemAt(position)
                showRemoveLinkConfirmationDialog(forecastLink, position)
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(forecastLinks)

        viewModel.forecastLinks.observe(viewLifecycleOwner) { links ->
            val sortedLinks = separateLinks(sortLinks(links.toMutableList()))
            adapter.updateForecastLinks(sortedLinks)
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
        Snackbar.make(binding.root, "Add the edit ForecastLink option here", Snackbar.LENGTH_LONG).show()
        return true
    }

    private fun showRemoveLinkConfirmationDialog(link: ForecastLink, position: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Remove Forecast Link")
        builder.setMessage("Are you sure you want to remove\n${link.emoji} ${link.name}?")

        builder.setPositiveButton("OK") { dialog, _ ->
            viewModel.removeForecastLink(link)
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            adapter.notifyItemChanged(position)
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
        val spinnerSeparation = dialogView.findViewById<Spinner>(R.id.spinner_separation)
        val spinnerCustomOptions = dialogView.findViewById<Spinner>(R.id.spinner_custom)

        val sharedPreferences = requireContext().getSharedPreferences("HomeFragmentSettings", Context.MODE_PRIVATE)

        // Load previously saved settings
        val savedSortBy = sharedPreferences.getString("sortBy", getString(R.string.pref_sort_val_alphabet))
        val savedSeparation = sharedPreferences.getString("separation", getString(R.string.prefs_separate_val_volcano))
        val savedCustom = sharedPreferences.getString("custom", getString(R.string.pref_custom_val_1))

        // Map saved values to entries and set them as selected values in spinners
        setSpinnerSelection(spinnerSortBy, savedSortBy, R.array.options_sort_by_entries, R.array.options_sort_by_values)
        setSpinnerSelection(spinnerSeparation, savedSeparation, R.array.options_separation_entries, R.array.options_separation_values)
        setSpinnerSelection(spinnerCustomOptions, savedCustom, R.array.options_custom_entries, R.array.options_custom_values)

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Organize Forecast Links")
            .setView(dialogView)
            .setPositiveButton("OK") { dialog, _ ->
                val sortBy = getSpinnerValue(spinnerSortBy, R.array.options_sort_by_values)
                val separation = getSpinnerValue(spinnerSeparation, R.array.options_separation_values)
                val custom = getSpinnerValue(spinnerCustomOptions, R.array.options_custom_values)

                // Save the selected settings to SharedPreferences
                with(sharedPreferences.edit()) {
                    putString("sortBy", sortBy)
                    putString("separation", separation)
                    putString("custom", custom)
                    apply()
                }

                // Apply the new sorting
                val links = viewModel.forecastLinks.value ?: emptyList()
                val sortedLinks = separateLinks(sortLinks(links.toMutableList()))
                adapter.updateForecastLinks(sortedLinks)

                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun setSpinnerSelection(spinner: Spinner, value: String?, entriesArrayResId: Int, valuesArrayResId: Int) {
        val entries = resources.getStringArray(entriesArrayResId)
        val values = resources.getStringArray(valuesArrayResId)
        val index = values.indexOf(value)
        if (index != -1) {
            spinner.setSelection(index)
        }
    }

    private fun getSpinnerValue(spinner: Spinner, valuesArrayResId: Int): String {
        val values = resources.getStringArray(valuesArrayResId)
        return values[spinner.selectedItemPosition]
    }

    private fun sortLinks(links: MutableList<ForecastLink>) : MutableList<ForecastLink> {
        val sharedPreferences = requireContext().getSharedPreferences("HomeFragmentSettings", Context.MODE_PRIVATE)
        val sortBy = sharedPreferences.getString("sortBy", getString(R.string.pref_sort_val_alphabet))

        return  when (sortBy) {
            getString(R.string.pref_sort_val_alphabet) -> {
                links.sortedBy { normalizeName(it.name) }.toMutableList()
            }
            getString(R.string.pref_sort_val_longitude) -> {
                // placeholder sorting style: reverse alphabetical order
                links.sortedByDescending { normalizeName(it.name) }.toMutableList()
            }
            else -> links
        }
    }

    private fun normalizeName(name: String): String {
        return name.replaceFirst("^(Mt\\.?\\s*|Mount\\s*)".toRegex(RegexOption.IGNORE_CASE), "").trim()
    }

    private fun separateLinks(links: MutableList<ForecastLink>) : MutableList<ForecastLink> {
        val sharedPreferences = requireContext().getSharedPreferences("HomeFragmentSettings", Context.MODE_PRIVATE)
        val separateOrder = sharedPreferences.getString("separation", getString(R.string.prefs_separate_val_volcano))
        var volcanoes = mutableListOf<ForecastLink>()
        var regions = mutableListOf<ForecastLink>()

        links.forEach {
            if (it.emoji == getString(R.string.emoji_volcano)) {
                volcanoes += it
            } else {
                regions += it
            }
        }

        return when (separateOrder) {
            getString(R.string.prefs_separate_val_volcano) -> {
                return (volcanoes + regions).toMutableList()
            }
            getString(R.string.prefs_separate_val_region) -> {
                return (regions + volcanoes).toMutableList()
            }
            else -> links
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d("HomeFragment", "onDestroyView")
    }
}