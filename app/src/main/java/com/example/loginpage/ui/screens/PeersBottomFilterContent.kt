package com.example.loginpage.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loginpage.ui.theme.Transparent
import com.example.loginpage.ui.theme.primaryBg
import com.example.loginpage.ui.theme.primaryText

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PeersBottomFilterContent (
    selectedFaculties: List<String>,
    selectedCampuses: List<String>,
    currentlyWorking: Boolean,
    onApply: (List<String>, List<String>, Boolean) -> Unit
) {
    var faculties = remember { mutableStateListOf<String>().apply { addAll(selectedFaculties) } }
    var campuses = remember { mutableStateListOf<String>().apply { addAll(selectedCampuses) } }
    var currentlyWorking by remember { mutableStateOf(currentlyWorking) }
    val colorScheme = MaterialTheme.colorScheme

    val facultyOptions = listOf("Faculty of Art Design & Architecture (MADA)",
        "Faculty of Arts",
        "Faculty of Business",
        "Faculty of Education",
        "Faculty of Engineering",
        "Faculty of Information Technology",
        "Faculty of Law",
        "Faculty of Medicine, Nursing and Health Sciences",
        "Faculty of Pharmacy and Pharmaceutical Sciences",
        "Faculty of Science")

    val campuseOptions = listOf(
        "Clayton",
        "Caulfield",
        "Peninsula",
        "Parkville"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Filters",
            color = colorScheme.primaryText,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }

    Column(modifier = Modifier.padding(start = 24.dp, top = 10.dp, end = 24.dp, bottom = 0.dp)) {
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Select Category: ",
            color = colorScheme.primaryText,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            facultyOptions.forEach { faculty ->
                FilterChip(
                    selected = faculties.contains(faculty),
                    onClick = {
                        if (faculty in faculties) faculties.remove(faculty) else faculties.add(faculty)
                    },
                    label = {
                        Text(
                            text = faculty,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = colorScheme.primaryText,
                        containerColor = colorScheme.Transparent,
                        labelColor = colorScheme.primaryText,
                        selectedLabelColor = colorScheme.primaryBg,
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = colorScheme.primaryText
                    )
                )
            }
        }

        Spacer(Modifier.height(20.dp))
        Text(
            text = "Select Campus: ",
            color = colorScheme.primaryText,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            campuseOptions.forEach { campus ->
                FilterChip(
                    selected = campuses.contains(campus),
                    onClick = {
                        if (campus in campuses) campuses.remove(campus) else campuses.add(campus)
                    },
                    label = {
                        Text(
                            text = campus,
                            fontSize = 16.sp
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = colorScheme.primaryText,
                        containerColor = colorScheme.Transparent,
                        labelColor = colorScheme.primaryText,
                        selectedLabelColor = colorScheme.primaryBg,
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = colorScheme.primaryText
                    )
                )
            }
        }

        Spacer(Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = currentlyWorking,
                onCheckedChange = { currentlyWorking = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = colorScheme.primaryText,
                    uncheckedColor = colorScheme.primaryText,
                    checkmarkColor = colorScheme.primaryBg,
                ),
                modifier = Modifier
                    .size(20.dp)
            )
            Text(
                text = "View peers who are working currently",
                color = colorScheme.primaryText,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 10.dp)
            )
        }

        Button(
            onClick = {
                onApply(faculties, campuses, currentlyWorking)
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(150.dp)
                .padding(top = 20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primaryText)
        ) {
            Text(
                "Apply",
                color = colorScheme.primaryBg,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }

}