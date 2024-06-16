package com.example.volcanoseason3.ui.checklist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChecklistViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is checklist Fragment"
    }
    val text: LiveData<String> = _text
}