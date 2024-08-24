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
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Switch
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
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

    // This property is only valid between onCreateView and onDestroyView.
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

//        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
//            0,
//            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
//        ) {
        val itemTouchHelperCallback = object : ItemTouchHelper.Callback() {

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                val swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                return makeMovementFlags(dragFlags, swipeFlags)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
//                return false
                adapter.moveItem(viewHolder.adapterPosition, target.adapterPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val forecastLink = adapter.getItemAt(position)
                showRemoveLinkConfirmationDialog(forecastLink, position)
            }

            override fun isLongPressDragEnabled(): Boolean {
                return false // False so drag is only enabled from the drag handle.
            }

            override fun isItemViewSwipeEnabled(): Boolean {
                return true
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        adapter.setItemTouchHelper(itemTouchHelper)
        itemTouchHelper.attachToRecyclerView(forecastLinks)

        viewModel.forecastLinks.observe(viewLifecycleOwner) { links ->
            val sortedLinks = separateLinks(sortLinks(links.toMutableList()))
            adapter.updateForecastLinks(sortedLinks)
            forecastLinks.scrollToPosition(0)
        }
    }

    fun addLink(name: String, link: String, emoji: String) {
        Log.d("HomeFragment", "Adding link for forecast: $name, $link")
        val newForecastLink = ForecastLink(name = name, url = link, emoji = emoji)
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
        showEditLinkDialog(link)
        return true
    }

    private fun showEditLinkDialog(link: ForecastLink): Unit {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_link, null)
        val editTextName = dialogView.findViewById<EditText>(R.id.edit_text_name)
        val editTextUrl = dialogView.findViewById<EditText>(R.id.edit_text_link)
        val radioGroupOptions = dialogView.findViewById<RadioGroup>(R.id.radioGroupOptions)
        val radioButtonVolcano = dialogView.findViewById<RadioButton>(R.id.radio_button_volcano)
        val radioButtonArea = dialogView.findViewById<RadioButton>(R.id.radio_button_region)

        // Set the default values to the current ForecastLink attribute values
        editTextName.setText(link.name)
        editTextUrl.setText(link.url)
        when (link.emoji) {
            getString(R.string.emoji_volcano) -> radioButtonVolcano.isChecked = true
            getString(R.string.emoji_region) -> radioButtonArea.isChecked = true
        }

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Edit Forecast Link")    // Maybe put this in strings.xml
            .setView(dialogView)
            .setPositiveButton("Update") { dialog, _ ->
                val updatedName = editTextName.text.toString()
                val updatedUrl = editTextUrl.text.toString()
                val updatedEmoji = when (radioGroupOptions.checkedRadioButtonId) {
                    R.id.radio_button_volcano -> getString(R.string.emoji_volcano)
                    R.id.radio_button_region -> getString(R.string.emoji_region)
                    else -> link.emoji
                }
                val updatedLink = link.copy(name = updatedName, url = updatedUrl, emoji = updatedEmoji)
                viewModel.updateForecastLink(updatedLink)
                adapter.notifyDataSetChanged()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
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
                    ForecastLink(name = name, url = link, emoji = regionEmoji)
                } else {
                    ForecastLink(name = name, url = link, emoji = volcanoEmoji)
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
        val switchDragDrop = dialogView.findViewById<SwitchCompat>(R.id.switch_drag_n_drop)

        val sharedPreferences = requireContext().getSharedPreferences("HomeFragmentSettings", Context.MODE_PRIVATE)

        // Load previously saved settings
        val savedSortBy = sharedPreferences.getString("sortBy", getString(R.string.pref_sort_val_alphabet))
        val savedSeparation = sharedPreferences.getString("separation", getString(R.string.prefs_separate_val_volcano))
        val savedDragDrop = sharedPreferences.getString("dragDrop", "disabled")

        // Set the state of the switch based on saved value
        var isDragDropEnabled = savedDragDrop == "enabled"
        switchDragDrop.isChecked = isDragDropEnabled
        spinnerSortBy.isEnabled = !isDragDropEnabled
        spinnerSeparation.isEnabled = !isDragDropEnabled

        // Set an OnCheckedChangeListener for the Switch
        switchDragDrop.setOnCheckedChangeListener { _, isChecked ->
            spinnerSortBy.isEnabled = !isChecked
            spinnerSeparation.isEnabled = !isChecked

            // Update the temporary Drag Drop variable
            isDragDropEnabled = isChecked
        }

        // Map saved values to entries and set them as selected values in spinners
        setSpinnerSelection(spinnerSortBy, savedSortBy, R.array.options_sort_by_entries, R.array.options_sort_by_values)
        setSpinnerSelection(spinnerSeparation, savedSeparation, R.array.options_separation_entries, R.array.options_separation_values)

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Organize Forecast Links")
            .setView(dialogView)
            .setPositiveButton("OK") { dialog, _ ->
                val sortBy = getSpinnerValue(spinnerSortBy, R.array.options_sort_by_values)
                val separation = getSpinnerValue(spinnerSeparation, R.array.options_separation_values)
                val dragDropState = if (isDragDropEnabled) "enabled" else "disabled"

                // Save the selected settings to SharedPreferences
                with(sharedPreferences.edit()) {
                    putString("sortBy", sortBy)
                    putString("separation", separation)
                    putString("dragDrop", dragDropState)
                    apply()
                }

                // Apply the new sorting
                val links = viewModel.forecastLinks.value ?: emptyList()
                val sortedLinks = separateLinks(sortLinks(links.toMutableList()))
                adapter.updateForecastLinks(sortedLinks)
                adapter.updateDragDropEnabled(isDragDropEnabled)

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