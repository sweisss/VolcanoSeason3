package com.example.volcanoseason3.data.gallery

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ForecastLinkDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(link: ForecastLink)

    @Delete
    suspend fun delete(link: ForecastLink)

    @Update
    suspend fun update(link: ForecastLink)

    @Query("SELECT * FROM ForecastLink")
    fun getAllLinks(): Flow<List<ForecastLink>>
}