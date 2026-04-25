package com.example.smart_campus.screen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.smart_campus.R
import com.example.smart_campus.ui.theme.Smart_campusTheme
import com.example.smart_campus.viewmodel.AuthState
import com.example.smart_campus.viewmodel.AuthViewModel

class LoginScreen : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Notifications enabled", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Notifications disabled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // Permission already granted
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        askNotificationPermission()

        setContent {
            Smart_campusTheme {
                LoginUI(
                    authViewModel = authViewModel,
                    onLoginSuccess = { user ->
                        // ── Regular student ───────────────────────────────────
                        Toast.makeText(this, "Welcome ${user.fullName}!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, Dashboard::class.java).apply {
                            putExtra("USER_ID",    user.id)
                            putExtra("STUDENT_ID", user.studentId)
                            putExtra("FULL_NAME",  user.fullName)
                            putExtra("EMAIL",      user.email)
                            putExtra("PROGRAM",    user.program)
                            putExtra("YEAR_LEVEL", user.yearLevel)
                        }
                        startActivity(intent)
                        finish()
                    },
                    onAdminLoginSuccess = { user ->
                        // ── Admin ─────────────────────────────────────────────
                        Toast.makeText(this, "Welcome, ${user.fullName}!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, AdminDashboardScreen::class.java).apply {
                            putExtra("ADMIN_ID",  user.id)
                            putExtra("FULL_NAME", user.fullName)
                        }
                        startActivity(intent)
                        finish()
                    },
                    onNavigateToSignUp = {
                        startActivity(Intent(this, SignUpScreen::class.java))
                    },
                    onNavigateToForgotPassword = {
                        startActivity(Intent(this, ForgotPasswordScreen::class.java))
                    }
                )
            }
        }
    }
}

// Custom color palette for Login
object LoginColors {
    val PrimaryGreen = Color(0xFF1B5E20)
    val SecondaryGreen = Color(0xFF2E7D32)
    val LightGreen = Color(0xFF4CAF50)
    val AccentGreen = Color(0xFF66BB6A)
    val BackgroundGradientStart = Color(0xFFE8F5E9)
    val BackgroundGradientEnd = Color(0xFFF1F8F4)
    val CardWhite = Color(0xFFFFFFFF)
    val TextPrimary = Color(0xFF212121)
    val TextSecondary = Color(0xFF666666)
    val LightGreenBg = Color(0xFFE8F5E9)
    val InputBorder = Color(0xFFE0E0E0)
}

