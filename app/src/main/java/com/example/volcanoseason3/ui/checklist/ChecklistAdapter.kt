package com.example.volcanoseason3.ui.checklist

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
    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1
    }

    sealed class ListItem {
        data class Header(val category: String, var collapsed: Boolean = false) : ListItem()
        data class Item(val checklistItem: ChecklistItem) : ListItem()
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

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val headerText: TextView = itemView.findViewById(R.id.tv_header)
        private val expandCollapseIcon: ImageView = itemView.findViewById(R.id.iv_expand_collapse)

        fun bind(header: ListItem.Header) {
            headerText.text = header.category
            expandCollapseIcon.setImageResource(
                if (header.collapsed) R.drawable.baseline_arrow_drop_down_24
                else R.drawable.baseline_arrow_drop_up_24
            )
            itemView.setOnClickListener {
                header.collapsed = !header.collapsed
                notifyDataSetChanged()
            }
        }
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkBox: CheckBox = itemView.findViewById(R.id.checkbox_item)

        fun bind(item: ChecklistItem) {
            checkBox.text = item.name
            checkBox.isChecked = item.isChecked
            // TODO: Handle the checkbox state changes and updates here
        }
    }

    class ChecklistDiffCallback : DiffUtil.ItemCallback<ListItem>() {
        override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return oldItem == newItem
        }
    }
}