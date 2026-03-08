package com.example.loginpage.ui.screens

import android.R
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
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
import com.example.loginpage.ui.data.model.Chat
import com.example.loginpage.ui.data.room.entity.ChatMessageEntity
import com.example.loginpage.ui.data.room.entity.ChatOverviewEntity
import com.example.loginpage.ui.data.room.viewmodel.ChatMessageViewModel
import com.example.loginpage.ui.data.room.viewmodel.ChatOverviewViewModel
import com.example.loginpage.ui.theme.cardBg
import com.example.loginpage.ui.theme.errorText
import com.example.loginpage.ui.theme.inactiveGrey
import com.example.loginpage.ui.theme.primaryBg
import com.example.loginpage.ui.theme.primaryText
import com.example.loginpage.utils.isInternetAvailable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun ChatDetailedScreen(navController: NavController, drawerState: DrawerState,
                 peer_user_id: String = "",
                 peer_name: String = "",
                 image: String)
{
    var chatMessage by remember { mutableStateOf("") }
    val chatList = remember { mutableStateListOf<Chat>() }
    val firestore = FirebaseFirestore.getInstance()
    val colorScheme = MaterialTheme.colorScheme
    val listState = rememberLazyListState()
    val context = LocalContext.current
    val user_id = UserIdHelper.getUserId(context)
    var peerOnline = remember { mutableStateOf(false) }

    val chatOverviewViewModel: ChatOverviewViewModel = viewModel()
    val chatMessageViewModel: ChatMessageViewModel = viewModel()

    val allChatOverviews by chatOverviewViewModel.allChatOverviews.collectAsState(initial = emptyList())

    var documentId by remember { mutableStateOf("") }
    var chatboxEmpty by remember { mutableStateOf(false) }

    var listenerRegistration by remember { mutableStateOf<ListenerRegistration?>(null) }


    LaunchedEffect(peer_user_id, isInternetAvailable(context), allChatOverviews) {
        // Online flow
        if (isInternetAvailable(context) && user_id != null) {
            documentId = getOrCreateChatDocumentId(firestore, user_id, peer_user_id)

            // Adding row in room databse
            val chatOverview = ChatOverviewEntity(
                chat_table_name = documentId,
                peer_user_id = peer_user_id,
                name = peer_name,
                image = image
            )

            // Add chatOverview to room db
            chatOverviewViewModel.insertChatOverview(chatOverview)

            updateUserStatus(firestore, documentId, user_id, "online")
            resetNewMsgCount(firestore, user_id, peer_user_id)
            listenerRegistration?.remove()
            listenerRegistration = listenToChatMessages(
                firestore = firestore,
                documentId = documentId,
                chatList = chatList,
                peer_user_id = peer_user_id,
                chatMessageViewModel,
                onUpdate = { onlineStatus ->
                    if (onlineStatus != null) {
                        peerOnline.value = onlineStatus
                    }
                }
            )
            if (chatList.isNotEmpty()) {
                listState.animateScrollToItem(chatList.size - 1)
            }
        } else {
            // Offline flow
            // Check for valid documentID
            val docId1 = "${user_id}_$peer_user_id"
            val docId2 = "${peer_user_id}_$user_id"
            allChatOverviews.forEach {
                if (it.chat_table_name == docId1) {
                    documentId = docId1
                } else if (it.chat_table_name == docId2) {
                    documentId = docId2
                }
            }
            Log.i("DOCUMENT ID: ",documentId)
        }
    }


    if (!isInternetAvailable(context)) {
        LaunchedEffect(documentId) {
            Log.i("DOC ID", documentId)
            Log.i("ROOM DB: ","Fetching data from Room DB...")
            // Fetch Chats from Room DB
            documentId.let { docId ->
                chatMessageViewModel.getMessagesByChatTableId(docId).collect { messages ->
                    chatList.clear()
                    messages.forEach { chat ->
                        Log.i("CHAT MSG:", "$chat")
                        val chatMsg = Chat(
                            message = chat.message,
                            sender_id = chat.sender_id,
                            receiver_id = chat.receiver_id,
                            timestamp = Timestamp(Date(chat.timestamp))  // convert Long to Firebase Timestamp
                        )
                        Log.i("MSG: ",chatMsg.message)
                        chatList.add(chatMsg)
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            // Set user status offline if user leaves screen
            if (user_id != null && isInternetAvailable(context)) {
                updateUserStatus(firestore, documentId, user_id, "offline")
            }
            listenerRegistration?.remove()
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = colorScheme.primaryBg
    ) {
        Column() {
            Header(drawerState = drawerState, navController = navController)

            Spacer(modifier = Modifier.height(26.dp))
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 0.dp, horizontal = 0.dp)
                        .clickable{navController.navigate("chat")},
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back arrow",
                        tint = colorScheme.primaryText,
                        modifier = Modifier.size(28.dp)
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    Image(
                        painter = loadDrawableByName(context,image),
                        contentDescription = null,
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(){
                        Text(
                            text = peer_name,
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.primaryText
                            )
                        )
                        Text(text = if (peerOnline.value) "online" else "offline",
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = colorScheme.primaryText,
                                fontWeight = if (peerOnline.value) FontWeight.Bold else FontWeight.Normal
                            ))
                    }
                }

                HorizontalDivider(
                    thickness = 2.dp,
                    color = colorScheme.primaryText,
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                )
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 85.dp)
                    ) {
                        items(chatList.size) { index ->
                            val chat = chatList[index]
                            val isCurrentUser = chat.sender_id == user_id

                            val currentDate = formatTimestampToDate(chat.timestamp) // Format: "dd/MM/yy"

                            val showDateHeader = if (index == 0) {
                                true
                            } else {
                                val prevDate = formatTimestampToDate(chatList[index - 1].timestamp)
                                currentDate != prevDate
                            }

                            if (showDateHeader) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 10.dp)
                                    ) {
                                        Card(
                                            shape = RectangleShape,
                                            modifier = Modifier
                                                .width(100.dp)
                                                .shadow(6.dp)
                                                .align(Alignment.CenterHorizontally),
                                            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                                            colors = CardDefaults.cardColors(containerColor = colorScheme.cardBg)
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(colorScheme.cardBg)
                                                    .padding(vertical = 10.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    text = currentDate.toString(),
                                                    style = TextStyle(
                                                        fontSize = 16.sp,
                                                        color = colorScheme.primaryText
                                                    )
                                                )
                                            }
                                        }
                                    }
                            }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 0.dp, vertical = 10.dp),
                                    horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
                                ) {
                                    Card(
                                        shape = RectangleShape,
                                        modifier = Modifier
                                            .width(300.dp)
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
                                            Text(
                                                text = chat.message,
                                                style = TextStyle(
                                                    fontSize = 16.sp,
                                                    color = colorScheme.primaryText
                                                )
                                            )

                                            Text(
                                                text = formatTimestampToTime(chat.timestamp),
                                                style = TextStyle(
                                                    fontSize = 16.sp,
                                                    color = colorScheme.primaryText
                                                ),
                                                modifier = Modifier
                                                    .align(Alignment.End)
                                                    .padding(top = 5.dp)
                                            )
                                        }
                                    }
                                }
                        }

                        item {
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }

                    LaunchedEffect(chatList.size) {
                        if (chatList.isNotEmpty()) {
                            listState.animateScrollToItem(chatList.size - 1)
                        }
                    }
                    OutlinedTextField(
                        value = chatMessage,
                        supportingText = {
                            if (chatboxEmpty) Text("Message cannot be empty", color = colorScheme.errorText)
                        },
                        onValueChange = {
                            chatMessage = it
                            if (chatMessage.trim().isNotEmpty()) {
                                chatboxEmpty = false // Clear error as soon as valid input starts
                            }
                        },
                        placeholder = { Text("Type your message here", color = colorScheme.inactiveGrey, fontSize = 18.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter).padding(bottom = 30.dp),
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    if(chatMessage.trim().isEmpty())
                                    {
                                        chatboxEmpty = true
                                        return@IconButton
                                    }
                                    if(!isInternetAvailable(context))
                                    {
                                        Toast.makeText(context,"No internet available",Toast.LENGTH_SHORT).show()
                                        Log.i("INTERNET STATUS: ","OFFLINE")
                                    }else{
                                        if (user_id != null) {
                                            sendMessageToChat(
                                                firestore = FirebaseFirestore.getInstance(),
                                                senderId = user_id,
                                                receiverId = peer_user_id,
                                                messageText = chatMessage,
                                                documentId = documentId,
                                                peerOnline = peerOnline.value
                                            )

                                            chatMessage = "" // Clear text after sending
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Send,
                                    contentDescription = "Send Icon",
                                    tint = colorScheme.inactiveGrey
                                )
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = colorScheme.primaryBg, // Solid background for focused state
                            unfocusedContainerColor = colorScheme.primaryBg,
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
        }
    }
}