@Composable
fun LoginUI(
    authViewModel: AuthViewModel,
    onLoginSuccess: (com.example.smart_campus.data.User) -> Unit,
    onAdminLoginSuccess: (com.example.smart_campus.data.User) -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // ── Handle auth state changes ─────────────────────────────────────────────
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                val user = (authState as AuthState.Success).user
                onLoginSuccess(user)
                authViewModel.resetAuthState()
            }
            is AuthState.AdminSuccess -> {
                val user = (authState as AuthState.AdminSuccess).user
                onAdminLoginSuccess(user)
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
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        LoginColors.BackgroundGradientStart,
                        LoginColors.BackgroundGradientEnd,
                        Color(0xFFFAFDFB)
                    )
                )
            )
    ) {
        // Decorative circles in background
        Box(
            modifier = Modifier
                .size(400.dp)
                .offset(x = (-100).dp, y = (-150).dp)
                .alpha(0.15f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            LoginColors.LightGreen,
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = 250.dp, y = 500.dp)
                .alpha(0.1f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            LoginColors.AccentGreen,
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Logo and branding section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Logo with modern design and glow effect
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .shadow(
                            elevation = 20.dp,
                            shape = CircleShape,
                            spotColor = LoginColors.PrimaryGreen.copy(alpha = 0.3f)
                        )
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color.White,
                                    LoginColors.LightGreenBg.copy(alpha = 0.3f)
                                )
                            ),
                            shape = CircleShape
                        )
                ) {
                    Surface(
                        modifier = Modifier
                            .size(100.dp)
                            .align(Alignment.Center),
                        shape = CircleShape,
                        color = Color.White
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            LoginColors.LightGreenBg,
                                            Color.White
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ccs),
                                contentDescription = "Campus Logo",
                                modifier = Modifier.size(70.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // App branding with gradient text effect
                Text(
                    text = "Smart Campus",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = LoginColors.PrimaryGreen,
                    fontSize = 38.sp,
                    letterSpacing = (-0.5).sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Student Portal",
                    style = MaterialTheme.typography.bodyLarge,
                    color = LoginColors.TextSecondary,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Login card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(28.dp),
                        spotColor = LoginColors.PrimaryGreen.copy(alpha = 0.15f),
                        ambientColor = LoginColors.PrimaryGreen.copy(alpha = 0.1f)
                    ),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier.padding(32.dp)
                ) {
                    // Header
                    Column {
                        Text(
                            text = "Sign In",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = LoginColors.TextPrimary,
                            fontSize = 28.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Enter your credentials to access your account",
                            style = MaterialTheme.typography.bodyMedium,
                            color = LoginColors.TextSecondary,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(36.dp))

                    // Username field
                    Column {
                        Text(
                            text = "USERNAME",
                            style = MaterialTheme.typography.labelSmall,
                            color = LoginColors.TextSecondary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )

                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            placeholder = {
                                Text(
                                    "Enter username",
                                    color = LoginColors.TextSecondary.copy(alpha = 0.5f)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Username",
                                    tint = LoginColors.SecondaryGreen
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedBorderColor = LoginColors.PrimaryGreen,
                                unfocusedBorderColor = LoginColors.InputBorder,
                                cursorColor = LoginColors.PrimaryGreen,
                                focusedLeadingIconColor = LoginColors.PrimaryGreen,
                                unfocusedLeadingIconColor = LoginColors.TextSecondary,
                                focusedContainerColor = LoginColors.LightGreenBg.copy(alpha = 0.15f),
                                unfocusedContainerColor = Color(0xFFFAFAFA)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Password field
                    Column {
                        Text(
                            text = "PASSWORD",
                            style = MaterialTheme.typography.labelSmall,
                            color = LoginColors.TextSecondary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            placeholder = {
                                Text(
                                    "Enter password",
                                    color = LoginColors.TextSecondary.copy(alpha = 0.5f)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Password",
                                    tint = LoginColors.SecondaryGreen
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible)
                                            Icons.Default.Visibility
                                        else
                                            Icons.Default.VisibilityOff,
                                        contentDescription = if (passwordVisible)
                                            "Hide password"
                                        else
                                            "Show password",
                                        tint = LoginColors.TextSecondary
                                    )
                                }
                            },
                            singleLine = true,
                            visualTransformation = if (passwordVisible)
                                VisualTransformation.None
                            else
                                PasswordVisualTransformation(),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedBorderColor = LoginColors.PrimaryGreen,
                                unfocusedBorderColor = LoginColors.InputBorder,
                                cursorColor = LoginColors.PrimaryGreen,
                                focusedLeadingIconColor = LoginColors.PrimaryGreen,
                                unfocusedLeadingIconColor = LoginColors.TextSecondary,
                                focusedContainerColor = LoginColors.LightGreenBg.copy(alpha = 0.15f),
                                unfocusedContainerColor = Color(0xFFFAFAFA)
                            )
                        )
                    }

                    // Forgot password
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "Forgot Password?",
                            style = MaterialTheme.typography.bodySmall,
                            color = LoginColors.PrimaryGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.clickable { onNavigateToForgotPassword() }
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Login button with gradient
                    Button(
                        onClick = {
                            if (username.isEmpty() || password.isEmpty()) {
                                Toast.makeText(
                                    context,
                                    "Please fill in all fields",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                authViewModel.login(username, password)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(14.dp),
                                spotColor = LoginColors.PrimaryGreen.copy(alpha = 0.4f)
                            ),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LoginColors.PrimaryGreen,
                            disabledContainerColor = LoginColors.PrimaryGreen.copy(alpha = 0.6f)
                        ),
                        enabled = authState !is AuthState.Loading
                    ) {
                        if (authState is AuthState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(26.dp),
                                color = Color.White,
                                strokeWidth = 3.dp
                            )
                        } else {
                            Text(
                                text = "Sign In",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Divider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = LoginColors.InputBorder,
                            thickness = 1.dp
                        )
                        Text(
                            text = "OR",
                            modifier = Modifier.padding(horizontal = 16.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = LoginColors.TextSecondary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = LoginColors.InputBorder,
                            thickness = 1.dp
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Sign up section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "New to Smart Campus? ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = LoginColors.TextSecondary,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Create Account",
                            style = MaterialTheme.typography.bodyMedium,
                            color = LoginColors.PrimaryGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable { onNavigateToSignUp() }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Footer
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Text(
                    text = "Smart Campus v1.0.5",
                    style = MaterialTheme.typography.bodySmall,
                    color = LoginColors.TextSecondary.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
                Text(
                    text = "© 2026 All rights reserved",
                    style = MaterialTheme.typography.bodySmall,
                    color = LoginColors.TextSecondary.copy(alpha = 0.5f),
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}