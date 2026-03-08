package com.example.smart_campus.screen

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.smart_campus.ui.theme.Smart_campusTheme
import com.example.smart_campus.viewmodel.AuthState
import com.example.smart_campus.viewmodel.AuthViewModel

class SignUpScreen : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Smart_campusTheme {
                SignUpScreenContent(
                    authViewModel = authViewModel,
                    onSignUpSuccess = { user ->
                        Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, Dashboard::class.java).apply {
                            putExtra("USER_ID", user.id)
                            putExtra("STUDENT_ID", user.studentId)
                            putExtra("FULL_NAME", user.fullName)
                            putExtra("EMAIL", user.email)
                            putExtra("PROGRAM", user.program)
                            putExtra("YEAR_LEVEL", user.yearLevel)
                        }
                        startActivity(intent)
                        finish()
                    },
                    onNavigateToLogin = {
                        startActivity(Intent(this, LoginScreen::class.java))
                        finish()
                    }
                )
            }
        }
    }
}

// ── Colors ────────────────────────────────────────────────────────────────────

private val GreenPrimary    = Color(0xFF1B5E20)
private val GreenLight      = Color(0xFFE8F5E9)
private val GreenAccent     = Color(0xFF4CAF50)
private val TextDark        = Color(0xFF212121)
private val TextMedium      = Color(0xFF424242)
private val TextHint        = Color(0xFF757575)
private val BorderIdle      = Color(0xFFBDBDBD)

