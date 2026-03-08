package com.example.smart_campus.screen

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.window.Dialog
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smart_campus.data.Task
import com.example.smart_campus.ui.theme.Smart_campusTheme
import com.example.smart_campus.viewmodel.TaskViewModel
import com.example.smart_campus.viewmodel.TaskViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

// ── Activity ─────────────────────────────────────────────────────────────────

class ToDoScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Smart_campusTheme {
                ToDoScreenContent(onBack = { finish() })
            }
        }
    }
}

// ── Color tokens ──────────────────────────────────────────────────────────────

private val TodoGreen       = Color(0xFF1B5E20)
private val TodoGreenLight  = Color(0xFF2E7D32)
private val TodoGreenBg     = Color(0xFFE8F5E9)
private val TodoBg          = Color(0xFFF8F9FA)
private val TodoCardBg      = Color.White
private val TodoTextPrimary = Color(0xFF212121)
private val TodoTextSecondary = Color(0xFF757575)
private val TodoRed         = Color(0xFFD32F2F)
private val TodoRedBg       = Color(0xFFFFEBEE)
private val TodoAmber       = Color(0xFFF57C00)
private val TodoAmberBg     = Color(0xFFFFF3E0)

// ── Helpers ──────────────────────────────────────────────────────────────────

private fun Long.toDisplayDate(): String {
    if (this == 0L) return "No due date"
    val sdf = SimpleDateFormat("MMM dd, yyyy  hh:mm a", Locale.getDefault())
    return sdf.format(Date(this))
}

private fun Long.isOverdue(): Boolean =
    this > 0L && this < System.currentTimeMillis()

private fun Long.isDueSoon(): Boolean {
    if (this == 0L) return false
    val diff = this - System.currentTimeMillis()
    return diff in 0..(24 * 60 * 60 * 1000) // within 24 hours
}

