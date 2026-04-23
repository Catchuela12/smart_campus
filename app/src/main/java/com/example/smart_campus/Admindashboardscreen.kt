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
                    viewModel.addAnnouncement(newAnn)
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
                            adminName,
                            fontSize = 12.sp,
                            color    = AdminAccent
                        )
                    }
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout",
                            tint = AdminSubText
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick           = { showPostDialog = true },
                containerColor    = AdminAccent,
                contentColor      = AdminDark,
                shape             = CircleShape,
                modifier          = Modifier.size(60.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Post Announcement",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            // ── Stats banner ──────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(AdminMid, AdminBg)
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Announcements Overview",
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color      = AdminSubText,
                        letterSpacing = 0.8.sp
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AdminStatChip(
                            label  = "Total",
                            value  = totalCount.toString(),
                            color  = AdminAccent,
                            modifier = Modifier.weight(1f)
                        )
                        AdminStatChip(
                            label  = "Unread",
                            value  = unreadCount.toString(),
                            color  = AdminAmber,
                            modifier = Modifier.weight(1f)
                        )
                        AdminStatChip(
                            label  = "Read",
                            value  = readCount.toString(),
                            color  = AdminGreen,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // ── List ──────────────────────────────────────────────────────────
            if (announcements.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(AdminAccent.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = null,
                                tint     = AdminAccent.copy(alpha = 0.4f),
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Text(
                            "No announcements yet",
                            fontSize   = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = AdminText
                        )
                        Text(
                            "Tap + to post your first announcement",
                            fontSize = 13.sp,
                            color    = AdminSubText,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    item {
                        Text(
                            "${announcements.size} Announcement${if (announcements.size != 1) "s" else ""}",
                            fontSize   = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color      = AdminSubText,
                            letterSpacing = 0.5.sp,
                            modifier   = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    items(announcements, key = { it.id }) { ann ->
                        AnimatedVisibility(
                            visible = true,
                            enter   = fadeIn(tween(300)) + slideInVertically(tween(300)) { it / 2 }
                        ) {
                            AdminAnnouncementCard(
                                announcement = ann,
                                onEdit       = { announcementToEdit  = ann },
                                onDelete     = { announcementToDelete = ann }
                            )
                        }
                    }
                    item { Spacer(modifier = Modifier.height(72.dp)) } // FAB clearance
                }
            }
        }
    }
}

// ── AdminStatChip ─────────────────────────────────────────────────────────────

@Composable
private fun AdminStatChip(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape    = RoundedCornerShape(14.dp),
        colors   = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text       = value,
                fontSize   = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = color
            )
            Text(
                text     = label,
                fontSize = 11.sp,
                color    = color.copy(alpha = 0.75f),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ── AdminAnnouncementCard ─────────────────────────────────────────────────────

@Composable
private fun AdminAnnouncementCard(
    announcement: Announcement,
    onEdit:   () -> Unit,
    onDelete: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (!announcement.isRead) AdminAccent.copy(alpha = 0.6f)
        else AdminSubText.copy(alpha = 0.15f),
        label = "border"
    )

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = AdminSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Notification icon
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (!announcement.isRead)
                                AdminAccent.copy(alpha = 0.12f)
                            else
                                AdminSubText.copy(alpha = 0.08f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = null,
                        tint     = if (!announcement.isRead) AdminAccent else AdminSubText,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text       = announcement.title,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 15.sp,
                        color      = AdminText,
                        maxLines   = 1,
                        overflow   = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint     = AdminSubText,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text     = announcement.date,
                            fontSize = 11.sp,
                            color    = AdminSubText
                        )
                    }
                }
                // Unread badge
                if (!announcement.isRead) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = AdminAccent.copy(alpha = 0.15f)
                    ) {
                        Text(
                            "UNREAD",
                            fontSize   = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color      = AdminAccent,
                            modifier   = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            letterSpacing = 0.8.sp
                        )
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = AdminGreen.copy(alpha = 0.1f)
                    ) {
                        Text(
                            "READ",
                            fontSize   = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color      = AdminGreen,
                            modifier   = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            letterSpacing = 0.8.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Content preview
            Text(
                text      = announcement.content,
                fontSize  = 13.sp,
                color     = AdminSubText,
                maxLines  = 2,
                overflow  = TextOverflow.Ellipsis,
                lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Divider
            HorizontalDivider(
                color     = AdminSubText.copy(alpha = 0.1f),
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                // Edit button
                OutlinedButton(
                    onClick = onEdit,
                    shape   = RoundedCornerShape(10.dp),
                    colors  = ButtonDefaults.outlinedButtonColors(contentColor = AdminAccent),
                    border  = androidx.compose.foundation.BorderStroke(1.dp, AdminAccent.copy(alpha = 0.4f)),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Edit", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Delete button
                Button(
                    onClick  = onDelete,
                    shape    = RoundedCornerShape(10.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = AdminRed.copy(alpha = 0.15f)),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    modifier = Modifier.height(36.dp),
                    elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null,
                        tint     = AdminRed,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Delete", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = AdminRed)
                }
            }
        }
    }
}

// ── PostAnnouncementDialog ────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PostAnnouncementDialog(
    existing:  Announcement?,
    onDismiss: () -> Unit,
    onConfirm: (title: String, content: String) -> Unit
) {
    val isEditing = existing != null

    var title   by remember { mutableStateOf(existing?.title   ?: "") }
    var content by remember { mutableStateOf(existing?.content ?: "") }

    val titleError   = title.isBlank()
    val contentError = content.isBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        shape     = RoundedCornerShape(24.dp),
        containerColor = AdminSurface,
        icon = {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(AdminAccent.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (isEditing) Icons.Default.Edit else Icons.Default.Add,
                    contentDescription = null,
                    tint     = AdminAccent,
                    modifier = Modifier.size(26.dp)
                )
            }
        },
        title = {
            Text(
                if (isEditing) "Edit Announcement" else "New Announcement",
                fontWeight = FontWeight.Bold,
                fontSize   = 20.sp,
                color      = AdminText,
                textAlign  = TextAlign.Center
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

                // Title field
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        "TITLE",
                        fontSize      = 10.sp,
                        fontWeight    = FontWeight.ExtraBold,
                        color         = AdminAccent,
                        letterSpacing = 1.sp
                    )
                    OutlinedTextField(
                        value        = title,
                        onValueChange = { title = it },
                        placeholder  = { Text("e.g. Mid-term exam schedule", color = AdminSubText.copy(alpha = 0.5f)) },
                        singleLine   = true,
                        isError      = titleError && title.isEmpty(),
                        supportingText = {
                            if (titleError && title.isEmpty())
                                Text("Title is required", color = AdminRed, fontSize = 11.sp)
                        },
                        modifier     = Modifier.fillMaxWidth(),
                        shape        = RoundedCornerShape(12.dp),
                        colors       = adminTextFieldColors()
                    )
                }

                // Content field
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        "CONTENT",
                        fontSize      = 10.sp,
                        fontWeight    = FontWeight.ExtraBold,
                        color         = AdminAccent,
                        letterSpacing = 1.sp
                    )
                    OutlinedTextField(
                        value         = content,
                        onValueChange = { content = it },
                        placeholder   = { Text("Write the full announcement here…", color = AdminSubText.copy(alpha = 0.5f)) },
                        minLines      = 4,
                        maxLines      = 7,
                        isError       = contentError && content.isEmpty(),
                        supportingText = {
                            if (contentError && content.isEmpty())
                                Text("Content is required", color = AdminRed, fontSize = 11.sp)
                        },
                        modifier      = Modifier.fillMaxWidth(),
                        shape         = RoundedCornerShape(12.dp),
                        colors        = adminTextFieldColors()
                    )
                }

                // Character count hint
                Text(
                    "${content.length} characters",
                    fontSize = 11.sp,
                    color    = AdminSubText,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        onConfirm(title.trim(), content.trim())
                    }
                },
                enabled = title.isNotBlank() && content.isNotBlank(),
                shape   = RoundedCornerShape(12.dp),
                colors  = ButtonDefaults.buttonColors(containerColor = AdminAccent),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(
                    if (isEditing) Icons.Default.Save else Icons.Default.Send,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint     = AdminDark
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    if (isEditing) "Save Changes" else "Post Announcement",
                    fontWeight = FontWeight.Bold,
                    color      = AdminDark
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape   = RoundedCornerShape(12.dp),
                colors  = ButtonDefaults.outlinedButtonColors(contentColor = AdminSubText),
                border  = androidx.compose.foundation.BorderStroke(1.dp, AdminSubText.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Cancel") }
        }
    )
}

// ── TextField colors ──────────────────────────────────────────────────────────

@Composable
private fun adminTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor        = AdminText,
    unfocusedTextColor      = AdminText,
    focusedBorderColor      = AdminAccent,
    unfocusedBorderColor    = AdminSubText.copy(alpha = 0.3f),
    focusedLabelColor       = AdminAccent,
    unfocusedLabelColor     = AdminSubText,
    cursorColor             = AdminAccent,
    focusedContainerColor   = AdminAccent.copy(alpha = 0.05f),
    unfocusedContainerColor = Color.Transparent,
    errorBorderColor        = AdminRed,
    errorCursorColor        = AdminRed
)