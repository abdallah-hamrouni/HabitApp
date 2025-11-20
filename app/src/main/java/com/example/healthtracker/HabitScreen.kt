package com.example.healthtracker

import android.app.TimePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.foundation.clickable



sealed class BottomNavItem(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Habits : BottomNavItem("Habitudes", Icons.Default.List)
    object Stats : BottomNavItem("Stats", Icons.Default.Favorite)
    object Profile : BottomNavItem("Profil", Icons.Default.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitScreen(userEmail: String,
                onSignOut: () -> Unit) {

    var selectedTab by remember { mutableStateOf<BottomNavItem>(BottomNavItem.Habits) }

    val context = LocalContext.current
    val dao = AppDatabase.getDatabase(context).habitDao()
    val repository = HabitRepository(dao)
    val viewModel: HabitViewModel = viewModel(factory = HabitViewModelFactory(repository, userEmail))
    val habits by viewModel.habits.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mes Habitudes", fontWeight = FontWeight.Bold, fontSize = 22.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6200EE),
                    titleContentColor = Color.White
                )
            )

        },
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 5.dp) {
                listOf(BottomNavItem.Habits, BottomNavItem.Stats, BottomNavItem.Profile).forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label, fontSize = 12.sp) },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF6200EE))
                    )
                }
            }
        }
    ) { padding ->

        when (selectedTab) {
            is BottomNavItem.Habits -> HabitFormAndList(habits, padding, viewModel, userEmail)
            is BottomNavItem.Stats -> StatisticScreen( habits = habits, onBack = {})
            is BottomNavItem.Profile -> ProfileScreen(
                userEmail = userEmail,
                onSignOut = onSignOut // <-- transmettre ici
            )        }
    }
}


@Composable
fun HabitFormAndList(
    habits: List<Habit>,
    padding: PaddingValues,
    viewModel: HabitViewModel,
    userEmail: String
) {
    var newName by remember { mutableStateOf("") }
    var newFrequency by remember { mutableStateOf("") }
    var newTime by remember { mutableStateOf("") }
    var newCategory by remember { mutableStateOf("") }

    var editingHabit by remember { mutableStateOf<Habit?>(null) }
    var editName by remember { mutableStateOf("") }
    var editFrequency by remember { mutableStateOf("") }
    var editTime by remember { mutableStateOf("") }
    var editCategory by remember { mutableStateOf("") }
    var showEditDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .background(Color(0xFFF2F2F2))
            .padding(16.dp)
    ) {


        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Ajouter une habitude",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Nom") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = newFrequency,
                    onValueChange = { newFrequency = it },
                    label = { Text("Fréquence") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = newTime,
                    onValueChange = {newTime = it},
                    readOnly = false,
                    label = { Text("Heure") },
                    modifier = Modifier.fillMaxWidth()


                )

                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = newCategory,
                    onValueChange = { newCategory = it },
                    label = { Text("Catégorie") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (newName.isNotBlank() && newFrequency.isNotBlank() && newCategory.isNotBlank()) {
                            val habit = Habit(
                                id = 0,
                                name = newName,
                                frequency = newFrequency,
                                time = newTime,
                                category = newCategory,
                                title = newName,
                                done = false,
                                userEmail = userEmail
                            )
                            viewModel.addOrUpdateHabit(habit)
                            newName = ""
                            newFrequency = ""
                            newTime = ""
                            newCategory = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6200EE),
                        contentColor = Color.White
                    )
                ) {
                    Text("Ajouter", fontSize = 16.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))


        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(habits, key = { it.id }) { habit ->
                HabitItem(
                    habit = habit,
                    onEdit = {
                        editingHabit = habit
                        editName = habit.name
                        editFrequency = habit.frequency
                        editTime = habit.time
                        editCategory = habit.category
                        showEditDialog = true
                    },
                    onDelete = { viewModel.deleteHabit(habit) },
                    onToggle = { viewModel.addOrUpdateHabit(habit.copy(done = !habit.done)) }
                )
            }
        }


        if (showEditDialog && editingHabit != null) {
            AlertDialog(
                onDismissRequest = { showEditDialog = false },
                title = { Text("Modifier l’habitude") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            label = { Text("Nom") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = editFrequency,
                            onValueChange = { editFrequency = it },
                            label = { Text("Fréquence") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = editTime,
                            onValueChange = {editTime = it},
                            readOnly = false,
                            label = { Text("Heure") },
                            modifier = Modifier
                                .fillMaxWidth()


                        )

                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = editCategory,
                            onValueChange = { editCategory = it },
                            label = { Text("Catégorie") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            editingHabit?.let {
                                val updatedHabit = it.copy(
                                    name = editName,
                                    frequency = editFrequency,
                                    time = editTime,
                                    category = editCategory,
                                    title = editName
                                )
                                viewModel.addOrUpdateHabit(updatedHabit)
                                showEditDialog = false
                            }
                        }
                    ) { Text("Valider") }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showEditDialog = false }) { Text("Annuler") }
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
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = habit.done, onCheckedChange = { onToggle() })
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(habit.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF333333))
                Text("${habit.category} • ${habit.frequency} • ${habit.time}", fontSize = 14.sp, color = Color.Gray)
            }
            IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, contentDescription = "Modifier", tint = Color(0xFF6200EE)) }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = Color(0xFFF44336)) }
        }
    }
}


