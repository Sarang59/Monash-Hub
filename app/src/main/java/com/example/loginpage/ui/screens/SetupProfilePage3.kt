package com.example.loginpage.ui.screens

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.loginpage.ui.theme.Transparent
import com.example.loginpage.ui.theme.errorText
import com.example.loginpage.ui.theme.inactiveGrey
import com.example.loginpage.ui.theme.primaryBg
import com.example.loginpage.ui.theme.primaryText
import com.google.gson.Gson
import kotlin.text.isEmpty

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SetupProfilePage3(navController: NavController,
                      email: String,
                      name: String,
                      avatarId: String,
                      faculty: String,
                      course: String,
                      campus: String,
                      graduationYear: String,
                      company: String,
                      role: String,
                      doj: String) {
    // Variables
    val allTopics = listOf(
        "Technology",
        "Finance",
        "Design & Arts",
        "Engineering",
        "Health & Medicine",
        "Sports",
        "Volunteering",
        "Career Advice",
        "Startups/Entrepreneurship",
        "Study",
        "Internships/Jobs",
        "Buying/Selling",
        "Events"
    )

    val selectedTopics = remember { mutableStateListOf<String>() }
    var summary by remember { mutableStateOf("") }
    val colorScheme = MaterialTheme.colorScheme

    var isSummaryEmpty by remember { mutableStateOf(false) }
    var isSelectedTopicsEmpty by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.primaryBg)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            // Header of the page
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome to MonashHub",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primaryText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Secure, social and student-centered",
                    fontSize = 16.sp,
                    color = colorScheme.primaryText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    thickness = 2.dp,
                    color = colorScheme.primaryText
                )
            }

            Text(
                text = "Please provide other information",
                fontSize = 16.sp,
                color = colorScheme.primaryText,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
            )

            // Profile Summary
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colorScheme.primaryText
                        )
                    ) {
                        append("Tell us something about yourself: ")
                    }

                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            color = colorScheme.errorText
                        )
                    ) {
                        append("*")
                    }
                }
            )

            // Display an error message if the summary is empty
            if(isSummaryEmpty)
            {
                Text(
                    text = "Summary cannot be blank",
                    color = colorScheme.errorText,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }

            OutlinedTextField(
                value = summary,
                onValueChange = {
                    summary = it
                    isSummaryEmpty = false
                },
                placeholder = { Text(text = "Type here", color = colorScheme.inactiveGrey)},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(vertical = 6.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colorScheme.Transparent,
                    unfocusedContainerColor = colorScheme.Transparent,
                    errorContainerColor = colorScheme.errorText,
                    focusedLabelColor = colorScheme.primaryText,
                    unfocusedLabelColor = colorScheme.inactiveGrey,
                    focusedIndicatorColor = colorScheme.primaryText,
                    unfocusedIndicatorColor = colorScheme.inactiveGrey,
                ),
                shape = RectangleShape,
            )


            //Multi select filter chip
            Spacer(modifier = Modifier.height(20.dp))
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState()),
            ) {
                Text(
                    buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colorScheme.primaryText
                            )
                        ) {
                            append("What topics are you most interested in? ")
                        }

                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = colorScheme.errorText
                            )
                        ) {
                            append("*")
                        }
                    },
                    modifier = Modifier.padding(bottom = 5.dp)
                )

                // Display an error is 0 topics are selected
                if(isSelectedTopicsEmpty)
                {
                    Text(
                        text = "Please select at least one topic",
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    allTopics.forEach { topic ->
                        FilterChip(
                            selected = selectedTopics.contains(topic),
                            onClick = {
                                if (selectedTopics.contains(topic)) {
                                    selectedTopics.remove(topic)
                                } else {
                                    selectedTopics.add(topic)
                                    isSelectedTopicsEmpty = false
                                }
                            },
                            label = {
                                Text(
                                    text = topic,
                                    fontSize = 16.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = colorScheme.primaryText,     // background when selected
                                containerColor = colorScheme.Transparent,                   // background when not selected
                                labelColor = colorScheme.primaryText,                       // text color
                                selectedLabelColor = colorScheme.primaryBg,         // text color when selected
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
                    // Check if the variable is empty
                    val fields = listOf(
                        summary to { isSummaryEmpty = true }
                    )

                    for ((value, setError) in fields) {
                        if (value.isEmpty()) {
                            setError()
                            return@Button
                        }
                    }

                    // Check if the selected topics are empty
                    // As it is a list it throws an error if this variable is added
                    // in above empty checking block
                    if(selectedTopics.isEmpty())
                    {
                        isSelectedTopicsEmpty = true
                        return@Button
                    }

                    // If the conditions are met then send the user to last setup page
                    if(!isSelectedTopicsEmpty && !isSummaryEmpty)
                    {
                        // Encode the selected topics in JSON format while
                        // transferring it between screens
                        val selectedTopicsJson = Uri.encode(Gson().toJson(selectedTopics))

                        val route = "setupPage4/" +
                                "${Uri.encode(email)}/${Uri.encode(name)}/${Uri.encode(avatarId)}/${Uri.encode(faculty)}" +
                                "/${Uri.encode(course)}/${Uri.encode(campus)}/${Uri.encode(graduationYear)}" +
                                "/${Uri.encode(summary)}/${Uri.encode(selectedTopicsJson)}" +
                                "?company=${Uri.encode(company)}&role=${Uri.encode(role)}&doj=${Uri.encode(doj)}"

                        navController.navigate(route)
                    }
                },
                modifier = Modifier
                    .padding(top = 20.dp)
                    .align(Alignment.CenterHorizontally)
                    .width(150.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primaryText)
            ) {
                Text("Next", color = colorScheme.primaryBg, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }

        }
    }
}