package com.example.volcanoseason3.ui.checklist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.volcanoseason3.R
import com.example.volcanoseason3.data.checklist.ChecklistItem
import com.example.volcanoseason3.databinding.FragmentChecklistBinding

class ChecklistFragment : Fragment() {
    private var _binding: FragmentChecklistBinding? = null
    private val binding get() = _binding!!
    private lateinit var checklistViewModel: ChecklistViewModel
    private lateinit var adapter: ChecklistAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Call to activate the actions menu
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_checklist, menu)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChecklistBinding.inflate(inflater, container, false)

        checklistViewModel = ViewModelProvider(this).get(ChecklistViewModel::class.java)

        // Set up the RecyclerView and Adapter
        val recyclerView = binding.recyclerViewChecklist
        adapter = ChecklistAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Observe Checklist Items
        checklistViewModel.checklistItems.observe(viewLifecycleOwner, Observer { items ->
            // Group the checklist items by category and prepare the list for the adapter.
            val groupedItems = items.groupBy { it.category }
            val listItems = mutableListOf<ChecklistAdapter.ListItem>()

            // Convert grouped items into ListItems with headers and items.
            groupedItems.forEach { (category, checklistItems) ->
                // Add a header for each category
                val header = ChecklistAdapter.ListItem.Header(category)
                listItems.add(header)
                // Add all items under the category
                if (!header.collapsed) {
                    listItems.addAll(checklistItems.map { ChecklistAdapter.ListItem.Item(it) })
                }
            }

            // Submit the transformed list to the adapter
            adapter.submitList(listItems)
        })

        val root: View = binding.root
        return root
    }

    private fun populateDefaultChecklistItems() {
        val categories = resources.getStringArray(R.array.default_checklist_categories)

        val baseLayers = resources.getStringArray(R.array.default_checklist_base_layers)
        val midLayers = resources.getStringArray(R.array.default_checklist_mid_layers)
        val shellsAndOuterware = resources.getStringArray(R.array.default_checklist_shells_outerwear)
        val headware = resources.getStringArray(R.array.default_checklist_headware)
        val feet = resources.getStringArray(R.array.default_checklist_feet)
        val hands = resources.getStringArray(R.array.default_checklist_hands)
        val gear = resources.getStringArray(R.array.default_checklist_gear)
        val safety = resources.getStringArray(R.array.default_checklist_safety)
        val extras = resources.getStringArray(R.array.default_checklist_extras)
        val carCamping = resources.getStringArray(R.array.default_checklist_car_camping)

        val defaultItems = mutableListOf<ChecklistItem>()

        // Add each item to its respective category
        categories.forEach { category ->
            when (category) {
                "Base Layers" -> {
                    baseLayers.forEach { itemName ->
                        defaultItems.add(ChecklistItem(name = itemName, category = category, isChecked = false))
                    }
                }
                "Mid Layers" -> {
                    midLayers.forEach { itemName ->
                        defaultItems.add(ChecklistItem(name = itemName, category = category, isChecked = false))
                    }
                }
                "Shells/Outerware" -> {
                    shellsAndOuterware.forEach { itemName ->
                        defaultItems.add(ChecklistItem(name = itemName, category = category, isChecked = false))
                    }
                }
                "Headware" -> {
                    headware.forEach { itemName ->
                        defaultItems.add(ChecklistItem(name = itemName, category = category, isChecked = false))
                    }
                }
                "Feet" -> {
                    feet.forEach { itemName ->
                        defaultItems.add(ChecklistItem(name = itemName, category = category, isChecked = false))
                    }
                }
                "Hands" -> {
                    hands.forEach { itemName ->
                        defaultItems.add(ChecklistItem(name = itemName, category = category, isChecked = false))
                    }
                }
                "Gear" -> {
                    gear.forEach { itemName ->
                        defaultItems.add(ChecklistItem(name = itemName, category = category, isChecked = false))
                    }
                }
                "Safety" -> {
                    safety.forEach { itemName ->
                        defaultItems.add(ChecklistItem(name = itemName, category = category, isChecked = false))
                    }
                }
                "Extras" -> {
                    extras.forEach { itemName ->
                        defaultItems.add(ChecklistItem(name = itemName, category = category, isChecked = false))
                    }
                }
                "Car Camping" -> {
                    carCamping.forEach { itemName ->
                        defaultItems.add(ChecklistItem(name = itemName, category = category, isChecked = false))
                    }
                }
            }
        }

        // Add the default items if they don't already exist
        Log.d("ChecklistFragment", "Default items size: ${defaultItems.size}")
        defaultItems.forEach { item ->
            val existingItems = checklistViewModel.checklistItems.value.orEmpty()
            Log.d("ChecklistFragment", "Adding item: ${item.name} in category: ${item.category}")
            if (existingItems.none { it.name == item.name && it.category == item.category }) {
                checklistViewModel.addChecklistItem(item)
                Log.d("ChecklistFragment", "Item added: ${item.name}")
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings_add_defaults -> {
                populateDefaultChecklistItems()
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}