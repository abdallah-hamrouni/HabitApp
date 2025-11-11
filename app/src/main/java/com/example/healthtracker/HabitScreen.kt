package com.example.healthtracker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.healthtracker.databese.AppDatabase
import com.example.healthtracker.Repositrory.HabitRepository
import com.example.healthtracker.Viewmodel.HabitViewModel
import com.example.healthtracker.Viewmodel.HabitViewModelFactory
import com.example.healthtracker.models.Habit
import androidx.compose.foundation.Canvas
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Size
import android.app.TimePickerDialog
import android.widget.TimePicker
import androidx.compose.material3.OutlinedTextField
import java.util.*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitScreen(userEmail: String) {
    val context = LocalContext.current
    val dao = AppDatabase.getDatabase(context).habitDao()
    val repository = HabitRepository(dao)

    val viewModel: HabitViewModel = viewModel(
        factory = HabitViewModelFactory(repository, userEmail)
    )

    val habits by viewModel.habits.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var editingHabit by remember { mutableStateOf<Habit?>(null) }
    var selectedTab by remember { mutableStateOf("Mes Habitudes") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Habit Tracker", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { /* TODO: profil */ }) {
                        Icon(Icons.Default.Person, contentDescription = "Profil")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == "Mes Habitudes",
                    onClick = { selectedTab = "Mes Habitudes" },
                    icon = { Icon(Icons.Default.List, contentDescription = null) },
                    label = { Text("Mes Habitudes") }
                )
                NavigationBarItem(
                    selected = selectedTab == "Statistiques",
                    onClick = { selectedTab = "Statistiques" },
                    icon = { Icon(Icons.Default.MoreVert, contentDescription = null) },
                    label = { Text("Stats") }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingHabit = null
                    showDialog = true
                },
                shape = CircleShape,
                containerColor = Color(0xFF00796B)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ajouter")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                "Mes Habitudes" -> HabitList(
                    habits = habits,
                    onEdit = { editingHabit = it; showDialog = true },
                    onDelete = { viewModel.deleteHabit(it) },
                    onUpdate = { viewModel.addOrUpdateHabit(it) } // Gestion update/insert
                )
                "Statistiques" -> StatisticsScreen(habits)
            }

            if (showDialog) {
                HabitDialog(
                    habit = editingHabit,
                    userEmail = userEmail,
                    onDismiss = { showDialog = false },
                    onSave = { newHabit ->
                        viewModel.addOrUpdateHabit(newHabit)
                        showDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun HabitList(
    habits: List<Habit>,
    onEdit: (Habit) -> Unit,
    onDelete: (Habit) -> Unit,
    onUpdate: (Habit) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(habits, key = { it.id }) { habit ->
            HabitItem(
                habit = habit,
                onEdit = { onEdit(habit) },
                onDelete = { onDelete(habit) },
                onUpdate = { updatedHabit -> onUpdate(updatedHabit) } // Passe l'objet modifié
            )
        }
    }
}

@Composable
fun HabitItem(
    habit: Habit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onUpdate: (Habit) -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = habit.done,
                onCheckedChange = { onUpdate(habit.copy(done = !habit.done)) }
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(habit.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(
                    "${habit.category} • ${habit.frequency} à ${habit.time}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            // Bouton pour éditer
            IconButton(onClick = { showEditDialog = true }) {
                Icon(Icons.Default.Edit, contentDescription = "Modifier")
            }
            // Bouton pour supprimer
            IconButton(onClick = { onDelete() }) {
                Icon(Icons.Default.Delete, contentDescription = "Supprimer")
            }
        }
    }

    if (showEditDialog) {
        // Si habit existe, pré-remplir ; sinon champs vides
        var name by remember { mutableStateOf(habit.name.ifEmpty { "" }) }
        var frequency by remember { mutableStateOf(habit.frequency.ifEmpty { "" }) }
        var time by remember { mutableStateOf(habit?.time ?: "") }
        var category by remember { mutableStateOf(habit.category.ifEmpty { "" }) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Modifier l’habitude") },
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
                    if (name.isNotBlank() && frequency.isNotBlank() && time.isNotBlank() && category.isNotBlank()) {
                        val updatedHabit = habit.copy(
                            name = name,
                            frequency = frequency,
                            time = time,
                            category = category
                        )
                        onUpdate(updatedHabit)
                        showEditDialog = false
                    }
                }) { Text("Enregistrer") }
            },
            dismissButton = {
                Button(onClick = { showEditDialog = false }) { Text("Annuler") }
            }
        )
    }

}



