package com.example.smart_campus

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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@Composable
fun ProfileView() {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var showDialog by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("Juan Dela Cruz") }
    var userEmail by remember { mutableStateOf("juan.dc@smartcampus.edu") }
    var textFieldValue by remember { mutableStateOf(userName) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Update Information", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Enter your display name:", fontSize = 14.sp)
                    OutlinedTextField(
                        value = textFieldValue,
                        onValueChange = { textFieldValue = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    userName = textFieldValue
                    showDialog = false
                    Toast.makeText(context, "Profile updated!", Toast.LENGTH_SHORT).show()
                }) { Text("Save Changes") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                Text("Smart Campus v1.0.4", fontSize = 12.sp, color = Color.LightGray)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.size(120.dp),
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 12.dp
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        modifier = Modifier.padding(30.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(userName, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
            Text("ID: 2024-00123 • BS IT Student", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)

            Spacer(modifier = Modifier.height(32.dp))

            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("PERSONAL DETAILS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)

                InfoActionCard(Icons.Default.Face, "Display Name", userName) {
                    textFieldValue = userName
                    showDialog = true
                }

                InfoActionCard(Icons.Default.Email, "Campus Email", userEmail) {
                    Toast.makeText(context, "Email is verified.", Toast.LENGTH_SHORT).show()
                }

                Spacer(modifier = Modifier.height(16.dp))

                // FIXED DIVIDER HERE
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                )

                Button(
                    onClick = { Toast.makeText(context, "Signing out...", Toast.LENGTH_SHORT).show() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = null)
                    Spacer(Modifier.width(12.dp))
                    Text("Logout from Device", fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoActionCard(icon: ImageVector, label: String, value: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(label, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                Text(value, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.weight(1f))
            Icon(Icons.Default.KeyboardArrowRight, null, tint = Color.LightGray)
        }
    }
}