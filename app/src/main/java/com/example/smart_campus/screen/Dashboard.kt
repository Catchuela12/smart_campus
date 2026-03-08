package com.example.smart_campus.screen

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smart_campus.screen.Announcement_data.AnnouncementAppDataBase
import com.example.smart_campus.screen.Announcement_data.AnnouncementRepository
import com.example.smart_campus.screen.Announcement_viewmodel.AnnouncementViewModel
import com.example.smart_campus.ui.theme.Smart_campusTheme
import androidx.compose.foundation.border
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// ── Activity ──────────────────────────────────────────────────────────────────

class Dashboard : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val userId    = intent.getIntExtra("USER_ID", 0)
        val studentId = intent.getStringExtra("STUDENT_ID") ?: "N/A"
        val fullName  = intent.getStringExtra("FULL_NAME") ?: "Student"
        val email     = intent.getStringExtra("EMAIL") ?: ""
        val program   = intent.getStringExtra("PROGRAM") ?: ""
        val yearLevel = intent.getStringExtra("YEAR_LEVEL") ?: ""

        // Load saved profile state (photo + display name) — must be after userId is read
        UserProfileState.init(applicationContext, userId)

        setContent {
            Smart_campusTheme {
                DashboardScreen(
                    userId     = userId,
                    name       = fullName,
                    studentNum = studentId,
                    email      = email,
                    program    = program,
                    yearLevel  = yearLevel
                )
            }
        }
    }
}

// ── Color palette ─────────────────────────────────────────────────────────────

object AppColors {
    val PrimaryGreen   = Color(0xFF1B5E20)
    val SecondaryGreen = Color(0xFF2E7D32)
    val LightGreen     = Color(0xFF4CAF50)
    val AccentGreen    = Color(0xFF66BB6A)
    val BackgroundGray = Color(0xFFF8F9FA)
    val CardWhite      = Color(0xFFFFFFFF)
    val TextPrimary    = Color(0xFF212121)
    val TextSecondary  = Color(0xFF757575)
    val DividerGray    = Color(0xFFE0E0E0)
    val ErrorRed       = Color(0xFFD32F2F)
    val LightGreenBg   = Color(0xFFE8F5E9)
}

// ── Recent Activity model ─────────────────────────────────────────────────────

data class RecentActivityItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val iconColor: Color,
    val timestamp: Long = System.currentTimeMillis()
)

private fun Long.toRelativeTime(): String {
    val diff = System.currentTimeMillis() - this
    return when {
        diff < 60_000      -> "Just now"
        diff < 3_600_000   -> "${diff / 60_000}m ago"
        diff < 86_400_000  -> "${diff / 3_600_000}h ago"
        else               -> SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(this))
    }
}

