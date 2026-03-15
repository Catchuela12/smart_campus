package com.example.smart_campus.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val studentId: String,
    val fullName: String,
    val email: String,
    val username: String,
    val password: String, // In production, this should be hashed
    val program: String = "",
    val yearLevel: String = "",
    val createdAt: Long = System.currentTimeMillis()
)