package com.example.loginpage.ui.screens

import android.net.Uri
import androidx.compose.ui.window.Dialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.loginpage.ui.theme.Transparent
import com.example.loginpage.ui.theme.errorText
import com.example.loginpage.ui.theme.inactiveGrey
import com.example.loginpage.ui.theme.primaryBg
import com.example.loginpage.ui.theme.primaryText
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupProfilePage2(navController: NavController,
                      email: String,
                      name: String,
                      avatarId: String,
                      faculty: String,
                      course: String,
                      campus: String,
                      graduationYear: String) {
    // Variables
    var company by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    val calendar = Calendar.getInstance()
    var joiningDate by remember { mutableStateOf("") }
    val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Instant.now().toEpochMilli()
    )
    var showDatePicker by remember {
        mutableStateOf(false)
    }
    var selectedDate by remember {
        mutableStateOf(calendar.timeInMillis)
    }
    val colorScheme = MaterialTheme.colorScheme
    val today = remember { System.currentTimeMillis() }
    var isDateOfJoiningInvalid by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.primaryBg)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
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
                text = "Please provide your job details",
                fontSize = 16.sp,
                color = colorScheme.primaryText,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
            )

            //Company Name - Optional field
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
                        append("Company Name: ")
                    }
                }
            )
            OutlinedTextField(
                value = company.toString(),
                onValueChange = { newCompany: String -> company = newCompany },
                placeholder = { Text("Enter your company name", color = colorScheme.inactiveGrey) },
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            )

            //Role - Optional field
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colorScheme.primaryText
                        )
                    ) {
                        append("Current Role: ")
                    }
                }
            )
            OutlinedTextField(
                value = role.toString(),
                onValueChange = { newRole: String -> role = newRole },
                placeholder = { Text("Enter your role ", color = colorScheme.inactiveGrey) },
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            )


            // Date of joining - Optional field
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colorScheme.primaryText
                        )
                    ) {
                        append("Date of Joining: ")
                    }
                }
            )

            // Display an error message if the date of joining is invalid
            if(isDateOfJoiningInvalid)
            {
                Text(
                    text = "Date of Joining is Invalid",
                    color = colorScheme.errorText,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }

            OutlinedTextField(
                value = joiningDate,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true }
                    .padding(vertical = 6.dp),
                trailingIcon = {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Filled.CalendarMonth,
                        contentDescription = "Select date",
                        tint = colorScheme.inactiveGrey,
                        modifier = Modifier
                            .clickable { showDatePicker = true }
                            .size(28.dp).padding(end = 2.dp)
                    )
                },
                placeholder = { Text("Select your joining date", color = colorScheme.inactiveGrey) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colorScheme.Transparent,
                    unfocusedContainerColor = colorScheme.Transparent,
                    errorContainerColor = colorScheme.errorText,
                    focusedLabelColor = colorScheme.primaryText,
                    unfocusedLabelColor = colorScheme.inactiveGrey,
                    focusedIndicatorColor = colorScheme.primaryText,
                    unfocusedIndicatorColor = colorScheme.inactiveGrey,
                ),
                shape = RectangleShape
            )


            if (showDatePicker) {
                Dialog(onDismissRequest = { showDatePicker = false }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(16.dp),
                        color = colorScheme.primaryBg,

                    ) {
                        Column(
                            modifier = Modifier
                                .background(colorScheme.primaryBg)
                                .padding(16.dp)
                        ) {
                            DatePicker(
                                state = datePickerState,
                                colors = DatePickerDefaults.colors(
                                    containerColor = colorScheme.primaryBg,
                                    titleContentColor = colorScheme.primaryText,
                                    headlineContentColor = colorScheme.primaryText,
                                    weekdayContentColor = colorScheme.primaryText,
                                    subheadContentColor = colorScheme.primaryText,
                                    selectedDayContainerColor = colorScheme.primaryText,
                                    selectedDayContentColor = colorScheme.primaryBg
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(colorScheme.primaryBg),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = {
                                    showDatePicker = false
                                }) {
                                    Text("Cancel", color = colorScheme.primaryText)
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                TextButton(onClick = {

                                    // Logic to get the date in an appropriate format for
                                    // pushing data in database
                                    val selected = datePickerState.selectedDateMillis
                                    if (selected != null && selected <= today)
                                    {
                                        val zoneId = ZoneId.of("Australia/Melbourne")

                                        val selectedLocalDate = Instant.ofEpochMilli(selected)
                                            .atZone(zoneId)
                                            .toLocalDate()

                                        val midnightDateTime = selectedLocalDate.atStartOfDay(zoneId)
                                        val dateAtMidnight = Date.from(midnightDateTime.toInstant())

                                        val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                                        formatter.timeZone = TimeZone.getTimeZone("Australia/Melbourne")

                                        joiningDate = formatter.format(dateAtMidnight)
                                        isDateOfJoiningInvalid = false
                                    }
                                    else
                                    {
                                        isDateOfJoiningInvalid = true
                                    }

                                    showDatePicker = false
                                }) {
                                    Text("OK", color = colorScheme.primaryText)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Button(
                onClick = {
                    // If the condition is met then redirect the user to next setup page
                    if(!isDateOfJoiningInvalid)
                    {
                        val route = "setupPage3/" +
                                "${Uri.encode(email)}/${Uri.encode(name)}/${Uri.encode(avatarId)}/${Uri.encode(faculty)}" +
                                "/${Uri.encode(course)}/${Uri.encode(campus)}/${Uri.encode(graduationYear)}" +
                                "?company=${Uri.encode(company)}&role=${Uri.encode(role)}&doj=${Uri.encode(joiningDate)}"
                        navController.navigate(route)
                    }
                },
                modifier = Modifier
                    .padding(top = 16.dp)
                    .align(Alignment.CenterHorizontally)
                    .width(150.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primaryText)
            ) {
                Text("Next", color = colorScheme.primaryBg, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
    }
}