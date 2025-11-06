package com.example.healthtracker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Modèle local pour l'habite
data class Habit(
    val id: Int,
    var name: String,
    var frequency: String,
    var time: String,
    var category: String,
    var done: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitScreen() {
    var habits by remember { mutableStateOf(mutableListOf<Habit>()) }
    var showDialog by remember { mutableStateOf(false) }
    var editingHabit by remember { mutableStateOf<Habit?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mes Habitudes") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editingHabit = null
                showDialog = true
            }) { Text("+") }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5)),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(habits, key = { it.id }) { habit ->
                HabitItem(
                    habit = habit,
                    onEdit = { editingHabit = habit; showDialog = true },
                    onDelete = { habits.remove(habit) },
                    onToggleDone = { habit.done = !habit.done }
                )
            }
        }

        if (showDialog) {
            HabitDialog(
                habit = editingHabit,
                onDismiss = { showDialog = false },
                onSave = { newHabit ->
                    if (editingHabit != null) {
                        val index = habits.indexOfFirst { it.id == editingHabit!!.id }
                        habits[index] = newHabit
                    } else {
                        habits.add(newHabit.copy(id = habits.size + 1))
                    }
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun HabitItem(
    habit: Habit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleDone: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onEdit() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = habit.done,
                onCheckedChange = { onToggleDone() }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(habit.name, fontSize = 18.sp)
                Text("${habit.category} - ${habit.frequency} à ${habit.time}", fontSize = 14.sp, color = Color.Gray)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Supprimer")
            }
        }
    }
}

@Composable
fun HabitDialog(
    habit: Habit?,
    onDismiss: () -> Unit,
    onSave: (Habit) -> Unit
) {
    var name by remember { mutableStateOf(habit?.name ?: "") }
    var frequency by remember { mutableStateOf(habit?.frequency ?: "Quotidienne") }
    var time by remember { mutableStateOf(habit?.time ?: "08:00") }
    var category by remember { mutableStateOf(habit?.category ?: "Autres") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (habit == null) "Ajouter une habitude" else "Modifier l’habitude") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nom") })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = frequency, onValueChange = { frequency = it }, label = { Text("Fréquence") })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("Heure") })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Catégorie") })
            }
        },
        confirmButton = {
            Button(onClick = {
                if (name.isNotBlank()) onSave(Habit(habit?.id ?: 0, name, frequency, time, category, habit?.done ?: false))
            }) { Text("Enregistrer") }
        },
        dismissButton = { Button(onClick = onDismiss) { Text("Annuler") } }
    )
}