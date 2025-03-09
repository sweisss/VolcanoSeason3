package com.example.volcanoseason3.data.checklist

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ChecklistItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: ChecklistItem)

    @Delete
    suspend fun delete(item: ChecklistItem)

    @Update
    suspend fun update(item: ChecklistItem)

    @Query("SELECT * FROM ChecklistItem")
    fun getAllItems(): Flow<List<ChecklistItem>>

    @Query("UPDATE ChecklistItem SET isChecked = :isChecked WHERE id = :id")
    suspend fun updateChecklistItemChecked(id: Int, isChecked: Boolean)

    @Query("UPDATE ChecklistItem SET isChecked = 0")
    suspend fun uncheckAllItems()
}