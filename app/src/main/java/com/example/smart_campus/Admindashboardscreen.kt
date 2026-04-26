package com.example.smart_campus.screen

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import java.text.SimpleDateFormat
import java.util.*

// ── Admin color palette ───────────────────────────────────────────────────────

private val AdminDark    = Color(0xFF0D1B2A)   // deep navy
private val AdminMid     = Color(0xFF1B3A4B)   // mid navy
private val AdminAccent  = Color(0xFF00B4D8)   // cyan accent
private val AdminGreen   = Color(0xFF2DC653)   // success green
private val AdminRed     = Color(0xFFE63946)   // danger red
private val AdminAmber   = Color(0xFFFCA311)
private val AdminSurface = Color(0xFF14213D)   // card bg
private val AdminBg      = Color(0xFF0A1628)   // screen bg
private val AdminText    = Color(0xFFE0E0E0)
private val AdminSubText = Color(0xFF9E9E9E)

// ── Activity ──────────────────────────────────────────────────────────────────

class AdminDashboardScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val adminId  = intent.getIntExtra("ADMIN_ID", 0)
        val fullName = intent.getStringExtra("FULL_NAME") ?: "Administrator"

        setContent {
            Smart_campusTheme {
                AdminDashboard(
                    adminName = fullName,
                    onLogout  = {
                        startActivity(Intent(this, LoginScreen::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        })
                        finish()
                    }
                )
            }
        }
    }
}

// ── AdminDashboard ────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboard(
    adminName: String,
    onLogout: () -> Unit
) {
    val context = LocalContext.current

    // ── ViewModel wired to the shared AnnouncementAppDataBase ─────────────────
    val viewModel: AnnouncementViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(AnnouncementViewModel::class.java)) {
                    val repo = AnnouncementRepository(
                        AnnouncementAppDataBase.getDatabase(context.applicationContext)
                            .announcementDao()
                    )
                    @Suppress("UNCHECKED_CAST")
                    return AnnouncementViewModel(repo) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    )

    val announcements by viewModel.allAnnouncements.collectAsState()

    // ── Dialog / sheet state ──────────────────────────────────────────────────
    var showPostDialog      by remember { mutableStateOf(false) }
    var announcementToEdit  by remember { mutableStateOf<Announcement?>(null) }
    var announcementToDelete by remember { mutableStateOf<Announcement?>(null) }
    var showLogoutDialog    by remember { mutableStateOf(false) }

    // ── Stats ─────────────────────────────────────────────────────────────────
    val totalCount  = announcements.size
    val unreadCount = announcements.count { !it.isRead }
    val readCount   = announcements.count { it.isRead }

    // ── Dialogs ───────────────────────────────────────────────────────────────

    if (showPostDialog || announcementToEdit != null) {
        PostAnnouncementDialog(
            existing   = announcementToEdit,
            onDismiss  = {
                showPostDialog     = false
                announcementToEdit = null
            },
            onConfirm  = { title, content ->
                val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(Date())
                if (announcementToEdit == null) {
                    // New announcement — insert
                    val newAnn = Announcement(
                        title   = title,
                        content = content,
                        date    = dateStr,
                        isRead  = false
                    )
                    viewModel.addAnnouncement(context, newAnn)
                    Toast.makeText(context, "Announcement posted!", Toast.LENGTH_SHORT).show()
                } else {
                    // Edit — update existing
                    val updated = announcementToEdit!!.copy(
                        title   = title,
                        content = content,
                        date    = dateStr
                    )
                    viewModel.editAnnouncement(updated)
                    Toast.makeText(context, "Announcement updated!", Toast.LENGTH_SHORT).show()
                }
                showPostDialog     = false
                announcementToEdit = null
            }
        )
    }

    announcementToDelete?.let { ann ->
        AlertDialog(
            onDismissRequest = { announcementToDelete = null },
            shape = RoundedCornerShape(20.dp),
            containerColor = AdminSurface,
            icon = {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint    = AdminRed,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    "Delete Announcement",
                    fontWeight = FontWeight.Bold,
                    color      = AdminText
                )
            },
            text = {
                Text(
                    "Are you sure you want to delete \"${ann.title}\"? This cannot be undone.",
                    color      = AdminSubText,
                    fontSize   = 14.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteAnnouncement(ann)
                        Toast.makeText(context, "Deleted.", Toast.LENGTH_SHORT).show()
                        announcementToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AdminRed),
                    shape  = RoundedCornerShape(12.dp)
                ) { Text("Delete", fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { announcementToDelete = null },
                    shape   = RoundedCornerShape(12.dp),
                    colors  = ButtonDefaults.outlinedButtonColors(contentColor = AdminSubText)
                ) { Text("Cancel") }
            }
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            shape = RoundedCornerShape(20.dp),
            containerColor = AdminSurface,
            icon = {
                Icon(
                    Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = null,
                    tint    = AdminRed,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text("Logout", fontWeight = FontWeight.Bold, color = AdminText)
            },
            text = {
                Text(
                    "Are you sure you want to log out of the admin panel?",
                    color    = AdminSubText,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { onLogout() },
                    colors  = ButtonDefaults.buttonColors(containerColor = AdminRed),
                    shape   = RoundedCornerShape(12.dp)
                ) { Text("Logout", fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showLogoutDialog = false },
                    shape   = RoundedCornerShape(12.dp),
                    colors  = ButtonDefaults.outlinedButtonColors(contentColor = AdminSubText)
                ) { Text("Cancel") }
            }
        )
    }

    // ── Scaffold ──────────────────────────────────────────────────────────────

    Scaffold(
        containerColor = AdminBg,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(AdminDark, AdminMid)
                        )
                    )
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Admin badge
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(AdminAccent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint     = AdminAccent,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Admin Panel",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize   = 18.sp,
                            color      = AdminText
                        )
                        Text(
                            "Welcome, $adminName",
                            fontSize = 12.sp,
                            color    = AdminSubText
                        )
                    }

                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout",
                            tint = AdminRed
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            LargeFloatingActionButton(
                onClick = { showPostDialog = true },
                containerColor = AdminAccent,
                contentColor   = Color.White,
                shape          = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Post Announcement")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Stats Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard("Total", totalCount.toString(), AdminAccent, Modifier.weight(1f))
                StatCard("Unread", unreadCount.toString(), AdminAmber, Modifier.weight(1f))
                StatCard("Read", readCount.toString(), AdminGreen, Modifier.weight(1f))
            }

            Text(
                "MANAGE ANNOUNCEMENTS",
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = AdminSubText,
                letterSpacing = 1.sp
            )

            if (announcements.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Campaign,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint     = AdminSurface
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No announcements posted yet.",
                            color = AdminSubText,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(announcements) { announcement ->
                        AdminAnnouncementItem(
                            announcement = announcement,
                            onEdit = { announcementToEdit = it },
                            onDelete = { announcementToDelete = it }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, accent: Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AdminSurface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 12.sp, color = AdminSubText)
            Text(
                value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = accent
            )
        }
    }
}

