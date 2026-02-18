package com.example.smart_campus.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


data class ScheduleEntry(
    val classNumber: String,
    val subject: String,
    val instructor: String,
    val type: String,
    val room: String,
    val dayIndex: Int,
    val startSlot: Int,
    val durationSlots: Int,
    val color: Color
)

// Color palette — one distinct color per subject group
private val ColorPink    = Color(0xFFD81B60)
private val ColorPurple  = Color(0xFF6A1B9A)
private val ColorNavy    = Color(0xFF1A237E)
private val ColorOrange  = Color(0xFFBF8040)
private val ColorTeal    = Color(0xFF00695C)
private val ColorOlive   = Color(0xFF558B2F)
private val ColorRed     = Color(0xFFC62828)
private val ColorGreen   = Color(0xFF2E7D32)

// Header / accent colors
private val HeaderGreen  = Color(0xFF1B5E20)
private val LightGray    = Color(0xFFF5F5F5)
private val BorderGray   = Color(0xFFDDDDDD)
private val TextWhite    = Color.White
private val TextDark     = Color(0xFF212121)

/** All 7 day labels */
private val DAY_LABELS = listOf("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY")

fun getScheduleEntries(): List<ScheduleEntry> = listOf(

    // ── SUBJECT A ────────────────────────────────────────────────────────────
    ScheduleEntry("#767", "SUBJECT A", "Instructor A", "Lecture", "Room 1",
        dayIndex = 0, startSlot = 6, durationSlots = 3, color = ColorPink),   // Mon 9:00–10:30
    ScheduleEntry("#767", "SUBJECT A", "Instructor A", "Laboratory", "Lab 1",
        dayIndex = 3, startSlot = 2, durationSlots = 5, color = ColorPink),   // Thu 7:00–9:30

    // ── SUBJECT B ────────────────────────────────────────────────────────────
    ScheduleEntry("#765", "SUBJECT B", "Instructor B", "Lecture", "Room 2",
        dayIndex = 0, startSlot = 11, durationSlots = 3, color = ColorNavy),  // Mon 11:30–1:00
    ScheduleEntry("#765", "SUBJECT B", "Instructor B", "Lecture", "Room 2",
        dayIndex = 3, startSlot = 11, durationSlots = 3, color = ColorNavy),  // Thu 11:30–1:00

    // ── SUBJECT C ────────────────────────────────────────────────────────────
    ScheduleEntry("#896", "SUBJECT C", "Instructor C", "Laboratory", "Lab 1",
        dayIndex = 1, startSlot = 8, durationSlots = 3, color = ColorOrange), // Tue 10:00–11:30
    ScheduleEntry("#896", "SUBJECT C", "Instructor C", "Lecture", "Room 3",
        dayIndex = 4, startSlot = 10, durationSlots = 3, color = ColorOrange),// Fri 11:00–12:30

    // ── SUBJECT D ────────────────────────────────────────────────────────────
    ScheduleEntry("#877", "SUBJECT D", "Instructor D", "Lecture", "Room 2",
        dayIndex = 1, startSlot = 14, durationSlots = 3, color = ColorTeal),  // Tue 1:00–2:30
    ScheduleEntry("#877", "SUBJECT D", "Instructor D", "Lecture", "Room 2",
        dayIndex = 4, startSlot = 14, durationSlots = 3, color = ColorTeal),  // Fri 1:00–2:30

    // ── SUBJECT E ────────────────────────────────────────────────────────────
    ScheduleEntry("#1069", "SUBJECT E", "Instructor E", "Lecture", "Room 4",
        dayIndex = 4, startSlot = 2, durationSlots = 5, color = ColorPurple), // Fri 7:00–9:30

    // ── SUBJECT F ────────────────────────────────────────────────────────────
    ScheduleEntry("#832", "SUBJECT F", "Instructor F", "Lecture", "Room 5",
        dayIndex = 6, startSlot = 2, durationSlots = 2, color = ColorRed),    // Sun 7:00–8:00

    // ── SUBJECT G ────────────────────────────────────────────────────────────
    ScheduleEntry("#1178", "SUBJECT G", "Instructor G", "Lecture", "Room 6",
        dayIndex = 0, startSlot = 16, durationSlots = 2, color = ColorOlive),

    // ── SUBJECT H ────────────────────────────────────────────────────────────
    ScheduleEntry("#804", "SUBJECT H", "Instructor H", "Lecture", "Room 7",
        dayIndex = 4, startSlot = 24, durationSlots = 3, color = ColorGreen), // Fri 6:00–7:30 PM
)
