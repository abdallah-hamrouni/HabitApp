package com.example.healthtracker

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import com.example.healthtracker.Viewmodel.HabitViewModel
import com.example.healthtracker.Viewmodel.HabitViewModelFactory
import com.example.healthtracker.Repositrory.HabitRepository
import com.example.healthtracker.databese.AppDatabase
import com.example.healthtracker.Viewmodel.WeatherViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(userEmail: String,
                  onSignOut: () -> Unit ) {
    val context = LocalContext.current

    // Habit ViewModel
    val dao = AppDatabase.getDatabase(context).habitDao()
    val repo = HabitRepository(dao)
    val habitVM: HabitViewModel = viewModel(factory = HabitViewModelFactory(repo, userEmail))
    val habits by habitVM.habits.collectAsState()

    val total = habits.size
    val doneCount = habits.count { it.done }
    val donePercent = if (total > 0) doneCount / total.toFloat() else 0f
    val animatedPercent by animateFloatAsState(targetValue = donePercent)

    // Weather ViewModel
    val weatherVM: WeatherViewModel = viewModel()
    val weatherData by weatherVM.weather.collectAsState()

    LaunchedEffect(true) {
        weatherVM.loadWeather(lat = 43.6, lon = 1.44)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2196F3))
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(Color(0xFFF5F5F5))
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar avec gradient
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF2196F3), Color(0xFF64B5F6))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userEmail.firstOrNull()?.uppercase() ?: "U",
                        color = Color.White,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(userEmail, fontSize = 18.sp, color = Color.Gray, fontWeight = FontWeight.Medium)

                Spacer(modifier = Modifier.height(20.dp))

                // Carte météo stylée
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF64B5F6))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val weather = weatherData?.current_weather

                        Column(
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Météo actuelle",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            if (weather != null) {
                                Text(
                                    text = "${weather.temperature}°C",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Vent: ${weather.windspeed} km/h",
                                    fontSize = 14.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            } else {
                                Text(
                                    text = "Chargement...",
                                    fontSize = 16.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Icône météo simple (placeholder)
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .background(Color.White.copy(alpha = 0.3f), shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "☀️",
                                fontSize = 28.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Carte statistiques avec donut chart
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Vos statistiques", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(20.dp))

                        Canvas(modifier = Modifier.size(180.dp)) {
                            val sweepDone = 360 * animatedPercent
                            val sweepNotDone = 360 - sweepDone

                            drawArc(
                                color = Color(0xFF4CAF50),
                                startAngle = -90f,
                                sweepAngle = sweepDone,
                                useCenter = true,
                                size = Size(size.width, size.height)
                            )
                            drawArc(
                                color = Color(0xFFF44336),
                                startAngle = -90f + sweepDone,
                                sweepAngle = sweepNotDone,
                                useCenter = true,
                                size = Size(size.width, size.height)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Total : $total", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Text("Complétées : $doneCount", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))

                // ---------- BOUTON SIGN OUT ----------
                Button(
                    onClick = { onSignOut() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF44336),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Se déconnecter", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    )
}
