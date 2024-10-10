package com.example.volcanoseason3.ui.checklist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.volcanoseason3.data.checklist.ChecklistItem
import com.example.volcanoseason3.data.checklist.ChecklistItemDao
import com.example.volcanoseason3.data.checklist.ChecklistItemsRepository
import com.example.volcanoseason3.data.database.AppDatabase
import kotlinx.coroutines.launch

class ChecklistViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ChecklistItemsRepository(
        AppDatabase.getInstance(application).checklistItemDao()
    )

    val checklistItems = repository.getAllChecklistItems().asLiveData()

    fun addChecklistItem(item: ChecklistItem) {
        viewModelScope.launch { repository.insertChecklistItem(item) }
    }

    fun updateChecklistItem(item: ChecklistItem) {
        viewModelScope.launch { repository.updateChecklistItem(item) }
    }

    fun deleteChecklistItem(item: ChecklistItem) {
        viewModelScope.launch { repository.deleteChecklistItem(item) }
    }
}