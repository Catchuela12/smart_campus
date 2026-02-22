package com.example.smart_campus.screen

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smart_campus.data.Task
import com.example.smart_campus.viewmodel.TaskViewModel
import com.example.smart_campus.viewmodel.TaskViewModelFactory


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDoScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val taskViewModel: TaskViewModel = viewModel(
        factory = TaskViewModelFactory(context.applicationContext as Application)
    )
    val tasks by taskViewModel.allTasks.collectAsState(initial = emptyList())
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("To-Do List") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TaskList(
                tasks = tasks,
                onDeleteTask = { task -> taskViewModel.delete(task) }
            )
        }

        if (showDialog) {
            AddTaskDialog(
                onDismiss = { showDialog = false },
                onTaskAdd = { title, description ->
                    taskViewModel.insert(
                        Task(
                            title = title,
                            description = description,
                            dueDate = System.currentTimeMillis() // Using current time as a placeholder
                        )
                    )
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onTaskAdd: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Task") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") }
                )
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        onTaskAdd(title, description)
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun TaskList(
    tasks: List<Task>,
    onDeleteTask: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.padding(horizontal = 16.dp)) {
        items(tasks) { task ->
            TaskItem(
                task = task,
                onDelete = { onDeleteTask(task) }
            )
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = task.title)
                Text(text = task.description)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Task")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ToDoScreenPreview() {
    ToDoScreen()
}
