package com.example.loginpage.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.loginpage.ui.data.model.Users
import com.example.loginpage.ui.theme.Transparent
import com.example.loginpage.ui.theme.errorText
import com.example.loginpage.ui.theme.inactiveGrey
import com.example.loginpage.ui.theme.primaryBg
import com.example.loginpage.ui.theme.primaryText
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Properties
import java.util.TimeZone

@Composable
fun SignUpOtpVerificationScreen(
    navController: NavController,
    onVerify: (String) -> Unit,
    recipientEmail: String,
    name: String,
    birthday: String,
    password: String
) {
    // Variables
    val otpLength = 6
    val focusRequesters = List(otpLength) { FocusRequester() }
    val otpValues = remember { mutableStateListOf("", "", "", "", "", "") }
    val focusedIndex = remember { mutableStateOf(-1) }
    var isOTPWrong by remember { mutableStateOf(false) }
    var isOTPEmpty by remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    // Generate random number using random function that will act as OTP
    val otp = (100000..999999).random()

    // Define the various parts of email
    val subject = "OTP for verification"
    val body = "Dear User, \n\nPlease find below OTP for verification \n\nOTP : ${otp} \n\n" +
            "Thank you \n\nRegards \n\n\nMonash-Hub Technical Team"

    // Credentials to send an email
    val senderEmail = "noreply.monashhub@gmail.com"
    val appPassword = "vevxzxqyxtkfxahm" // NOT your Gmail password

    // Create an object to send an email
    val sender = GMailSender(senderEmail, appPassword)

    // Send an email
    LaunchedEffect(recipientEmail) {
        withContext(Dispatchers.IO) {
            sender.sendMail(subject, body, recipientEmail)
        }
    }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(200) // Add delay to ensure focus is established
        focusRequesters.first().requestFocus()
        keyboardController?.show()
    }

    // Create UI Screen
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.primaryBg)
    ) {
        Column() {
            Spacer(modifier = Modifier.height(214.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Verification",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primaryText
                )
                Text(
                    text = "Enter the 6-digit code sent to $recipientEmail",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 12.dp, bottom = 6.dp),
                    color = colorScheme.inactiveGrey,
                    textAlign = TextAlign.Center
                )

                // Show an error message if the OTP is empty
                if(isOTPEmpty)
                {
                    Text(
                        text = "OTP cannot be blank",
                        color = colorScheme.errorText,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                // Show an error message if the OTP is incorrect
                if(isOTPWrong)
                {
                    Text(
                        text = "OTP is wrong",
                        color = colorScheme.errorText,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    repeat(otpLength) { index ->
                        OutlinedTextField(
                            value = otpValues[index],
                            onValueChange = { value ->
                                if (value.length <= 1 && value.all { it.isDigit() }) {
                                    otpValues[index] = value
                                    if (value.isNotEmpty() && index < otpLength - 1) {
                                        focusRequesters[index + 1].requestFocus()
                                    }
                                    if (index == otpLength - 1 && otpValues.all { it.isNotBlank() }) {
                                        keyboardController?.hide()
                                        onVerify(otpValues.joinToString(""))
                                    }
                                } else if (value.length == otpLength && value.all { it.isDigit() }) {
                                    value.forEachIndexed { i, c ->
                                        if (i < otpLength) otpValues[i] = c.toString()
                                    }
                                    focusManager.clearFocus()
                                    onVerify(otpValues.joinToString(""))
                                }
                                isOTPWrong = false
                                isOTPEmpty = false
                            },
                            modifier = Modifier
                                .width(48.dp)
                                .height(56.dp)
                                .focusRequester(focusRequesters[index])
                                .onFocusChanged {
                                    if (it.isFocused) focusedIndex.value = index
                                },
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(
                                textAlign = TextAlign.Center,
                                fontSize = 18.sp
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = if (index == otpLength - 1) ImeAction.Done else ImeAction.Next
                            ),
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
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = {
                        val joinedOTP = otpValues.joinToString("")
                        onVerify(joinedOTP)

                        if(joinedOTP.isEmpty())
                        {
                            isOTPEmpty = true
                            return@Button
                        }

                        if(joinedOTP.toInt() == otp) {

                            isOTPWrong = false
                            isOTPEmpty = false

                            // Convert the birthday format into firebase database format
                            val sDF = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            sDF.timeZone = TimeZone.getTimeZone("Australia/Melbourne")

                            val parsedDate = sDF.parse(birthday)  // 'birthday' is a string like "2001-09-28"
                            val calendar = Calendar.getInstance(TimeZone.getTimeZone("Australia/Melbourne"))
                            calendar.time = parsedDate!!
                            calendar.set(Calendar.HOUR_OF_DAY, 0)
                            calendar.set(Calendar.MINUTE, 0)
                            calendar.set(Calendar.SECOND, 0)
                            calendar.set(Calendar.MILLISECOND, 0)

                            val birthdayTimestamp = Timestamp(calendar.time)

                            // Create new user
                            val newUser = Users(
                                dob = birthdayTimestamp,
                                email = recipientEmail,
                                name = name,
                                password = password,
                                onboarded = false,
                                user_id = "",
                                google_login = false
                            )

                            // Add the user in the database
                            // Check if the user already exists based on email
                            var isEmailAlreadyRegistered = false

                            // Check if the user is already present in the database
                            Firebase.firestore.collection("users")
                                .whereEqualTo("email", recipientEmail)
                                .get()
                                .addOnSuccessListener { userDocuments ->
                                    if (!userDocuments.isEmpty) {
                                        isEmailAlreadyRegistered = true
                                    } else {
                                        Firebase.firestore.collection("users")
                                            .add(newUser)
                                            .addOnSuccessListener { referenceDocument ->
                                                // Generate the id and update the user Id variable
                                                // in the document
                                                val generateId = referenceDocument.id
                                                referenceDocument.update("user_id", generateId)
                                                Log.d("Success", "User ADDED")

                                                // Redirect the user to avatar selection page
                                                navController.navigate("avatarSelection/$recipientEmail/$name")
                                            }
                                            .addOnFailureListener {
                                                Log.e("Failed", "User not ADDED: ${it.message}")
                                            }
                                    }
                                }
                                // Failed to retrieve the user
                                .addOnFailureListener {
                                    Log.e("Error", "Failed to check email: ${it.message}")
                                }
                        }
                        else {
                            isOTPWrong = true
                            return@Button
                        }


                    },
                    enabled = otpValues.all { it.isNotBlank() },
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .align(Alignment.CenterHorizontally)
                        .width(150.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primaryText)
                ) {
                    Text("Verify", color = colorScheme.primaryBg, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
fun ForgotPasswordOTPVerificationScreen(navController: NavController,
                          onVerify: (String) -> Unit,
                          recipientEmail: String)
{
    // Variables
    val otpLength = 6
    val focusRequesters = List(otpLength) { FocusRequester() }
    val otpValues = remember { mutableStateListOf("", "", "", "", "", "") }
    val focusedIndex = remember { mutableStateOf(-1) }
    val colorScheme = MaterialTheme.colorScheme
    var isOTPEmpty by remember{ mutableStateOf(false) }
    var isOTPWrong by remember{ mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    // Create a random 6 digit number which will act as OTP using random function
    val otp = (100000..999999).random()

    // Define various different parts of the email
    val subject = "OTP for verification"
    val body = "Dear User, \n\nPlease find below OTP for verification \n\nOTP : ${otp} \n\n " +
            "Thank you \n\nRegards \n\n\nMonash-Hub Technical Team"

    // Credentials to send an email
    val senderEmail = "noreply.monashhub@gmail.com"
    val appPassword = "vevxzxqyxtkfxahm" // NOT your Gmail password

    // Object to call send email service
    val sender = GMailSender(senderEmail, appPassword)

    // Send an email
    LaunchedEffect(recipientEmail) {
        withContext(Dispatchers.IO) {
            sender.sendMail(subject, body, recipientEmail)
        }
    }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(200) // Add delay to ensure focus is established
        focusRequesters.first().requestFocus()
        keyboardController?.show()
    }

    // Create UI Screen
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.primaryBg)
    ) {
        Column() {


            Spacer(modifier = Modifier.height(214.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Verification",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primaryText
                )
                Text(
                    text = "Enter the 6-digit code sent to $recipientEmail",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 12.dp, bottom = 6.dp),
                    color = colorScheme.inactiveGrey,
                    textAlign = TextAlign.Center
                )

                // Display error message if OTP is empty
                if(isOTPEmpty)
                {
                    Text(
                        text = "OTP cannot be blank",
                        color = colorScheme.errorText,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                // Display error message if OTP is wrong
                if(isOTPWrong)
                {
                    Text(
                        text = "OTP is wrong",
                        color = colorScheme.errorText,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    repeat(otpLength) { index ->
                        OutlinedTextField(
                            value = otpValues[index],
                            onValueChange = { value ->
                                if (value.length <= 1 && value.all { it.isDigit() }) {
                                    otpValues[index] = value
                                    if (value.isNotEmpty() && index < otpLength - 1) {
                                        focusRequesters[index + 1].requestFocus()
                                    }
                                    if (index == otpLength - 1 && otpValues.all { it.isNotBlank() }) {
                                        keyboardController?.hide()
                                        onVerify(otpValues.joinToString(""))
                                    }
                                } else if (value.length == otpLength && value.all { it.isDigit() }) {
                                    value.forEachIndexed { i, c ->
                                        if (i < otpLength) otpValues[i] = c.toString()
                                    }
                                    focusManager.clearFocus()
                                    onVerify(otpValues.joinToString(""))
                                }
                                isOTPEmpty = false
                                isOTPWrong = false
                            },
                            modifier = Modifier
                                .width(48.dp)
                                .height(56.dp)
                                .focusRequester(focusRequesters[index])
                                .onFocusChanged {
                                    if (it.isFocused) focusedIndex.value = index
                                },
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(
                                textAlign = TextAlign.Center,
                                fontSize = 18.sp
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = if (index == otpLength - 1) ImeAction.Done else ImeAction.Next
                            ),
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
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = {
                        val joinedOTP = otpValues.joinToString("")
                        onVerify(joinedOTP)

                        if(joinedOTP.isEmpty())
                        {
                            isOTPEmpty = true
                            return@Button
                        }

                        // If the OTP matches then redirect the user to update password page
                        if(joinedOTP.toInt() == otp)
                        {
                            isOTPWrong = false
                            isOTPEmpty = false

                            navController.navigate("updatePassword/${recipientEmail}")
                        }
                        else {
                            isOTPWrong = true
                            return@Button
                        }
                    },
                    enabled = otpValues.all { it.isNotBlank() },
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .align(Alignment.CenterHorizontally)
                        .width(150.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primaryText)
                ) {
                    Text("Verify", color = colorScheme.primaryBg, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
        }
    }
}

