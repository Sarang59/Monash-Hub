package com.example.loginpage.ui.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.sharp.Bookmark
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
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
import com.example.loginpage.ui.data.model.Post
import com.example.loginpage.ui.data.model.UserProfile
import com.example.loginpage.ui.theme.inactiveGrey
import com.example.loginpage.ui.theme.primaryBg
import com.example.loginpage.ui.theme.primaryText
import com.google.firebase.firestore.FirebaseFirestore
import com.example.loginpage.R
import com.example.loginpage.ui.commonFunction.UserIdHelper
import com.example.loginpage.ui.commonFunction.calculateTimeDifference
import com.example.loginpage.ui.commonFunction.loadDrawableByName
import com.example.loginpage.ui.commonFunction.noDataAvailablePrompt
import com.example.loginpage.ui.commonFunction.sharePost
import com.example.loginpage.ui.data.model.Avatar
import com.example.loginpage.ui.data.model.CombinedPost
import com.example.loginpage.ui.theme.Transparent
import com.example.loginpage.ui.theme.cardBg
import com.example.loginpage.ui.theme.errorText
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Profile(navController: NavController, drawerState: DrawerState) {
    val context = LocalContext.current
    val user_id = UserIdHelper.getUserId(context).toString()
    val firestore = FirebaseFirestore.getInstance()
    var posts = remember { mutableStateListOf<Post>() }
    var user_profile by remember { mutableStateOf<UserProfile?>(null) }
    var avatar by remember { mutableStateOf<Avatar?>(null) }
    val allInterests = listOf(
        "Technology",
        "Finance",
        "Design & Arts",
        "Engineering",
        "Health & Medicine",
        "Sports",
        "Volunteering",
        "Career Advice",
        "Startups / Entrepreneurship",
        "Study",
        "Internships/Jobs",
        "Buying/Selling",
    )
    var selectedInterest by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        //Fetch user profile
        firestore.collection("user_profile")
            .whereEqualTo("user_id", user_id)
            .get()
            .addOnSuccessListener { userDocs ->
                val fetchedUser = userDocs.firstOrNull()?.toObject(UserProfile::class.java)
                user_profile = fetchedUser

                if (fetchedUser != null) {
                    firestore.collection("post")
                        .whereEqualTo("post_created_by", user_id)
                        .get()
                        .addOnSuccessListener { postDocs ->
                            for (postDoc in postDocs) {
                                val post = postDoc.toObject(Post::class.java)
                                posts.add(post)
                            }

                            posts.sortByDescending { it.post_created_at }
                        }

                    firestore.collection("avatar")
                        .whereEqualTo("avatar_id", fetchedUser.avatar_id)
                        .get()
                        .addOnSuccessListener { avatarDocs ->
                            var fetchedAvatar = avatarDocs.firstOrNull()?.toObject(Avatar::class.java)
                            avatar = fetchedAvatar
                        }
                } else {
                    Log.e("Error", "Error fetching data")
                }
            }
            .addOnFailureListener {
                Log.e("Error", "Error fetching data")
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
                        .padding(vertical = 0.dp, horizontal = 0.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your Profile",
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
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                if(avatar != null) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(10.dp))
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
                                            .padding(12.dp)
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
                                                .weight(1f)
                                        )
                                        {
                                            Text(
                                                text = user_profile?.name ?: "Unknown",
                                                modifier = Modifier.padding(
                                                    horizontal = 8.dp,
                                                ),
                                                color = colorScheme.primaryText,
                                                style = TextStyle(
                                                    fontSize = 18.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )

                                            Text(
                                                text = user_profile?.course_name
                                                    ?: "Course not provided",
                                                modifier = Modifier.padding(
                                                    horizontal = 8.dp,
                                                ),
                                                color = colorScheme.primaryText,
                                                style = TextStyle(fontSize = 16.sp)
                                            )

                                            Text(
                                                text = "${user_profile?.primary_campus ?: "Clayton"} Campus",
                                                modifier = Modifier.padding(
                                                    horizontal = 8.dp
                                                ),
                                                color = colorScheme.primaryText,
                                                style = TextStyle(fontSize = 16.sp)
                                            )

                                            Text(
                                                text = "Batch: ${user_profile?.graduation_year}",
                                                modifier = Modifier.padding(
                                                    horizontal = 8.dp
                                                ),
                                                color = colorScheme.primaryText,
                                                style = TextStyle(fontSize = 16.sp)
                                            )
                                        }

                                        IconButton(
                                            onClick = {
                                                navController.navigate("editProfile")
                                            }
                                        ){
                                            Icon(
                                                imageVector = Icons.Filled.Edit,
                                                contentDescription = "Edit Icon",
                                                tint = colorScheme.primaryText,
                                                modifier = Modifier.size(30.dp)
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

                            Spacer(modifier = Modifier.height(30.dp))

                            Text(
                                text = "Interests",
                                style = TextStyle(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colorScheme.primaryText
                                ),
                                modifier = Modifier.padding(bottom = 10.dp)
                            )

                            if(allInterests.size != user_profile?.interest?.size) {
                                ExposedDropdownMenuBox(
                                    expanded = expanded,
                                    onExpandedChange = { expanded = !expanded }
                                ) {
                                    OutlinedTextField(
                                        value = selectedInterest,
                                        onValueChange = { },
                                        readOnly = true,
                                        placeholder = {
                                            Text(
                                                "Choose your Interest",
                                                color = colorScheme.inactiveGrey
                                            )
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth().padding(vertical = 6.dp).menuAnchor(),
                                        trailingIcon = {
                                            Icon(
                                                imageVector = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                                                contentDescription = "Dropdown Arrow",
                                                tint = if (expanded) colorScheme.primaryText else colorScheme.inactiveGrey,
                                                modifier = Modifier.size(40.dp)
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
                                        shape = RectangleShape
                                    )

                                    ExposedDropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false },
                                        modifier = Modifier
                                            .widthIn(min = 100.dp, max = 400.dp)
                                            .heightIn(max = 200.dp).background(colorScheme.primaryBg)
                                    ) {
                                        allInterests
                                            .filter {
                                                it !in (user_profile?.interest ?: emptyList())
                                            }
                                            .forEach { interest ->
                                                DropdownMenuItem(
                                                    text = { Text(interest) },
                                                    onClick = {
                                                        scope.launch {
                                                            val result = addInterest(
                                                                user_id,
                                                                interest,
                                                                firestore
                                                            )

                                                            if (result == "success") {
                                                                user_profile = user_profile?.copy(
                                                                    interest = user_profile?.interest
                                                                        ?.plus(interest)
                                                                        ?: listOf(interest)
                                                                )
                                                            }
                                                        }
                                                        expanded = false
                                                    }
                                                )
                                            }
                                    }
                                }
                            }


                            user_profile?.interest?.forEach {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 5.dp)
                                        .background(colorScheme.cardBg),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = it,
                                        style = TextStyle(
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = colorScheme.primaryText
                                        ),
                                        modifier = Modifier
                                            .background(colorScheme.cardBg)
                                            .padding(15.dp)
                                    )

                                    user_profile?.interest?.size?.let { it1 ->
                                        if(it1 > 1) {
                                            Spacer(modifier = Modifier.weight(1f))
                                            Column(
                                                modifier = Modifier.padding(end = 0.dp)
                                            ) {
                                                IconButton(
                                                    onClick = {
                                                        scope.launch {
                                                            val removed_interest = it
                                                            val result = removeInterest(
                                                                user_id,
                                                                removed_interest,
                                                                firestore
                                                            )

                                                            if (result == "success") {
                                                                user_profile = user_profile?.copy(
                                                                    interest = user_profile?.interest
                                                                        ?.filterNot { it == removed_interest }
                                                                        ?: emptyList()
                                                                )
                                                            }
                                                        }
                                                    }
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Filled.Delete,
                                                        contentDescription = "Delete Icon",
                                                        tint = colorScheme.primaryText,
                                                        modifier = Modifier
                                                            .size(26.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(30.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            )
                            {
                                Button(
                                    onClick = {
                                        navController.navigate("profileLikedPost")
                                    },
                                    colors = ButtonDefaults.elevatedButtonColors(
                                        containerColor = colorScheme.primaryText
                                    ),
                                    modifier = Modifier.width(180.dp)
                                ) {
                                    Text(
                                        "Liked Posts",
                                        color = colorScheme.primaryBg,
                                        fontSize = 18.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        navController.navigate("profileSavedPost")
                                    },
                                    colors = ButtonDefaults.elevatedButtonColors(
                                        containerColor = colorScheme.primaryText
                                    ),
                                    modifier = Modifier.width(180.dp)
                                ) {
                                    Text(
                                        "Saved Posts",
                                        color = colorScheme.primaryBg,
                                        fontSize = 18.sp
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            )
                            {
                                Button(
                                    onClick = {
                                        navController.navigate("profileStatistics")
                                    },
                                    colors = ButtonDefaults.elevatedButtonColors(
                                        containerColor = colorScheme.primaryText
                                    ),
                                    modifier = Modifier.width(200.dp)
                                ) {
                                    Text(
                                        "Profile Statistics",
                                        color = colorScheme.primaryBg,
                                        fontSize = 18.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        navController.navigate("profileFollowers")
                                    },
                                    colors = ButtonDefaults.elevatedButtonColors(
                                        containerColor = colorScheme.primaryText
                                    ),
                                    modifier = Modifier.width(180.dp)
                                ) {
                                    Text(
                                        "Followers",
                                        color = colorScheme.primaryBg,
                                        fontSize = 18.sp
                                    )
                                }
                            }


                            Spacer(modifier = Modifier.height(13.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            )
                            {
                                Text(
                                    text = "Your Posts",
                                    style = TextStyle(
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colorScheme.primaryText
                                    )
                                )
                            }

                        }

                        if(posts.isEmpty()){
                            item{
                                noDataAvailablePrompt("You haven't created any post yet.")
                            }
                        } else{
                            items(posts.size)
                            { index ->
                                Card(
                                    shape = RectangleShape,
                                    modifier = Modifier
                                        .fillMaxWidth().shadow(6.dp),
                                    elevation = CardDefaults.cardElevation(10.dp),
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
                                                .padding(
                                                    start = 12.dp,
                                                    top = 12.dp,
                                                    end = 0.dp,
                                                    bottom = 12.dp
                                                ),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
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
                                                    text = calculateTimeDifference(posts[index].post_created_at),
                                                    modifier = Modifier.padding(
                                                        horizontal = 10.dp
                                                    ),
                                                    color = colorScheme.primaryText,
                                                    style = TextStyle(fontSize = 16.sp)
                                                )
                                            }

                                            Spacer(modifier = Modifier.weight(1F))

                                            IconButton(
                                                onClick = {
                                                    val updated_post_saved =
                                                        posts[index].post_saved.toMutableList()

                                                    if (updated_post_saved.contains(user_id)) {
                                                        updated_post_saved.remove(user_id)
                                                    } else {
                                                        updated_post_saved.add(user_id)
                                                    }

                                                    saveLikeProfilePost(
                                                        index,
                                                        updated_post_saved,
                                                        firestore,
                                                        posts,
                                                        "post_saved"
                                                    )
                                                },
                                                modifier = Modifier.padding(bottom = 10.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Sharp.Bookmark,
                                                    contentDescription = "bookmark",
                                                    tint = if (posts[index].post_saved.contains(
                                                            user_id
                                                        )
                                                    ) colorScheme.primaryText else colorScheme.inactiveGrey,
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
                                                    val updated_post_liked =
                                                        posts[index].post_liked.toMutableList()

                                                    if (updated_post_liked.contains(user_id)) {
                                                        updated_post_liked.remove(user_id)
                                                    } else {
                                                        updated_post_liked.add(user_id)
                                                    }

                                                    saveLikeProfilePost(
                                                        index,
                                                        updated_post_liked,
                                                        firestore,
                                                        posts,
                                                        "post_liked"
                                                    )
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

                                            Spacer(modifier = Modifier.weight(1f))

                                            IconButton(
                                                onClick = {
                                                    val text = posts[index].post_content
                                                    val imageUrl = posts[index].post_image
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
                                Spacer(Modifier.height(26.dp))
                            }
                        }
                    }
                } else{
                    noDataAvailablePrompt("No data loaded")
                }
            }
        }
    }
}

//Update like and save of a user
fun saveLikeProfilePost(
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

//Remove interest from the list of interests of user
suspend fun removeInterest(
    user_id: String,
    removed_interest: String,
    firestore: FirebaseFirestore
) : String {
    return try {
        val querySnapshot = firestore.collection("user_profile")
            .whereEqualTo("user_id", user_id)
            .get()
            .await()

        val document = querySnapshot.documents.firstOrNull() ?: return "error"
        val docId = document.id

        firestore.collection("user_profile")
            .document(docId)
            .update("interest", FieldValue.arrayRemove(removed_interest))
            .await()

        "success"
    } catch (e: Exception) {
        "error"
    }
}

//Add interest from the list of interests of user
suspend fun addInterest(
    user_id: String,
    added_interest: String,
    firestore: FirebaseFirestore
) : String {
    return try {
        val querySnapshot = firestore.collection("user_profile")
            .whereEqualTo("user_id", user_id)
            .get()
            .await()

        val document = querySnapshot.documents.firstOrNull() ?: return "error"
        val docId = document.id

        firestore.collection("user_profile")
            .document(docId)
            .update("interest", FieldValue.arrayUnion(added_interest))
            .await()

        "success"
    } catch (e: Exception) {
        "error"
    }
}