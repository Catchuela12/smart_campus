package com.example.smart_campus.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smart_campus.ui.theme.Smart_campusTheme

// ── Data model ────────────────────────────────────────────────────────────────

data class SubjectGrade(
    val subject: String,
    val code: String,
    val units: Int,
    val grade: String,
    val semester: String
)

// ── Activity ──────────────────────────────────────────────────────────────────

class GradeScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Smart_campusTheme {
                GradeView(onBack = { finish() })
            }
        }
    }
}

// ── Colors ────────────────────────────────────────────────────────────────────

private val GreenDark   = Color(0xFF1B5E20)
private val GreenMid    = Color(0xFF2E7D32)
private val GreenLight  = Color(0xFF4CAF50)
private val GreenBg     = Color(0xFFE8F5E9)
private val PageBg      = Color(0xFFF2F4F7)
private val CardBg      = Color.White
private val TextPrimary = Color(0xFF1A1A1A)
private val TextSec     = Color(0xFF757575)

// ── Grade helpers ─────────────────────────────────────────────────────────────

private fun gradeToColor(grade: String): Color {
    return when (grade.toDoubleOrNull() ?: 5.0) {
        in 1.0..1.25 -> Color(0xFF2E7D32)   // Excellent — dark green
        in 1.26..1.75 -> Color(0xFF43A047)  // Very Good — green
        in 1.76..2.25 -> Color(0xFF1976D2)  // Good — blue
        in 2.26..2.75 -> Color(0xFFF57C00)  // Satisfactory — amber
        in 2.76..3.0  -> Color(0xFFE53935)  // Passing — red
        else           -> Color(0xFF757575)  // INC / failed
    }
}

private fun gradeToLabel(grade: String): String {
    return when (grade.toDoubleOrNull() ?: 5.0) {
        in 1.0..1.25  -> "Excellent"
        in 1.26..1.75 -> "Very Good"
        in 1.76..2.25 -> "Good"
        in 2.26..2.75 -> "Satisfactory"
        in 2.76..3.0  -> "Passing"
        else           -> "Failed"
    }
}

private fun gradeToPercent(grade: String): Float {
    val g = grade.toDoubleOrNull() ?: 5.0
    // 1.0 = 100%, 3.0 = 30%, scale linearly
    return ((3.0 - g) / 2.0 * 0.7 + 0.3).toFloat().coerceIn(0f, 1f)
}

// ── Main composable ───────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradeView(onBack: () -> Unit) {

    val grades = remember {
        listOf(
            SubjectGrade("Mobile Programming 1",   "IT401", 3, "1.25", "1st Sem"),
            SubjectGrade("Data Structures",         "CS301", 3, "1.50", "1st Sem"),
            SubjectGrade("Database Management",     "IT302", 3, "1.75", "1st Sem"),
            SubjectGrade("Web Development",         "IT303", 3, "1.25", "1st Sem"),
            SubjectGrade("Mobile Programming 2",    "IT402", 3, "1.50", "2nd Sem"),
            SubjectGrade("Computer Ethics",         "CS201", 2, "1.00", "2nd Sem"),
        )
    }

    val gwa = "1.50"
    val totalUnits = grades.sumOf { it.units }

    // Tabs
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "All Grades")

    // Animate GWA ring on first load
    var ringAnimated by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { ringAnimated = true }
    val ringProgress by animateFloatAsState(
        targetValue = if (ringAnimated) gradeToPercent(gwa) else 0f,
        animationSpec = tween(durationMillis = 1200),
        label = "ring"
    )

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(GreenDark, GreenMid)))
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("My Grades", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.White)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(PageBg)
                .verticalScroll(rememberScrollState())
        ) {

            // ── GWA Hero Card ─────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(GreenMid, GreenMid.copy(alpha = 0.85f), PageBg),
                            startY = 0f,
                            endY = 600f
                        )
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // GWA ring
                    Box(
                        modifier = Modifier.size(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { 1f },
                            modifier = Modifier.fillMaxSize(),
                            color = Color.White.copy(alpha = 0.15f),
                            strokeWidth = 12.dp,
                            strokeCap = StrokeCap.Round
                        )
                        CircularProgressIndicator(
                            progress = { ringProgress },
                            modifier = Modifier.fillMaxSize(),
                            color = Color.White,
                            strokeWidth = 12.dp,
                            strokeCap = StrokeCap.Round
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "GWA",
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.8f),
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = gwa,
                                fontSize = 42.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = gradeToLabel(gwa),
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.9f),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Text(
                        text = "Academic Year 2025–2026",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )

                    // Stat chips row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatChip(label = "Subjects", value = "${grades.size}")
                        StatChip(label = "Units", value = "$totalUnits")
                        StatChip(label = "Semester", value = "2nd")
                    }
                }
            }

            // ── Tab bar ───────────────────────────────────────────────────────
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = CardBg,
                contentColor = GreenMid,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = GreenMid,
                        height = 3.dp
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        },
                        selectedContentColor = GreenMid,
                        unselectedContentColor = TextSec
                    )
                }
            }

            // ── Tab content ───────────────────────────────────────────────────
            when (selectedTab) {

                // ── Overview tab ──────────────────────────────────────────────
                0 -> {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Grade distribution
                        SectionLabel("Grade Distribution")
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = CardBg),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                grades.forEach { sg ->
                                    GradeBarRow(sg)
                                }
                            }
                        }

                        // Highest & Lowest
                        SectionLabel("Performance Highlights")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            val best  = grades.minByOrNull { it.grade.toDoubleOrNull() ?: 5.0 }
                            val worst = grades.maxByOrNull { it.grade.toDoubleOrNull() ?: 5.0 }

                            HighlightCard(
                                modifier = Modifier.weight(1f),
                                label = "Highest",
                                subject = best?.subject ?: "-",
                                grade = best?.grade ?: "-",
                                icon = Icons.Default.EmojiEvents,
                                iconBg = Color(0xFFFFF8E1),
                                iconTint = Color(0xFFF9A825)
                            )
                            HighlightCard(
                                modifier = Modifier.weight(1f),
                                label = "Needs Focus",
                                subject = worst?.subject ?: "-",
                                grade = worst?.grade ?: "-",
                                icon = Icons.Default.TrendingUp,
                                iconBg = Color(0xFFE3F2FD),
                                iconTint = Color(0xFF1565C0)
                            )
                        }

                        // Latin honors hint
                        LatinHonorsCard(gwa = gwa)
                    }
                }

                // ── All Grades tab ────────────────────────────────────────────
                1 -> {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SectionLabel("${grades.size} Subjects · $totalUnits Units Total")
                        grades.forEach { sg ->
                            SubjectGradeCard(sg)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

// ── StatChip ──────────────────────────────────────────────────────────────────

@Composable
private fun StatChip(label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.2f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color.White)
            Text(label, fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
        }
    }
}

