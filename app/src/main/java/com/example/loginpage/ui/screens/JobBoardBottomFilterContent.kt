package com.example.loginpage.ui.screens

import android.util.Log
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
fun JobBoardBottomFilterContent (
    categoryOptions: List<String>,
    contractOptions: List<String>,
    selectedCategories: List<String>,
    selectedContracts: List<String>,
    onApply: (List<String>, List<String>) -> Unit
) {
    var categories = remember { mutableStateListOf<String>().apply { addAll(selectedCategories) } }
    var contracts = remember { mutableStateListOf<String>().apply { addAll(selectedContracts) } }
    val colorScheme = MaterialTheme.colorScheme

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
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            categoryOptions.forEach { category ->
                FilterChip(
                    selected = categories.contains(category),
                    onClick = {
                        if (category in categories) categories.remove(category) else categories.add(category)
                    },
                    label = {
                        Text(
                            text = category,
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

        if(contractOptions.size > 1) {
            Spacer(Modifier.height(20.dp))
            Text(
                text = "Select contract type: ",
                color = colorScheme.primaryText,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                contractOptions.forEach { contract ->
                    FilterChip(
                        selected = contracts.contains(contract),
                        onClick = {
                            if (contract in contracts) contracts.remove(contract) else contracts.add(
                                contract
                            )
                        },
                        label = {
                            Text(
                                text = contract,
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
        }

        Button(
            onClick = {
                onApply(categories, contracts)
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