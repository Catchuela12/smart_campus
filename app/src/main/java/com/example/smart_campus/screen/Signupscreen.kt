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
import androidx.compose.ui.graphics.Color
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
                        // Pass user data to Dashboard
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreenContent(
    authViewModel: AuthViewModel,
    onSignUpSuccess: (com.example.smart_campus.data.User) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val context = LocalContext.current
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    var studentId by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var selectedProgram by remember { mutableStateOf("") }
    var selectedYearLevel by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Dropdown states
    var programExpanded by remember { mutableStateOf(false) }
    var yearExpanded by remember { mutableStateOf(false) }

    // Program options
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

    // Year level options
    val yearLevels = listOf(
        "1st Year",
        "2nd Year",
        "3rd Year",
        "4th Year"
    )

    // Handle auth state changes
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                currentUser?.let { user ->
                    onSignUpSuccess(user)
                }
                authViewModel.resetAuthState()
            }
            is AuthState.Error -> {
                Toast.makeText(context, (authState as AuthState.Error).message, Toast.LENGTH_LONG).show()
                authViewModel.resetAuthState()
            }
            else -> {}
        }
    }

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

            // Header
            Text(
                text = "Create Account",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B5E20),
                fontSize = 32.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Join Smart Campus Today",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF424242)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Form Card
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
                    // Student ID
                    OutlinedTextField(
                        value = studentId,
                        onValueChange = { studentId = it },
                        label = { Text("Student ID", color = Color(0xFF424242)) },
                        leadingIcon = {
                            Icon(Icons.Default.Badge, contentDescription = null, tint = Color(0xFF1B5E20))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = Color(0xFF1B5E20),
                            unfocusedBorderColor = Color(0xFF9E9E9E),
                            focusedLabelColor = Color(0xFF1B5E20),
                            unfocusedLabelColor = Color(0xFF757575),
                            cursorColor = Color(0xFF1B5E20)
                        )
                    )

                    // First Name
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        label = { Text("First Name", color = Color(0xFF424242)) },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF1B5E20))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = Color(0xFF1B5E20),
                            unfocusedBorderColor = Color(0xFF9E9E9E),
                            focusedLabelColor = Color(0xFF1B5E20),
                            unfocusedLabelColor = Color(0xFF757575),
                            cursorColor = Color(0xFF1B5E20)
                        )
                    )

                    // Last Name
                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = { Text("Last Name", color = Color(0xFF424242)) },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF1B5E20))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = Color(0xFF1B5E20),
                            unfocusedBorderColor = Color(0xFF9E9E9E),
                            focusedLabelColor = Color(0xFF1B5E20),
                            unfocusedLabelColor = Color(0xFF757575),
                            cursorColor = Color(0xFF1B5E20)
                        )
                    )

                    // Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email", color = Color(0xFF424242)) },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF1B5E20))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = Color(0xFF1B5E20),
                            unfocusedBorderColor = Color(0xFF9E9E9E),
                            focusedLabelColor = Color(0xFF1B5E20),
                            unfocusedLabelColor = Color(0xFF757575),
                            cursorColor = Color(0xFF1B5E20)
                        )
                    )

                    // Username
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username", color = Color(0xFF424242)) },
                        leadingIcon = {
                            Icon(Icons.Default.AccountCircle, contentDescription = null, tint = Color(0xFF1B5E20))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = Color(0xFF1B5E20),
                            unfocusedBorderColor = Color(0xFF9E9E9E),
                            focusedLabelColor = Color(0xFF1B5E20),
                            unfocusedLabelColor = Color(0xFF757575),
                            cursorColor = Color(0xFF1B5E20)
                        )
                    )

                    // Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password", color = Color(0xFF424242)) },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF1B5E20))
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = Color(0xFF757575)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = Color(0xFF1B5E20),
                            unfocusedBorderColor = Color(0xFF9E9E9E),
                            focusedLabelColor = Color(0xFF1B5E20),
                            unfocusedLabelColor = Color(0xFF757575),
                            cursorColor = Color(0xFF1B5E20)
                        )
                    )

                    // Confirm Password
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm Password", color = Color(0xFF424242)) },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF1B5E20))
                        },
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(
                                    if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = Color(0xFF757575)
                                )
                            }
                        },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = Color(0xFF1B5E20),
                            unfocusedBorderColor = Color(0xFF9E9E9E),
                            focusedLabelColor = Color(0xFF1B5E20),
                            unfocusedLabelColor = Color(0xFF757575),
                            cursorColor = Color(0xFF1B5E20)
                        ),
                        isError = confirmPassword.isNotEmpty() && password != confirmPassword,
                        supportingText = {
                            if (confirmPassword.isNotEmpty() && password != confirmPassword) {
                                Text("Passwords do not match", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    )

                    // Program Dropdown
                    ExposedDropdownMenuBox(
                        expanded = programExpanded,
                        onExpandedChange = { programExpanded = !programExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedProgram,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Program", color = Color(0xFF424242)) },
                            leadingIcon = {
                                Icon(Icons.Default.School, contentDescription = null, tint = Color(0xFF1B5E20))
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = programExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedBorderColor = Color(0xFF1B5E20),
                                unfocusedBorderColor = Color(0xFF9E9E9E),
                                focusedLabelColor = Color(0xFF1B5E20),
                                unfocusedLabelColor = Color(0xFF757575),
                                cursorColor = Color(0xFF1B5E20)
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = programExpanded,
                            onDismissRequest = { programExpanded = false }
                        ) {
                            programs.forEach { program ->
                                DropdownMenuItem(
                                    text = { Text(program, color = Color.Black) },
                                    onClick = {
                                        selectedProgram = program
                                        programExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Year Level Dropdown
                    ExposedDropdownMenuBox(
                        expanded = yearExpanded,
                        onExpandedChange = { yearExpanded = !yearExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedYearLevel,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Year Level", color = Color(0xFF424242)) },
                            leadingIcon = {
                                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color(0xFF1B5E20))
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = yearExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedBorderColor = Color(0xFF1B5E20),
                                unfocusedBorderColor = Color(0xFF9E9E9E),
                                focusedLabelColor = Color(0xFF1B5E20),
                                unfocusedLabelColor = Color(0xFF757575),
                                cursorColor = Color(0xFF1B5E20)
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = yearExpanded,
                            onDismissRequest = { yearExpanded = false }
                        ) {
                            yearLevels.forEach { year ->
                                DropdownMenuItem(
                                    text = { Text(year, color = Color.Black) },
                                    onClick = {
                                        selectedYearLevel = year
                                        yearExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Sign Up Button
                    Button(
                        onClick = {
                            val fullName = "$firstName $lastName".trim()
                            // Validation
                            when {
                                studentId.isBlank() -> Toast.makeText(context, "Please enter Student ID", Toast.LENGTH_SHORT).show()
                                firstName.isBlank() -> Toast.makeText(context, "Please enter First Name", Toast.LENGTH_SHORT).show()
                                lastName.isBlank() -> Toast.makeText(context, "Please enter Last Name", Toast.LENGTH_SHORT).show()
                                email.isBlank() -> Toast.makeText(context, "Please enter Email", Toast.LENGTH_SHORT).show()
                                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                                    Toast.makeText(context, "Please enter valid Email", Toast.LENGTH_SHORT).show()
                                username.isBlank() -> Toast.makeText(context, "Please enter Username", Toast.LENGTH_SHORT).show()
                                username.length < 4 -> Toast.makeText(context, "Username must be at least 4 characters", Toast.LENGTH_SHORT).show()
                                password.isBlank() -> Toast.makeText(context, "Please enter Password", Toast.LENGTH_SHORT).show()
                                password.length < 6 -> Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                                password != confirmPassword -> Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                                selectedProgram.isBlank() -> Toast.makeText(context, "Please select Program", Toast.LENGTH_SHORT).show()
                                selectedYearLevel.isBlank() -> Toast.makeText(context, "Please select Year Level", Toast.LENGTH_SHORT).show()
                                else -> {
                                    authViewModel.register(
                                        studentId = studentId,
                                        fullName = fullName,
                                        email = email,
                                        username = username,
                                        password = password,
                                        program = selectedProgram,
                                        yearLevel = selectedYearLevel
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1B5E20)
                        ),
                        enabled = authState !is AuthState.Loading
                    ) {
                        if (authState is AuthState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Create Account", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Already have account
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account? ",
                    color = Color(0xFF424242)
                )
                Text(
                    text = "Sign In",
                    color = Color(0xFF1B5E20),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}