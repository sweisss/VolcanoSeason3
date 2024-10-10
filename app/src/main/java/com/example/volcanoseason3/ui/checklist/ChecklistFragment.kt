package com.example.volcanoseason3.ui.checklist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.volcanoseason3.R
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

        val checklistViewModel =
            ViewModelProvider(this).get(ChecklistViewModel::class.java)

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
                listItems.add(ChecklistAdapter.ListItem.Header(category))
                // Add all items under the category
                listItems.addAll(checklistItems.map { ChecklistAdapter.ListItem.Item(it) })
            }

            // Submit the transformed list to the adapter
            adapter.submitList(listItems)
        })

        val root: View = binding.root
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}