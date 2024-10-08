package com.example.volcanoseason3.data.checklist

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ChecklistItem")
data class ChecklistItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    var isChecked: Boolean = false,
    val category: String
)
