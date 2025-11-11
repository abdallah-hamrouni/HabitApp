package com.example.healthtracker.Dao

import androidx.room.*
import com.example.healthtracker.models.Habit



import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Query("SELECT * FROM habits WHERE userEmail = :email")
    fun getHabitsByUserEmail(email: String): Flow<List<Habit>>


    @Insert
    suspend fun insertHabit(habit: Habit)

    @Delete
    suspend fun deleteHabit(habit: Habit)

    @Update
    suspend fun updateHabit(habit: Habit)
}
