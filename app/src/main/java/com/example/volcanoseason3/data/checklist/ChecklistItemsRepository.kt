package com.example.volcanoseason3.data.checklist

class ChecklistItemsRepository(
    private val dao: ChecklistItemDao
) {
    suspend fun insertChecklistItem(item: ChecklistItem) = dao.insert(item)
    suspend fun deleteChecklistItem(item: ChecklistItem) = dao.delete(item)
    suspend fun updateChecklistItem(item: ChecklistItem) = dao.update(item)
    fun getAllChecklistItems() = dao.getAllItems()
}