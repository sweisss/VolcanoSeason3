package com.example.volcanoseason3.ui.gallery

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.volcanoseason3.R
import com.example.volcanoseason3.data.gallery.MountainLink
import com.google.android.material.snackbar.Snackbar

class MountainLinkAdapter(
    context: Context,
    private val mountains: List<MountainLink>
) : ArrayAdapter<MountainLink>(context, 0, mountains) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItemView = convertView

        // Check if an existing view is being reused, otherwise inflate the view
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(
                R.layout.gallery_list_item,
                parent,
                false
            )
        }

        val currentMountain = mountains[position]

        // Set the name of the mountain to the TextView
        val nameTextView: TextView = listItemView!!.findViewById(R.id.tv_mountain_name)
        nameTextView.text = currentMountain.name

        // Set the link of the mountain to the click listener
        listItemView.setOnClickListener {
            Snackbar.make(listItemView, "Put the link here", Snackbar.LENGTH_LONG).show()
        }

        return listItemView
    }
}