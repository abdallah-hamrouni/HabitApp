package com.example.healthtracker.Repositrory

import com.example.healthtracker.models.Habit
import com.example.healthtracker.Dao.HabitDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HabitRepository(private val habitDao: HabitDao) {

    fun getHabitsByUserEmail(email: String) =
        habitDao.getHabitsByUserEmail(email)

    // HabitRepository.kt
    suspend fun insertHabit(habit: Habit) {
        withContext(Dispatchers.IO) {
            habitDao.insertHabit(habit)
        }
    }


    suspend fun deleteHabit(habit: Habit) {
        withContext(Dispatchers.IO) {
            habitDao.deleteHabit(habit)
        }
    }
    suspend fun updateHabit(habit: Habit) {
        withContext(Dispatchers.IO) {
            habitDao.updateHabit(habit)
        }
    }

}
