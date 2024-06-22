package com.example.volcanoseason3.data.gallery

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert

@Dao
interface ForecastLinkDao {
    @Insert
    suspend fun insert(link: ForecastLink)

    @Delete
    suspend fun delete(link: ForecastLink)
}