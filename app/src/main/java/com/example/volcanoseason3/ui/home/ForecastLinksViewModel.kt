package com.example.volcanoseason3.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.volcanoseason3.data.gallery.AppDatabase
import com.example.volcanoseason3.data.gallery.ForecastLink
import com.example.volcanoseason3.data.gallery.ForecastLinksRepository
import kotlinx.coroutines.launch

class ForecastLinksViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ForecastLinksRepository(
        AppDatabase.getInstance(application).forecastLinkDao()
    )

    fun addForecastLink(link: ForecastLink) {
        viewModelScope.launch {
            repository.insertForecastLink(link)
        }
    }

    fun removeForecastLink(link: ForecastLink) {
        viewModelScope.launch {
            repository.deleteForecastLink(link)
        }
    }
}