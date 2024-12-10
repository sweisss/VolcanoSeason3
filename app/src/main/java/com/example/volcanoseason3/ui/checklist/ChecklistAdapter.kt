package com.example.volcanoseason3.ui.checklist

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.volcanoseason3.R
import com.example.volcanoseason3.data.checklist.ChecklistItem

class ChecklistAdapter : ListAdapter<ChecklistAdapter.ListItem, RecyclerView.ViewHolder>(ChecklistDiffCallback()) {
    private val expandedCategories = mutableSetOf<String>()

    // Callback interface
    interface CategoryStateListener {
        fun onCategoryStateChanged()
    }
    var categoryStateListener: CategoryStateListener? = null

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1
    }

    sealed class ListItem {
        data class Header(val category: String) : ListItem()
        data class Item(val checklistItem: ChecklistItem) : ListItem()
    }

    fun isCategoryExpanded(category: String): Boolean {
        Log.d("ChecklistAdapter", "Checking if category is expanded: $category -> ${expandedCategories.contains(category)}")
        return expandedCategories.contains(category)
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ListItem.Header -> VIEW_TYPE_HEADER
            is ListItem.Item -> VIEW_TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_checklist_header, parent, false)
                HeaderViewHolder(view)
            }
            VIEW_TYPE_ITEM -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_checklist_item, parent, false)
                ItemViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val listItem = getItem(position)) {
            is ListItem.Header -> (holder as HeaderViewHolder).bind(listItem)
            is ListItem.Item -> (holder as ItemViewHolder).bind(listItem.checklistItem)
        }
    }

    override fun submitList(list: MutableList<ListItem>?) {
        // Retain the expandedCategories set when submitting a new list
        val previousExpandedCategories = expandedCategories.toSet()
        Log.d("ChecklistAdapter", "submitList called with list: $list")

        super.submitList(list)

        // Restore the expanded categories after the new list is submitted
        expandedCategories.clear()
        expandedCategories.addAll(previousExpandedCategories)
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val headerText: TextView = itemView.findViewById(R.id.tv_header)
        private val expandCollapseIcon: ImageView = itemView.findViewById(R.id.iv_expand_collapse)

        fun bind(header: ListItem.Header) {
            headerText.text = header.category
            val isExpanded = expandedCategories.contains(header.category)

            Log.d("ChecklistAdapter", "Binding header: ${header.category}, isExpanded: $isExpanded")

            // Update the expand/collapse icon
            expandCollapseIcon.setImageResource(
                if (isExpanded) {
                    R.drawable.baseline_arrow_drop_up_24
                } else {
                    R.drawable.baseline_arrow_drop_down_24
                }
            )

            itemView.setOnClickListener {
                Log.d("ChecklistAdapter", "Category clicked: ${header.category}")
                Log.d("ChecklistAdapter", "isExpanded: ${isExpanded}")
                Log.d("ChecklistAdapter", "Before update: $expandedCategories")
                val currentPosition = adapterPosition
                if (isExpanded) {
                    // Collapse the category
                    expandedCategories.remove(header.category)
                    Log.d("ChecklistAdapter", "Category collapsed: ${header.category}")
                } else {
                    // Expand the category
                    expandedCategories.add(header.category)
                    Log.d("ChecklistAdapter", "Category expanded: ${header.category}")
                }

                // Notify the listener
                categoryStateListener?.onCategoryStateChanged()

                // Update the expand/collapse icon
                expandCollapseIcon.setImageResource(
                    if (isExpanded) {
                        R.drawable.baseline_arrow_drop_down_24
                    } else {
                        R.drawable.baseline_arrow_drop_up_24
                    }
                )
                Log.d("ChecklistAdapter", "After update: $expandedCategories")
                notifyItemChanged(adapterPosition)
            }
        }
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkBox: CheckBox = itemView.findViewById(R.id.checkbox_item)

        fun bind(item: ChecklistItem) {
            Log.d("ChecklistAdapter", "Binding checklist item: ${item.name}")
            checkBox.text = item.name
            checkBox.isChecked = item.isChecked
            // TODO: Handle the checkbox state changes and updates here
        }
    }

    class ChecklistDiffCallback : DiffUtil.ItemCallback<ListItem>() {
        override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
//            return oldItem == newItem
            return when {
                oldItem is ListItem.Header && newItem is ListItem.Header ->
                    oldItem.category == newItem.category
                oldItem is ListItem.Item && newItem is ListItem.Item ->
                    oldItem.checklistItem.id == newItem.checklistItem.id
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return oldItem == newItem
        }
    }
}