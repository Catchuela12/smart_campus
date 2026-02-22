package com.example.smart_campus.screen

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smart_campus.ui.theme.Smart_campusTheme

class ProfileScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Smart_campusTheme {
                ProfileView()
            }
        }
    }
}

// Custom color palette for Profile
object ProfileColors {
    val PrimaryGreen = Color(0xFF1B5E20)
    val SecondaryGreen = Color(0xFF2E7D32)
    val LightGreen = Color(0xFF4CAF50)
    val AccentGreen = Color(0xFF66BB6A)
    val BackgroundGray = Color(0xFFF8F9FA)
    val CardWhite = Color(0xFFFFFFFF)
    val TextPrimary = Color(0xFF212121)
    val TextSecondary = Color(0xFF757575)
    val LightGreenBg = Color(0xFFE8F5E9)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileView() {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var showDialog by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("JohnEric L. Catchuela") }
    var userEmail by remember { mutableStateOf("johneric.catchuela@smartcampus.edu") }
    var studentId by remember { mutableStateOf("2300432") }
    var program by remember { mutableStateOf("BS Information Technology") }
    var yearLevel by remember { mutableStateOf("3rd Year") }
    var textFieldValue by remember { mutableStateOf(userName) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = ProfileColors.PrimaryGreen,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "Update Display Name",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Enter your new display name:",
                        fontSize = 14.sp,
                        color = ProfileColors.TextSecondary
                    )
                    OutlinedTextField(
                        value = textFieldValue,
                        onValueChange = { textFieldValue = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ProfileColors.PrimaryGreen,
                            unfocusedBorderColor = ProfileColors.TextSecondary.copy(alpha = 0.3f),
                            cursorColor = ProfileColors.PrimaryGreen
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        userName = textFieldValue
                        showDialog = false
                        Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ProfileColors.PrimaryGreen
                    )
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDialog = false },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ProfileColors.BackgroundGray,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Profile",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            (context as? ComponentActivity)?.finish()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ProfileColors.PrimaryGreen,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with gradient background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                ProfileColors.PrimaryGreen,
                                ProfileColors.SecondaryGreen,
                                ProfileColors.LightGreen
                            )
                        )
                    )
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Profile picture with elevation
                    Surface(
                        modifier = Modifier.size(120.dp),
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 8.dp,
                        tonalElevation = 4.dp
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp)
                                .clip(CircleShape)
                                .background(ProfileColors.LightGreenBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile",
                                modifier = Modifier.size(60.dp),
                                tint = ProfileColors.SecondaryGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        userName,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 24.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.25f)
                    ) {
                        Text(
                            "ID: $studentId",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Information Cards
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "PERSONAL INFORMATION",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ProfileColors.TextSecondary,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .height(2.dp)
                            .weight(1f)
                            .background(
                                ProfileColors.AccentGreen.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(1.dp)
                            )
                    )
                }

                InfoActionCard(
                    icon = Icons.Outlined.Badge,
                    label = "Display Name",
                    value = userName,
                    iconColor = ProfileColors.PrimaryGreen,
                    onClick = {
                        textFieldValue = userName
                        showDialog = true
                    }
                )

                InfoActionCard(
                    icon = Icons.Outlined.Email,
                    label = "Campus Email",
                    value = userEmail,
                    iconColor = Color(0xFF1976D2),
                    onClick = {
                        Toast.makeText(context, "Email is verified ✓", Toast.LENGTH_SHORT).show()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Academic Section Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "ACADEMIC DETAILS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ProfileColors.TextSecondary,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .height(2.dp)
                            .weight(1f)
                            .background(
                                ProfileColors.AccentGreen.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(1.dp)
                            )
                    )
                }

                InfoCard(
                    icon = Icons.Outlined.School,
                    label = "Program",
                    value = program,
                    iconColor = Color(0xFF7B1FA2)
                )

                InfoCard(
                    icon = Icons.Outlined.CalendarToday,
                    label = "Year Level",
                    value = yearLevel,
                    iconColor = Color(0xFFF57C00)
                )

                InfoCard(
                    icon = Icons.Outlined.Numbers,
                    label = "Student ID",
                    value = studentId,
                    iconColor = Color(0xFFD32F2F)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Footer
            Text(
                "Smart Campus v1.0.4",
                fontSize = 12.sp,
                color = ProfileColors.TextSecondary.copy(alpha = 0.5f),
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoActionCard(
    icon: ImageVector,
    label: String,
    value: String,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = ProfileColors.CardWhite
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 6.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    label,
                    fontSize = 12.sp,
                    color = ProfileColors.TextSecondary,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    value,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ProfileColors.TextPrimary
                )
            }

            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = ProfileColors.TextSecondary.copy(alpha = 0.5f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun InfoCard(
    icon: ImageVector,
    label: String,
    value: String,
    iconColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = ProfileColors.CardWhite
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    label,
                    fontSize = 12.sp,
                    color = ProfileColors.TextSecondary,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    value,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ProfileColors.TextPrimary
                )
            }
        }
    }
}