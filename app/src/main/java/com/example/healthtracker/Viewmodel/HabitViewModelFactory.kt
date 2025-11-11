package com.example.healthtracker.Viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.healthtracker.Repositrory.HabitRepository

class HabitViewModelFactory(
    private val repository: HabitRepository,
    private val loggedEmail: String
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HabitViewModel::class.java)) {
            return HabitViewModel(repository, loggedEmail) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
