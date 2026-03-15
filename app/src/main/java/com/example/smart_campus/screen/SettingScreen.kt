package com.example.smart_campus.screen

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.smart_campus.ui.theme.Smart_campusTheme
import com.example.smart_campus.viewmodel.AuthState
import com.example.smart_campus.viewmodel.AuthViewModel

// ── Activity ──────────────────────────────────────────────────────────────────

class SettingScreen : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val userId = intent.getIntExtra("USER_ID", 0)

        // Load persisted theme preference
        AppThemeState.init(applicationContext)

        setContent {
            Smart_campusTheme {
                SettingsView(
                    userId = userId,
                    authViewModel = authViewModel,
                    onBack = { finish() }
                )
            }
        }
    }
}

// ── Colors ────────────────────────────────────────────────────────────────────

private val GreenDark    = Color(0xFF1B5E20)
private val GreenMid     = Color(0xFF2E7D32)
private val GreenLight   = Color(0xFF4CAF50)
private val GreenBg      = Color(0xFFE8F5E9)
private val PageBg       = Color(0xFFF2F4F7)
private val CardBg       = Color.White
private val TextPrimary  = Color(0xFF1A1A1A)
private val TextSecondary = Color(0xFF757575)
private val DividerColor = Color(0xFFF0F0F0)