@Composable
fun AdminAnnouncementItem(
    announcement: Announcement,
    onEdit: (Announcement) -> Unit,
    onDelete: (Announcement) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AdminSurface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (announcement.isRead) AdminMid else AdminAccent.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (announcement.isRead) Icons.Default.DoneAll else Icons.Default.NotificationsActive,
                    contentDescription = null,
                    tint = if (announcement.isRead) AdminSubText else AdminAccent,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    announcement.title,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp,
                    color      = AdminText,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis
                )
                Text(
                    announcement.date,
                    fontSize = 12.sp,
                    color    = AdminSubText
                )
            }

            Row {
                IconButton(onClick = { onEdit(announcement) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = AdminAccent)
                }
                IconButton(onClick = { onDelete(announcement) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AdminRed)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostAnnouncementDialog(
    existing: Announcement?,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var title by remember { mutableStateOf(existing?.title ?: "") }
    var content by remember { mutableStateOf(existing?.content ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = AdminSurface,
        title = {
            Text(
                if (existing == null) "New Announcement" else "Edit Announcement",
                fontWeight = FontWeight.Bold,
                color      = AdminText
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = AdminText,
                        unfocusedTextColor = AdminText,
                        focusedBorderColor = AdminAccent,
                        unfocusedBorderColor = AdminMid
                    )
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Message Body") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = AdminText,
                        unfocusedTextColor = AdminText,
                        focusedBorderColor = AdminAccent,
                        unfocusedBorderColor = AdminMid
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (title.isNotBlank() && content.isNotBlank()) onConfirm(title, content) },
                enabled = title.isNotBlank() && content.isNotBlank(),
                shape   = RoundedCornerShape(12.dp),
                colors  = ButtonDefaults.buttonColors(containerColor = AdminAccent)
            ) {
                Text(if (existing == null) "Post" else "Save Changes", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = AdminSubText)
            }
        }
    )
}
