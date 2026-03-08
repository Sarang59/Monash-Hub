package com.example.loginpage.ui.screens

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
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
import com.example.loginpage.ui.commonFunction.SortByFunction
import com.example.loginpage.ui.commonFunction.UserIdHelper
import com.example.loginpage.ui.commonFunction.loadDrawableByName
import com.example.loginpage.ui.commonFunction.noDataAvailablePrompt
import com.example.loginpage.ui.data.model.Avatar
import com.example.loginpage.ui.data.model.CombineProfileAvatar
import com.example.loginpage.ui.data.model.UserProfile
import com.example.loginpage.ui.theme.Transparent
import com.example.loginpage.ui.theme.cardBg
import com.example.loginpage.ui.theme.errorText
import com.example.loginpage.ui.theme.inactiveGrey
import com.example.loginpage.ui.theme.primaryBg
import com.example.loginpage.ui.theme.primaryText
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun Followers(navController: NavController, drawerState: DrawerState) {
    val context = LocalContext.current
    val user_id = UserIdHelper.getUserId(context).toString()
    var name by remember { mutableStateOf("") }
    val firestore = FirebaseFirestore.getInstance()
    val followersList = remember { mutableStateListOf<CombineProfileAvatar>() }
    val colorScheme = MaterialTheme.colorScheme
    var sortSelected = remember { mutableStateOf("A to Z") }
    val sortOptions = listOf(
        "A to Z" to { followersList.sortBy { it.user.name } },
        "Z to A" to { followersList.sortByDescending { it.user.name } },
    )

    LaunchedEffect(user_id) {
        try {
            //Fetch user profile for those users who follow the current user
            val userSnapshot = firestore.collection("user_profile")
                .whereEqualTo("user_id", user_id)
                .get()
                .await()

            val userDoc = userSnapshot.firstOrNull()
            val followersIds = userDoc?.get("followers") as? List<String>

            if (!followersIds.isNullOrEmpty()) {
                for (followerId in followersIds) {
                    val followerSnapshot = firestore.collection("user_profile")
                        .whereEqualTo("user_id", followerId)
                        .get()
                        .await()

                    val userProfile = followerSnapshot.firstOrNull()?.toObject(UserProfile::class.java)

                    if (userProfile != null) {
                        val avatarSnapshot = firestore.collection("avatar")
                            .whereEqualTo("avatar_id", userProfile.avatar_id)
                            .get()
                            .await()

                        val avatar = avatarSnapshot.firstOrNull()?.toObject(Avatar::class.java)

                        followersList.add(CombineProfileAvatar(userProfile, avatar))
                    }
                }

                followersList.sortBy { it.user.name }
            }
        } catch (e: Exception) {
            Log.d("exception", "Exception in loading the data: $e")
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
                        text = "Followers",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primaryText
                        )
                    )

                    if(!followersList.isEmpty()) {
                        Spacer(modifier = Modifier.weight(1f))

                        SortByFunction(sortOptions, sortSelected)
                    }
                }

                HorizontalDivider(
                    thickness = 2.dp,
                    color = colorScheme.primaryText,
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                )

                if(followersList.isEmpty()) {
                    noDataAvailablePrompt("No followers yet")
                }
                else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                    )
                    {
                        //Search follower by name
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            placeholder = {
                                Text(
                                    "Search by follower's name",
                                    color = colorScheme.inactiveGrey,
                                    fontSize = 18.sp
                                )
                            },
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
                    LazyColumn()
                    {
                        items(followersList.size) { index ->
                            if(followersList[index].user.name.contains(name, ignoreCase = true)) {
                                val userId = followersList[index].user.user_id
                                Card(
                                    onClick = { navController.navigate("peerProfile/$userId") },
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
                                            .padding(10.dp)
                                            .background(colorScheme.cardBg)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Image(
                                                painter = loadDrawableByName(
                                                    context,
                                                    followersList[index].avatar?.avatar_file_path
                                                ),
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .size(65.dp)
                                                    .clip(CircleShape)
                                            )

                                            Column(
                                                modifier = Modifier
                                                    .padding(horizontal = 8.dp)
                                            ) {
                                                Text(
                                                    text = followersList[index].user.name,
                                                    color = colorScheme.primaryText,
                                                    style = TextStyle(
                                                        fontSize = 18.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                )

                                                val course = followersList[index].user.course_name
                                                val truncatedCourse =
                                                    if (course.length > 20) course.take(20) + "..." else course

                                                Text(
                                                    text = "$truncatedCourse\n${followersList[index].user.primary_campus}",
                                                    color = colorScheme.primaryText,
                                                    style = TextStyle(fontSize = 16.sp)
                                                )
                                            }

                                            Spacer(modifier = Modifier.weight(1f))

                                            IconButton(
                                                onClick = {
                                                    val updated_follower =
                                                        followersList[index].user.followers.toMutableList()

                                                    if (updated_follower.contains(user_id)) {
                                                        updated_follower.remove(user_id)
                                                    } else {
                                                        updated_follower.add(user_id)
                                                    }

                                                    updateFollowings(
                                                        index,
                                                        updated_follower,
                                                        firestore,
                                                        followersList
                                                    )
                                                }
                                            ) {
                                                Icon(
                                                    painter = painterResource(
                                                        id = if (followersList[index].user.followers.contains(
                                                                user_id
                                                            )
                                                        ) R.drawable.ic_unfollow_icon else R.drawable.ic_follow_icon
                                                    ),
                                                    contentDescription = "",
                                                    tint = Color.Unspecified,
                                                    modifier = Modifier.size(45.dp)
                                                        .align(Alignment.CenterVertically)
                                                )
                                            }
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

//Update Followings of the current user
fun updateFollowings(
    index: Int,
    updated_followers: List<String>,
    firestore: FirebaseFirestore,
    followersList: MutableList<CombineProfileAvatar>
) {
    firestore.collection("user_profile")
        .whereEqualTo("user_id", followersList[index].user.user_id)
        .get()
        .addOnSuccessListener { documents ->
            val document = documents.firstOrNull()
            if (document != null) {
                val docId = document.id
                firestore.collection("user_profile")
                    .document(docId)
                    .update("followers", updated_followers)
                    .addOnSuccessListener {
                        val current = followersList[index]
                        val updatedUser = current.user.copy(
                            followers = updated_followers
                        )
                        val updatedCombinedUser = current.copy(user = updatedUser)
                        followersList[index] = updatedCombinedUser
                    }
            }
        }
}