@Composable
fun HabitDialog(
    habit: Habit?, // peut être null si c'est un nouvel habit
    userEmail: String,
    onDismiss: () -> Unit,
    onSave: (Habit) -> Unit
) {
    // Toujours vide par défaut, même si habit existe
    var name by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("") }
    var time by remember { mutableStateOf(habit?.time ?: "") }
    var category by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (habit == null) "Ajouter une habitude" else "Modifier l’habitude") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = frequency,
                    onValueChange = { frequency = it },
                    label = { Text("Fréquence") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Heure") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Catégorie") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (name.isNotBlank() && frequency.isNotBlank() && time.isNotBlank() && category.isNotBlank()) {
                    val habitToSave = Habit(
                        id = habit?.id ?: 0, // 0 = nouvel habit, sinon update
                        name = name,
                        frequency = frequency,
                        time = time,
                        category = category,
                        done = habit?.done ?: false,
                        title = name,
                        userEmail = userEmail
                    )
                    onSave(habitToSave)
                }
            }) { Text("Enregistrer") }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text("Annuler") }
        }
    )
}


@Composable
fun StatisticsScreen(habits: List<Habit>) {
    val total = habits.size
    val doneCount = habits.count { it.done }
    val notDoneCount = total - doneCount

    val donePercentage = if (total > 0) doneCount / total.toFloat() else 0f
    val notDonePercentage = 1f - donePercentage

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Text(
            "Statistiques des habitudes",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp // <-- ici sp fonctionne
        )
        Spacer(modifier = Modifier.height(16.dp))

        Canvas(modifier = Modifier.size(200.dp)) {
            val radius = size.minDimension / 2
            val topLeftX = center.x - radius
            val topLeftY = center.y - radius
            val arcSize = Size(radius * 2, radius * 2)

            // Habitudes complétées (vert)
            drawArc(
                color = Color(0xFF4CAF50),
                startAngle = -90f,
                sweepAngle = 360 * donePercentage,
                useCenter = true,
                size = arcSize,
                topLeft = androidx.compose.ui.geometry.Offset(topLeftX, topLeftY)
            )

            // Habitudes non complétées (rouge)
            drawArc(
                color = Color(0xFFF44336),
                startAngle = -90f + 360 * donePercentage,
                sweepAngle = 360 * notDonePercentage,
                useCenter = true,
                size = arcSize,
                topLeft = androidx.compose.ui.geometry.Offset(topLeftX, topLeftY)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Total des habitudes: $total")
        Text("Habitudes complétées: $doneCount")
    }
}


@Composable
fun TimePickerField(time: String, onTimeSelected: (String) -> Unit) {
    val context = LocalContext.current
    var timeText by remember { mutableStateOf(time) }

    OutlinedTextField(
        value = timeText,
        onValueChange = { },
        label = { Text("Heure") },
        readOnly = true, // L'utilisateur ne peut pas taper directement
        modifier = Modifier.clickable {
            // Récupère l'heure actuelle pour initialiser le TimePicker
            val calendar = Calendar.getInstance()
            val initialHour = calendar.get(Calendar.HOUR_OF_DAY)
            val initialMinute = calendar.get(Calendar.MINUTE)

            TimePickerDialog(
                context,
                { _: TimePicker, hour: Int, minute: Int ->
                    val formatted = String.format("%02d:%02d", hour, minute)
                    timeText = formatted
                    onTimeSelected(formatted)
                },
                initialHour,
                initialMinute,
                true // 24h format
            ).show()
        }
    )
}