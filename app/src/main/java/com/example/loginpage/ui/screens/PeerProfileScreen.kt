package com.example.loginpage.ui.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.sharp.Bookmark
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.loginpage.R
import com.example.loginpage.ui.commonFunction.UserIdHelper
import com.example.loginpage.ui.commonFunction.calculateTimeDifference
import com.example.loginpage.ui.commonFunction.loadDrawableByName
import com.example.loginpage.ui.commonFunction.noDataAvailablePrompt
import com.example.loginpage.ui.data.model.Avatar
import com.example.loginpage.ui.data.model.CombineProfileAvatar
import com.example.loginpage.ui.data.model.Post
import com.example.loginpage.ui.data.model.UserProfile
import com.example.loginpage.ui.theme.cardBg
import com.example.loginpage.ui.theme.inactiveGrey
import com.example.loginpage.ui.theme.primaryBg
import com.example.loginpage.ui.theme.primaryText
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun PeerProfileScreen(navController: NavController, drawerState: DrawerState, peerUserId : String) {

    val context = LocalContext.current
    val user_id = UserIdHelper.getUserId(context).toString()
    var name by remember { mutableStateOf("") }
    val firestore = FirebaseFirestore.getInstance()
    val posts = remember { mutableStateListOf<Post>() }
    var user_profile by remember { mutableStateOf<UserProfile?>(null) }
    var avatar by remember { mutableStateOf<Avatar?>(null) }
    val colorScheme = MaterialTheme.colorScheme
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        //Fetch peer's profile and posts
        firestore.collection("user_profile")
            .whereEqualTo("user_id", peerUserId)
            .get()
            .addOnSuccessListener { userDocs ->
                val fetchedUser = userDocs.firstOrNull()?.toObject(UserProfile::class.java)
                user_profile = fetchedUser

                if (fetchedUser != null) {
                    firestore.collection("post")
                        .whereEqualTo("post_created_by", peerUserId)
                        .get()
                        .addOnSuccessListener { postDocs ->
                            for (postDoc in postDocs) {
                                val post = postDoc.toObject(Post::class.java)
                                posts.add(post)
                            }
                            posts.sortByDescending { it.post_created_at }
                        }
                        .addOnFailureListener {
                            Log.e("Error", "Error fetching post data")
                        }

                    firestore.collection("avatar")
                        .whereEqualTo("avatar_id", fetchedUser.avatar_id)
                        .get()
                        .addOnSuccessListener { avatarDocs ->
                            val fetchedAvatar = avatarDocs.firstOrNull()?.toObject(Avatar::class.java)
                            avatar = fetchedAvatar
                        }
                        .addOnFailureListener {
                            Log.e("Error", "Error fetching post data")
                        }
                } else {
                    Log.e("Error", "Error fetching post data")
                }
            }
            .addOnFailureListener {
                Log.e("Error", "Error fetching post data")
            }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = colorScheme.primaryBg
    ) {
        Column() {
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
                        .padding(vertical = 0.dp, horizontal = 0.dp)
                        .clickable{navController.navigate("peer")},
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Notification",
                        tint = colorScheme.primaryText,
                        modifier = Modifier.size(28.dp)
                    )

                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = user_profile?.name ?: "",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primaryText
                        )
                    )
                }

                HorizontalDivider(
                    thickness = 2.dp,
                    color = colorScheme.primaryText,
                    modifier = Modifier
                        .fillMaxWidth().padding(vertical = 16.dp),
                )

                if(avatar != null) {
                    Row(
                        modifier = Modifier
                            .padding(vertical = 10.dp)
                    )
                    {
                        Button(
                            modifier = Modifier.width(150.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primaryText),
                            onClick = {

                                navController.navigate("chatDetailed/${peerUserId}/${user_profile?.name}/${avatar!!.avatar_file_path}")
//                                navController.navigate("chatDetailed")
                            }
                        ) {
                            Text(
                                "Chat",
                                color = colorScheme.primaryBg,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Button(
                            modifier = Modifier.width(150.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primaryText),
                            onClick = {
                                scope.launch {
                                    val updated_follower = user_profile?.followers?.toMutableList()

                                    if (updated_follower?.contains(user_id) == true) {
                                        updated_follower.remove(user_id)
                                    } else {
                                        updated_follower?.add(user_id)
                                    }

                                    val result = updateFollower(peerUserId, updated_follower ?: listOf(), firestore)

                                    if (result == "success") {
                                        val updatedUserProfile = user_profile?.copy(
                                            followers = updated_follower ?: listOf()
                                        )
                                        user_profile = updatedUserProfile
                                    }
                                }
                            }
                        ) {
                            Text(
                                text = if (user_profile?.followers?.contains(user_id) ?: false) "Unfollow" else "Follow",
                                color = colorScheme.primaryBg,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                    )
                    {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Profile Summary",
                                style = TextStyle(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colorScheme.primaryText
                                )
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Card(
                                shape = RectangleShape,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(6.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                                colors = CardDefaults.cardColors(containerColor = colorScheme.cardBg)
                            )
                            {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp)
                                        .background(colorScheme.cardBg)
                                )
                                {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    )
                                    {
                                        Image(
                                            painter = loadDrawableByName(
                                                context,
                                                avatar?.avatar_file_path
                                            ),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(60.dp)
                                                .clip(CircleShape)
                                        )

                                        Column(
                                            modifier = Modifier
                                                .background(colorScheme.cardBg)
                                        )
                                        {
                                            Text(
                                                text = user_profile?.course_name ?: "",
                                                modifier = Modifier.padding(
                                                    horizontal = 10.dp,
                                                ),
                                                color = colorScheme.primaryText,
                                                style = TextStyle(
                                                    fontSize = 18.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )

                                            Text(
                                                text = "${user_profile?.primary_campus ?: "Clayton"} Campus",
                                                modifier = Modifier.padding(
                                                    horizontal = 8.dp,

                                                    ),
                                                color = colorScheme.primaryText,
                                                style = TextStyle(fontSize = 16.sp)
                                            )

                                            Text(
                                                text = "Batch: ${user_profile?.graduation_year}",
                                                modifier = Modifier.padding(
                                                    horizontal = 8.dp,

                                                    ),
                                                color = colorScheme.primaryText,
                                                style = TextStyle(fontSize = 16.sp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = user_profile?.profile_statement
                                            ?: "No profile statement added",
                                        modifier = Modifier.padding(10.dp),
                                        color = colorScheme.primaryText,
                                        style = TextStyle(
                                            fontSize = 16.sp,
                                            letterSpacing = 0.5.sp,
                                            lineHeight = 20.sp
                                        )
                                    )

                                    if (user_profile?.company_name?.trim() != "\"\"" && user_profile?.company_name?.trim()?.isEmpty() == false) {
                                        val timestamp: Timestamp? = user_profile?.doj
                                        val sdf = SimpleDateFormat("MMMM, yyyy", Locale.getDefault())
                                        var formattedDoj = ""

                                        timestamp?.toDate()?.let { date ->
                                            formattedDoj = sdf.format(date)
                                        }

                                        Text(
                                            text = "Working at ${user_profile?.company_name}${if(user_profile?.current_role?.trim() != "\"\"" && user_profile?.current_role?.trim()?.isEmpty() == false) " as ${user_profile?.current_role}" else ""}${if(user_profile?.doj != null) " since ${formattedDoj}" else ""}.",
                                            modifier = Modifier.padding(10.dp),
                                            color = colorScheme.primaryText,
                                            style = TextStyle(
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(30.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            )
                            {
                                Text(
                                    text = "Recent Posts",
                                    style = TextStyle(
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colorScheme.primaryText
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        if(posts.isEmpty()){
                            item{
                                noDataAvailablePrompt("Peer has not created any post yet.")
                            }
                        } else{
                            items(posts.size)
                            { index ->
                                Card(
                                    shape = RectangleShape,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .shadow(6.dp),
                                    elevation = CardDefaults.cardElevation(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = colorScheme.cardBg)
                                )
                                {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(colorScheme.cardBg)
                                            .padding(10.dp)
                                    )
                                    {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(start = 12.dp, top = 12.dp, end = 0.dp, bottom = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        )
                                        {
                                            Image(
                                                painter = loadDrawableByName(
                                                    context,
                                                    avatar?.avatar_file_path
                                                ),
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .size(60.dp)
                                                    .clip(CircleShape)
                                            )

                                            Column(
                                                modifier = Modifier
                                                    .background(colorScheme.cardBg)
                                            )
                                            {
                                                Text(
                                                    text = user_profile?.name ?: "Unknown",
                                                    modifier = Modifier.padding(horizontal = 10.dp),
                                                    color = colorScheme.primaryText,
                                                    style = TextStyle(
                                                        fontSize = 18.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                )

                                                Text(
                                                    text = calculateTimeDifference(posts[index].post_created_at),
                                                    modifier = Modifier.padding(horizontal = 10.dp),
                                                    color = colorScheme.primaryText,
                                                    style = TextStyle(fontSize = 16.sp)
                                                )
                                            }

                                            Spacer(modifier = Modifier.weight(1f))

                                            IconButton(
                                                onClick = {
                                                    val updated_post_saved = posts[index].post_saved.toMutableList()

                                                    if (updated_post_saved.contains(user_id)) {
                                                        updated_post_saved.remove(user_id)
                                                    } else {
                                                        updated_post_saved.add(user_id)
                                                    }

                                                    saveLikePeerProfilePost(index, updated_post_saved, firestore, posts, "post_saved")
                                                },
                                                modifier = Modifier.padding(bottom = 10.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Sharp.Bookmark,
                                                    contentDescription = "bookmark",
                                                    tint = if (posts[index].post_saved.contains(
                                                            user_id
                                                        )
                                                    ) primaryText else inactiveGrey,
                                                    modifier = Modifier.size(32.dp)
                                                )
                                            }
                                        }

                                        Text(
                                            text = posts[index].post_content,
                                            modifier = Modifier.padding(10.dp),
                                            color = colorScheme.primaryText,
                                            style = TextStyle(
                                                fontSize = 16.sp,
                                                letterSpacing = 0.5.sp,
                                                lineHeight = 20.sp
                                            )
                                        )

                                        if(posts[index].post_image?.isEmpty() == false) {
                                            AsyncImage(
                                                model = posts[index].post_image,
                                                contentDescription = "Loaded image",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(250.dp)
                                                    .padding(horizontal = 10.dp)
                                            )
                                        }

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 12.dp, bottom = 8.dp)
                                        ) {
                                            IconButton(
                                                onClick = {
                                                    val updated_post_liked = posts[index].post_liked.toMutableList()

                                                    if (updated_post_liked.contains(user_id)) {
                                                        updated_post_liked.remove(user_id)
                                                    } else {
                                                        updated_post_liked.add(user_id)
                                                    }

                                                    saveLikePeerProfilePost(index, updated_post_liked, firestore, posts, "post_liked")
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.ThumbUp,
                                                    contentDescription = "Like",
                                                    tint = if (posts[index].post_liked.contains(
                                                            user_id
                                                        )
                                                    ) colorScheme.primaryText else colorScheme.inactiveGrey,
                                                    modifier = Modifier.size(28.dp)
                                                )
                                            }

                                            Text(
                                                text = " Liked by " + posts[index].post_liked.size,
                                                modifier = Modifier.align(Alignment.CenterVertically),
                                                color = colorScheme.primaryText,
                                                style = TextStyle(fontSize = 16.sp)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(26.dp))
                            }
                        }
                    }
                } else {
                    noDataAvailablePrompt("No data loaded")
                }
            }
        }

    }
}

//Update like and save of peer's posts
fun saveLikePeerProfilePost(
    index: Int,
    updated_save_like: List<String>,
    firestore: FirebaseFirestore,
    posts: SnapshotStateList<Post>,
    field_name: String
) {
    firestore.collection("post")
        .document(posts[index].post_id)
        .update(field_name, updated_save_like)
        .addOnSuccessListener {
            val post = posts[index]
            val updatedPost = post.copy(
                post_liked = if (field_name == "post_liked") updated_save_like else post.post_liked,
                post_saved = if (field_name == "post_saved") updated_save_like else post.post_saved
            )
            posts[index] = updatedPost
        }
}

//Update follower of the peer
suspend fun updateFollower(
    peerUserId: String,
    updated_followers: List<String>,
    firestore: FirebaseFirestore,
): String {
    return try {
        val querySnapshot = firestore.collection("user_profile")
            .whereEqualTo("user_id", peerUserId)
            .get()
            .await()

        val document = querySnapshot.firstOrNull() ?: return "error"
        val doc_id = document.id

        firestore.collection("user_profile")
            .document(doc_id)
            .update("followers", updated_followers)
            .await()

        "success"
    } catch (e: Exception) {
        "error"
    }
}