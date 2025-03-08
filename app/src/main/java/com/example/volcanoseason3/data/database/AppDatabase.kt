package com.example.volcanoseason3.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.volcanoseason3.data.checklist.ChecklistItem
import com.example.volcanoseason3.data.checklist.ChecklistItemDao
import com.example.volcanoseason3.data.forecastLinks.ForecastLink
import com.example.volcanoseason3.data.forecastLinks.ForecastLinkDao

const val DATABASE_NAME = "volcano-season-db"

@Database(
    entities = [ForecastLink::class, ChecklistItem::class],
    version = 5
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun forecastLinkDao() : ForecastLinkDao
    abstract fun checklistItemDao() : ChecklistItemDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                .fallbackToDestructiveMigration()
//                .addMigrations(MIGRATION_4_5)
                .build()

        fun getInstance(context: Context) : AppDatabase {
            return  instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also {
                    instance = it
                }
            }
        }

        // Migration from version 4 to 5
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add the isChecked column to the existing table with a default value of false
                db.execSQL("ALTER TABLE checklist_items ADD COLUMN isChecked INTEGER NOT NULL DEFAULT 0")
            }
        }

        // Migration from version 3 to 4 (currently unused with fallbackToDestructiveMigration)
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS ChecklistItem (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        category TEXT NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }
    }
}