// ── Main composable ───────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDoScreenContent(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val taskViewModel: TaskViewModel = viewModel(
        factory = TaskViewModelFactory(context.applicationContext as Application)
    )
    val tasks by taskViewModel.allTasks.collectAsState(initial = emptyList())

    // Sort: incomplete first (by due date), then completed
    val sortedTasks = remember(tasks) {
        val incomplete = tasks.filter { !it.isCompleted }.sortedWith(
            compareBy { if (it.dueDate == 0L) Long.MAX_VALUE else it.dueDate }
        )
        val completed = tasks.filter { it.isCompleted }
        incomplete + completed
    }

    var showAddEditDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "To-Do List",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            "${tasks.count { !it.isCompleted }} pending · ${tasks.count { it.isCompleted }} done",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TodoGreen,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    taskToEdit = null
                    showAddEditDialog = true
                },
                containerColor = TodoGreen,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        },
        containerColor = TodoBg
    ) { innerPadding ->

        if (sortedTasks.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(TodoGreenBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = TodoGreen.copy(alpha = 0.4f),
                            modifier = Modifier.size(52.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "All caught up!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TodoTextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Tap + to add a new task",
                        fontSize = 14.sp,
                        color = TodoTextSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                // Pending section header
                val pending = sortedTasks.filter { !it.isCompleted }
                val completed = sortedTasks.filter { it.isCompleted }

                if (pending.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "Pending",
                            count = pending.size,
                            color = TodoGreen
                        )
                    }
                    items(pending, key = { it.id }) { task ->
                        TaskCard(
                            task = task,
                            onToggleComplete = { taskViewModel.update(task.copy(isCompleted = !task.isCompleted)) },
                            onEdit = {
                                taskToEdit = task
                                showAddEditDialog = true
                            },
                            onDelete = { taskToDelete = task }
                        )
                    }
                }

                if (completed.isNotEmpty()) {
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                    item {
                        SectionHeader(
                            title = "Completed",
                            count = completed.size,
                            color = TodoTextSecondary
                        )
                    }
                    items(completed, key = { it.id }) { task ->
                        TaskCard(
                            task = task,
                            onToggleComplete = { taskViewModel.update(task.copy(isCompleted = !task.isCompleted)) },
                            onEdit = {
                                taskToEdit = task
                                showAddEditDialog = true
                            },
                            onDelete = { taskToDelete = task }
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(72.dp)) } // FAB clearance
            }
        }
    }

    // ── Add / Edit dialog ─────────────────────────────────────────────────────
    if (showAddEditDialog) {
        AddEditTaskDialog(
            taskToEdit = taskToEdit,
            onDismiss = { showAddEditDialog = false },
            onConfirm = { title, description, dueDate ->
                if (taskToEdit == null) {
                    taskViewModel.insert(Task(title = title, description = description, dueDate = dueDate))
                } else {
                    taskViewModel.update(taskToEdit!!.copy(title = title, description = description, dueDate = dueDate))
                }
                showAddEditDialog = false
            }
        )
    }

    // ── Delete confirmation dialog ────────────────────────────────────────────
    taskToDelete?.let { task ->
        AlertDialog(
            onDismissRequest = { taskToDelete = null },
            icon = {
                Icon(Icons.Default.Delete, contentDescription = null, tint = TodoRed, modifier = Modifier.size(36.dp))
            },
            title = { Text("Delete Task", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete \"${task.title}\"?") },
            confirmButton = {
                Button(
                    onClick = {
                        taskViewModel.delete(task)
                        taskToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TodoRed),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Delete") }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { taskToDelete = null },
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Cancel") }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}

// ── Section header ────────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(title: String, count: Int, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = color,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Surface(
            shape = CircleShape,
            color = color.copy(alpha = 0.12f)
        ) {
            Text(
                text = count.toString(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
    }
}

// ── Task card ─────────────────────────────────────────────────────────────────

@Composable
private fun TaskCard(
    task: Task,
    onToggleComplete: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isOverdue = task.dueDate.isOverdue() && !task.isCompleted
    val isDueSoon = task.dueDate.isDueSoon() && !task.isCompleted

    val cardBg by animateColorAsState(
        targetValue = when {
            task.isCompleted -> Color(0xFFFAFAFA)
            isOverdue        -> TodoRedBg
            isDueSoon        -> TodoAmberBg
            else             -> TodoCardBg
        },
        label = "cardBg"
    )

    val accentColor = when {
        task.isCompleted -> TodoTextSecondary
        isOverdue        -> TodoRed
        isDueSoon        -> TodoAmber
        else             -> TodoGreen
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (task.isCompleted) 1.dp else 3.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Completion toggle
            val checkScale by animateFloatAsState(
                targetValue = if (task.isCompleted) 1.1f else 1f,
                label = "checkScale"
            )
            IconButton(
                onClick = onToggleComplete,
                modifier = Modifier
                    .size(36.dp)
                    .scale(checkScale)
            ) {
                Icon(
                    imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = "Toggle complete",
                    tint = accentColor,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = if (task.isCompleted) TodoTextSecondary else TodoTextPrimary,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (task.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = task.description,
                        fontSize = 13.sp,
                        color = TodoTextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Due date chip
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = accentColor.copy(alpha = 0.1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = if (isOverdue) Icons.Default.Warning else Icons.Default.Schedule,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = when {
                                    task.dueDate == 0L    -> "No due date"
                                    isOverdue             -> "Overdue · ${task.dueDate.toDisplayDate()}"
                                    isDueSoon             -> "Due soon · ${task.dueDate.toDisplayDate()}"
                                    else                  -> task.dueDate.toDisplayDate()
                                },
                                fontSize = 11.sp,
                                color = accentColor,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Action buttons
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = TodoGreenLight,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = TodoRed.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

// ── Add / Edit Bottom Sheet ───────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditTaskDialog(
    taskToEdit: Task?,
    onDismiss: () -> Unit,
    onConfirm: (title: String, description: String, dueDate: Long) -> Unit
) {
    val isEditing = taskToEdit != null

    var title       by remember { mutableStateOf(taskToEdit?.title ?: "") }
    var description by remember { mutableStateOf(taskToEdit?.description ?: "") }

    val initialCal = remember {
        Calendar.getInstance().apply {
            if ((taskToEdit?.dueDate ?: 0L) > 0L) timeInMillis = taskToEdit!!.dueDate
        }
    }
    var selectedYear   by remember { mutableStateOf(initialCal.get(Calendar.YEAR)) }
    var selectedMonth  by remember { mutableStateOf(initialCal.get(Calendar.MONTH)) }
    var selectedDay    by remember { mutableStateOf(initialCal.get(Calendar.DAY_OF_MONTH)) }
    var selectedHour   by remember { mutableStateOf(initialCal.get(Calendar.HOUR_OF_DAY)) }
    var selectedMinute by remember { mutableStateOf(initialCal.get(Calendar.MINUTE)) }
    var hasDueDate     by remember { mutableStateOf((taskToEdit?.dueDate ?: 0L) > 0L) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = run {
            val cal = Calendar.getInstance().apply {
                if (hasDueDate) set(selectedYear, selectedMonth, selectedDay, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }
            cal.timeInMillis
        }
    )
    val timePickerState = rememberTimePickerState(
        initialHour = selectedHour,
        initialMinute = selectedMinute,
        is24Hour = false
    )

    fun buildDueDate(): Long {
        if (!hasDueDate) return 0L
        return Calendar.getInstance().apply {
            set(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    fun formatDateLine(): String {
        val cal = Calendar.getInstance().apply {
            set(selectedYear, selectedMonth, selectedDay, 0, 0, 0)
        }
        return SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault()).format(cal.time)
    }

    fun formatTimeLine(): String {
        val cal = Calendar.getInstance().apply {
            set(2000, 0, 1, selectedHour, selectedMinute, 0)
        }
        return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(cal.time)
    }

    // ── Bottom Sheet ──────────────────────────────────────────────────────────
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = Color.White,
        dragHandle = {
            // Custom drag handle with coloured header strip
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                            colors = listOf(TodoGreen, TodoGreenLight)
                        ),
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                    )
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                // Pill handle
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.White.copy(alpha = 0.5f))
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isEditing) Icons.Default.Edit else Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = if (isEditing) "Edit Task" else "New Task",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = Color.White
                        )
                        Text(
                            text = if (isEditing) "Update your task details below" else "Fill in the details below",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp)
                .padding(top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Title ─────────────────────────────────────────────────────────
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(TodoGreen)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "TASK TITLE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TodoTextSecondary,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("e.g. Submit assignment", color = TodoTextSecondary.copy(alpha = 0.5f)) },
                    leadingIcon = {
                        Icon(Icons.Default.Title, contentDescription = null, tint = if (title.isNotBlank()) TodoGreen else TodoTextSecondary)
                    },
                    trailingIcon = {
                        if (title.isNotBlank()) {
                            IconButton(onClick = { title = "" }) {
                                Icon(Icons.Default.Cancel, contentDescription = "Clear", tint = TodoTextSecondary, modifier = Modifier.size(18.dp))
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TodoGreen,
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedLabelColor = TodoGreen,
                        cursorColor = TodoGreen,
                        focusedContainerColor = TodoGreenBg.copy(alpha = 0.3f),
                        unfocusedContainerColor = Color(0xFFFAFAFA)
                    )
                )
            }

            // ── Description ───────────────────────────────────────────────────
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(TodoGreen))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("DESCRIPTION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TodoTextSecondary, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("(optional)", fontSize = 11.sp, color = TodoTextSecondary.copy(alpha = 0.6f))
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Add more details...", color = TodoTextSecondary.copy(alpha = 0.5f)) },
                    leadingIcon = {
                        Icon(Icons.Default.Notes, contentDescription = null, tint = if (description.isNotBlank()) TodoGreen else TodoTextSecondary)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 4,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TodoGreen,
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedLabelColor = TodoGreen,
                        cursorColor = TodoGreen,
                        focusedContainerColor = TodoGreenBg.copy(alpha = 0.3f),
                        unfocusedContainerColor = Color(0xFFFAFAFA)
                    )
                )
            }

            // ── Due date section ──────────────────────────────────────────────
            HorizontalDivider(color = Color(0xFFF0F0F0))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (hasDueDate) TodoGreenBg else Color(0xFFF5F5F5)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = if (hasDueDate) TodoGreen else TodoTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Due Date", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TodoTextPrimary)
                    Text(
                        if (hasDueDate) "Tap date or time to change" else "No deadline set",
                        fontSize = 12.sp,
                        color = TodoTextSecondary
                    )
                }
                Switch(
                    checked = hasDueDate,
                    onCheckedChange = { hasDueDate = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = TodoGreen,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFBDBDBD)
                    )
                )
            }

            // ── Date & Time picker tiles (visible when hasDueDate) ────────────
            if (hasDueDate) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Date tile
                    Surface(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        color = TodoGreenBg,
                        tonalElevation = 0.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.DateRange,
                                    contentDescription = null,
                                    tint = TodoGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "DATE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TodoGreen,
                                    letterSpacing = 1.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = formatDateLine(),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TodoGreen
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Tap to change",
                                fontSize = 11.sp,
                                color = TodoGreen.copy(alpha = 0.65f)
                            )
                        }
                    }

                    // Time tile
                    Surface(
                        onClick = { showTimePicker = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFE8EAF6),
                        tonalElevation = 0.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = Color(0xFF3949AB),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "TIME",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF3949AB),
                                    letterSpacing = 1.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = formatTimeLine(),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF3949AB)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Tap to change",
                                fontSize = 11.sp,
                                color = Color(0xFF3949AB).copy(alpha = 0.65f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // ── Action buttons ────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TodoTextSecondary),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Cancel", fontWeight = FontWeight.SemiBold)
                }
                Button(
                    onClick = {
                        if (title.isNotBlank()) onConfirm(title.trim(), description.trim(), buildDueDate())
                    },
                    modifier = Modifier
                        .weight(2f)
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TodoGreen),
                    enabled = title.isNotBlank()
                ) {
                    Icon(
                        imageVector = if (isEditing) Icons.Default.Save else Icons.Default.AddTask,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (isEditing) "Save Changes" else "Add Task",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }

    // ── Date picker dialog ────────────────────────────────────────────────────
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val cal = Calendar.getInstance().apply { timeInMillis = millis }
                            selectedYear  = cal.get(Calendar.YEAR)
                            selectedMonth = cal.get(Calendar.MONTH)
                            selectedDay   = cal.get(Calendar.DAY_OF_MONTH)
                        }
                        showDatePicker = false
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TodoGreen)
                ) { Text("Confirm", fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDatePicker = false },
                    shape = RoundedCornerShape(10.dp)
                ) { Text("Cancel") }
            },
            shape = RoundedCornerShape(24.dp)
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = TodoGreen,
                    todayDateBorderColor = TodoGreen,
                    selectedYearContainerColor = TodoGreen,
                    currentYearContentColor = TodoGreen
                )
            )
        }
    }

    // ── Time picker dialog (custom styled) ────────────────────────────────────
    if (showTimePicker) {
        Dialog(onDismissRequest = { showTimePicker = false }) {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF3949AB), Color(0xFF5C6BC0))
                                ),
                                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                            )
                            .padding(horizontal = 24.dp, vertical = 20.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        "Set Time",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        "Select the due time for your task",
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.75f)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Live time preview badge
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.2f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        Icons.Default.AccessTime,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    val previewHour = timePickerState.hour
                                    val previewMin  = timePickerState.minute
                                    val previewCal  = Calendar.getInstance().apply {
                                        set(Calendar.HOUR_OF_DAY, previewHour)
                                        set(Calendar.MINUTE, previewMin)
                                    }
                                    Text(
                                        text = SimpleDateFormat("hh : mm a", Locale.getDefault()).format(previewCal.time),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = Color.White,
                                        letterSpacing = 2.sp
                                    )
                                }
                            }
                        }
                    }

                    // Clock
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp, start = 16.dp, end = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        TimePicker(
                            state = timePickerState,
                            colors = TimePickerDefaults.colors(
                                clockDialColor        = Color(0xFFEEF0FB),
                                selectorColor         = Color(0xFF3949AB),
                                containerColor        = Color.Transparent,
                                clockDialSelectedContentColor  = Color.White,
                                clockDialUnselectedContentColor = Color(0xFF3949AB),
                                timeSelectorSelectedContainerColor   = Color(0xFF3949AB),
                                timeSelectorUnselectedContainerColor  = Color(0xFFEEF0FB),
                                timeSelectorSelectedContentColor     = Color.White,
                                timeSelectorUnselectedContentColor   = Color(0xFF3949AB)
                            )
                        )
                    }

                    // Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showTimePicker = false },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TodoTextSecondary),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
                        ) {
                            Text("Cancel", fontWeight = FontWeight.SemiBold)
                        }
                        Button(
                            onClick = {
                                selectedHour   = timePickerState.hour
                                selectedMinute = timePickerState.minute
                                showTimePicker = false
                            },
                            modifier = Modifier
                                .weight(2f)
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3949AB))
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Confirm Time", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
fun ToDoScreenPreview() {
    Smart_campusTheme {
        ToDoScreenContent()
    }
}