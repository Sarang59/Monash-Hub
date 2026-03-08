package com.example.loginpage.ui.screens

import android.R.attr.label
import android.R.attr.onClick
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.loginpage.R
import com.example.loginpage.ui.commonFunction.UserIdHelper
import com.example.loginpage.ui.theme.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(
    navController: NavController,
    drawerState: DrawerState,
    isDark: Boolean,                    // Boolean flag indicating if dark mode is enabled
    onToggleDark: (Boolean) -> Unit     // Callback function to toggle dark mode
) {

    val interactionSource = remember { MutableInteractionSource() }
    val colorScheme = MaterialTheme.colorScheme
    val context = LocalContext.current
    val options = listOf(
        "Terms And Conditions" to "terms",
        "Privacy Policy" to "privacypolicy"
    )

    Surface(modifier = Modifier
        .fillMaxSize(),
        color = colorScheme.primaryBg
    ) {

        Column {
            // Reusable drawer header
            Header(navController = navController,drawerState = drawerState)
            Spacer(modifier = Modifier.height(26.dp))

            Column(modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()) {

                Text(
                    text = "Settings",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primaryText
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    thickness = 2.dp,
                    color = colorScheme.primaryText
                )

                // Dark mode toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 0.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Dark Mode",
                        style = TextStyle(
                            color = colorScheme.primaryText,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    // Toggle switch for dark mode
                    Switch(
                        checked = isDark,
                        onCheckedChange = { onToggleDark(it) },
                        interactionSource = interactionSource,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = colorScheme.primaryText,
                            uncheckedThumbColor = colorScheme.primaryText,
                            checkedTrackColor = colorScheme.inactiveGrey.copy(alpha = 0.3f),
                            uncheckedTrackColor = colorScheme.Transparent,
                            uncheckedBorderColor = colorScheme.primaryText
                        )
                    )
                }



                Spacer(modifier = Modifier.height(16.dp))

                // Dynamically create clickable cards for each setting option
                options.forEach { (label, link) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(6.dp)
                            .clickable { navController.navigate(link) },
                        shape = RectangleShape,
                        elevation = CardDefaults.cardElevation(10.dp),
                        colors = CardDefaults.cardColors(containerColor = colorScheme.cardBg)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {

                                Text(
                                    text = label,
                                    style = TextStyle(
                                        color = colorScheme.primaryText,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.ArrowForwardIos,
                                contentDescription = "Arrow",
                                tint = colorScheme.primaryText,
                                modifier = Modifier.size(25.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

            }

            // Logout button at bottom
            Button(
                    onClick = {
                        UserIdHelper.clearUserId(context)  // Clear saved user ID/session
                        navController.navigate("login")     // Navigate back to login screen
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(150.dp)
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primaryText)
                ) {
                    Text(
                        text = "Logout",
                        color = colorScheme.primaryBg,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
}