// ── SettingsView ──────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsView(
    userId: Int = 0,
    authViewModel: AuthViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    // ── Local preference states ───────────────────────────────────────────────
    // Dark mode driven by the app-wide singleton
    val darkMode = AppThemeState.isDarkMode
    var pushEnabled    by remember { mutableStateOf(true) }
    var emailEnabled   by remember { mutableStateOf(true) }
    var fontSize       by remember { mutableStateOf(1) }  // 0=Small 1=Medium 2=Large

    // ── Dialog states ─────────────────────────────────────────────────────────
    var showChangePassword by remember { mutableStateOf(false) }
    var showAboutApp       by remember { mutableStateOf(false) }
    var showFontSize       by remember { mutableStateOf(false) }

    // ── Change password fields ────────────────────────────────────────────────
    var currentPassword    by remember { mutableStateOf("") }
    var newPassword        by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }
    var currentPassVisible by remember { mutableStateOf(false) }
    var newPassVisible     by remember { mutableStateOf(false) }
    var confirmPassVisible by remember { mutableStateOf(false) }

    // Handle AuthViewModel responses for change password
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                Toast.makeText(context, "Password changed successfully!", Toast.LENGTH_SHORT).show()
                showChangePassword = false
                currentPassword = ""; newPassword = ""; confirmNewPassword = ""
                authViewModel.resetAuthState()
            }
            is AuthState.Error -> {
                val msg = (authState as AuthState.Error).message
                // Only show if we triggered it from this screen
                if (showChangePassword) {
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                }
                authViewModel.resetAuthState()
            }
            else -> {}
        }
    }

    // ── Change Password Dialog ────────────────────────────────────────────────
    if (showChangePassword) {
        AlertDialog(
            onDismissRequest = {
                showChangePassword = false
                currentPassword = ""; newPassword = ""; confirmNewPassword = ""
            },
            shape = RoundedCornerShape(24.dp),
            icon = {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(GreenBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = GreenDark, modifier = Modifier.size(26.dp))
                }
            },
            title = {
                Text("Change Password", fontWeight = FontWeight.Bold, fontSize = 20.sp, textAlign = TextAlign.Center)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Current Password
                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        label = { Text("Current Password") },
                        leadingIcon = { Icon(Icons.Default.LockOpen, contentDescription = null, tint = GreenMid) },
                        trailingIcon = {
                            IconButton(onClick = { currentPassVisible = !currentPassVisible }) {
                                Icon(
                                    if (currentPassVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null, tint = TextSecondary
                                )
                            }
                        },
                        visualTransformation = if (currentPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = settingsTextFieldColors()
                    )

                    // New Password
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("New Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = GreenMid) },
                        trailingIcon = {
                            IconButton(onClick = { newPassVisible = !newPassVisible }) {
                                Icon(
                                    if (newPassVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null, tint = TextSecondary
                                )
                            }
                        },
                        visualTransformation = if (newPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = settingsTextFieldColors(),
                        supportingText = {
                            if (newPassword.isNotBlank() && newPassword.length < 6)
                                Text("At least 6 characters", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                        }
                    )

                    // Confirm New Password
                    OutlinedTextField(
                        value = confirmNewPassword,
                        onValueChange = { confirmNewPassword = it },
                        label = { Text("Confirm New Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = GreenMid) },
                        trailingIcon = {
                            IconButton(onClick = { confirmPassVisible = !confirmPassVisible }) {
                                Icon(
                                    if (confirmPassVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null, tint = TextSecondary
                                )
                            }
                        },
                        visualTransformation = if (confirmPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = settingsTextFieldColors(),
                        isError = confirmNewPassword.isNotBlank() && newPassword != confirmNewPassword,
                        supportingText = {
                            if (confirmNewPassword.isNotBlank() && newPassword != confirmNewPassword)
                                Text("Passwords do not match", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                        }
                    )

                    // Strength indicator
                    if (newPassword.isNotBlank()) {
                        PasswordStrengthBar(password = newPassword)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        when {
                            currentPassword.isBlank() ->
                                Toast.makeText(context, "Enter your current password", Toast.LENGTH_SHORT).show()
                            newPassword.length < 6 ->
                                Toast.makeText(context, "New password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                            newPassword != confirmNewPassword ->
                                Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                            newPassword == currentPassword ->
                                Toast.makeText(context, "New password must be different", Toast.LENGTH_SHORT).show()
                            else ->
                                authViewModel.changePassword(userId, currentPassword, newPassword)
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenDark),
                    enabled = authState !is AuthState.Loading,
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    if (authState is AuthState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Update Password", fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showChangePassword = false
                        currentPassword = ""; newPassword = ""; confirmNewPassword = ""
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Cancel") }
            }
        )
    }

    // ── Font Size Dialog ──────────────────────────────────────────────────────
    if (showFontSize) {
        AlertDialog(
            onDismissRequest = { showFontSize = false },
            shape = RoundedCornerShape(24.dp),
            icon = {
                Box(
                    modifier = Modifier.size(52.dp).clip(RoundedCornerShape(14.dp)).background(GreenBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.FormatSize, contentDescription = null, tint = GreenDark, modifier = Modifier.size(26.dp))
                }
            },
            title = { Text("Font Size", fontWeight = FontWeight.Bold, fontSize = 20.sp, textAlign = TextAlign.Center) },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Preview
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = GreenBg,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Smart Campus",
                            fontSize = when (fontSize) { 0 -> 13.sp; 2 -> 19.sp; else -> 16.sp },
                            fontWeight = FontWeight.SemiBold,
                            color = GreenDark,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }

                    // Three option chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf("Small" to 0, "Medium" to 1, "Large" to 2).forEach { (label, value) ->
                            val selected = fontSize == value
                            Surface(
                                onClick = { fontSize = value },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                color = if (selected) GreenDark else GreenBg
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 14.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = when (value) { 0 -> "A"; 2 -> "A"; else -> "A" },
                                        fontSize = when (value) { 0 -> 13.sp; 2 -> 22.sp; else -> 17.sp },
                                        fontWeight = FontWeight.Bold,
                                        color = if (selected) Color.White else GreenDark
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        color = if (selected) Color.White.copy(alpha = 0.85f) else GreenDark.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val label = when (fontSize) { 0 -> "Small"; 2 -> "Large"; else -> "Medium" }
                        Toast.makeText(context, "Font size set to $label", Toast.LENGTH_SHORT).show()
                        showFontSize = false
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenDark),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Apply", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showFontSize = false },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Cancel") }
            }
        )
    }

    // ── About App Dialog ──────────────────────────────────────────────────────
    if (showAboutApp) {
        AlertDialog(
            onDismissRequest = { showAboutApp = false },
            shape = RoundedCornerShape(24.dp),
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // App icon
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(GreenMid, GreenLight)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.School, contentDescription = null, tint = Color.White, modifier = Modifier.size(44.dp))
                    }

                    Text("Smart Campus", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = GreenDark)
                    Surface(shape = RoundedCornerShape(8.dp), color = GreenBg) {
                        Text(
                            "Version 1.0.5",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GreenMid,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }

                    HorizontalDivider(color = DividerColor)

                    // Info rows
                    AboutRow(label = "Platform", value = "Android")
                    AboutRow(label = "Developed by", value = "Smart Campus Team")
                    AboutRow(label = "Build", value = "Release")
                    AboutRow(label = "Min SDK", value = "Android 8.0+")

                    HorizontalDivider(color = DividerColor)

                    Text(
                        "A modern student portal for managing schedules, grades, tasks, and campus announcements.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showAboutApp = false },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenDark),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) { Text("Close", fontWeight = FontWeight.Bold) }
            },
            title = null
        )
    }

    // ── Main Scaffold ─────────────────────────────────────────────────────────
    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(colors = listOf(GreenDark, GreenMid))
                    )
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Settings", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.White)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(PageBg)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // ── Account ───────────────────────────────────────────────────────
            SettingsSection(title = "Account", icon = Icons.Default.Person) {
                SettingsRow(
                    icon = Icons.Default.Security,
                    iconBg = Color(0xFFE3F2FD),
                    iconTint = Color(0xFF1565C0),
                    label = "Change Password",
                    subtitle = "Update your login password",
                    onClick = { showChangePassword = true }
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.Default.School,
                    iconBg = Color(0xFFEDE7F6),
                    iconTint = Color(0xFF4527A0),
                    label = "Academic Records",
                    subtitle = "View your enrollment info",
                    onClick = { Toast.makeText(context, "Coming soon!", Toast.LENGTH_SHORT).show() }
                )
            }

            // ── Appearance ────────────────────────────────────────────────────
            SettingsSection(title = "Appearance", icon = Icons.Default.Palette) {
                SettingsSwitchRow(
                    icon = Icons.Default.DarkMode,
                    iconBg = Color(0xFF263238),
                    iconTint = Color.White,
                    label = "Dark Mode",
                    subtitle = if (darkMode) "Currently on" else "Currently off",
                    checked = darkMode,
                    onCheckedChange = {
                        AppThemeState.setDarkMode(context, it)
                        Toast.makeText(context, if (it) "Dark mode enabled" else "Dark mode disabled", Toast.LENGTH_SHORT).show()
                    }
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.Default.FormatSize,
                    iconBg = Color(0xFFFFF3E0),
                    iconTint = Color(0xFFE65100),
                    label = "Font Size",
                    subtitle = when (fontSize) { 0 -> "Small"; 2 -> "Large"; else -> "Medium" },
                    onClick = { showFontSize = true }
                )
            }

            // ── Notifications ─────────────────────────────────────────────────
            SettingsSection(title = "Notifications", icon = Icons.Default.Notifications) {
                SettingsSwitchRow(
                    icon = Icons.Default.Notifications,
                    iconBg = Color(0xFFE8F5E9),
                    iconTint = GreenMid,
                    label = "Push Notifications",
                    subtitle = if (pushEnabled) "Enabled" else "Disabled",
                    checked = pushEnabled,
                    onCheckedChange = { pushEnabled = it }
                )
                SettingsDivider()
                SettingsSwitchRow(
                    icon = Icons.Default.Email,
                    iconBg = Color(0xFFFCE4EC),
                    iconTint = Color(0xFFC62828),
                    label = "Email Announcements",
                    subtitle = if (emailEnabled) "Receiving emails" else "Emails off",
                    checked = emailEnabled,
                    onCheckedChange = { emailEnabled = it }
                )
            }

            // ── Support & About ───────────────────────────────────────────────
            SettingsSection(title = "Support & About", icon = Icons.Default.Info) {
                SettingsRow(
                    icon = Icons.Default.Help,
                    iconBg = Color(0xFFE0F7FA),
                    iconTint = Color(0xFF00695C),
                    label = "Help Center",
                    subtitle = "FAQs and support",
                    onClick = { Toast.makeText(context, "Help Center coming soon!", Toast.LENGTH_SHORT).show() }
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.Default.Info,
                    iconBg = GreenBg,
                    iconTint = GreenDark,
                    label = "About App",
                    subtitle = "Version 1.0.5",
                    onClick = { showAboutApp = true }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// ── SettingsSection ───────────────────────────────────────────────────────────

@Composable
private fun SettingsSection(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Section header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = GreenMid, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = GreenMid,
                letterSpacing = 1.sp
            )
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(content = content)
        }
    }
}

// ── SettingsRow ───────────────────────────────────────────────────────────────

@Composable
private fun SettingsRow(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    label: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text(subtitle, fontSize = 12.sp, color = TextSecondary)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFFBDBDBD), modifier = Modifier.size(20.dp))
    }
}

// ── SettingsSwitchRow ─────────────────────────────────────────────────────────

@Composable
private fun SettingsSwitchRow(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    label: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text(subtitle, fontSize = 12.sp, color = TextSecondary)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = GreenMid,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFBDBDBD)
            )
        )
    }
}

