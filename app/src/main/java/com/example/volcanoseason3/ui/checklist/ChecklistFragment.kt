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
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.volcanoseason3.R
import com.example.volcanoseason3.data.checklist.ChecklistItem
import com.example.volcanoseason3.databinding.FragmentChecklistBinding
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class ChecklistFragment : Fragment(), ChecklistAdapter.CategoryStateListener {
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
        adapter = ChecklistAdapter { id, isChecked ->
            checklistViewModel.updateChecklistItemChecked(id, isChecked)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Set the listener
        adapter.categoryStateListener = this

        // Observe Checklist Items
        checklistViewModel.checklistItems.observe(viewLifecycleOwner, Observer { items ->
            val groupedItems = items.groupBy { it.category }
            val listItems = mutableListOf<ChecklistAdapter.ListItem>()

            Log.d("ChecklistFragment", "Grouped items: $groupedItems")
            groupedItems.forEach { (category, checklistItems) ->
                val isExpanded = adapter.isCategoryExpanded(category)
                Log.d("ChecklistFragment", "Processing category: $category, isExpanded: $isExpanded")

                listItems.add(ChecklistAdapter.ListItem.Header(category))
                if (isExpanded) {
                    Log.d("ChecklistFragment", "Adding items for category: $category")
                    listItems.addAll(checklistItems.map { ChecklistAdapter.ListItem.Item(it) })
                } else {
                    Log.d("ChecklistFragment", "Skipping items for collapsed category: $category")
                }
            }

            Log.d("ChecklistFragment", "Final list items: $listItems")
            adapter.submitList(listItems)
        })

        setupSwipeToDelete()

        val root: View = binding.root
        return root
    }

    private fun setupSwipeToDelete() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false // No drag-and-drop support
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val item = adapter.currentList[position] as? ChecklistAdapter.ListItem.Item

                if (item != null) {
                    showDeleteConfirmationDialog(item.checklistItem)
                } else {
                    adapter.notifyItemChanged(position) // Reset if swipe is invalid
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewChecklist)
    }

    private fun showDeleteConfirmationDialog(item: ChecklistItem) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Item")
            .setMessage("Are you sure you want to remove '${item.name}' from the checklist?")
            .setPositiveButton("Delete") { _, _ ->
                checklistViewModel.deleteChecklistItem(item)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                adapter.notifyDataSetChanged() // Reset swipe animation
            }
            .setOnDismissListener {
                adapter.notifyDataSetChanged() // Ensure the list refreshes properly
            }
            .show()
    }

    override fun onCategoryStateChanged() {
        Log.d("ChecklistFragment", "Category state changed, recomputing list")
        // Recompute the list and submit to the adapter
        val items = checklistViewModel.checklistItems.value ?: emptyList()
        val groupedItems = items.groupBy { it.category }
        val listItems = mutableListOf<ChecklistAdapter.ListItem>()

        groupedItems.forEach { (category, checklistItems) ->
            listItems.add(ChecklistAdapter.ListItem.Header(category))
            if (adapter.isCategoryExpanded(category)) {
                listItems.addAll(checklistItems.map { ChecklistAdapter.ListItem.Item(it) })
            }
        }
        adapter.submitList(listItems)
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