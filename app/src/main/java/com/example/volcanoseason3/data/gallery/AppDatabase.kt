package com.example.volcanoseason3.data.gallery

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

const val DATABASE_NAME = "forecast-links-db"

@Database(entities = [ForecastLink::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun forecastLinkDao() : ForecastLinkDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                DATABASE_NAME
            ).build()

        fun getInstance(context: Context) : AppDatabase {
            return  instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also {
                    instance = it
                }
            }
        }
    }
}