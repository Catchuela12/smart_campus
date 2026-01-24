package com.example.smart_campus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smart_campus.ui.theme.Smart_campusTheme

class CampusInfo : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Smart_campusTheme {
                Scaffold { padding ->
                    CampusInfoScreen(Modifier.padding(padding))
                }
            }
        }
    }
}

data class College(
    val name: String,
    val contact: String,
    val image: Int
)

@Composable
fun CampusInfoScreen(modifier: Modifier = Modifier) {
    val colleges = listOf(
        College(
            "College of Health and Allied Sciences",
            "chas.new.email@gmail.com",
            R.drawable.chas
        ),
        College(
            "College of Business, Accountancy and Administration",
            "pnccbaa@gmail.com",
            R.drawable.cbaa
        ),
        College(
            "College of Computing Studies",
            "ccscsg@pnc.edu.ph",
            R.drawable.ccs
        ),
        College(
            "College of Engineering",
            "coe@gmail.com",
            R.drawable.coe
        ),
        College(
            "College of Education",
            "coedcsg@pnc.edu.ph",
            R.drawable.coed
        ),
        College(
            "College of Arts and Sciences",
            "pnccas23@gmail.com",
            R.drawable.cas
        )
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(colleges) { college ->
            CollegeCard(college)
        }
    }
}

@Composable
fun CollegeCard(college: College) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = college.image),
                contentDescription = college.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(10.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(college.name, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                if (college.contact.isNotEmpty()) {
                    Text(college.contact)
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
