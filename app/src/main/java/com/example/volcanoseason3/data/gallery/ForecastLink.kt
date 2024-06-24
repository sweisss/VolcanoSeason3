package com.example.volcanoseason3.data.gallery

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ForecastLink(
    @PrimaryKey val name: String,
    val url: String,
    @ColumnInfo(name = "emoji", defaultValue = "\uD83C\uDF0B")
    val emoji: String = "\uD83C\uDF0B"
)
