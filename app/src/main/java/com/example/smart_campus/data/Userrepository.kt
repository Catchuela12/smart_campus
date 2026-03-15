package com.example.smart_campus.data

import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {

    suspend fun registerUser(user: User): Result<Long> {
        return try {
            // Check if username already exists
            val existingUser = userDao.getUserByUsername(user.username)
            if (existingUser != null) {
                return Result.failure(Exception("Username already exists"))
            }

            // Check if email already exists
            val existingEmail = userDao.getUserByEmail(user.email)
            if (existingEmail != null) {
                return Result.failure(Exception("Email already registered"))
            }

            // Check if student ID already exists
            val existingStudentId = userDao.getUserByStudentId(user.studentId)
            if (existingStudentId != null) {
                return Result.failure(Exception("Student ID already registered"))
            }

            val userId = userDao.insert(user)
            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser(username: String, password: String): Result<User> {
        return try {
            val user = userDao.login(username, password)
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Invalid username or password"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
    }

    suspend fun resetPassword(email: String, newPassword: String): Result<Boolean> {
        return try {
            val user = userDao.getUserByEmail(email)
            if (user == null) {
                return Result.failure(Exception("Email not found"))
            }

            val rowsUpdated = userDao.updatePasswordByEmail(email, newPassword)
            if (rowsUpdated > 0) {
                Result.success(true)
            } else {
                Result.failure(Exception("Failed to update password"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getUserById(userId: Int): Flow<User?> {
        return userDao.getUserById(userId)
    }

    suspend fun updateUser(user: User) {
        userDao.update(user)
    }

    suspend fun changePassword(userId: Int, currentPassword: String, newPassword: String): Boolean {
        val user = userDao.getUserByIdAndPassword(userId, currentPassword)
        return if (user != null) {
            userDao.updatePassword(userId, newPassword)
            true
        } else {
            false  // current password was wrong
        }
    }
}