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

/** Total number of 30-minute slots from 6:00 AM to 10:00 PM = 32 slots */
private const val TOTAL_SLOTS = 32

/** Height in dp of each 30-minute row */
private val SLOT_HEIGHT = 40.dp

/** Width of the time-label column */
private val TIME_COL_WIDTH = 110.dp

/** Width of each day column */
private val DAY_COL_WIDTH = 130.dp

/**
 * Converts a slot index to a human-readable time label.
 * Slot 0 → "6:00 am", Slot 1 → "6:30 am", …
 */
fun slotToTimeLabel(slot: Int): String {
    val totalMinutes = 6 * 60 + slot * 30
    val hour24 = totalMinutes / 60
    val minute = totalMinutes % 60
    val amPm = if (hour24 < 12) "am" else "pm"
    val hour12 = when {
        hour24 == 0 -> 12
        hour24 > 12 -> hour24 - 12
        else -> hour24
    }
    val minuteStr = if (minute == 0) "00" else "30"
    return "$hour12:$minuteStr $amPm"
}

/**
 * Returns the display string for a time-range row header.
 * e.g. slot 0 → "6:00 am - 6:30 am"
 */
fun slotToRangeLabel(slot: Int): String =
    "${slotToTimeLabel(slot)} - ${slotToTimeLabel(slot + 1)}"

@Composable
fun ScheduleCard(entry: ScheduleEntry, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(1.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(entry.color)
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Class ${entry.classNumber}",
                color = TextWhite.copy(alpha = 0.85f),
                fontSize = 9.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = entry.subject,
                color = TextWhite,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = entry.instructor,
                color = TextWhite,
                fontSize = 9.sp,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "(${entry.type}) ${entry.room}",
                color = TextWhite.copy(alpha = 0.9f),
                fontSize = 8.sp,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Single header cell — used for both TIME column and day-name columns.
 */
@Composable
fun HeaderCell(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .width(width)
            .height(44.dp)
            .background(HeaderGreen)
            .border(0.5.dp, TextWhite.copy(alpha = 0.3f))
    ) {
        Text(
            text = text,
            color = TextWhite,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Time label cell for one 30-minute row slot.
 */
@Composable
fun TimeCell(slot: Int) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .width(TIME_COL_WIDTH)
            .height(SLOT_HEIGHT)
            .background(LightGray)
            .border(0.5.dp, BorderGray)
    ) {
        Text(
            text = slotToRangeLabel(slot),
            color = TextDark.copy(alpha = 0.7f),
            fontSize = 9.sp,
            textAlign = TextAlign.Center,
            lineHeight = 12.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen() {
    val entries = getScheduleEntries()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Schedule",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = TextDark
                )
            )
        },
        containerColor = Color.White
    ) { paddingValues ->

        val verticalScroll   = rememberScrollState()
        val horizontalScroll = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // ── Horizontal scroll wrapper ────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(horizontalScroll)
            ) {
                Column {
                    // ── Header row ───────────────────────────────────────────
                    Row {
                        HeaderCell(text = "TIME",    width = TIME_COL_WIDTH)
                        DAY_LABELS.forEach { day ->
                            HeaderCell(text = day, width = DAY_COL_WIDTH)
                        }
                    }

                    // ── Grid body (vertically scrollable) ────────────────────
                    Box(
                        modifier = Modifier.verticalScroll(verticalScroll)
                    ) {
                        // Background grid of empty cells
                        Column {
                            repeat(TOTAL_SLOTS) { slot ->
                                Row {
                                    TimeCell(slot)
                                    repeat(7) { // 7 days
                                        Box(
                                            modifier = Modifier
                                                .width(DAY_COL_WIDTH)
                                                .height(SLOT_HEIGHT)
                                                .background(Color.White)
                                                .border(0.5.dp, BorderGray)
                                        )
                                    }
                                }
                            }
                        }

                        // Overlay: class entry cards
                        entries.forEach { entry ->
                            val topOffset   = SLOT_HEIGHT * entry.startSlot
                            val cardHeight  = SLOT_HEIGHT * entry.durationSlots
                            val leftOffset  = TIME_COL_WIDTH + DAY_COL_WIDTH * entry.dayIndex

                            ScheduleCard(
                                entry = entry,
                                modifier = Modifier
                                    .absoluteOffset(x = leftOffset, y = topOffset)
                                    .width(DAY_COL_WIDTH)
                                    .height(cardHeight)
                            )
                        }
                    }

                    // ── Footer header row (mirrors top) ──────────────────────
                    Row {
                        HeaderCell(text = "TIME", width = TIME_COL_WIDTH)
                        DAY_LABELS.forEach { day ->
                            HeaderCell(text = day, width = DAY_COL_WIDTH)
                        }
                    }
                }
            }
        }
    }
}