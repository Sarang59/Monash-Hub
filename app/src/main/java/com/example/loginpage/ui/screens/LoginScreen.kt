package com.example.loginpage.ui.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.loginpage.R
import com.example.loginpage.ui.commonFunction.UserIdHelper
import com.example.loginpage.ui.theme.Transparent
import com.example.loginpage.ui.theme.errorText
import com.example.loginpage.ui.theme.inactiveGrey
import com.example.loginpage.ui.theme.primaryBg
import com.example.loginpage.ui.theme.primaryText
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlin.text.matches
import androidx.compose.runtime.LaunchedEffect
import com.example.loginpage.ui.commonFunction.RememberMeHelper
import kotlinx.coroutines.delay
import com.example.loginpage.ui.commonFunction.GoogleSignInHelper


@Composable
fun LoginScreen(navController: NavController,
                onLoginSuccess: () -> Unit,
                signInLauncher: ActivityResultLauncher<Intent>) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    var isEmailInvalid by remember { mutableStateOf(false) }
    var isEmailRegistered by remember { mutableStateOf(false) }
    var isEmailEmpty by remember { mutableStateOf(false) }
    var isPasswordInvalid by remember { mutableStateOf(false) }
    var isPasswordEmpty by remember { mutableStateOf(false) }
    var isMonashAccountInvalid by remember { mutableStateOf(false) }
    var isChecked by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        // Remove all the data before logging in
        UserIdHelper.clearAll(context)

        // To stabalise
        delay(100)

        // Check if the remember is activated or not
        if (RememberMeHelper.isRemembered(context)) {
            email.value = RememberMeHelper.getSavedEmail(context) ?: ""
            password.value = RememberMeHelper.getSavedPassword(context) ?: ""
            isChecked = true
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorScheme.primaryBg
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
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome to MonashHub",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primaryText
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Secure, social and student-centered",
                    fontSize = 16.sp,
                    color = colorScheme.primaryText
                )

                Divider(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth(),
                    color = colorScheme.primaryText,
                    thickness = 2.dp
                )
            }

            Spacer(modifier = Modifier.height(48.dp))
            // Center content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp)
            ) {
                Text(
                    text = "Please Login with your credentials",
                    fontSize = 16.sp,
                    color = colorScheme.primaryText,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .align(Alignment.CenterHorizontally)
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

                // Display an error of email is empty
                if(isEmailEmpty)
                {
                    Text(
                        text = "Email cannot be blank",
                        color = colorScheme.errorText,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                // Display an error message saying email has incorrect format
                if (isEmailInvalid) {
                    Text(
                        text = "Email is not in proper format",
                        color = colorScheme.errorText,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                // Display a message telling not a monash student account
                if(isMonashAccountInvalid)
                {
                    Text(
                        text = "Not a Monash Account",
                        color = colorScheme.errorText,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                // Display a message saying email is already registered and present in database
                if(isEmailRegistered)
                {
                    Text(
                        text = "Email is not registered",
                        color = colorScheme.errorText,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                    )
                }

                OutlinedTextField(
                    value = email.value,
                    onValueChange = {
                        email.value = it
                        isEmailInvalid = false
                        isEmailEmpty = false
                        isMonashAccountInvalid = false
                        isEmailRegistered = false
                    },
                    placeholder = { Text("Enter your email", color = colorScheme.inactiveGrey) },
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
                            append("Password: ")
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

                // Display an error message of password is empty
                if(isPasswordEmpty)
                {
                    Text(
                        text = "Password cannot be blank",
                        color = colorScheme.errorText,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                // Display an error message of password is incorrect
                if(isPasswordInvalid)
                {
                    Text(
                        text = "Password is Invalid",
                        color = colorScheme.errorText,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                OutlinedTextField(
                    value = password.value,
                    onValueChange = {
                        password.value = it
                        isPasswordInvalid = false
                        isPasswordEmpty = false
                    },
                    placeholder = { Text("Enter your password", color = colorScheme.inactiveGrey) },
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
                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        modifier = Modifier
                            .size(20.dp)  // Set a desired size
                            .align(Alignment.CenterVertically),
                        checked = isChecked,
                        onCheckedChange = { isChecked = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = colorScheme.primaryText,
                            uncheckedColor = colorScheme.primaryText,
                            checkmarkColor = colorScheme.primaryBg,
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp)) // Optional spacing for clarity
                    Text(
                        text = "Remember me",
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Button(
                    onClick = {
                        // Create variables
                        val db = Firebase.firestore
                        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
                        val validEmailformat = email.value.matches(emailRegex.toRegex())
                        val isMonashStudent = email.value.endsWith("@student.monash.edu")

                        // Check if the variables are empty or not
                        val fields = listOf(
                            email.value to { isEmailEmpty = true },
                            password.value to { isPasswordEmpty = true },
                        )

                        // Iterate over the variables to check emptiness
                        for ((value, setError) in fields) {
                            if (value.isEmpty()) {
                                setError()
                                return@Button
                            }
                        }

                        // Check if email format is valid or not
                        if(!validEmailformat) {
                            isEmailInvalid = true
                            return@Button
                        }

                        // Check if the email id belongs to a monash student only
                        if(!isMonashStudent) {
                            isMonashAccountInvalid = true
                            return@Button
                        }

                        // Check if the user is present in the database
                        db.collection("users")
                            .whereEqualTo("email", email.value.trim())
                            .get()
                            .addOnSuccessListener { documentSnapshot ->
                                // Check if user document fetched is empty or not
                                if(!documentSnapshot.isEmpty) {
                                    // Get the stored information from database
                                    val user = documentSnapshot.documents[0]
                                    val name = user.get("name")
                                    val boardedOrNot = user.get("onboarded") as? Boolean
                                    val storedPassword = user.getString("password")
                                    val googleLoggedIn = user.get("google_login") as? Boolean

                                    // Check if the user is logged in through google or not
                                    if(googleLoggedIn == false) {
                                        // Entered password is correct or not
                                        if(storedPassword == password.value) {
                                            // Has user completed the formalities or not
                                            if(boardedOrNot == true) {
                                                val userId = documentSnapshot.documents[0].id
                                                UserIdHelper.saveUserId(context, userId)
                                                onLoginSuccess()


                                                // Update the remember me config file
                                                if (isChecked) {
                                                    RememberMeHelper.saveCredentials(context, email.value.trim(), password.value)
                                                } else {
                                                    RememberMeHelper.clearCredentials(context)
                                                }

                                                // redirect to home page
                                                navController.navigate("home")
                                            }
                                            else {
                                                // If user hasn't completed formalities then redirect to avatar screen
                                                val encodedEmail = Uri.encode(email.value.trim())
                                                navController.navigate("avatarSelection/${encodedEmail}/$name")
                                            }
                                        } else {
                                            // Password is incorrect but email is correct
                                            isPasswordInvalid = true
                                            isEmailRegistered = false
                                        }
                                    } else {
                                        // Ask the user to log in through google
                                        Toast.makeText(context, "Login with google", Toast.LENGTH_LONG).show()
                                    }
                                } else {
                                    // If email is incorrect
                                    isEmailRegistered = true
                                    isPasswordInvalid = false
                                }
                            }
                            // If application is unable to connect to firebase
                            .addOnFailureListener {
                                Toast.makeText(
                                    context,
                                    "Login failed: ${it.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                    },
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .align(Alignment.CenterHorizontally)
                        .width(150.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primaryText)
                ) {
                    Text("Login", color = colorScheme.primaryBg, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }

                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = colorScheme.primaryText)) {
                            append("Forgot password ? ")
                        }

                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.primaryText,
                                textDecoration = TextDecoration.Underline
                            )
                        ) {
                            append("Reset Password")
                        }
                    },
                    fontSize = 16.sp,
                    color = colorScheme.primaryText,
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .align(Alignment.CenterHorizontally)
                        .clickable {
                            // Navigation logic here
                            navController.navigate("ForgotPasswordScreen")
                        },
                )

                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = colorScheme.primaryText)) {
                            append("Don't have an account ? ")
                        }

                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = colorScheme.primaryText,
                                textDecoration = TextDecoration.Underline
                            )
                        ) {
                            append("Sign up")
                        }
                    },
                    fontSize = 16.sp,
                    color = colorScheme.primaryText,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .align(Alignment.CenterHorizontally)
                        .clickable {
                            navController.navigate("signup")
                        }

                )

                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = colorScheme.primaryText)) {
                            append("OR")
                        }
                    },
                    fontSize = 18.sp,
                    color = colorScheme.primaryText,
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = {
                        // Sign in through google login
                        val activity = (context as? Activity)
                        activity?.let {
                            GoogleSignInHelper.initGoogleSignIn(it)
                            signInLauncher.let { launcher ->
                                GoogleSignInHelper.startSignInIntent(launcher)
                            }
                        }
                    },
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .align(Alignment.CenterHorizontally)
                        .width(250.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.Transparent),
                    border = BorderStroke(2.dp, colorScheme.primaryText)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.google_logo),
                        contentDescription = "Google Logo",
                        modifier = Modifier
                            .height(30.dp)
                            .padding(end = 10.dp)
                    )
                    Text(
                        text = "Login With Google",
                        color = colorScheme.primaryText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

            }
        }
    }
}

