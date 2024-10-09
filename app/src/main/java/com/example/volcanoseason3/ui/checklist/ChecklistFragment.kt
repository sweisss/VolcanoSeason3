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
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Observe Checklist Items
        checklistViewModel.checklistItems.observe(viewLifecycleOwner, Observer { items ->
            adapter.submitList(items)
        })

        val root: View = binding.root
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}