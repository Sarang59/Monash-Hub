package com.example.loginpage.ui.screens

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.loginpage.ui.theme.Transparent
import com.example.loginpage.ui.theme.errorText
import com.example.loginpage.ui.theme.inactiveGrey
import com.example.loginpage.ui.theme.primaryBg
import com.example.loginpage.ui.theme.primaryText
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUp(navController: NavController) {
    // Variables
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val passwordRequirements = remember { mutableStateListOf(
        "At least 8 characters",
        "At least contains one digit",
        "At least contains one lowercase letter",
        "At least contains one uppercase letter",
        "At least contains one special character"
    ) }
    var confirmPassword by remember { mutableStateOf("") }
    val calendar = Calendar.getInstance()
    var birthday by remember { mutableStateOf("") }
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

    var isNameEmpty by remember { mutableStateOf(false) }
    var isBirthdayEmpty by remember { mutableStateOf(false) }
    var isEmailEmpty by remember { mutableStateOf(false) }
    var isPasswordEmpty by remember { mutableStateOf(false) }
    var isConfirmPasswordEmpty by remember { mutableStateOf(false) }

    var isNameInvalid by remember { mutableStateOf(false) }
    var isBirthdayInvalid by remember { mutableStateOf(false) }
    var isEmailInvalid by remember { mutableStateOf(false) }
    var isPasswordInvalid by remember { mutableStateOf(false) }
    var arePasswordsMatching by remember { mutableStateOf(false) }
    var isMonashAccountInvalid by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme
    var isEmailAlreadyRegistered by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Surface (
        modifier = Modifier.fillMaxSize(),
        color = colorScheme.primaryBg
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp)
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = "Please add your details to get started",
                    fontSize = 16.sp,
                    color = colorScheme.primaryText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
                )


                //Name
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
                            append("Name: ")
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

                // Display an error message if name field is empty
                if(isNameEmpty)
                {
                    Text(
                        text = "Name cannot be blank",
                        color = colorScheme.errorText,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                // Display an error message if the name has something else than alphabets
                if(isNameInvalid)
                {
                    Text(
                        text = "Name should have only alphabets",
                        color = colorScheme.errorText,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        isNameEmpty = false
                        isNameInvalid = false
                    },
                    placeholder = { Text("Enter your name", color = colorScheme.inactiveGrey) },
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


                //date of birth
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
                            append("Date of Birth: ")
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

                // Display an error message if the date of birth field is empty
                if(isBirthdayEmpty)
                {
                    Text(
                        text = "Birth date cannot be blank",
                        color = colorScheme.errorText,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                // Display an error if the date of birth field is invalid:
                // for instance the user doesn't match the age criteria
                if(isBirthdayInvalid)
                {
                    Text(
                        text = "Date of Birth is Invalid",
                        color = colorScheme.errorText,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                val today = remember { System.currentTimeMillis() }

                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = today
                )

                OutlinedTextField(
                    value = birthday,
                    onValueChange = {
                        isBirthdayEmpty = false
                        isBirthdayInvalid = false
                    },
                    readOnly = true,
                    placeholder = { Text("Select your birthdate", color = inactiveGrey)},
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
                                    modifier = Modifier.fillMaxWidth(),
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
                                        val selected = datePickerState.selectedDateMillis

                                        // Logic to select a date, transform it into
                                        // appropriate format and check if the date is
                                        // valid or not
                                        if (selected != null && selected <= today) {

                                            // Retrieve proper date as per zone
                                            val zoneId = ZoneId.of("Australia/Melbourne")
                                            val selectedDate = Instant.ofEpochMilli(selected)
                                                .atZone(zoneId)
                                                .toLocalDate()
                                            val todayLocal = LocalDate.now(zoneId)

                                            // Compute the age of the user
                                            val age = Period.between(selectedDate, todayLocal).years

                                            // Check if the user is 17 years old or not
                                            if (age >= 17) {
                                                val midnightDateTime = selectedDate.atStartOfDay(zoneId)
                                                val dateAtMidnight = Date.from(midnightDateTime.toInstant())
                                                val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                                                formatter.timeZone = TimeZone.getTimeZone("Australia/Melbourne")
                                                birthday = formatter.format(dateAtMidnight)
                                                isBirthdayInvalid = false
                                                isBirthdayEmpty = false
                                            } else {
                                                isBirthdayInvalid = true
                                                isBirthdayEmpty = false
                                            }
                                        } else {
                                            isBirthdayInvalid = true
                                            isBirthdayEmpty = false
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





                //Email
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
                            append("Email: ")
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

                // Display an error message if the email is empty
                if(isEmailEmpty)
                {
                    Text(
                        text = "Email cannot be blank",
                        color = colorScheme.errorText,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                // Display an error message if the email is not in proper format
                if(isEmailInvalid)
                {
                    Text(
                        text = "Email is not in proper format",
                        color = colorScheme.errorText,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                // Display an error message if the email entered doesn't belong to Monash
                if(isMonashAccountInvalid)
                {
                    Text(
                        text = "Not a Monash Account",
                        color = colorScheme.errorText,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                // Display an error message if email is already present in database
                if(isEmailAlreadyRegistered)
                {
                    Text(
                        text = "This email is already registered",
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                OutlinedTextField(
                    value = email.trim().toLowerCase(Locale.ROOT).toString(),
                    onValueChange = {
                        email = it.trim().toLowerCase(Locale.ROOT).toString()
                        isEmailEmpty = false
                        isEmailInvalid = false
                        isMonashAccountInvalid = false
                        isEmailAlreadyRegistered = false
                    },
                    placeholder = { Text("Enter your email", color = inactiveGrey) },
                    modifier = Modifier
                        .fillMaxWidth()
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


                //Password
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
                            append("Create Password: ")
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

                // Display an error message if the password is empty
                if(isPasswordEmpty)
                {
                    Text(
                        text = "Password cannot be blank",
                        color = colorScheme.errorText,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                // Display an error message if the password is too short
                if(isPasswordInvalid)
                {
                    Text(
                        text = "Password is too short, it should be 8 characters long",
                        color = colorScheme.errorText,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                OutlinedTextField(
                    value = password.toString(),
                    onValueChange = {
                        password = it
                        isPasswordEmpty = false
                        isPasswordInvalid = false
                    },
                    placeholder = { Text("Create your password", color = colorScheme.inactiveGrey) },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val icon = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = icon,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = colorScheme.inactiveGrey
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
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


                //Confirm Password
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
                            append("Confirm Password: ")
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

                // Display an error message if the confirm password field is empty
                if(isConfirmPasswordEmpty)
                {
                    Text(
                        text = "Confirm Password cannot be blank",
                        color = colorScheme.errorText,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                // Display an error message if the passwords are not matching
                if(arePasswordsMatching)
                {
                    Text(
                        text = "Passwords do not match",
                        color = colorScheme.errorText,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                OutlinedTextField(
                    value = confirmPassword.toString(),
                    onValueChange = {
                        confirmPassword = it
                        isConfirmPasswordEmpty = false
                        arePasswordsMatching = false
                    },
                    placeholder = { Text("Enter your password", color = inactiveGrey) },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val icon = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = icon,
                                contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                                tint = colorScheme.inactiveGrey
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
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

                Spacer(modifier = Modifier.height(6.dp))
                Button(
                    onClick = {
                        // Check if the fields are empty
                        val fields = listOf(
                            name to { isNameEmpty = true },
                            birthday to { isBirthdayEmpty = true },
                            email to { isEmailEmpty = true },
                            password to { isPasswordEmpty = true },
                            confirmPassword to { isConfirmPasswordEmpty = true }
                        )

                        for ((value, setError) in fields) {
                            if (value.isEmpty()) {
                                setError()
                                return@Button
                            }
                        }

                        // Regex - Check if the name field contains
                        // only spaces and alphabets
                        val isValidName = name.trim().matches(Regex("^[A-Za-z ]+$"))

                        if(!isValidName)
                        {
                            isNameInvalid = true
                            return@Button
                        }

                        // Check if Email is valid or not
                        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
                        val validEmailformat = email.matches(emailRegex.toRegex())
                        val isMonashStudent = email.endsWith("@student.monash.edu")

                        // Check if the email is in proper format or not
                        if(!validEmailformat) {
                             isEmailInvalid = true
                             return@Button
                        }

                        // Check if the email belongs to Monash or not
                        if(!isMonashStudent) {
                            isMonashAccountInvalid = true
                            return@Button
                        }

                        // Check if Password is 8 characters long
                        if(password.length < 8)
                        {
                            isPasswordInvalid = true
                            return@Button
                        }

                        // Check if the Password and confirm Passwords are matching
                        if(password != confirmPassword)
                        {
                            arePasswordsMatching = true
                            return@Button
                        }

                        // Check if the user is already registered in the database
                        Firebase.firestore.collection("users")
                            .whereEqualTo("email", email)
                            .get()
                            .addOnSuccessListener { userDocuments ->
                                if (!userDocuments.isEmpty) {
                                    isEmailAlreadyRegistered = true
                                    return@addOnSuccessListener
                                } else {
                                    isEmailAlreadyRegistered = false

                                    // If all the conditions are met then redirect the
                                    // user to OTP verification page
                                    if(!isNameInvalid && !isBirthdayInvalid && !isEmailInvalid
                                        && !isPasswordInvalid && !isMonashAccountInvalid
                                        && !arePasswordsMatching && !isNameEmpty
                                        && !isBirthdayEmpty && !isPasswordEmpty && !isEmailEmpty
                                        && !isConfirmPasswordEmpty)
                                    {
                                        val encodedEmail = Uri.encode(email)
                                        navController.navigate("SignUpOtpVerification/${encodedEmail}/" +
                                                "${name}/${birthday}/${password}")
                                    }
                                }
                            }
                            .addOnFailureListener {
                                Log.e("Error", "Failed to check email: ${it.message}")
                            }
                    },
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .align(Alignment.CenterHorizontally)
                        .width(150.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primaryText)
                ) {
                    Text(
                        "Sign Up",
                        color = colorScheme.primaryBg,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = colorScheme.primaryText)) {
                            append("Already Registered ? ")
                        }

                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = colorScheme.primaryText,
                                textDecoration = TextDecoration.Underline
                            )
                        ) {
                            append("Login")
                        }
                    },
                    fontSize = 16.sp,
                    color = colorScheme.primaryText,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .align(Alignment.CenterHorizontally)
                        .clickable {
                            navController.navigate("login")
                        }

                )
            }
        }
    }
}
