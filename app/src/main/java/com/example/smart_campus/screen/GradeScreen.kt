package com.example.smart_campus.screen

import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smart_campus.ui.theme.Smart_campusTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class GradeInfo(
    val code: String,
    val subject: String,
    val units: String,
    val grade: String,
    val status: String
)

class GradeScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Smart_campusTheme {
                GradeScreenContent(onBackClick = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradeScreenContent(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val isRefreshing = remember { mutableStateOf(false) }
    val lastUpdated = remember { mutableStateOf(SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date())) }
    var selectedSemester by remember { mutableStateOf("1st Sem") }
    
    val firstSemGrades = listOf(
        GradeInfo("CCS203", "Mobile Programming 1", "3.0", "1.25", "PASSED"),
        GradeInfo("CCS102", "Data Structures", "3.0", "1.50", "PASSED"),
        GradeInfo("CCS202", "Database Management", "3.0", "1.75", "PASSED"),
        GradeInfo("CCS201", "Web Development", "3.0", "1.25", "PASSED"),
        GradeInfo("CCS301", "Mobile Programming 2", "3.0", "1.50", "PASSED"),
        GradeInfo("GE101", "Computer Ethics", "3.0", "1.00", "PASSED")
    )
    
    val secondSemGrades = listOf(
        GradeInfo("CCS204", "Algorithm Analysis", "3.0", "1.50", "PASSED"),
        GradeInfo("CCS205", "Operating Systems", "3.0", "1.75", "PASSED"),
        GradeInfo("GE103", "Environmental Science", "3.0", "1.25", "PASSED"),
        GradeInfo("MAT102", "Discrete Mathematics", "3.0", "2.00", "PASSED")
    )

    val currentGrades = if (selectedSemester == "1st Sem") firstSemGrades else secondSemGrades
    val currentGWA = if (selectedSemester == "1st Sem") "1.50" else "1.63"
    val totalUnits = currentGrades.sumOf { it.units.toDoubleOrNull() ?: 0.0 }.toString()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Grades", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                isRefreshing.value = true
                                delay(1500)
                                lastUpdated.value = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date())
                                isRefreshing.value = false
                                Toast.makeText(context, "Grades refreshed!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = !isRefreshing.value
                    ) {
                        if (isRefreshing.value) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh",
                                tint = Color.White
                            )
                        }
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
                    Text(currentGWA, color = Color.White, fontSize = 42.sp, fontWeight = FontWeight.Bold)
                    Text(selectedSemester, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    Text("Academic Year 2025-2026", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Total Units: $totalUnits", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Last Updated: ${lastUpdated.value}", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedSemester == "1st Sem",
                    onClick = { selectedSemester = "1st Sem" },
                    label = { Text("1st Semester") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF2E7D32),
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = selectedSemester == "2nd Sem",
                    onClick = { selectedSemester = "2nd Sem" },
                    label = { Text("2nd Semester") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF2E7D32),
                        selectedLabelColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Semester Grades", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))

            currentGrades.forEach { grade ->
                GradeRow(
                    code = grade.code,
                    subject = grade.subject,
                    units = grade.units,
                    grade = grade.grade,
                    status = grade.status
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
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
