package com.example.loginpage.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import android.util.Log

@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    onResetPassword: (String) -> Unit
) {
    val email = remember { mutableStateOf("") }
    val isEmailValid = remember(email.value) {
        android.util.Patterns.EMAIL_ADDRESS.matcher(email.value).matches()
    }
    val colorScheme = MaterialTheme.colorScheme
    var isEmailInvalid by remember { mutableStateOf(false) }
    var isMonashAccountInvalid by remember { mutableStateOf(false) }
    var notPresentInDatabase by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.primaryBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(64.dp))

            // Top content (always at top)
//            Column(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text(
//                    text = "Welcome to MonashHub",
//                    fontSize = 28.sp,
//                    fontWeight = FontWeight.Bold,
//                    color = primaryText
//                )
//
//                Spacer(modifier = Modifier.height(4.dp))
//
//                Text(
//                    text = "Secure, social and student-centered",
//                    fontSize = 16.sp,
//                    color = primaryText
//                )
//
//                Divider(
//                    modifier = Modifier
//                        .padding(vertical = 16.dp)
//                        .fillMaxWidth(),
//                    color = primaryText,
//                    thickness = 2.dp
//                )
//            }

            Spacer(modifier = Modifier.height(150.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize(),
//                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Forgot Password ?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primaryText,
                    modifier = Modifier.align(Alignment.CenterHorizontally)

                )
                Text(
                    text = "Enter your registered email to receive Verification Code.",
                    fontSize = 16.sp,
                    color = colorScheme.inactiveGrey,
                    modifier = Modifier.padding(top = 12.dp, bottom = 16.dp).align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center
                )

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

                if(isEmailInvalid)
                {
                    Text(
                        text = "Email is Invalid",
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                if(isMonashAccountInvalid)
                {
                    Text(
                        text = "Not a Monash Account",
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                if(notPresentInDatabase)
                {
                    Text(
                        text = "Not Present in database",
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                OutlinedTextField(
                    value = email.value,
                    onValueChange = {
                        email.value = it
                        isEmailInvalid = false
                        isMonashAccountInvalid = false
                        notPresentInDatabase = false
                    },
                    placeholder = { Text("Enter your email ", color = colorScheme.inactiveGrey) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
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

                Spacer(modifier = Modifier.height(6.dp))
                Button(
                    onClick = {
                        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
                        val validEmailformat = email.value.matches(emailRegex.toRegex())
                        val isMonashStudent = email.value.endsWith("@student.monash.edu")

                        if(!validEmailformat) {
                            isEmailInvalid = true
                            return@Button
                        }

                        if(!isMonashStudent) {
                            isMonashAccountInvalid = true
                            return@Button
                        }

                        Firebase.firestore.collection("users")
                            .whereEqualTo("email", email.value)
                            .get()
                            .addOnSuccessListener { documentSnapshot ->
                                if(!documentSnapshot.isEmpty) {
                                    notPresentInDatabase = false
                                    val encodedEmail = Uri.encode(email.value)
                                    navController.navigate("ForgotPasswordOtpVerification/${encodedEmail}")
                                } else {
                                    notPresentInDatabase = true
                                }
                            }
                            .addOnFailureListener {
                                Log.e(
                                    "context",
                                    "Unable to fetch record: ${it.message}"
                                )
                            }

                        /*if (!isEmailInvalid && !isMonashAccountInvalid && !notPresentInDatabase)
                        {
                            //onResetPassword(email.value)  // Optional business logic (e.g., send email)
                            val encodedEmail = Uri.encode(email.value)
                            navController.navigate("ForgotPasswordOtpVerification/${encodedEmail}") // Navigate to another screen
                        }*/
                    },
                    enabled = isEmailValid,
                    modifier = Modifier
                        .width(150.dp)
                        .padding(top = 16.dp)
                        .align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.primaryText,
                        disabledContainerColor = colorScheme.inactiveGrey
                    )
                ) {
                    Text(
                        "Send",
                        color = colorScheme.primaryBg,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}
