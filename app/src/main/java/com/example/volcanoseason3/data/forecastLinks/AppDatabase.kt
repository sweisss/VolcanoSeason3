package com.example.volcanoseason3.data.forecastLinks

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

const val DATABASE_NAME = "forecast-links-db"

@Database(
    entities = [ForecastLink::class],
    version = 3
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun forecastLinkDao() : ForecastLinkDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                .addMigrations(MIGRATION_2_3)
                .build()

        fun getInstance(context: Context) : AppDatabase {
            return  instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also {
                    instance = it
                }
            }
        }

        // Manual Migration to version 3
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Create a new table with the new schema
                db.execSQL(
                    """
                    CREATE TABLE new_ForecastLink (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        url TEXT NOT NULL,
                        emoji TEXT NOT NULL DEFAULT '🌋'
                    )
                    """.trimIndent()
                )
                // Copy the data from the old table to the new table
                db.execSQL(
                    """
                    INSERT INTO new_ForecastLink (name, url, emoji)
                    SELECT name, url, emoji FROM ForecastLink
                    """.trimIndent()
                )
                // Remove the old table
                db.execSQL("DROP TABLE ForecastLink")
                // Rename the new table to the old table name
                db.execSQL("ALTER TABLE new_ForecastLink RENAME TO ForecastLink")
            }
        }
    }
}