// ── SectionLabel ──────────────────────────────────────────────────────────────

@Composable
private fun SectionLabel(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(16.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(GreenMid)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
    }
}

// ── GradeBarRow (Overview) ────────────────────────────────────────────────────

@Composable
private fun GradeBarRow(sg: SubjectGrade) {
    var barAnimated by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { barAnimated = true }
    val barWidth by animateFloatAsState(
        targetValue = if (barAnimated) gradeToPercent(sg.grade) else 0f,
        animationSpec = tween(800),
        label = "bar_${sg.subject}"
    )
    val color = gradeToColor(sg.grade)

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = sg.subject,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = sg.grade,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color(0xFFF0F0F0))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(barWidth)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(3.dp))
                    .background(color)
            )
        }
    }
}

// ── SubjectGradeCard (All Grades tab) ────────────────────────────────────────

@Composable
private fun SubjectGradeCard(sg: SubjectGrade) {
    val gradeColor = gradeToColor(sg.grade)
    val label      = gradeToLabel(sg.grade)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Grade circle
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(gradeColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = sg.grade,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = gradeColor,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Subject info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = sg.subject,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(3.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Code chip
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = GreenBg
                    ) {
                        Text(
                            text = sg.code,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenDark,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Text("·", color = TextSec, fontSize = 11.sp)
                    Text("${sg.units} units", fontSize = 11.sp, color = TextSec)
                    Text("·", color = TextSec, fontSize = 11.sp)
                    Text(sg.semester, fontSize = 11.sp, color = TextSec)
                }
            }

            // Label badge
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = gradeColor.copy(alpha = 0.1f)
            ) {
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = gradeColor,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

// ── HighlightCard ─────────────────────────────────────────────────────────────

@Composable
private fun HighlightCard(
    modifier: Modifier,
    label: String,
    subject: String,
    grade: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    iconTint: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
            }
            Text(label, fontSize = 11.sp, color = TextSec, fontWeight = FontWeight.Medium)
            Text(
                text = grade,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = gradeToColor(grade)
            )
            Text(
                text = subject,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary,
                lineHeight = 16.sp
            )
        }
    }
}

// ── LatinHonorsCard ───────────────────────────────────────────────────────────

@Composable
private fun LatinHonorsCard(gwa: String) {
    val gwaVal = gwa.toDoubleOrNull() ?: 5.0

    val (honorsTitle, honorsDesc, honorsColor, honorsIcon) = when {
        gwaVal <= 1.20 -> Quad("Summa Cum Laude", "Outstanding achievement! You are on track for the highest honor.", Color(0xFFF9A825), Icons.Default.EmojiEvents)
        gwaVal <= 1.45 -> Quad("Magna Cum Laude", "Excellent standing! Keep it up to maintain this honor.", Color(0xFF43A047), Icons.Default.Star)
        gwaVal <= 1.75 -> Quad("Cum Laude", "Great work! You qualify for Latin Honors.", Color(0xFF1976D2), Icons.Default.School)
        else           -> Quad("Keep Pushing", "Aim for 1.75 or below to qualify for Latin Honors.", TextSec, Icons.Default.TrendingUp)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = honorsColor.copy(alpha = 0.08f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(honorsColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(honorsIcon, contentDescription = null, tint = honorsColor, modifier = Modifier.size(26.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(honorsTitle, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = honorsColor)
                Spacer(modifier = Modifier.height(3.dp))
                Text(honorsDesc, fontSize = 12.sp, color = TextSec, lineHeight = 17.sp)
            }
        }
    }
}

// Helper data class for destructuring 4 values
private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)