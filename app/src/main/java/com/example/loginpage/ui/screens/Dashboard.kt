package com.example.loginpage.ui.screens

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material.icons.sharp.Bookmark
import androidx.compose.material.icons.sharp.FilterAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import com.example.loginpage.ui.commonFunction.SortByFunction
import com.example.loginpage.ui.commonFunction.UserIdHelper
import com.example.loginpage.ui.commonFunction.ThemePrefHelper
import com.example.loginpage.ui.commonFunction.calculateTimeDifference
import com.example.loginpage.ui.commonFunction.loadDrawableByName
import com.example.loginpage.ui.data.model.*
import com.example.loginpage.ui.theme.*
import com.google.firebase.firestore.FirebaseFirestore
import com.example.loginpage.ui.commonFunction.noDataAvailablePrompt
import com.example.loginpage.ui.commonFunction.sharePost
import com.example.loginpage.ui.theme.inactiveGrey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Dashboard(navController: NavController, drawerState: DrawerState) {
    val context = LocalContext.current
    val user_id = UserIdHelper.getUserId(context).toString()
    val firestore = FirebaseFirestore.getInstance()
    val postList = remember { mutableStateListOf<CombinedPost>() }
    val colorScheme = MaterialTheme.colorScheme
    var user_profile by remember { mutableStateOf<UserProfile?>(null) }
    val interestPostList = remember { mutableStateListOf<CombinedPost>()}
    val uninterestPostList = remember { mutableStateListOf<CombinedPost>()}
    var sortSelected = remember { mutableStateOf("Most relevant") }
    val sortOptions = listOf(
        "Most relevant" to { combinePost(postList, interestPostList, uninterestPostList) },
        "Most liked" to { postList.sortByDescending { it.post.post_liked.size } },
        "Most saved" to { postList.sortByDescending { it.post.post_saved.size } },
        "Newest first" to { postList.sortByDescending { it.post.post_created_at } }
    )
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true )
    var showSheet by remember { mutableStateOf(false) }
    var selectedCategories by remember { mutableStateOf(listOf<String>()) }
    var selectedCampuses by remember { mutableStateOf(listOf<String>()) }
    var followersOnly by remember { mutableStateOf(false) }
    var isDarkTheme by remember { mutableStateOf( ThemePrefHelper.isDark(context) ) }

    LaunchedEffect(Unit) {
        //fetch post data
        val userDocs = firestore.collection("user_profile")
            .whereEqualTo("user_id", user_id)
            .get()
            .await()

        user_profile = userDocs.firstOrNull()?.toObject(UserProfile::class.java)

        if (user_profile != null) {
            fetchPosts(firestore, user_id, user_profile, postList, interestPostList, uninterestPostList)
        }

        ThemePrefHelper.loadFromFirestore(context) { loaded ->
            isDarkTheme = loaded
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
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
                ) {
                    Text(
                        text = "Feed",
                        style = TextStyle(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primaryText
                        )
                    )

                    if (!postList.isEmpty()) {
                        Spacer(modifier = Modifier.weight(1f))

                        IconButton(onClick = { showSheet = true }) {
                            Icon(
                                imageVector = Icons.Sharp.FilterAlt,
                                contentDescription = "Filter Icon",
                                tint = colorScheme.primaryText,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        if (showSheet) {
                            ModalBottomSheet(
                                containerColor = colorScheme.primaryBg,
                                onDismissRequest = { showSheet = false },
                                sheetState = sheetState
                            ) {
                                DashboardBottomFilterContent(
                                    selectedCategories,
                                    selectedCampuses,
                                    followersOnly
                                ) { newCategories, newCampuses, newFollowersOnly ->
                                    selectedCategories = newCategories
                                    selectedCampuses = newCampuses
                                    followersOnly = newFollowersOnly
                                    showSheet = false
                                }
                            }
                        }

                        SortByFunction(sortOptions, sortSelected)
                    }
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
                        noDataAvailablePrompt("No post loaded")
                    } else {
                        LazyColumn()
                        {
                            items(postList.size)
                            { index ->
                                if(
                                    (selectedCategories.size > 0 && postList[index].post.post_category in selectedCategories) ||
                                    (selectedCampuses.size > 0 && postList[index].user?.primary_campus in selectedCampuses) ||
                                    (followersOnly && postList[index].user?.followers?.contains(user_id) == true) ||
                                    (selectedCampuses.size == 0 && selectedCategories.size==0 && !followersOnly)
                                ) {
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
                                                    .padding(
                                                        start = 12.dp,
                                                        top = 12.dp,
                                                        end = 0.dp,
                                                        bottom = 12.dp
                                                    ),
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

                                                Column()
                                                {
                                                    Text(
                                                        text = postList[index].user?.name
                                                            ?: "Unknown",
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
                                                        modifier = Modifier.padding(horizontal = 10.dp),
                                                        color = colorScheme.primaryText,
                                                        style = TextStyle(fontSize = 16.sp)
                                                    )
                                                }

                                                Spacer(modifier = Modifier.weight(1f))

                                                IconButton(
                                                    onClick = {
                                                        val updated_post_saved =
                                                            postList[index].post.post_saved.toMutableList()

                                                        if (updated_post_saved.contains(user_id)) {
                                                            updated_post_saved.remove(user_id)
                                                        } else {
                                                            updated_post_saved.add(user_id.toString())
                                                        }

                                                        saveLikeFeedPost(
                                                            index,
                                                            updated_post_saved,
                                                            firestore,
                                                            postList,
                                                            "post_saved"
                                                        )
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

                                            if (postList[index].post.post_image?.isEmpty() == false) {
                                                AsyncImage(
                                                    model = postList[index].post.post_image,
                                                    contentDescription = "Post Image",
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
                                                        val updated_post_liked =
                                                            postList[index].post.post_liked.toMutableList()

                                                        if (updated_post_liked.contains(user_id)) {
                                                            updated_post_liked.remove(user_id)
                                                        } else {
                                                            updated_post_liked.add(user_id.toString())
                                                        }

                                                        saveLikeFeedPost(
                                                            index,
                                                            updated_post_liked,
                                                            firestore,
                                                            postList,
                                                            "post_liked"
                                                        )
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

                            item {
                                if(
                                    !postList.any {
                                        (selectedCategories.size > 0 && it.post.post_category in selectedCategories) ||
                                                (selectedCampuses.size > 0 && it.user?.primary_campus in selectedCampuses) ||
                                                (followersOnly && it.user?.followers?.contains(user_id) == true) ||
                                                (selectedCampuses.size == 0 && selectedCategories.size==0 && !followersOnly)
                                    }
                                ) {
                                    noDataAvailablePrompt("No post found for your preferences")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

//Function to fetch posts data from firebase
suspend fun fetchPosts(
    firestore: FirebaseFirestore,
    user_id: String,
    user_profile: UserProfile?,
    postList: SnapshotStateList<CombinedPost>,
    interestPostList: SnapshotStateList<CombinedPost>,
    uninterestPostList: SnapshotStateList<CombinedPost>
) {
    //Clear the lists
    interestPostList.clear()
    uninterestPostList.clear()

    //Fetch post data with its user and avatar
    val postDocs = firestore.collection("post").get().await()
    for (postDoc in postDocs) {
        val post = postDoc.toObject(Post::class.java)

        val userDocs = firestore.collection("user_profile")
            .whereEqualTo("user_id", post.post_created_by)
            .get()
            .await()

        val user = userDocs.firstOrNull()?.toObject(UserProfile::class.java) ?: continue

        val avatarDocs = firestore.collection("avatar")
            .whereEqualTo("avatar_id", user.avatar_id)
            .get()
            .await()

        val avatar = avatarDocs.firstOrNull()?.toObject(Avatar::class.java)

        val combinedPost = CombinedPost(post, user, avatar)

        //Store the posts in interested and uninterested post based on user interest and follower
        if ((user_profile?.interest?.contains(post.post_category) == true)
            || (user.followers.contains(user_id))
        ) {
            interestPostList.add(combinedPost)
        } else {
            uninterestPostList.add(combinedPost)
        }
    }

    combinePost(postList, interestPostList, uninterestPostList)
}

//Function to sort and combine interested and uninterested post
fun combinePost(
    postList: SnapshotStateList<CombinedPost>,
    interestPostList: SnapshotStateList<CombinedPost>,
    uninterestPostList: SnapshotStateList<CombinedPost>
) {
    //Sort both the lists based on timestamp
    interestPostList.sortByDescending { it.post.post_created_at }
    uninterestPostList.sortByDescending { it.post.post_created_at }

    postList.clear()
    postList.addAll(interestPostList + uninterestPostList)
}

//Function to update the like and save of a post
fun saveLikeFeedPost(
    index: Int,
    updated_save_like: List<String>,
    firestore: FirebaseFirestore,
    postList: MutableList<CombinedPost>,
    field_name: String
) {
    firestore.collection("post")
        .document(postList[index].post.post_id)
        .update(field_name, updated_save_like)
        .addOnSuccessListener {
            val current = postList[index]
            val updatedPost = current.post.copy(
                post_liked = if (field_name == "post_liked") updated_save_like else current.post.post_liked,
                post_saved = if (field_name == "post_saved") updated_save_like else current.post.post_saved
            )
            val updatedCombinedPost = current.copy(post = updatedPost)
            postList[index] = updatedCombinedPost
        }
}