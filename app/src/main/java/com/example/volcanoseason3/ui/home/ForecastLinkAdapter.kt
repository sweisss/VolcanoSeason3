package com.example.volcanoseason3.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.volcanoseason3.R
import com.example.volcanoseason3.data.gallery.ForecastLink

class ForecastLinkAdapter(
    private val onClick: (ForecastLink) -> Unit,
    private val onLongClick: (ForecastLink) -> Boolean
) : RecyclerView.Adapter<ForecastLinkAdapter.ForecastLinkViewHolder>() {
    private var forecastLinks: MutableList<ForecastLink> = mutableListOf()

    fun getItemAt(position: Int): ForecastLink {
        return forecastLinks[position]
    }

    fun updateForecastLinks(updatedLinks: MutableList<ForecastLink>) {
        notifyItemRangeRemoved(0, forecastLinks.size)
        forecastLinks = updatedLinks
        notifyItemRangeInserted(0, forecastLinks.size)
    }

    override fun getItemCount(): Int = forecastLinks.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ForecastLinkViewHolder {
        val view =
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.forecast_link_list_item, parent, false)
        return ForecastLinkViewHolder(view, onClick, onLongClick)
    }

    override fun onBindViewHolder(
        holder: ForecastLinkViewHolder,
        position: Int
    ) {
        holder.bind(forecastLinks[position])
    }

    class ForecastLinkViewHolder(
        view: View,
        onClick: (ForecastLink) -> Unit,
        onLongClick: (ForecastLink) -> Boolean
    ) : RecyclerView.ViewHolder(view) {
        private val forecastNameTV: TextView = view.findViewById(R.id.tv_forecast_name)
        private var currentForecast: ForecastLink? = null

        init {
            itemView.setOnClickListener {
                currentForecast?.let { onClick(it) }
            }
            itemView.setOnLongClickListener {
                currentForecast?.let {
                    onLongClick(it)
                } ?: false // Return false if currentForecast is null
            }
        }

        fun bind(forecastlink: ForecastLink) {
            currentForecast = forecastlink
            forecastNameTV.text = forecastlink.name
        }
    }

    private fun isValidUrl(url: String): Boolean {
        return android.util.Patterns.WEB_URL.matcher(url).matches()
    }
}