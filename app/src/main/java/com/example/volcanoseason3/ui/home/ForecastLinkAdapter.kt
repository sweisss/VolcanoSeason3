package com.example.volcanoseason3.ui.home

import android.content.ActivityNotFoundException
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.volcanoseason3.R
import com.example.volcanoseason3.data.forecastLinks.ForecastLink
import com.google.android.material.snackbar.Snackbar
import java.util.Collections

class ForecastLinkAdapter(
    private val onClick: (ForecastLink) -> Unit,
    private val onLongClick: (ForecastLink) -> Boolean,
    private var dragDropEnabled: Boolean = false
) : RecyclerView.Adapter<ForecastLinkAdapter.ForecastLinkViewHolder>() {

    private var forecastLinks: MutableList<ForecastLink> = mutableListOf()
    private lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var sharedPreferences: SharedPreferences

    fun setItemTouchHelper(itemTouchHelper: ItemTouchHelper) {
        this.itemTouchHelper = itemTouchHelper
    }

    fun setSharedPreferences(sharedPreferences: SharedPreferences) {
        this.sharedPreferences = sharedPreferences
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(forecastLinks, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(forecastLinks, i, i - 1)
            }
        }
        saveOrderToPreferences()
        notifyItemMoved(fromPosition, toPosition)
    }

    fun saveOrderToPreferences() {
        Log.d("ForecastLinkAdapter", "saveOrderToPreferences called")
        val editor = sharedPreferences.edit()
        val order = forecastLinks.map { it.id }
        editor.putString("forecast_order", order.joinToString(","))
        editor.apply()
    }

    fun loadOrderFromPreferences() {
        val orderString = sharedPreferences.getString("forecast_order", null)
        orderString?.let {
            val order = it.split(",").map { it.toInt() }
            forecastLinks.sortBy { forecastLink -> order.indexOf(forecastLink.id) }
        }
    }

    fun getItemAt(position: Int): ForecastLink {
        return forecastLinks[position]
    }

    fun getForecastLinks(): List<ForecastLink> {
        return forecastLinks.toList()
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
        return ForecastLinkViewHolder(view, onClick, onLongClick, dragDropEnabled, itemTouchHelper)
    }

    override fun onBindViewHolder(
        holder: ForecastLinkViewHolder,
        position: Int
    ) {
        holder.bind(forecastLinks[position], dragDropEnabled)
    }

    fun updateDragDropEnabled(enabled: Boolean) {
        Log.d("ForecastLinkAdapter", "updateDragDropEnabled called with enabled: $enabled")
        dragDropEnabled = enabled
        notifyDataSetChanged()
    }

    class ForecastLinkViewHolder(
        view: View,
        onClick: (ForecastLink) -> Unit,
        onLongClick: (ForecastLink) -> Boolean,
        private val dragDropEnabled: Boolean,
        private val itemTouchHelper: ItemTouchHelper
    ) : RecyclerView.ViewHolder(view) {
        private val forecastNameTV: TextView = view.findViewById(R.id.tv_forecast_name)
        private val dragIndicatorIV: ImageView = view.findViewById(R.id.iv_drag_indicator)
        private var currentForecast: ForecastLink? = null

        init {
            itemView.setOnClickListener {
                currentForecast?.let {
                    if (isValidUrl(it.url)) {
                        try {
                            onClick(it)
                        } catch (e: ActivityNotFoundException) {
                            Log.d("ForecastLinkAdapter", "===>Error: $e")
                            showInvalidUrlSnackbar(
                                "No application can handle this request.\n" +
                                "Please install a web browser or check the URL.\n" +
                                "Ex: https://www.exmple.com"
                            )
                        }
                    } else {
                        showInvalidUrlSnackbar("Invalid URL. Cannot navigate to ${it.name} forecast.")
                    }
                }
            }

            itemView.setOnLongClickListener {
                currentForecast?.let {
                    onLongClick(it)
                } ?: false // Return false if currentForecast is null
            }

            dragIndicatorIV.setOnTouchListener {_, event ->
                if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                    itemTouchHelper.startDrag(this)
                }
                false
            }
        }

        fun bind(forecastlink: ForecastLink, dragDropEnabled: Boolean) {
            val concatenatedName = "${forecastlink.emoji}  ${forecastlink.name}"
            currentForecast = forecastlink
            forecastNameTV.text = concatenatedName
            dragIndicatorIV.visibility = if (dragDropEnabled) View.VISIBLE else View.GONE
        }

        private fun isValidUrl(url: String): Boolean {
            return android.util.Patterns.WEB_URL.matcher(url).matches()
        }

        private fun showInvalidUrlSnackbar(message: String) {
            val snackbar = Snackbar.make(itemView, message, Snackbar.LENGTH_LONG)
            val snackbarView = snackbar.view
            val textView = snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
            textView.maxLines = 5
            textView.textSize = 16f
            snackbar.show()
        }
    }
}