package com.example.volcanoseason3.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.volcanoseason3.data.database.AppDatabase
import com.example.volcanoseason3.data.forecastLinks.ForecastLink
import com.example.volcanoseason3.data.forecastLinks.ForecastLinksRepository
import kotlinx.coroutines.launch

class ForecastLinksViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ForecastLinksRepository(
        AppDatabase.getInstance(application).forecastLinkDao()
    )

    val forecastLinks = repository.getAllForecastLinks().asLiveData()

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

    fun updateForecastLink(link: ForecastLink) {
        viewModelScope.launch {
            repository.updateForecastLink(link)
        }
    }
}