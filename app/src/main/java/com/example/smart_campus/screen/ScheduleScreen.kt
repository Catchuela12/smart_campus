package com.example.smart_campus.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Color palette ────────────────────────────────────────────────────────────

private val ColorPink    = Color(0xFFE91E8C)
private val ColorPurple  = Color(0xFF7C3AED)
private val ColorNavy    = Color(0xFF1E3A8A)
private val ColorOrange  = Color(0xFFD97706)
private val ColorTeal    = Color(0xFF0D9488)
private val ColorOlive   = Color(0xFF65A30D)
private val ColorRed     = Color(0xFFDC2626)
private val ColorGreen   = Color(0xFF16A34A)

// ── UI tokens ────────────────────────────────────────────────────────────────

private val HeaderStart   = Color(0xFF1B4332)
private val HeaderEnd     = Color(0xFF2D6A4F)
private val BgScreen      = Color(0xFFF8FAFC)
private val BgTimeCol     = Color(0xFFEFF6FF)
private val BorderColor   = Color(0xFFE2E8F0)
private val TimeTextColor = Color(0xFF64748B)
private val TextWhite     = Color.White
private val TextDark      = Color(0xFF0F172A)
private val TopBarBg      = Color(0xFF1B4332)

private val DAY_LABELS    = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")

// ── Dimensions ───────────────────────────────────────────────────────────────

private val SLOT_HEIGHT    = 44.dp
private val TIME_COL_WIDTH = 80.dp
private val DAY_COL_WIDTH  = 120.dp
private const val TOTAL_SLOTS = 32

// ── Data model ───────────────────────────────────────────────────────────────

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

// ── Data source ──────────────────────────────────────────────────────────────

fun getScheduleEntries(): List<ScheduleEntry> = listOf(
    ScheduleEntry("#767",  "SUBJECT A", "Instructor A", "Lecture",    "Room 1", 0, 6,  3, ColorPink),
    ScheduleEntry("#767",  "SUBJECT A", "Instructor A", "Laboratory", "Lab 1",  3, 2,  5, ColorPink),
    ScheduleEntry("#765",  "SUBJECT B", "Instructor B", "Lecture",    "Room 2", 0, 11, 3, ColorNavy),
    ScheduleEntry("#765",  "SUBJECT B", "Instructor B", "Lecture",    "Room 2", 3, 11, 3, ColorNavy),
    ScheduleEntry("#896",  "SUBJECT C", "Instructor C", "Laboratory", "Lab 1",  1, 8,  3, ColorOrange),
    ScheduleEntry("#896",  "SUBJECT C", "Instructor C", "Lecture",    "Room 3", 4, 10, 3, ColorOrange),
    ScheduleEntry("#877",  "SUBJECT D", "Instructor D", "Lecture",    "Room 2", 1, 14, 3, ColorTeal),
    ScheduleEntry("#877",  "SUBJECT D", "Instructor D", "Lecture",    "Room 2", 4, 14, 3, ColorTeal),
    ScheduleEntry("#1069", "SUBJECT E", "Instructor E", "Lecture",    "Room 4", 4, 2,  5, ColorPurple),
    ScheduleEntry("#832",  "SUBJECT F", "Instructor F", "Lecture",    "Room 5", 6, 2,  2, ColorRed),
    ScheduleEntry("#1178", "SUBJECT G", "Instructor G", "Lecture",    "Room 6", 0, 16, 2, ColorOlive),
    ScheduleEntry("#804",  "SUBJECT H", "Instructor H", "Lecture",    "Room 7", 4, 24, 3, ColorGreen),
)

// ── Time helpers ─────────────────────────────────────────────────────────────

fun slotToTimeLabel(slot: Int): String {
    val totalMinutes = 6 * 60 + slot * 30
    val hour24 = totalMinutes / 60
    val minute = totalMinutes % 60
    val amPm = if (hour24 < 12) "am" else "pm"
    val hour12 = when {
        hour24 == 0  -> 12
        hour24 > 12  -> hour24 - 12
        else         -> hour24
    }
    val minuteStr = if (minute == 0) "00" else "30"
    return "$hour12:$minuteStr $amPm"
}

fun slotToRangeLabel(slot: Int): String =
    "${slotToTimeLabel(slot)}\n${slotToTimeLabel(slot + 1)}"

// ── ScheduleCard ─────────────────────────────────────────────────────────────

