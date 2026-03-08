package com.example.loginpage.ui.screens

import android.content.Context
import android.util.Log
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.sharp.Bookmark
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
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
import com.example.loginpage.ui.data.model.Avatar
import com.example.loginpage.ui.data.model.CombinedPost
import com.example.loginpage.ui.data.model.Post
import com.example.loginpage.ui.data.model.UserProfile
import com.example.loginpage.ui.theme.primaryBg
import com.example.loginpage.ui.theme.primaryText
import com.google.firebase.firestore.FirebaseFirestore
import com.example.loginpage.R
import com.example.loginpage.ui.commonFunction.UserIdHelper
import com.example.loginpage.ui.commonFunction.calculateTimeDifference
import com.example.loginpage.ui.commonFunction.loadDrawableByName
import com.example.loginpage.ui.commonFunction.noDataAvailablePrompt
import com.example.loginpage.ui.commonFunction.sharePost
import com.example.loginpage.ui.theme.cardBg
import com.example.loginpage.ui.theme.inactiveGrey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun LikedPost(navController: NavController, drawerState: DrawerState) {
    val context = LocalContext.current
    val user_id = UserIdHelper.getUserId(context).toString()
    val firestore = FirebaseFirestore.getInstance()
    val postList = remember { mutableStateListOf<CombinedPost>() }
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(Unit) {
        fetchLikedPostData(user_id, firestore, postList, context)
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
                        .padding(vertical = 0.dp, horizontal = 0.dp)
                        .clickable{navController.navigate("profile")},
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
                    Text(
                        text = "Liked Post",
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
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 10.dp)
                )
                {
                    if (postList.isEmpty()) {
                        noDataAvailablePrompt("You haven't liked any post yet.")
                    } else {
                        LazyColumn()
                        {
                            items(postList.size)
                            { index ->
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
                                                    postList[index].avatar?.avatar_file_path
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
                                                    text = postList[index].user?.name ?: "Unknown",
                                                    modifier = Modifier.padding(
                                                        horizontal = 10.dp
                                                    ),
                                                    color = colorScheme.primaryText,
                                                    style = TextStyle(
                                                        fontSize = 18.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                )

                                                Text(
                                                    text = calculateTimeDifference(postList[index].post.post_created_at),
                                                    modifier = Modifier.padding(
                                                        horizontal = 10.dp
                                                    ),
                                                    color = colorScheme.primaryText,
                                                    style = TextStyle(fontSize = 16.sp)
                                                )
                                            }

                                            Spacer(modifier = Modifier.weight(1f))

                                            IconButton(
                                                onClick = {
                                                    val updated_post_saved = postList[index].post.post_saved.toMutableList()

                                                    if (updated_post_saved.contains(user_id)) {
                                                        updated_post_saved.remove(user_id)
                                                    } else {
                                                        updated_post_saved.add(user_id)
                                                    }

                                                    toggleForLikedPost(index, updated_post_saved, firestore, postList, "post_saved")
                                                },
                                                modifier = Modifier.padding(bottom = 10.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Sharp.Bookmark,
                                                    contentDescription = "bookmark",
                                                    tint = if (postList[index].post.post_saved.contains(
                                                            user_id
                                                        )
                                                    ) colorScheme.primaryText else colorScheme.inactiveGrey,
                                                    modifier = Modifier.size(32.dp)
                                                )
                                            }
                                        }

                                        Text(
                                            text = postList[index].post.post_content,
                                            modifier = Modifier.padding(10.dp),
                                            color = colorScheme.primaryText,
                                            style = TextStyle(
                                                fontSize = 16.sp,
                                                letterSpacing = 0.5.sp,
                                                lineHeight = 20.sp
                                            )
                                        )

                                        if(postList[index].post.post_image?.isEmpty() == false) {
                                            AsyncImage(
                                                model = postList[index].post.post_image,
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
                                                    val updated_post_liked = postList[index].post.post_liked.toMutableList()

                                                    if (updated_post_liked.contains(user_id)) {
                                                        updated_post_liked.remove(user_id)
                                                    } else {
                                                        updated_post_liked.add(user_id)
                                                    }

                                                    toggleForLikedPost(index, updated_post_liked, firestore, postList, "post_liked")
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.ThumbUp,
                                                    contentDescription = "Like",
                                                    tint = if (postList[index].post.post_liked.contains(
                                                            user_id
                                                        )
                                                    ) colorScheme.primaryText else colorScheme.inactiveGrey,
                                                    modifier = Modifier.size(28.dp)
                                                )
                                            }

                                            Text(
                                                text = " Liked by " + postList[index].post.post_liked.size,
                                                modifier = Modifier.align(Alignment.CenterVertically),
                                                color = colorScheme.primaryText,
                                                style = TextStyle(fontSize = 16.sp)
                                            )

                                            Spacer(modifier = Modifier.weight(1f))

                                            IconButton(
                                                onClick = {
                                                    val text = postList[index].post.post_content
                                                    val imageUrl = postList[index].post.post_image
                                                    CoroutineScope(Dispatchers.Main).launch {
                                                        sharePost(context, text, imageUrl)
                                                    }

                                                }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Share,
                                                    contentDescription = "Share",
                                                    tint = colorScheme.primaryText,
                                                    modifier = Modifier.size(28.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(26.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

//Fetch the posts which are liked by the current user
suspend fun fetchLikedPostData(
    user_id: String,
    firestore: FirebaseFirestore,
    postList: MutableList<CombinedPost>,
    context: Context
) {
    postList.clear()
    try {
        val postDocs = firestore.collection("post")
            .whereArrayContains("post_liked", user_id)
            .get()
            .await()

        for (postDoc in postDocs) {
            val post = postDoc.toObject(Post::class.java)

            val userDoc = firestore.collection("user_profile")
                .whereEqualTo("user_id", post.post_created_by)
                .get()
                .await()

            val user = userDoc.firstOrNull()?.toObject(UserProfile::class.java)

            val avatarDoc = user?.let {
                firestore.collection("avatar")
                    .whereEqualTo("avatar_id", it.avatar_id)
                    .get()
                    .await()
            }

            val avatar = avatarDoc?.firstOrNull()?.toObject(Avatar::class.java)

            if (user != null) {
                postList.add(CombinedPost(post, user, avatar))
            }
        }

        postList.sortByDescending { it.post.post_created_at }

    } catch (e: Exception) {
        Log.d("exception", "Exception in loading the data: $e")
    }
}

//Update saves of the post and unlike the post
fun toggleForLikedPost(
    index: Int,
    updated_save_like: List<String>,
    firestore: FirebaseFirestore,
    postList: MutableList<CombinedPost>,
    field_name: String
){
    firestore.collection("post")
        .document(postList[index].post.post_id)
        .update(field_name, updated_save_like)
        .addOnSuccessListener {
            if (field_name == "post_saved") {
                val current = postList[index]
                val updatedPost = current.post.copy(
                    post_saved = updated_save_like
                )
                val updatedCombinedPost = current.copy(post = updatedPost)
                postList[index] = updatedCombinedPost
            }
            else {
                postList.removeAt(index)
            }
        }
}