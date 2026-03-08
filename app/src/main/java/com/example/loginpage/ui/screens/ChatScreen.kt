package com.example.loginpage.ui.screens

import android.content.Context
import android.util.Log
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.loginpage.ui.commonFunction.UserIdHelper
import com.example.loginpage.ui.commonFunction.loadDrawableByName
import com.example.loginpage.ui.commonFunction.noDataAvailablePrompt
import com.example.loginpage.ui.data.model.ChatHelper
import com.example.loginpage.ui.data.room.entity.ChatOverviewEntity
import com.example.loginpage.ui.data.room.viewmodel.ChatOverviewViewModel
import com.example.loginpage.ui.theme.Transparent
import com.example.loginpage.ui.theme.cardBg
import com.example.loginpage.ui.theme.errorText
import com.example.loginpage.ui.theme.inactiveGrey
import com.example.loginpage.ui.theme.primaryBg
import com.example.loginpage.ui.theme.primaryText
import com.example.loginpage.utils.isInternetAvailable
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await

@Composable
fun ChatScreen(navController: NavController, drawerState: DrawerState) {
    // Remember scroll state
    val scrollState = rememberScrollState()
    val firestore = FirebaseFirestore.getInstance()
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    val chatOverviewList = remember { mutableStateListOf<ChatHelper>() }
    val colorScheme = MaterialTheme.colorScheme
    val user_id = UserIdHelper.getUserId(context)
    val chatOverviewViewModel: ChatOverviewViewModel = viewModel()
    val allChatOverviews by chatOverviewViewModel.allChatOverviews.collectAsState(initial = emptyList())

    // Internet is available
    if (isInternetAvailable(context)) {
        var listenerRegistration by remember { mutableStateOf<ListenerRegistration?>(null) }

        LaunchedEffect(user_id) {
            user_id?.let {
                listenerRegistration = listenToChatOverview(firestore, chatOverviewList, context, it){}
            }
        }

        DisposableEffect(user_id) {
            onDispose {
                listenerRegistration?.remove()
            }
        }
    }
    else {
        // No internet connection
        Log.i("INTERNET STATUS:","OFFLINE")
        LaunchedEffect(allChatOverviews) {
            Log.i("ChatOverviewScreen", "Updated chat overviews, count: ${allChatOverviews.size}")
            chatOverviewList.clear()
            allChatOverviews.forEach { overview ->
                Log.i("ChatOverviewScreen", "Overview: $overview")
                val chatHelper = ChatHelper(
                    new_msg_count = 0,  // use properties from each overview
                    peer_user_id = overview.peer_user_id,
                    name = overview.name,
                    image = overview.image
                )
                chatOverviewList.add(chatHelper)
            }
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
                        text = "Chat",
                        style = TextStyle(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primaryText
                        )
                    )

                    Spacer(modifier = Modifier.weight(1f))
                }

                HorizontalDivider(
                    thickness = 2.dp,
                    color = colorScheme.primaryText,
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                )
                {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = { Text("Search", color = colorScheme.inactiveGrey, fontSize = 18.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Icon",
                                modifier = Modifier.size(30.dp),
                                tint = colorScheme.inactiveGrey
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
                        shape = RectangleShape,
                    )
                }


                Spacer(modifier = Modifier.height(10.dp))
                if(chatOverviewList.size==0)
                {
                    noDataAvailablePrompt("No Chat Messages")
                }else{
                    LazyColumn()
                    {
                        items(chatOverviewList.size)
                        { index ->
                            if(chatOverviewList[index].name.contains(name, ignoreCase = true)) {

                                Card(
                                    onClick = {
                                        val chat = chatOverviewList[index]
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
                                            verticalAlignment = Alignment.CenterVertically)
                                        {
                                            Image(
                                                painter = loadDrawableByName(context,chatOverviewList[index].image), // e.g., painterResource(R.drawable.your_image)
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .size(65.dp) // Set the desired size
                                                    .clip(CircleShape)
                                            )
                                            Text(
                                                text = chatOverviewList[index].name,
                                                modifier = Modifier
                                                    .padding(horizontal = 10.dp)
                                                    .align(Alignment.CenterVertically),
                                                color = colorScheme.primaryText,
                                                style = TextStyle(
                                                    fontSize = 18.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )

                                            if(chatOverviewList[index].new_msg_count>0)
                                            {
                                                Text(text = chatOverviewList[index].new_msg_count.toString(),
                                                color = colorScheme.primaryText
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
}

// Listen to chat overview collection
fun listenToChatOverview(
    firestore: FirebaseFirestore,
    chatOverviewList: SnapshotStateList<ChatHelper>,
    context: Context,
    user_id: String,
    onUpdate: () -> Unit
): ListenerRegistration {
    return firestore.collection("chat_overview")
        .document(user_id)
        .addSnapshotListener { snapshot, e ->
            if (e != null || snapshot == null || !snapshot.exists()) {
                Log.e("FIRESTORE_ERROR", "Failed to listen to chat overview", e)
                return@addSnapshotListener
            }

            val peerChats = snapshot.get("peer_chats") as? Map<String, Map<String, Any>> ?: emptyMap()

            chatOverviewList.clear()

            for ((peerId, peerData) in peerChats) {
                val newMsgCount = (peerData["new_msg_count"] as? Long)?.toInt() ?: 0

                firestore.collection("user_profile")
                    .whereEqualTo("user_id", peerId)
                    .get()
                    .addOnSuccessListener { userDocs ->
                        val userDoc = userDocs.firstOrNull()
                        val name = userDoc?.getString("name") ?: "Deleted User"
                        val avatarId = userDoc?.getString("avatar_id") ?: "1"

                        firestore.collection("avatar")
                            .whereEqualTo("avatar_id", avatarId)
                            .get()
                            .addOnSuccessListener { avatarDocs ->
                                val avatarDoc = avatarDocs.firstOrNull()
                                val avatarFilePath = avatarDoc?.getString("avatar_file_path") ?: ""

                                val chatHelper = ChatHelper(
                                    new_msg_count = newMsgCount,
                                    peer_user_id = peerId,
                                    name = name,
                                    image = avatarFilePath
                                )

                                chatOverviewList.add(chatHelper)
                                onUpdate()
                            }
                    }
            }
        }
}