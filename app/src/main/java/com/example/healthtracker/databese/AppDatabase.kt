package com.example.healthtracker.databese


import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.healthtracker.Dao.UserDao
import com.example.healthtracker.models.UserEntity

import android.content.Context
import androidx.room.Room

@Database(entities = [UserEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "healthtracker.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
