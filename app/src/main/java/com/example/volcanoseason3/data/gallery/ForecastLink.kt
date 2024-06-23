package com.example.volcanoseason3.data.gallery

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ForecastLink(
    @PrimaryKey val name: String,
    val url: String
)
