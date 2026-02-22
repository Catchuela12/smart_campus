package com.example.smart_campus.screen

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
                    val repository = AnnouncementRepository(AnnouncementAppDataBase.getDatabase(context.applicationContext).announcementDao())
                    @Suppress("UNCHECKED_CAST")
                    return AnnouncementViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    )
    val announcements by viewModel.allAnnouncements.collectAsState()

    if (announcements.isEmpty()) {
        viewModel.addSampleData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Campus Announcements") },
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
        containerColor = AppColors.BackgroundGray
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(announcements) { announcement ->
                AnnouncementItem(
                    announcement = announcement,
                    onAnnouncementClicked = { viewModel.markAsRead(announcement) }
                )
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
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (!announcement.isRead) Color(0xFFE3F2FD) else AppColors.CardWhite
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AppColors.SecondaryGreen.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Announcement Icon",
                    tint = AppColors.PrimaryGreen,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = announcement.title,
                    fontWeight = if (!announcement.isRead) FontWeight.Bold else FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = AppColors.TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = announcement.content,
                    fontSize = 14.sp,
                    color = AppColors.TextSecondary,
                    maxLines = 2, // Limit content preview
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = announcement.date,
                    fontSize = 12.sp,
                    color = AppColors.TextSecondary
                )
            }
        }
    }
}