// ── SettingsDivider ───────────────────────────────────────────────────────────

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = DividerColor,
        thickness = 1.dp
    )
}

// ── PasswordStrengthBar ───────────────────────────────────────────────────────

@Composable
private fun PasswordStrengthBar(password: String) {
    val strength = when {
        password.length < 6                                      -> 0
        password.length < 8                                      -> 1
        password.any { it.isDigit() } && password.any { it.isLetter() } -> 2
        password.any { it.isDigit() } && password.any { it.isLetter() } &&
                password.any { !it.isLetterOrDigit() }          -> 3
        else                                                     -> 2
    }
    val (label, color) = when (strength) {
        0    -> "Too short"  to Color(0xFFEF5350)
        1    -> "Weak"       to Color(0xFFFF7043)
        2    -> "Good"       to Color(0xFFFFA726)
        else -> "Strong"     to Color(0xFF66BB6A)
    }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Password strength", fontSize = 11.sp, color = TextSecondary)
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            (1..3).forEach { step ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (strength >= step) color else Color(0xFFE0E0E0))
                )
            }
        }
    }
}

// ── AboutRow ──────────────────────────────────────────────────────────────────

@Composable
private fun AboutRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 13.sp, color = TextSecondary)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
    }
}

// ── TextField colors ──────────────────────────────────────────────────────────

@Composable
private fun settingsTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor        = TextPrimary,
    unfocusedTextColor      = TextPrimary,
    focusedBorderColor      = GreenMid,
    unfocusedBorderColor    = Color(0xFFBDBDBD),
    focusedLabelColor       = GreenMid,
    unfocusedLabelColor     = TextSecondary,
    cursorColor             = GreenMid,
    focusedContainerColor   = GreenBg.copy(alpha = 0.3f),
    unfocusedContainerColor = Color.Transparent
)