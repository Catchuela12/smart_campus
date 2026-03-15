package com.example.smart_campus.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smart_campus.ui.theme.Smart_campusTheme

class GradeScreen : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Smart_campusTheme {
                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = { Text("My Grades", color = Color.White, fontWeight = FontWeight.Bold) },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color.White
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = Color(0xFF2E7D32)
                            )
                        )
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(Color(0xFFF5F5F5))
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF2E7D32)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("General Weighted Average", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                                Text("1.50", color = Color.White, fontSize = 42.sp, fontWeight = FontWeight.Bold)
                                Text("Academic Year 2025-2026", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Total Units: 21", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Last Updated: October 24, 2025", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text("Semester Grades", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Added status (PASSED) to each row
                        GradeRow("CCS203", "Mobile Programming 1", "3.0", "1.25", "PASSED")
                        GradeRow("CCS102", "Data Structures", "3.0", "1.50", "PASSED")
                        GradeRow("CCS202", "Database Management", "3.0", "1.75", "PASSED")
                        GradeRow("CCS201", "Web Development", "3.0", "1.25", "PASSED")
                        GradeRow("CCS301", "Mobile Programming 2", "3.0", "1.50", "PASSED")
                        GradeRow("GE101", "Computer Ethics", "3.0", "1.00", "PASSED")

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun GradeRow(code: String, subject: String, units: String, grade: String, status: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(code, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                Text(subject, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Row {
                    Text("Units: $units", fontSize = 11.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(status, fontSize = 11.sp, color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                }
            }
            Text(grade, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
        }
    }
}