// ── SignUpScreenContent ───────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreenContent(
    authViewModel: AuthViewModel,
    onSignUpSuccess: (com.example.smart_campus.data.User) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val context     = LocalContext.current
    val authState   by authViewModel.authState.collectAsStateWithLifecycle()
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    var studentId           by remember { mutableStateOf("") }
    var firstName           by remember { mutableStateOf("") }
    var lastName            by remember { mutableStateOf("") }
    var email               by remember { mutableStateOf("") }
    var username            by remember { mutableStateOf("") }
    var password            by remember { mutableStateOf("") }
    var confirmPassword     by remember { mutableStateOf("") }
    var selectedProgram     by remember { mutableStateOf("") }
    var selectedYearLevel   by remember { mutableStateOf("") }
    var passwordVisible     by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var showProgramSheet   by remember { mutableStateOf(false) }
    var showYearSheet      by remember { mutableStateOf(false) }

    val programs = listOf(
        "BS Computer Science",
        "BS Information Technology",
        "BSEd major in English",
        "BSEd major in Filipino",
        "BSEd major in Social Studies",
        "Bachelor of Elementary Education",
        "BS in Accountancy",
        "BSBA major in Financing Management",
        "BSBA major in Marketing Management",
        "BS in Nursing",
        "BS in Psychology",
        "BS in Computer Engineering",
        "BS in Industrial Engineering",
        "BS in Electronics Engineering"
    )

    val yearLevels = listOf("1st Year", "2nd Year", "3rd Year", "4th Year")

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                currentUser?.let { user -> onSignUpSuccess(user) }
                authViewModel.resetAuthState()
            }
            is AuthState.Error -> {
                Toast.makeText(context, (authState as AuthState.Error).message, Toast.LENGTH_LONG).show()
                authViewModel.resetAuthState()
            }
            else -> {}
        }
    }

    // ── Program bottom sheet ──────────────────────────────────────────────────
    if (showProgramSheet) {
        PickerBottomSheet(
            title = "Select Program",
            icon = Icons.Default.School,
            items = programs,
            selectedItem = selectedProgram,
            onItemSelected = {
                selectedProgram = it
                showProgramSheet = false
            },
            onDismiss = { showProgramSheet = false }
        )
    }

    // ── Year level bottom sheet ───────────────────────────────────────────────
    if (showYearSheet) {
        PickerBottomSheet(
            title = "Select Year Level",
            icon = Icons.Default.CalendarToday,
            items = yearLevels,
            selectedItem = selectedYearLevel,
            onItemSelected = {
                selectedYearLevel = it
                showYearSheet = false
            },
            onDismiss = { showYearSheet = false }
        )
    }

    // ── Screen ────────────────────────────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Create Account",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = GreenPrimary,
                fontSize = 32.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Join Smart Campus Today",
                style = MaterialTheme.typography.bodyLarge,
                color = TextMedium
            )
            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    // ── Text fields ───────────────────────────────────────────
                    SignUpTextField(value = studentId, onValueChange = { studentId = it },
                        label = "Student ID", icon = Icons.Default.Badge)

                    SignUpTextField(value = firstName, onValueChange = { firstName = it },
                        label = "First Name", icon = Icons.Default.Person)

                    SignUpTextField(value = lastName, onValueChange = { lastName = it },
                        label = "Last Name", icon = Icons.Default.Person)

                    SignUpTextField(value = email, onValueChange = { email = it },
                        label = "Email", icon = Icons.Default.Email)

                    SignUpTextField(value = username, onValueChange = { username = it },
                        label = "Username", icon = Icons.Default.AccountCircle)

                    // Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = GreenPrimary) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null, tint = TextHint
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = signUpTextFieldColors()
                    )

                    // Confirm Password
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = GreenPrimary) },
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(
                                    if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null, tint = TextHint
                                )
                            }
                        },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = signUpTextFieldColors(),
                        isError = confirmPassword.isNotEmpty() && password != confirmPassword,
                        supportingText = {
                            if (confirmPassword.isNotEmpty() && password != confirmPassword) {
                                Text("Passwords do not match", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    )

                    // ── Program selector tile ─────────────────────────────────
                    SelectorTile(
                        label = "Program",
                        value = selectedProgram,
                        placeholder = "Select your program",
                        icon = Icons.Default.School,
                        onClick = { showProgramSheet = true }
                    )

                    // ── Year level selector tiles (4 chips) ───────────────────
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = GreenPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Year Level",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextMedium
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            yearLevels.forEach { year ->
                                val isSelected = selectedYearLevel == year
                                Surface(
                                    onClick = { selectedYearLevel = year },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) GreenPrimary else GreenLight,
                                    tonalElevation = if (isSelected) 4.dp else 0.dp
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = year.substringBefore(" "),   // "1st"
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else GreenPrimary
                                        )
                                        Text(
                                            text = "Year",
                                            fontSize = 10.sp,
                                            color = if (isSelected) Color.White.copy(alpha = 0.85f)
                                            else GreenPrimary.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            }
                        }
                        // Show selected label underneath
                        if (selectedYearLevel.isNotBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = GreenAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = selectedYearLevel,
                                    fontSize = 12.sp,
                                    color = GreenPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // ── Sign Up Button ────────────────────────────────────────
                    Button(
                        onClick = {
                            val fullName = "$firstName $lastName".trim()
                            when {
                                studentId.isBlank()      -> Toast.makeText(context, "Please enter Student ID", Toast.LENGTH_SHORT).show()
                                firstName.isBlank()      -> Toast.makeText(context, "Please enter First Name", Toast.LENGTH_SHORT).show()
                                lastName.isBlank()       -> Toast.makeText(context, "Please enter Last Name", Toast.LENGTH_SHORT).show()
                                email.isBlank()          -> Toast.makeText(context, "Please enter Email", Toast.LENGTH_SHORT).show()
                                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                                    Toast.makeText(context, "Please enter valid Email", Toast.LENGTH_SHORT).show()
                                username.isBlank()       -> Toast.makeText(context, "Please enter Username", Toast.LENGTH_SHORT).show()
                                username.length < 4      -> Toast.makeText(context, "Username must be at least 4 characters", Toast.LENGTH_SHORT).show()
                                password.isBlank()       -> Toast.makeText(context, "Please enter Password", Toast.LENGTH_SHORT).show()
                                password.length < 6      -> Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                                password != confirmPassword -> Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                                selectedProgram.isBlank()  -> Toast.makeText(context, "Please select Program", Toast.LENGTH_SHORT).show()
                                selectedYearLevel.isBlank() -> Toast.makeText(context, "Please select Year Level", Toast.LENGTH_SHORT).show()
                                else -> {
                                    authViewModel.register(
                                        studentId  = studentId,
                                        fullName   = fullName,
                                        email      = email,
                                        username   = username,
                                        password   = password,
                                        program    = selectedProgram,
                                        yearLevel  = selectedYearLevel
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                        enabled = authState !is AuthState.Loading
                    ) {
                        if (authState is AuthState.Loading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text("Create Account", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Already have an account? ", color = TextMedium)
                Text(
                    text = "Sign In",
                    color = GreenPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ── PickerBottomSheet — clean white sheet with green accents ──────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PickerBottomSheet(
    title: String,
    icon: ImageVector,
    items: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            // Sheet header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(GreenLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextDark
                )
            }

            HorizontalDivider(color = Color(0xFFF0F0F0))

            // Items list
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                items.forEach { item ->
                    val isSelected = item == selectedItem
                    Surface(
                        onClick = { onItemSelected(item) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) GreenPrimary else Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item,
                                fontSize = 15.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) Color.White else TextDark,
                                modifier = Modifier.weight(1f)
                            )
                            if (isSelected) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

// ── SelectorTile — tappable field that opens the bottom sheet ─────────────────

@Composable
private fun SelectorTile(
    label: String,
    value: String,
    placeholder: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    val hasValue = value.isNotBlank()

    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = if (hasValue) GreenLight else Color(0xFFFAFAFA),
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (hasValue) GreenPrimary.copy(alpha = 0.12f) else Color(0xFFEEEEEE)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = if (hasValue) GreenPrimary else TextHint,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (hasValue) GreenPrimary else TextHint,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (hasValue) value else placeholder,
                    fontSize = 14.sp,
                    fontWeight = if (hasValue) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (hasValue) TextDark else TextHint
                )
            }
            Icon(
                if (hasValue) Icons.Default.CheckCircle else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = if (hasValue) GreenAccent else TextHint,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// ── Reusable text field ───────────────────────────────────────────────────────

@Composable
private fun SignUpTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = GreenPrimary) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = signUpTextFieldColors()
    )
}

@Composable
private fun signUpTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor       = TextDark,
    unfocusedTextColor     = TextDark,
    focusedBorderColor     = GreenPrimary,
    unfocusedBorderColor   = BorderIdle,
    focusedLabelColor      = GreenPrimary,
    unfocusedLabelColor    = TextHint,
    cursorColor            = GreenPrimary,
    focusedContainerColor  = GreenLight.copy(alpha = 0.3f),
    unfocusedContainerColor = Color.Transparent
)