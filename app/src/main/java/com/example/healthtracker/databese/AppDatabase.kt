package com.example.healthtracker.databese


import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.healthtracker.Dao.UserDao
import com.example.healthtracker.models.UserEntity
import com.example.healthtracker.models.Habit

import com.example.healthtracker.Dao.HabitDao
import android.content.Context
import androidx.room.Room

@Database(entities = [UserEntity::class, Habit::class], version = 2, exportSchema = false) // <--- version++
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun habitDao(): HabitDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "healthtracker_db"
                )
                    .fallbackToDestructiveMigration() // <--- ajoute ça
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
