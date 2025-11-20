package com.example.healthtracker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Canvas
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthtracker.models.Habit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticScreen(habits: List<Habit>, onBack: () -> Unit) {
    val total = habits.size
    val done = habits.count { it.done }
    val donePercentage = if (total > 0) done / total.toFloat() else 0f
    val notDonePercentage = 1f - donePercentage

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistiques", fontWeight = FontWeight.Bold, fontSize = 22.sp) },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6200EE),
                    titleContentColor = Color.White
                )
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFFF2F2F2))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    "Statistiques des habitudes",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color(0xFF333333)
                )
                Spacer(Modifier.height(16.dp))

                Canvas(modifier = Modifier.size(220.dp)) {
                    val radius = size.minDimension / 2
                    val centerOffset = androidx.compose.ui.geometry.Offset(center.x - radius, center.y - radius)
                    val arcSize = Size(radius * 2, radius * 2)

                    drawArc(
                        color = Color(0xFF4CAF50),
                        startAngle = -90f,
                        sweepAngle = 360 * donePercentage,
                        useCenter = true,
                        size = arcSize,
                        topLeft = centerOffset
                    )

                    drawArc(
                        color = Color(0xFFF44336),
                        startAngle = -90f + 360 * donePercentage,
                        sweepAngle = 360 * notDonePercentage,
                        useCenter = true,
                        size = arcSize,
                        topLeft = centerOffset
                    )
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Total : $total", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    Text("Complétées : $done", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    )
}
