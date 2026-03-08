package com.example.loginpage.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import com.example.loginpage.ui.commonFunction.loadDrawableByName
import com.example.loginpage.ui.commonFunction.noDataAvailablePrompt
import com.example.loginpage.ui.data.model.ChatHelper
import com.example.loginpage.ui.theme.cardBg
import com.example.loginpage.ui.theme.primaryBg
import com.example.loginpage.ui.theme.primaryText
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun NotificationScreen(navController: NavController, drawerState: DrawerState)
{
    val scrollState = rememberScrollState()
    val firestore = FirebaseFirestore.getInstance()
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var chatOverviewList = remember { mutableStateListOf<ChatHelper>() }
    val colorScheme = MaterialTheme.colorScheme
    val user_id = UserIdHelper.getUserId(context)
    var new_msg_counter = remember { mutableStateOf(0) };

    LaunchedEffect(Unit) {
        if (user_id != null) {
            listenToChatOverview(firestore,chatOverviewList,context,user_id){}
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = colorScheme.primaryBg
    ) {
        Column()
        {
            Header(navController = navController,drawerState = drawerState)
            Spacer(modifier = Modifier.height(26.dp))
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 0.dp, horizontal = 0.dp),
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    Text(
                        text = "Notifications",
                        style = TextStyle(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primaryText
                        )
                    )
                }

                HorizontalDivider(
                    thickness = 2.dp,
                    color = colorScheme.primaryText,
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                if(new_msg_counter.value==0)
                {
                        noDataAvailablePrompt("No new notifications")
                }
                    LazyColumn()
                    {
                        items(chatOverviewList.size) { index ->
                            val chat = chatOverviewList[index]
                            new_msg_counter.value += chat.new_msg_count
                            if (chat.new_msg_count > 0) {
                                Card(
                                    onClick = {
                                        navController.navigate("chatDetailed/${chat.peer_user_id}/${chat.name}/${chat.image}")
                                    },
                                    shape = RectangleShape,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .shadow(6.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                                    colors = CardDefaults.cardColors(containerColor = colorScheme.cardBg)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(colorScheme.cardBg)
                                            .padding(10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {

                                            Column(modifier = Modifier.padding(start = 10.dp)) {
                                                Text(
                                                    text = "You have ${chat.new_msg_count} unread message${if (chat.new_msg_count > 1) "s" else ""} from ${chat.name}",
                                                    color = colorScheme.primaryText,
                                                    style = TextStyle(
                                                        fontSize = 18.sp,
                                                        fontWeight = FontWeight.Bold
                                                    ),
                                                    modifier = Modifier.padding(top = 4.dp)
                                                )
                                            }

                                            Spacer(modifier = Modifier.weight(1f))

                                            Icon(
                                                imageVector = Icons.Default.Send,
                                                contentDescription = "Send Request",
                                                tint = colorScheme.primaryText,
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .align(Alignment.CenterVertically)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        item { Spacer(Modifier.height(26.dp)) }
                    }

            }
        }
    }
}

