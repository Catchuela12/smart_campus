package com.example.smart_campus.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smart_campus.data.User
import com.example.smart_campus.data.UserDatabase
import com.example.smart_campus.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class AdminSuccess(val user: User) : AuthState() // Added AdminSuccess
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        val userDao = UserDatabase.getDatabase(application).userDao()
        repository = UserRepository(userDao)
    }

    // ── Login ─────────────────────────────────────────────────────────────────

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val result = repository.loginUser(username, password)

            result.onSuccess { user ->
                _currentUser.value = user
                // Simple logic: if username contains "admin", it's an admin
                if (user.username.contains("admin", ignoreCase = true)) {
                    _authState.value = AuthState.AdminSuccess(user)
                } else {
                    _authState.value = AuthState.Success(user)
                }
            }.onFailure { exception ->
                _authState.value = AuthState.Error(exception.message ?: "Login failed")
            }
        }
    }

    // ── Register ──────────────────────────────────────────────────────────────

    fun register(
        studentId: String,
        fullName: String,
        email: String,
        username: String,
        password: String,
        program: String,
        yearLevel: String
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val user = User(
                studentId = studentId,
                fullName  = fullName,
                email     = email,
                username  = username,
                password  = password, // In production, hash this!
                program   = program,
                yearLevel = yearLevel
            )

            val result = repository.registerUser(user)

            result.onSuccess { userId ->
                // Auto-login after successful registration
                val newUser = user.copy(id = userId.toInt())
                _currentUser.value = newUser
                _authState.value = AuthState.Success(newUser)
            }.onFailure { exception ->
                _authState.value = AuthState.Error(exception.message ?: "Registration failed")
            }
        }
    }

    // ── Reset Password (Forgot Password flow) ─────────────────────────────────

    fun resetPassword(email: String, newPassword: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val result = repository.resetPassword(email, newPassword)

            result.onSuccess {
                _authState.value = AuthState.Error("Password reset successful! Please login.")
            }.onFailure { exception ->
                _authState.value = AuthState.Error(exception.message ?: "Password reset failed")
            }
        }
    }

    // ── Change Password (Settings flow) ───────────────────────────────────────

    fun changePassword(userId: Int, currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val success = repository.changePassword(userId, currentPassword, newPassword)
                if (success) {
                    val user = _currentUser.value
                    if (user != null) {
                        // Update in-memory user so session stays in sync
                        val updatedUser = user.copy(password = newPassword)
                        _currentUser.value = updatedUser
                        _authState.value = AuthState.Success(updatedUser)
                    } else {
                        _authState.value = AuthState.Error("Session expired. Please log in again.")
                    }
                } else {
                    _authState.value = AuthState.Error("Current password is incorrect")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Failed to update password: ${e.message}")
            }
        }
    }

    // ── Logout ────────────────────────────────────────────────────────────────

    fun logout() {
        _currentUser.value = null
        _authState.value = AuthState.Idle
    }

    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }
}