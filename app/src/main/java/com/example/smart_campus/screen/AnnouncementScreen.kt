package com.example.smart_campus.screen

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smart_campus.screen.Announcement_data.Announcement
import com.example.smart_campus.screen.Announcement_data.AnnouncementAppDataBase
import com.example.smart_campus.screen.Announcement_data.AnnouncementRepository
import com.example.smart_campus.screen.Announcement_viewmodel.AnnouncementViewModel
import com.example.smart_campus.ui.theme.Smart_campusTheme

class AnnouncementScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Load persisted theme so dark mode applies here too
        AppThemeState.init(applicationContext)

        setContent {
            Smart_campusTheme {
                AnnouncementScreenContent()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementScreenContent() {
    val context = LocalContext.current

    val viewModel: AnnouncementViewModel = viewModel(
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

    val announcements by viewModel.allAnnouncements.collectAsState()

    LaunchedEffect(announcements.isEmpty()) {
        if (announcements.isEmpty()) {
            viewModel.addSampleData()
        }
    }

    val unreadCount = announcements.count { !it.isRead }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Announcements",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        if (unreadCount > 0) {
                            Text(
                                "$unreadCount unread",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.PrimaryGreen,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = { (context as? Activity)?.finish() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background  // ✅ was AppColors.BackgroundGray
    ) { paddingValues ->

        if (announcements.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)  // ✅ was AppColors.TextSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No announcements yet",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant  // ✅ was AppColors.TextSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(announcements, key = { it.id }) { announcement ->
                    EnhancedAnnouncementItem(
                        announcement = announcement,
                        onAnnouncementClicked = { viewModel.markAsRead(announcement) }
                    )
                }
            }
        }
    }
}

@Composable
fun EnhancedAnnouncementItem(
    announcement: Announcement,
    onAnnouncementClicked: () -> Unit
) {
    val isDark = AppThemeState.isDarkMode

    val backgroundColor by animateColorAsState(
        targetValue = when {
            !announcement.isRead && isDark -> Color(0xFF1A2E1A)   // dark unread
            !announcement.isRead           -> Color(0xFFE8F5E9)   // light unread
            isDark                         -> MaterialTheme.colorScheme.surface
            else                           -> Color.White
        },
        label = "background"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAnnouncementClicked() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (!announcement.isRead) 4.dp else 2.dp
        ),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icon box
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                AppColors.PrimaryGreen.copy(alpha = 0.15f),
                                AppColors.SecondaryGreen.copy(alpha = 0.15f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = AppColors.PrimaryGreen,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = announcement.title,
                        fontWeight = if (!announcement.isRead) FontWeight.Bold else FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface,  // ✅ was AppColors.TextPrimary
                        modifier = Modifier.weight(1f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (!announcement.isRead) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(AppColors.PrimaryGreen)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = announcement.content,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,  // ✅ was AppColors.TextSecondary
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Circle,
                        contentDescription = null,
                        modifier = Modifier.size(4.dp),
                        tint = AppColors.AccentGreen
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = announcement.date,
                        fontSize = 12.sp,
                        color = AppColors.AccentGreen,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}