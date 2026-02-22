package com.example.smart_campus.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smart_campus.R
import com.example.smart_campus.ui.theme.Smart_campusTheme

class CampusInfo : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Smart_campusTheme {
                CampusInfoScreen()
            }
        }
    }
}

data class College(
    val name: String,
    val contact: String,
    val image: Int,
    val accentColor: Color = Color(0xFF1976D2)
)

// Custom color palette for Campus Info
object CampusColors {
    val PrimaryGreen = Color(0xFF1B5E20)
    val SecondaryGreen = Color(0xFF2E7D32)
    val LightGreen = Color(0xFF4CAF50)
    val AccentGreen = Color(0xFF66BB6A)
    val BackgroundGray = Color(0xFFF8F9FA)
    val CardWhite = Color(0xFFFFFFFF)
    val TextPrimary = Color(0xFF212121)
    val TextSecondary = Color(0xFF757575)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampusInfoScreen() {
    val context = LocalContext.current

    val colleges = listOf(
        College(
            "College of Health and Allied Sciences",
            "chas.new.email@gmail.com",
            R.drawable.chas,
            Color(0xFF00897B)
        ),
        College(
            "College of Business, Accountancy and Administration",
            "pnccbaa@gmail.com",
            R.drawable.cbaa,
            Color(0xFFD32F2F)
        ),
        College(
            "College of Computing Studies",
            "ccscsg@pnc.edu.ph",
            R.drawable.ccs,
            Color(0xFF1976D2)
        ),
        College(
            "College of Engineering",
            "coe@gmail.com",
            R.drawable.coe,
            Color(0xFFF57C00)
        ),
        College(
            "College of Education",
            "coedcsg@pnc.edu.ph",
            R.drawable.coed,
            Color(0xFF7B1FA2)
        ),
        College(
            "College of Arts and Sciences",
            "pnccas23@gmail.com",
            R.drawable.cas,
            Color(0xFF388E3C)
        )
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = CampusColors.BackgroundGray,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Campus Info",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            "${colleges.size} Colleges",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            (context as? ComponentActivity)?.finish()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CampusColors.PrimaryGreen,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Welcome section with gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                CampusColors.PrimaryGreen,
                                CampusColors.SecondaryGreen.copy(alpha = 0.8f)
                            )
                        )
                    )
                    .padding(24.dp)
            ) {
                Column {
                    Text(
                        text = "Explore Our Colleges",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 22.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Connect with different departments and discover opportunities",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = 20.sp
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(colleges) { college ->
                    CollegeCard(college)
                }

                // Footer spacing
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollegeCard(college: College) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = CampusColors.CardWhite
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Image section with overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Image(
                    painter = painterResource(id = college.image),
                    contentDescription = college.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.7f)
                                )
                            )
                        )
                )

                // College name overlay
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = college.accentColor.copy(alpha = 0.9f)
                    ) {
                        Text(
                            text = college.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            // Contact information section
            if (college.contact.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CampusColors.BackgroundGray.copy(alpha = 0.3f))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Email icon
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(college.accentColor.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email",
                            tint = college.accentColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Contact Email",
                            style = MaterialTheme.typography.labelSmall,
                            color = CampusColors.TextSecondary,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = college.contact,
                            style = MaterialTheme.typography.bodyMedium,
                            color = CampusColors.TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCampusInfo() {
    Smart_campusTheme {
        CampusInfoScreen()
    }
}