package com.example.loginpage.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.loginpage.ui.commonFunction.UserIdHelper
import com.example.loginpage.ui.data.model.ChatHelper
import com.example.loginpage.ui.theme.cardBg
import com.example.loginpage.ui.theme.errorText
import com.example.loginpage.ui.theme.primaryBg
import com.example.loginpage.ui.theme.primaryText
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@Composable
fun Header(navController: NavController, drawerState: DrawerState) {
    val scope = rememberCoroutineScope()        // Coroutine scope for drawer opening
    val colorScheme = MaterialTheme.colorScheme
    val firestore = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    // Holds chat overview data and unread message counter
    var chatOverviewList = remember { mutableStateListOf<ChatHelper>() }
    val user_id = UserIdHelper.getUserId(context)
    var new_msg_counter = remember { mutableIntStateOf(0) };

    // Launch effect to initialize chat listener if user is logged in
    LaunchedEffect(Unit) {
        if (user_id != null) {

            // Firebase listener to update chat list and count unread messages
            listenToChatOverview(firestore, chatOverviewList, context, user_id) {
                Log.i("OVERVIEWSIZE:", chatOverviewList.size.toString())

                // Surface acts as a container for the header with elevation and theme support
                new_msg_counter.value = chatOverviewList.sumOf { it.new_msg_count }
                Log.i("NEWMSGCOUNTER:", new_msg_counter.value.toString())
            }
        }
    }




    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        tonalElevation = 1024.dp,
        shadowElevation = 16.dp,
        color = colorScheme.cardBg
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 25.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Hamburger icon to open drawer
                IconButton(
                    onClick = {
                        scope.launch { drawerState.open() }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = colorScheme.primaryText,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Notifications icon with unread message badge
                Box(
                    modifier = Modifier.wrapContentSize(),
                    contentAlignment = Alignment.TopEnd
                ) {
                    IconButton(
                        onClick = {
                            navController.navigate("notification")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notification",
                            tint = colorScheme.primaryText,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    // Red badge showing count of unread messages
                    if (new_msg_counter.value > 0) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .offset(x = 2.dp, y = (-1).dp) // Position badge at top-right
                                .background(colorScheme.errorText, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = new_msg_counter.intValue.toString(),
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }
                }


            }

            // Heading in the center
            Text(
                text = "MonashHub",
                style = TextStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primaryText
                ),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}


