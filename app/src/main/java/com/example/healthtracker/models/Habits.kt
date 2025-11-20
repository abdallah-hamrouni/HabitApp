package com.example.healthtracker.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val frequency: String,
    val time: String,
    val category: String,
    val done: Boolean = false,
    val title: String,
    val userEmail: String
)