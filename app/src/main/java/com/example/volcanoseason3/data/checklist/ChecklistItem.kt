package com.example.volcanoseason3.data.checklist

data class ChecklistItem(
    val id: Int = 0, // Will autogenerate when connected to the Room database
    val name: String,
    var isChecked: Boolean = false,
    val category: String
)
