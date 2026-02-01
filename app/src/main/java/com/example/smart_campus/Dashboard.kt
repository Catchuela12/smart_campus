package com.example.smart_campus

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smart_campus.ui.theme.Smart_campusTheme
import kotlinx.coroutines.launch

class Dashboard : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Smart_campusTheme {
                DashboardScreen(
                    name = "JohnEric L. Catchuela",
                    studentNum = "2300432"
                )
            }
        }
    }
}

// Custom color palette
object AppColors {
    val PrimaryGreen = Color(0xFF1B5E20)
    val SecondaryGreen = Color(0xFF2E7D32)
    val LightGreen = Color(0xFF4CAF50)
    val AccentGreen = Color(0xFF66BB6A)
    val BackgroundGray = Color(0xFFF8F9FA)
    val CardWhite = Color(0xFFFFFFFF)
    val TextPrimary = Color(0xFF212121)
    val TextSecondary = Color(0xFF757575)
    val DividerGray = Color(0xFFE0E0E0)
    val ErrorRed = Color(0xFFD32F2F)
    val LightGreenBg = Color(0xFFE8F5E9)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    name: String = "JohnEric L. Catchuela",
    studentNum: String = "2300432"
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Animation for cards
    var cardsVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        cardsVisible = true
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(320.dp),
                drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
                drawerContainerColor = Color(0xFFFAFAFA)
            ) {
                // Header with gradient and improved design
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    AppColors.PrimaryGreen,
                                    AppColors.SecondaryGreen,
                                    AppColors.LightGreen
                                )
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column {
                        // Avatar with border
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.3f))
                                .padding(3.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(42.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = name,
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.25f)
                        ) {
                            Text(
                                text = "ID: $studentNum",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Menu section title
                Text(
                    text = "MENU",
                    style = MaterialTheme.typography.labelMedium,
                    color = AppColors.TextSecondary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                    fontSize = 11.sp,
                    letterSpacing = 1.2.sp
                )

                // Menu items
                DrawerMenuItem(
                    icon = Icons.Default.Person,
                    label = "Profile",
                    onClick = {
                        scope.launch { drawerState.close() }
                        context.startActivity(Intent(context, ProfileScreen::class.java))
                    }
                )

                DrawerMenuItem(
                    icon = Icons.Default.Settings,
                    label = "Settings",
                    onClick = {
                        scope.launch { drawerState.close() }
                        context.startActivity(Intent(context, SettingScreen::class.java))
                    }
                )

                DrawerMenuItem(
                    icon = Icons.Default.Info,
                    label = "Campus Info",
                    onClick = {
                        scope.launch { drawerState.close() }
                        context.startActivity(Intent(context, CampusInfo::class.java))
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    color = AppColors.DividerGray,
                    thickness = 1.dp
                )

                Spacer(modifier = Modifier.weight(1f))

                // Logout button with distinct styling
                DrawerMenuItem(
                    icon = Icons.Default.ExitToApp,
                    label = "Logout",
                    onClick = { showLogoutDialog = true },
                    isDestructive = true
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                "EXE CAMPUS",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = AppColors.PrimaryGreen,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    ),
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Menu",
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* Notifications */ }) {
                            Badge(
                                containerColor = AppColors.ErrorRed
                            ) {
                                Icon(
                                    Icons.Default.Notifications,
                                    contentDescription = "Notifications",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(AppColors.BackgroundGray)
                    .verticalScroll(rememberScrollState())
            ) {
                // Welcome Section with gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    AppColors.PrimaryGreen,
                                    AppColors.SecondaryGreen.copy(alpha = 0.8f)
                                )
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column {
                        Text(
                            text = "Welcome back,",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = name.split(" ").firstOrNull() ?: name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Have a productive day! 🎓",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }

                // Content
                Column(modifier = Modifier.padding(16.dp)) {
                    // Quick Access Section
                    SectionHeader(title = "Quick Access")

                    Spacer(modifier = Modifier.height(12.dp))

                    AnimatedVisibility(
                        visible = cardsVisible,
                        enter = fadeIn() + expandVertically()
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                EnhancedCard(
                                    icon = Icons.Default.DateRange,
                                    title = "Schedule",
                                    subtitle = "View classes",
                                    color = Color(0xFF1976D2),
                                    modifier = Modifier.weight(1f)

                                )
                                EnhancedCard(
                                    icon = Icons.Default.Person,
                                    title = "Attendance",
                                    subtitle = "Check status",
                                    color = Color(0xFFD32F2F),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                EnhancedCard(
                                    icon = Icons.Default.Star,
                                    title = "Grades",
                                    subtitle = "View results",
                                    color = Color(0xFFF57C00),
                                    modifier = Modifier.weight(1f)
                                )
                                EnhancedCard(
                                    icon = Icons.Default.Notifications,
                                    title = "Announcements",
                                    subtitle = "5 new",
                                    color = Color(0xFF7B1FA2),
                                    modifier = Modifier.weight(1f)

                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Recent Activity Section
                    SectionHeader(title = "Recent Activity")

                    Spacer(modifier = Modifier.height(12.dp))

                    EnhancedActivityItem(
                        title = "Mobile Programming Class",
                        subtitle = "Today at 10:00 AM",
                        icon = Icons.Default.List,
                        iconColor = Color(0xFF1976D2)
                    )

                    EnhancedActivityItem(
                        title = "Assignment Submission",
                        subtitle = "Due tomorrow",
                        icon = Icons.Default.Edit,
                        iconColor = Color(0xFFF57C00)
                    )

                    EnhancedActivityItem(
                        title = "Campus Event",
                        subtitle = "Friday, 2:00 PM",
                        icon = Icons.Default.Place,
                        iconColor = Color(0xFF7B1FA2)
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    // Enhanced Logout Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = {
                Icon(
                    Icons.Default.ExitToApp,
                    contentDescription = null,
                    tint = AppColors.ErrorRed,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    "Logout",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("Are you sure you want to log out of your account?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        context.startActivity(Intent(context, LoginScreen::class.java))
                        if (context is ComponentActivity) context.finish()
                        showLogoutDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.ErrorRed
                    )
                ) {
                    Text("Logout")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = if (isDestructive) Color(0xFFFFEBEE) else Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon with background
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (isDestructive)
                            AppColors.ErrorRed.copy(alpha = 0.15f)
                        else
                            AppColors.LightGreenBg
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = if (isDestructive) AppColors.ErrorRed else AppColors.SecondaryGreen
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Label
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = if (isDestructive) AppColors.ErrorRed else AppColors.TextPrimary,
                fontSize = 15.sp,
                modifier = Modifier.weight(1f)
            )

            // Arrow indicator
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = if (isDestructive)
                    AppColors.ErrorRed.copy(alpha = 0.5f)
                else
                    AppColors.TextSecondary.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = AppColors.TextPrimary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .height(3.dp)
                .width(40.dp)
                .background(
                    AppColors.AccentGreen,
                    shape = RoundedCornerShape(2.dp)
                )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    var pressed by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .height(140.dp)
            .shadow(
                elevation = if (pressed) 1.dp else 4.dp,
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = AppColors.CardWhite),
        shape = RoundedCornerShape(16.dp),
        onClick = {
            pressed = !pressed
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Colored accent bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(color)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(color.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF424242),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun EnhancedActivityItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.CardWhite),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.TextSecondary
                )
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = AppColors.TextSecondary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}