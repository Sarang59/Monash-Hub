package com.example.loginpage.ui.screens


import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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

@Composable
fun UpdatePassword(
    navController: NavController,
    email: String)
{
    // Variables
    var password = remember { mutableStateOf("") }
    val passwordRequirements = remember { mutableStateListOf(
        "At least 8 characters",
        //"At least contains one digit",
        //"At least contains one lowercase letter",
        //"At least contains one uppercase letter",
        //"At least contains one special character"
    ) }
    var confirmPassword = remember { mutableStateOf("") }
    var isPasswordEmpty by remember { mutableStateOf(false) }
    var isPasswordInvalid by remember { mutableStateOf(false) }
    var isConfirmPasswordEmpty by remember { mutableStateOf(false) }
    var arePasswordsNotMatching by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme

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
            Spacer(modifier = Modifier.height(150.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp)
            ) {
                Text(
                    text = "Reset Password",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primaryText,
                    modifier = Modifier.align(Alignment.CenterHorizontally)

                )
                Text(
                    text = "Create a new password",
                    fontSize = 16.sp,
                    color = colorScheme.inactiveGrey,
                    modifier = Modifier.padding(top = 4.dp, bottom = 26.dp)
                        .align(Alignment.CenterHorizontally),
//                    textAlign = TextAlign.Center
                )

                Text(
                    buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colorScheme.primaryText
                            )
                        ) {
                            append("New Password: ")
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

                // Display an error message if password field is empty
                if(isPasswordEmpty)
                {
                    Text(
                        text = "Password field cannot be blank",
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                // Display an error message if password is not 8 character long
                if(isPasswordInvalid)
                {
                    Text(
                        text = "Password should be at least 8 characters long",
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                OutlinedTextField(
                    value = password.value,
                    onValueChange = {
                        password.value = it
                        isPasswordEmpty = false
                        arePasswordsNotMatching = false
                        isPasswordInvalid = false
                    },
                    placeholder = { Text("Enter your password", color = colorScheme.inactiveGrey) },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val icon = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = icon,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = inactiveGrey
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
                            append("Confirm New Password: ")
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
                        text = "Confirm password field is empty",
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                // Display an error message if the passwords are mismatching
                if(arePasswordsNotMatching)
                {
                    Text(
                        text = "Passwords are not matching",
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                OutlinedTextField(
                    value = confirmPassword.value,
                    onValueChange = {
                        confirmPassword.value = it
                        isConfirmPasswordEmpty = false
                        arePasswordsNotMatching = false
                    },
                    placeholder = { Text("Enter your password", color = colorScheme.inactiveGrey) },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val icon = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = icon,
                                contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                                tint = inactiveGrey
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
                            password.value to { isPasswordEmpty = true },
                            confirmPassword.value to { isConfirmPasswordEmpty = true }
                        )

                        for ((value, setError) in fields) {
                            if (value.isEmpty()) {
                                setError()
                                return@Button
                            }
                        }

                        // Check if the password length is less than 8 characters
                        if(password.value.length < 8)
                        {
                            isPasswordInvalid = true
                        }

                        // Check if the passwords are matching or not
                        if(password.value != confirmPassword.value)
                        {
                            arePasswordsNotMatching = true
                        }

                        // If all the conditions are met then update the user in the
                        // database with new password
                        if(!isPasswordEmpty && !isConfirmPasswordEmpty
                            && !arePasswordsNotMatching && !isPasswordInvalid)
                        {
                            Firebase.firestore.collection("users")
                                .whereEqualTo("email", email)
                                .get()
                                .addOnSuccessListener { userDocuments ->
                                    if (!userDocuments.isEmpty) {
                                        val document = userDocuments.documents[0]
                                        val documentReference = Firebase
                                            .firestore
                                            .collection("users")
                                            .document(document.id)

                                        documentReference.update("password", confirmPassword.value)
                                            .addOnSuccessListener {

                                                navController.navigate("successPage/updatePassword")
                                            }
                                            .addOnFailureListener {
                                                Log.e("Failed", "Password not updated: ${it.message}")
                                            }
                                    }
                                }
                                .addOnFailureListener {
                                    Log.e("Error", "Failed to check email: ${it.message}")
                                }
                        }
                    },
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
                        "Submit",
                        color = colorScheme.primaryBg,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}