// ── DashboardScreen ───────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    userId: Int = 0,
    name: String = "Student",
    studentNum: String = "N/A",
    email: String = "",
    program: String = "",
    yearLevel: String = ""
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope       = rememberCoroutineScope()
    val context     = LocalContext.current
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Announcements ViewModel
    val announcementViewModel: AnnouncementViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(AnnouncementViewModel::class.java)) {
                    val repository = AnnouncementRepository(
                        AnnouncementAppDataBase.getDatabase(context.applicationContext).announcementDao()
                    )
                    @Suppress("UNCHECKED_CAST")
                    return AnnouncementViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    )
    val announcements by announcementViewModel.allAnnouncements.collectAsState()
    val unreadCount    = announcements.count { !it.isRead }

    // Recent activity list — max 5 entries, most recent first, no duplicates
    val recentActivity = remember { mutableStateListOf<RecentActivityItem>() }

    fun logActivity(item: RecentActivityItem) {
        recentActivity.removeAll { it.title == item.title }
        recentActivity.add(0, item)
        if (recentActivity.size > 5) recentActivity.removeAt(recentActivity.lastIndex)
    }

    var cardsVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { cardsVisible = true }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(320.dp),
                drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
                drawerContainerColor = Color(0xFFFAFAFA)
            ) {
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
                        // Dynamic avatar — photo or fallback icon
                        val drawerImageUri = UserProfileState.profileImageUri
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.25f))
                                .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (drawerImageUri != null) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(drawerImageUri)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Profile picture",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(CircleShape)
                                )
                            } else {
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
                        Surface(shape = RoundedCornerShape(12.dp), color = Color.White.copy(alpha = 0.25f)) {
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
                Text(
                    text = "MENU",
                    style = MaterialTheme.typography.labelMedium,
                    color = AppColors.TextSecondary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                    fontSize = 11.sp,
                    letterSpacing = 1.2.sp
                )

                DrawerMenuItem(icon = Icons.Default.Person, label = "Profile", onClick = {
                    scope.launch { drawerState.close() }
                    context.startActivity(Intent(context, ProfileScreen::class.java).apply {
                        putExtra("USER_ID", userId)
                        putExtra("STUDENT_ID", studentNum)
                        putExtra("FULL_NAME", name)
                        putExtra("EMAIL", email)
                        putExtra("PROGRAM", program)
                        putExtra("YEAR_LEVEL", yearLevel)
                    })
                })
                DrawerMenuItem(icon = Icons.Default.Settings, label = "Settings", onClick = {
                    scope.launch { drawerState.close() }
                    context.startActivity(Intent(context, SettingScreen::class.java))
                })
                DrawerMenuItem(icon = Icons.Default.Info, label = "Campus Info", onClick = {
                    scope.launch { drawerState.close() }
                    context.startActivity(Intent(context, CampusInfo::class.java))
                })

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    color = AppColors.DividerGray,
                    thickness = 1.dp
                )
                Spacer(modifier = Modifier.weight(1f))

                DrawerMenuItem(
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
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
                            Text("Smart CAMPUS", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = AppColors.PrimaryGreen,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    ),
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", modifier = Modifier.size(28.dp))
                        }
                    },
                    actions = {
                        // Notification bell also logs activity
                        IconButton(onClick = {
                            logActivity(RecentActivityItem(
                                title     = "Announcements",
                                subtitle  = if (unreadCount > 0) "$unreadCount unread messages" else "No new announcements",
                                icon      = Icons.Default.Notifications,
                                iconColor = Color(0xFF7B1FA2)
                            ))
                            context.startActivity(Intent(context, AnnouncementScreen::class.java))
                        }) {
                            if (unreadCount > 0) {
                                BadgedBox(badge = {
                                    Badge(containerColor = Color(0xFFFF5252), contentColor = Color.White) {
                                        Text(unreadCount.toString(), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }) {
                                    Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.White)
                                }
                            } else {
                                Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.White)
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
                // Welcome banner
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
                            text = name.substringBeforeLast(" ", name),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Have a productive day! ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }

                Column(modifier = Modifier.padding(16.dp)) {

                    // ── Quick Access ──────────────────────────────────────────
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
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        logActivity(RecentActivityItem(
                                            title     = "Schedule",
                                            subtitle  = "Viewed class schedule",
                                            icon      = Icons.Default.DateRange,
                                            iconColor = Color(0xFF1976D2)
                                        ))
                                        context.startActivity(Intent(context, ScheduleScreen::class.java))
                                    }
                                )
                                EnhancedCard(
                                    icon = Icons.Default.CheckCircle,
                                    title = "To-Do List",
                                    subtitle = "Manage tasks",
                                    color = Color(0xFFFF8F00),
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        logActivity(RecentActivityItem(
                                            title     = "To-Do List",
                                            subtitle  = "Managed tasks",
                                            icon      = Icons.Default.CheckCircle,
                                            iconColor = Color(0xFFFF8F00)
                                        ))
                                        context.startActivity(Intent(context, ToDoScreen::class.java))
                                    }
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
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        logActivity(RecentActivityItem(
                                            title     = "Grades",
                                            subtitle  = "Checked academic grades",
                                            icon      = Icons.Default.Star,
                                            iconColor = Color(0xFFF57C00)
                                        ))
                                        context.startActivity(Intent(context, GradeScreen::class.java))
                                    }
                                )
                                EnhancedCard(
                                    icon = Icons.Default.Notifications,
                                    title = "Announcements",
                                    subtitle = if (unreadCount > 0) "$unreadCount new" else "No new",
                                    color = Color(0xFF7B1FA2),
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        logActivity(RecentActivityItem(
                                            title     = "Announcements",
                                            subtitle  = if (unreadCount > 0) "$unreadCount unread messages" else "No new announcements",
                                            icon      = Icons.Default.Notifications,
                                            iconColor = Color(0xFF7B1FA2)
                                        ))
                                        context.startActivity(Intent(context, AnnouncementScreen::class.java))
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // ── Recent Activity ───────────────────────────────────────
                    SectionHeader(title = "Recent Activity")
                    Spacer(modifier = Modifier.height(12.dp))

                    if (recentActivity.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = AppColors.CardWhite),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(AppColors.LightGreenBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.History,
                                        contentDescription = null,
                                        tint = AppColors.AccentGreen,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "No activity yet",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp,
                                    color = AppColors.TextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Features you visit will appear here",
                                    fontSize = 13.sp,
                                    color = AppColors.TextSecondary
                                )
                            }
                        }
                    } else {
                        recentActivity.forEach { item ->
                            RecentActivityCard(item = item)
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    // Logout dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = {
                Icon(
                    Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = null,
                    tint = AppColors.ErrorRed,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = { Text("Logout", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to log out of your account?") },
            confirmButton = {
                Button(
                    onClick = {
                        UserProfileState.clear()
                        context.startActivity(Intent(context, LoginScreen::class.java))
                        if (context is ComponentActivity) context.finish()
                        showLogoutDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.ErrorRed)
                ) { Text("Logout") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showLogoutDialog = false }) { Text("Cancel") }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

// ── RecentActivityCard ────────────────────────────────────────────────────────

@Composable
fun RecentActivityCard(item: RecentActivityItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppColors.CardWhite),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(item.iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = item.iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = item.timestamp.toRelativeTime(),
                fontSize = 11.sp,
                color = AppColors.TextSecondary.copy(alpha = 0.7f),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ── Unchanged composables ─────────────────────────────────────────────────────

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
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (isDestructive) AppColors.ErrorRed.copy(alpha = 0.15f)
                        else AppColors.LightGreenBg
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
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = if (isDestructive) AppColors.ErrorRed else AppColors.TextPrimary,
                fontSize = 15.sp,
                modifier = Modifier.weight(1f)
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
                .background(AppColors.AccentGreen, shape = RoundedCornerShape(2.dp))
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
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    var pressed by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .height(140.dp)
            .shadow(elevation = if (pressed) 1.dp else 4.dp, shape = RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = AppColors.CardWhite),
        shape = RoundedCornerShape(16.dp),
        onClick = { pressed = !pressed; onClick?.invoke() }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(color))
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
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
                    Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AppColors.TextPrimary)
                Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF424242), fontWeight = FontWeight.Medium)
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
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.CardWhite),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = AppColors.TextPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = AppColors.TextSecondary)
            }
        }
    }
}