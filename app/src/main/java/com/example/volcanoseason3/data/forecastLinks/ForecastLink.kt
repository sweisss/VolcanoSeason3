package com.example.volcanoseason3.data.forecastLinks

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ForecastLink(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val url: String,
    @ColumnInfo(name = "emoji", defaultValue = "\uD83C\uDF0B")
    val emoji: String = "\uD83C\uDF0B"
)
