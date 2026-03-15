package com.example.smart_campus.screen

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.smart_campus.ui.theme.Smart_campusTheme

class ProfileScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val userId    = intent.getIntExtra("USER_ID", 0)
        val studentId = intent.getStringExtra("STUDENT_ID") ?: "N/A"
        val fullName  = intent.getStringExtra("FULL_NAME") ?: "Student"
        val email     = intent.getStringExtra("EMAIL") ?: "No email"
        val program   = intent.getStringExtra("PROGRAM") ?: "No program"
        val yearLevel = intent.getStringExtra("YEAR_LEVEL") ?: "N/A"

        // Init shared profile state — must be after userId is read
        UserProfileState.init(applicationContext, userId)
        AppThemeState.init(applicationContext)
        UserProfileState.seedDisplayNameIfEmpty(applicationContext, fullName)

        setContent {
            Smart_campusTheme {
                ProfileView(
                    userId    = userId,
                    userName  = fullName,
                    userEmail = email,
                    studentId = studentId,
                    program   = program,
                    yearLevel = yearLevel
                )
            }
        }
    }
}

// ── Color palette ─────────────────────────────────────────────────────────────

object ProfileColors {
    // Brand greens stay fixed
    val PrimaryGreen   = Color(0xFF1B5E20)
    val SecondaryGreen = Color(0xFF2E7D32)
    val LightGreen     = Color(0xFF4CAF50)
    val AccentGreen    = Color(0xFF66BB6A)
    val LightGreenBg   = Color(0xFFE8F5E9)
    // Dynamic colors — use MaterialTheme.colorScheme inside composables
}

// ── ProfileView ───────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileView(
    userId: Int = 0,
    userName: String = "Student",
    userEmail: String = "No email",
    studentId: String = "N/A",
    program: String = "No program",
    yearLevel: String = "N/A"
) {
    val context     = LocalContext.current
    val scrollState = rememberScrollState()

    // Read directly from the shared singleton — auto-recomposes on change
    val profileImageUri = UserProfileState.profileImageUri
    val displayName     = UserProfileState.displayName.ifBlank { userName }

    // Display name dialog state
    var showNameDialog by remember { mutableStateOf(false) }
    var textFieldValue by remember { mutableStateOf(displayName) }

    // Photo picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            // Take persistent permission so URI stays readable after navigation
            context.contentResolver.takePersistableUriPermission(
                uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            UserProfileState.saveProfileImage(context, uri)
            Toast.makeText(context, "Profile picture updated!", Toast.LENGTH_SHORT).show()
        }
    }

    // ── Display name dialog ───────────────────────────────────────────────────
    if (showNameDialog) {
        AlertDialog(
            onDismissRequest = { showNameDialog = false },
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = textFieldValue,
                        onValueChange = { textFieldValue = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = ProfileColors.PrimaryGreen,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                            cursorColor = ProfileColors.PrimaryGreen
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (textFieldValue.isNotBlank()) {
                            UserProfileState.saveDisplayName(context, textFieldValue.trim())
                        }
                        showNameDialog = false
                        Toast.makeText(context, "Display name updated!", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ProfileColors.PrimaryGreen)
                ) { Text("Save Changes") }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showNameDialog = false },
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Cancel") }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }

    // ── Scaffold ──────────────────────────────────────────────────────────────
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { (context as? ComponentActivity)?.finish() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
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

            // ── Gradient header ───────────────────────────────────────────────
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
                    .padding(vertical = 36.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // ── Avatar with camera badge ──────────────────────────────
                    Box(
                        modifier = Modifier.size(124.dp),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .clickable { photoPickerLauncher.launch(arrayOf("image/*")) },
                            shape = CircleShape,
                            color = Color.White,
                            shadowElevation = 8.dp
                        ) {
                            if (profileImageUri != null) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(profileImageUri)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Profile picture",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                )
                            } else {
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
                        }

                        // Camera badge
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(ProfileColors.PrimaryGreen)
                                .border(2.5.dp, Color.White, CircleShape)
                                .clickable { photoPickerLauncher.launch(arrayOf("image/*")) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Change photo",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tap hint
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = if (profileImageUri == null) "Tap photo to upload" else "Tap photo to change",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = displayName,
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

            // ── Info cards ────────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SectionLabel(title = "PERSONAL INFORMATION")

                InfoActionCard(
                    icon = Icons.Outlined.Badge,
                    label = "Display Name",
                    value = displayName,
                    iconColor = ProfileColors.PrimaryGreen,
                    onClick = {
                        textFieldValue = displayName
                        showNameDialog = true
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

                InfoActionCard(
                    icon = Icons.Default.CameraAlt,
                    label = "Profile Picture",
                    value = if (profileImageUri == null) "No photo set · tap to upload" else "Custom photo · tap to change",
                    iconColor = Color(0xFF00897B),
                    onClick = { photoPickerLauncher.launch(arrayOf("image/*")) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                SectionLabel(title = "ACADEMIC DETAILS")

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

            Text(
                "Smart Campus v1.0.5",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }
}

// ── SectionLabel ──────────────────────────────────────────────────────────────

@Composable
private fun SectionLabel(title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
}

// ── InfoActionCard ────────────────────────────────────────────────────────────

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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp, pressedElevation = 6.dp),
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
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium, letterSpacing = 0.5.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(value, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            }
            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

// ── InfoCard ──────────────────────────────────────────────────────────────────

@Composable
fun InfoCard(
    icon: ImageVector,
    label: String,
    value: String,
    iconColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
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
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium, letterSpacing = 0.5.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(value, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}