// Send Message to Peer
fun sendMessageToChat(
    firestore: FirebaseFirestore,
    senderId: String,
    receiverId: String,
    messageText: String,
    documentId: String,
    peerOnline: Boolean
) {
    if (messageText.isBlank()) return

    val chatMessage = Chat(
        message = messageText,
        sender_id = senderId,
        receiver_id = receiverId,
        timestamp = Timestamp.now()
    )

    val chatCollection = firestore.collection("chat")
    val docRef1 = chatCollection.document(documentId)

    // Step 1: Send the message
    docRef1.get().addOnSuccessListener { doc1 ->
        if (doc1.exists()) {
            Log.i("FIRESTORE STATUS:","Sending chat to firestore");
            docRef1.update("messages", FieldValue.arrayUnion(chatMessage))
                .addOnSuccessListener {
                    val chatMessageEntity = ChatMessageEntity(
                        chat_table_name = documentId,
                        message = messageText,
                        sender_id = senderId,
                        receiver_id = receiverId,
                        timestamp = Timestamp.now().seconds * 1000 + Timestamp.now().nanoseconds / 1_000_000
                    )
                            if (!peerOnline) {
                                // Peer is offline, increment new_msg_count in sender's chat_overview
                                val chatOverviewRef = firestore.collection("chat_overview").document(receiverId)

                                firestore.runTransaction { transaction ->
                                    val snapshot = transaction.get(chatOverviewRef)
                                    if (snapshot.exists()) {
                                        val peerChats = snapshot.get("peer_chats") as? MutableMap<String, MutableMap<String, Any>>
                                            ?: mutableMapOf()

                                        // Get current count or 0 if missing
                                        val currentCount = (peerChats[senderId]?.get("new_msg_count") as? Long)?.toInt() ?: 0
                                        val updatedCount = currentCount + 1

                                        // Update the count in map
                                        if (peerChats.containsKey(senderId)) {
                                            peerChats[senderId]?.set("new_msg_count", updatedCount)
                                        } else {
                                            // If no entry, create one
                                            peerChats[senderId] = mutableMapOf("new_msg_count" to updatedCount)
                                        }

                                        // Commit the updated map back
                                        transaction.update(chatOverviewRef, "peer_chats", peerChats)
                                    }
                                }.addOnSuccessListener {
                                    Log.d("Chat", "new_msg_count incremented for peer $receiverId")
                                }.addOnFailureListener { e ->
                                    Log.e("Chat", "Failed to increment new_msg_count", e)
                                }
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("Chat", "Failed to get user status", e)
                        }
                }
    }.addOnFailureListener { e ->
        Log.e("Chat", "Error checking document existence", e)
    }
}

// Fetch only time
fun formatTimestampToTime(timestamp: Timestamp): String {
    val date = timestamp.toDate()
    val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
    sdf.timeZone = TimeZone.getDefault()
    return sdf.format(date)
}

// Convert Firebase Timestamp to java.util.Date
fun formatTimestampToDate(timestamp: Timestamp): String {
    val sdf = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
    return sdf.format(timestamp.toDate())
}


// Get chat from document id or create one if not exists
suspend fun getOrCreateChatDocumentId(
    firestore: FirebaseFirestore,
    userId1: String,
    userId2: String
): String {
    val docId1 = "${userId1}_$userId2"
    val docId2 = "${userId2}_$userId1"

    val doc1 = firestore.collection("chat").document(docId1).get().await()
    if (doc1.exists()) return docId1

    val doc2 = firestore.collection("chat").document(docId2).get().await()
    if (doc2.exists()) return docId2

    // Neither exists — create docId1
    val chatData = mapOf(
        "messages" to emptyList<Map<String, Any>>(),
        "user_status" to mapOf(
            userId1 to "offline",
            userId2 to "offline"
        )
    )
    firestore.collection("chat").document(docId1).set(chatData)

    // Update chat_overview for both users (optimistically)
    val user1Overview = mapOf(
        "peer_chats" to mapOf(
            userId2 to mapOf("new_msg_count" to 0)
        )
    )
    firestore.collection("chat_overview")
        .document(userId1)
        .set(user1Overview, SetOptions.merge())

    val user2Overview = mapOf(
        "peer_chats" to mapOf(
            userId1 to mapOf("new_msg_count" to 0)
        )
    )
    firestore.collection("chat_overview")
        .document(userId2)
        .set(user2Overview, SetOptions.merge())

    return docId1
}

// Get chat messages in realtime
fun listenToChatMessages(
    firestore: FirebaseFirestore,
    documentId: String,
    chatList: SnapshotStateList<Chat>,
    peer_user_id: String,
    chatMessageViewModel: ChatMessageViewModel,
    onUpdate: (peerStatus: Boolean?) -> Unit
): ListenerRegistration {

    val chatCollection = firestore.collection("chat")

    // First, check which document exists, then attach listener
    val docRef1 = chatCollection.document(documentId)

    return docRef1.addSnapshotListener { snapshot1, error ->
        if (error != null) return@addSnapshotListener

        if (snapshot1 != null && snapshot1.exists()) {
            updateChatListFromSnapshot(snapshot1, chatList,documentId, chatMessageViewModel)

            val userStatusMap = snapshot1.get("user_status") as? Map<String, String>
            val peerStatus = userStatusMap?.get(peer_user_id)=="online"
            onUpdate(peerStatus)
        }
    }
}

// Update Lazy column based on firestore document updates
fun updateChatListFromSnapshot(
    snapshot: DocumentSnapshot,
    chatList: SnapshotStateList<Chat>,
    documentId: String,
    chatMessageViewModel: ChatMessageViewModel
) {
    val messages = snapshot.get("messages") as? List<Map<String, Any>> ?: return

    val newMessages = messages.map {
        Chat(
            message = it["message"] as? String ?: "",
            sender_id = it["sender_id"] as? String ?: "",
            receiver_id = it["receiver_id"] as? String ?: "",
            timestamp = it["timestamp"] as? Timestamp ?: Timestamp.now()
        )
    }.sortedBy { it.timestamp }

    chatList.clear()
    chatList.addAll(newMessages)

    val chatEntities = newMessages.map {
        ChatMessageEntity(
            chat_table_name = documentId,
            message = it.message,
            sender_id = it.sender_id,
            receiver_id = it.receiver_id,
            timestamp = it.timestamp.toDate().time
        )
    }

    val uniqueMessages = chatEntities.distinctBy { it.timestamp to it.message }
    chatMessageViewModel.insertAllChats(uniqueMessages)


    // Let DAO handle duplicates by defining correct primary key and insert strategy
    chatMessageViewModel.insertAllChats(uniqueMessages)
}


// Update user status to online/offline
fun updateUserStatus(
    firestore: FirebaseFirestore,
    chatDocId: String,
    userId: String,
    status: String
) {
    val chatDocRef = firestore.collection("chat").document(chatDocId)
    val statusField = "user_status.$userId"
    chatDocRef.update(statusField, status)
        .addOnSuccessListener {
            Log.d("Firestore", "User status updated to $status")
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "Failed to update user status", e)
        }
}

// Reset new_msg_count/unread to 0
fun resetNewMsgCount(
    firestore: FirebaseFirestore,
    userId: String,
    peerId: String
) {
    val chatOverviewRef = firestore.collection("chat_overview").document(userId)

    firestore.runTransaction { transaction ->
        val snapshot = transaction.get(chatOverviewRef)
        if (snapshot.exists()) {
            val peerChats = snapshot.get("peer_chats") as? MutableMap<String, MutableMap<String, Any>>
                ?: mutableMapOf()

            if (peerChats.containsKey(peerId)) {
                peerChats[peerId]?.set("new_msg_count", 0)
                transaction.update(chatOverviewRef, "peer_chats", peerChats)
            }
        }
    }.addOnSuccessListener {
        Log.d("Chat", "new_msg_count reset to 0 for peer $peerId")
    }.addOnFailureListener { e ->
        Log.e("Chat", "Failed to reset new_msg_count", e)
    }
}