@Composable
fun ScheduleCard(entry: ScheduleEntry, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(2.dp)
            .shadow(elevation = 3.dp, shape = RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(entry.color, entry.color.copy(alpha = 0.82f))
                )
            )
            .padding(horizontal = 6.dp, vertical = 6.dp)
    ) {
        // Left accent bar
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .width(3.dp)
                .fillMaxHeight(0.75f)
                .clip(RoundedCornerShape(2.dp))
                .background(TextWhite.copy(alpha = 0.45f))
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(TextWhite.copy(alpha = 0.2f))
                    .padding(horizontal = 5.dp, vertical = 1.dp)
            ) {
                Text(
                    text = "Class ${entry.classNumber}",
                    color = TextWhite,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(Modifier.height(3.dp))
            Text(
                text = entry.subject,
                color = TextWhite,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                letterSpacing = 0.3.sp
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = entry.instructor,
                color = TextWhite.copy(alpha = 0.9f),
                fontSize = 9.sp,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "(${entry.type})  ${entry.room}",
                color = TextWhite.copy(alpha = 0.8f),
                fontSize = 8.sp,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ── HeaderCell ───────────────────────────────────────────────────────────────

@Composable
fun HeaderCell(text: String, width: androidx.compose.ui.unit.Dp) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .width(width)
            .height(48.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(HeaderStart, HeaderEnd)
                )
            )
            .border(0.5.dp, TextWhite.copy(alpha = 0.15f))
    ) {
        Text(
            text = text,
            color = TextWhite,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            letterSpacing = 1.sp
        )
    }
}

// ── TimeCell ─────────────────────────────────────────────────────────────────

@Composable
fun TimeCell(slot: Int) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .width(TIME_COL_WIDTH)
            .height(SLOT_HEIGHT)
            .background(BgTimeCol)
            .border(0.5.dp, BorderColor)
            .padding(horizontal = 4.dp)
    ) {
        Text(
            text = slotToRangeLabel(slot),
            color = TimeTextColor,
            fontSize = 8.sp,
            textAlign = TextAlign.Center,
            lineHeight = 11.sp
        )
    }
}

// ── ScheduleScreen ───────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(onBack: () -> Unit = {}) {
    val entries = getScheduleEntries()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Schedule",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = TextWhite
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TopBarBg,
                    titleContentColor = TextWhite,
                    navigationIconContentColor = TextWhite
                )
            )
        },
        containerColor = BgScreen
    ) { paddingValues ->

        val verticalScroll   = rememberScrollState()
        val horizontalScroll = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(horizontalScroll)
            ) {
                Column {
                    // Header row
                    Row {
                        HeaderCell(text = "TIME", width = TIME_COL_WIDTH)
                        DAY_LABELS.forEach { day -> HeaderCell(text = day, width = DAY_COL_WIDTH) }
                    }

                    // Grid body
                    Box(modifier = Modifier.verticalScroll(verticalScroll)) {

                        // Background grid
                        Column {
                            repeat(TOTAL_SLOTS) { slot ->
                                Row {
                                    TimeCell(slot)
                                    repeat(7) {
                                        Box(
                                            modifier = Modifier
                                                .width(DAY_COL_WIDTH)
                                                .height(SLOT_HEIGHT)
                                                .background(
                                                    if (slot % 2 == 0) Color.White
                                                    else Color(0xFFFAFAFA)
                                                )
                                                .border(0.5.dp, BorderColor)
                                        )
                                    }
                                }
                            }
                        }

                        // Class cards overlay
                        entries.forEach { entry ->
                            val topOffset  = SLOT_HEIGHT * entry.startSlot
                            val cardHeight = SLOT_HEIGHT * entry.durationSlots
                            val leftOffset = TIME_COL_WIDTH + DAY_COL_WIDTH * entry.dayIndex

                            ScheduleCard(
                                entry = entry,
                                modifier = Modifier
                                    .absoluteOffset(x = leftOffset, y = topOffset)
                                    .width(DAY_COL_WIDTH)
                                    .height(cardHeight)
                            )
                        }
                    }

                    // Footer header row
                    Row {
                        HeaderCell(text = "TIME", width = TIME_COL_WIDTH)
                        DAY_LABELS.forEach { day -> HeaderCell(text = day, width = DAY_COL_WIDTH) }
                    }
                }
            }
        }
    }
}