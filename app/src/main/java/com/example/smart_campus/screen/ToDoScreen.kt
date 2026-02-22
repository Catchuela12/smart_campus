package com.example.smart_campus.screen

import android.app.Application
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("To-Do List") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TaskList(tasks = tasks)
        }
    }
}

@Composable
fun TaskList(tasks: List<Task>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(tasks) { task ->
            Text(text = task.title)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ToDoScreenPreview() {
    ToDoScreen()
}
