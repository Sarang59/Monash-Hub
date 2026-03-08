package com.example.loginpage.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.loginpage.ui.theme.primaryBg
import com.example.loginpage.ui.theme.primaryText
import com.example.loginpage.ui.theme.success
import kotlinx.coroutines.delay

@Composable
fun SuccessPage(
    navController: NavController,
    lastPageVisited: String
) {

    // Delay redirecting to a particular page to showcase the success of accomplishing the work
    LaunchedEffect(Unit) {
        delay(3_000)

        if(lastPageVisited == "updatePassword")
        {
            navController.navigate("login") {
                popUpTo("success") { inclusive = true } // optional: remove SuccessPage from backstack
            }
        }
        else if(lastPageVisited == "setupPage4" || lastPageVisited == "postcreation")
        {

            navController.navigate("home") {
                popUpTo("success") { inclusive = true } // optional: remove SuccessPage from backstack
            }
        }
        else if(lastPageVisited == "editProfile")
        {
            navController.navigate("profile") {
                popUpTo("success") { inclusive = true } // optional: remove SuccessPage from backstack
            }
        }
    }

    val colorScheme = MaterialTheme.colorScheme

    // Display the success response depending on the previous page visited
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.primaryBg).padding(bottom = 60.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Success",
            tint = colorScheme.success, // Green
            modifier = Modifier.size(120.dp)
        )

        if(lastPageVisited == "updatePassword")
        {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Password reset successfully!",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primaryText
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "You can now log in with your new password.",
                style = TextStyle(
                    fontSize = 16.sp,
                    color = colorScheme.primaryText
                ),
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }

        if(lastPageVisited == "setupPage4")
        {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Onboarded Successfully!",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primaryText
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Your account has been successfully created.",
                style = TextStyle(
                    fontSize = 16.sp,
                    color = colorScheme.primaryText
                ),
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }

        if(lastPageVisited == "postcreation")
        {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Post Created!",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primaryText
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Your post has been shared successfully.",
                style = TextStyle(
                    fontSize = 16.sp,
                    color = colorScheme.primaryText
                ),
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }

        if(lastPageVisited == "editProfile")
        {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Profile updated!",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primaryText
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Your profile has been updated successfully.",
                style = TextStyle(
                    fontSize = 16.sp,
                    color = colorScheme.primaryText
                ),
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }

        /*Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Product Listed!",
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primaryText
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Your product is now live on M-Kart.",
            style = TextStyle(
                fontSize = 16.sp,
                color = colorScheme.primaryText
            ),
            modifier = Modifier.padding(horizontal = 16.dp),
        )*/
    }
}
