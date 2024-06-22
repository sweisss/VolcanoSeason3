package com.example.volcanoseason3.ui.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.volcanoseason3.R
import com.example.volcanoseason3.data.gallery.ForecastLink
import com.google.android.material.snackbar.Snackbar

class ForecastLinkAdapter(
    context: Context,
    private val mountains: List<ForecastLink>
) : ArrayAdapter<ForecastLink>(context, 0, mountains) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItemView = convertView

        // Check if an existing view is being reused, otherwise inflate the view
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(
                R.layout.forecast_link_list_item,
                parent,
                false
            )
        }

        val currentMountain = mountains[position]

        // Set the name of the forecast mountain or region to the TextView
        val nameTextView: TextView = listItemView!!.findViewById(R.id.tv_mountain_name)
        nameTextView.text = currentMountain.name

        // Set the link of the forecast mountain or region to the click listener
        listItemView.setOnClickListener {
            if (isValidUrl(currentMountain.url)) {
                try {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setData(Uri.parse(currentMountain.url))
                    listItemView.context.startActivity(intent)
                } catch (e: Exception) {
                    Snackbar.make(
                        listItemView,
                        "No application can handle this request. Please install a web browser or check the URL.",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            } else {
                Snackbar.make(
                    listItemView,
                    "Invalid URL. Cannot navigate to ${currentMountain.name} forecast.",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        // Set a long-press listener for each ForecastLink item
        listItemView.setOnLongClickListener {
            Snackbar.make(
                listItemView,
                "Remove ${currentMountain.name}",
                Snackbar.LENGTH_LONG).show()
            true
        }

        return listItemView
    }

    private fun isValidUrl(url: String): Boolean {
        return android.util.Patterns.WEB_URL.matcher(url).matches()
    }
}