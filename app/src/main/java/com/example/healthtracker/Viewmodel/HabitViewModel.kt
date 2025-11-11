package com.example.healthtracker.Viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.Repositrory.HabitRepository
import com.example.healthtracker.models.Habit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HabitViewModel(
    private val repository: HabitRepository,
    private val userEmail: String
) : ViewModel() {

    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits: StateFlow<List<Habit>> = _habits

    init {
        viewModelScope.launch {
            repository.getHabitsByUserEmail(userEmail).collect {
                _habits.value = it
            }
        }
    }

    fun addHabit(habit: Habit) {
        viewModelScope.launch {
            repository.insertHabit(habit)
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }

    fun toggleDone(habit: Habit) {
        val updatedHabit = habit.copy(done = !habit.done)
        viewModelScope.launch {
            repository.updateHabit(updatedHabit)
        }
    }
    fun addOrUpdateHabit(habit: Habit) {
        viewModelScope.launch {
            if (habit.id == 0) repository.insertHabit(habit)
            else repository.updateHabit(habit)
        }
    }
}


