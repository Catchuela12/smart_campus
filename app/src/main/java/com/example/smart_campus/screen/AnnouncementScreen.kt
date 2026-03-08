package com.example.smart_campus.screen

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smart_campus.screen.Announcement_viewmodel.AnnouncementViewModel
import com.example.smart_campus.screen.Announcement_data.Announcement
import com.example.smart_campus.ui.theme.Smart_campusTheme

/**
 * Activity for the Announcement Screen.
 * This class is what Dashboard.kt refers to in its Intent.
 */
class AnnouncementScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Smart_campusTheme {
                // Use the renamed composable
                AnnouncementScreenContent()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementScreenContent(
    viewModel: AnnouncementViewModel = viewModel(factory = AnnouncementViewModel.Factory)
) {
    val announcements by viewModel.allAnnouncements.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF1B5E20), Color(0xFF2E7D32))
                        )
                    )
                    .windowInsetsPadding(WindowInsets.statusBars)
            ) {
                // Modified back button and title to be close to each other, like in ScheduleScreen
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { (context as? Activity)?.finish() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "Announcements",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    )
                }
                
                // Keep the subtitle below
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp, start = 20.dp, end = 20.dp)
                ) {
                    Text(
                        text = "Stay updated with the latest campus news",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    )
                }
            }
        },
        containerColor = Color(0xFFF5F7FA),
        contentWindowInsets = WindowInsets.navigationBars
    ) { paddingValues ->
        if (announcements.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color(0xFF2E7D32))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Loading announcements...", color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(announcements) { announcement ->
                    AnnouncementItem(announcement = announcement, onAnnouncementClicked = {
                        viewModel.markAsRead(announcement)
                    })
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
fun AnnouncementItem(announcement: Announcement, onAnnouncementClicked: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAnnouncementClicked() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Badge
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(announcement.categoryColor).copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getIconForName(announcement.iconName),
                    contentDescription = null,
                    tint = Color(announcement.categoryColor),
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = announcement.date,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                    if (!announcement.isRead) {
                        Surface(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape),
                            color = Color(0xFF1E88E5)
                        ) {}
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = announcement.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = if (announcement.isRead) FontWeight.SemiBold else FontWeight.Bold,
                        fontSize = 17.sp
                    ),
                    color = Color(0xFF263238),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = announcement.content,
                    style = MaterialTheme.typography.bodySmall.copy(
                        lineHeight = 18.sp
                    ),
                    color = Color(0xFF546E7A),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

fun getIconForName(name: String): ImageVector {
    return when (name) {
        "Event" -> Icons.Default.Event
        "LibraryBooks" -> Icons.Default.LibraryBooks
        "Work" -> Icons.Default.Work
        "School" -> Icons.Default.School
        "Build" -> Icons.Default.Build
        "Info" -> Icons.Default.Info
        else -> Icons.Default.Notifications
    }
}