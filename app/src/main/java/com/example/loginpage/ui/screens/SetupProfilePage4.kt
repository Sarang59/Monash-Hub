package com.example.loginpage.ui.screens

import android.util.Log
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.loginpage.ui.commonFunction.UserIdHelper
import com.example.loginpage.ui.data.model.UserProfile
import com.example.loginpage.ui.theme.Transparent
import com.example.loginpage.ui.theme.errorText
import com.example.loginpage.ui.theme.primaryBg
import com.example.loginpage.ui.theme.primaryText
import com.google.common.reflect.TypeToken
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.gson.Gson
import java.net.URLDecoder
import java.text.SimpleDateFormat
import java.util.Locale
import com.google.firebase.Timestamp
import java.util.Calendar
import java.util.TimeZone

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SetupProfilePage4(navController: NavController,
                      email: String,
                      name: String,
                      avatarId: String,
                      faculty: String,
                      course: String,
                      campus: String,
                      graduationYear: String,
                      company: String,
                      role: String,
                      doj: String,
                      summary: String,
                      encodedSelectedTopics: String,
                      onLoginSuccess: () -> Unit) {
    // Variables
    val options = listOf(
        "Making friends / connecting socially",
        "Finding career guidance",
        "Exploring job / internship opportunities",
        "Joining study groups or project teams",
        "Buying, selling, or giving away items",
        "Learning more about companies",
        "Just here to explore for now"
    )

    val colorScheme = MaterialTheme.colorScheme
    var selectedOptions = remember { mutableStateListOf<String>() }
    var isSelectedOptionsEmpty by remember { mutableStateOf( false ) }
    var isUserProfileCreated by remember { mutableStateOf( true ) }
    val context = LocalContext.current

    val topicsJson = URLDecoder.decode(encodedSelectedTopics, "UTF-8")
    val selectedTopics: List<String> = Gson().fromJson(topicsJson, object : TypeToken<List<String>>() {}.type)

    LaunchedEffect(Unit) {
        // Remove all the data before logging in
        UserIdHelper.clearAll(context)
    }

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
                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
            )

            //Multi select filter chip
            Spacer(modifier = Modifier.height(10.dp))
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
                            append("What is your purpose for joining? ")
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

                // Display an error message if no options are selected
                if(isSelectedOptionsEmpty)
                {
                    Text(
                        text = "Please select at least one option",
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
                    options.forEach { topic ->
                        FilterChip(
                            selected = selectedOptions.contains(topic),
                            onClick = {
                                if (selectedOptions.contains(topic)) {
                                    selectedOptions.remove(topic)
                                } else {
                                    selectedOptions.add(topic)
                                    isSelectedOptionsEmpty = false
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
                    // Check if the variable is empty or not
                    if(selectedOptions.isEmpty())
                    {
                        isSelectedOptionsEmpty = true
                        return@Button
                    }

                    // if the condition is met then redirect the user to login page
                    if(!isSelectedOptionsEmpty)
                    {
                        var dojTimestamp : Timestamp? = null
                        if(doj != "")
                        {
                            // Convert the date in an appropriate format which is accepted
                            // by firebase database
                            val sDF = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                            sDF.timeZone = TimeZone.getTimeZone("Australia/Melbourne")

                            val parsedDate = sDF.parse(doj)
                            val calendar = Calendar.getInstance(TimeZone.getTimeZone("Australia/Melbourne"))
                            calendar.time = parsedDate!!
                            calendar.set(Calendar.HOUR_OF_DAY, 0)
                            calendar.set(Calendar.MINUTE, 0)
                            calendar.set(Calendar.SECOND, 0)
                            calendar.set(Calendar.MILLISECOND, 0)

                            dojTimestamp = Timestamp(calendar.time)
                        }

                        // Check if the user is present in the database
                        Firebase.firestore.collection("users")
                            .whereEqualTo("email", email)
                            .get()
                            .addOnSuccessListener { userDocuments ->
                                // If it is not empty then make an user profile
                                if (!userDocuments.isEmpty) {
                                    val userId = userDocuments.documents[0].id

                                    // Check if user profile is already created
                                    Firebase.firestore.collection("user_profile")
                                        .whereEqualTo("user_id", userId)
                                        .get()
                                        .addOnSuccessListener { fetchedDocument ->
                                            // Add the user profile if it is not present in DB
                                            if(fetchedDocument.isEmpty)
                                            {
                                                val newUserProfile = UserProfile (
                                                    avatar_id = avatarId,
                                                    company_name = company,
                                                    course_name = course,
                                                    faculty = faculty,
                                                    current_role = role,
                                                    dark_mode = false,
                                                    doj = dojTimestamp,
                                                    followers = emptyList(),
                                                    graduation_year = graduationYear,
                                                    interest = selectedTopics,
                                                    name = name,
                                                    primary_campus = campus,
                                                    profile_statement = summary,
                                                    purpose_of_joining = selectedOptions,
                                                    user_id = userId
                                                )

                                                // Update the onboarded variable to TRUE in
                                                // users document
                                                val documentReference = Firebase.firestore.collection("users").document(userId)
                                                documentReference.update(
                                                    mapOf(
                                                        "onboarded" to true
                                                    )
                                                ).addOnSuccessListener {
                                                    Log.d("Success", "User document is UPDATED")
                                                } .addOnFailureListener {
                                                    Log.e("Error", "User document is not UPDATED")
                                                }

                                                // Add the user profile document in database
                                                // having Id as user Id
                                                Firebase.firestore.collection("user_profile")
                                                    .document(userId)
                                                    .set(newUserProfile)
                                                    .addOnSuccessListener {
                                                        Log.d("Success", "User Profile ADDED")
                                                        isUserProfileCreated = true
                                                        UserIdHelper.saveUserId(context, userId)
                                                        onLoginSuccess()
                                                        navController.navigate("successPage/setupPage4")
                                                    }
                                                    .addOnFailureListener {
                                                        Log.e("Failed", "User profile not ADDED: ${it.message}")
                                                    }
                                            }
                                            else {
                                                Log.e("Error", "User profile is already present in database")
                                            }
                                        }
                                        .addOnFailureListener {
                                            Log.e("Error", "Failed to fetch the user profile: ${it.message}")
                                        }
                                }
                                else
                                {
                                    isUserProfileCreated = false
                                }
                            }
                            .addOnFailureListener {
                                Log.e("Error", "Failed to retrieve the user: ${it.message}")
                            }


                    }
                },
                modifier = Modifier
                    .padding(top = 20.dp)
                    .align(Alignment.CenterHorizontally)
                    .width(150.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primaryText)
            ) {
                Text("Save", color = colorScheme.primaryBg, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
    }
}