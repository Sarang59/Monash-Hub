package com.example.loginpage.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.sharp.FilterAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeersListScreen(navController: NavController, drawerState: DrawerState) {
    var name by remember { mutableStateOf("") }
    val context = LocalContext.current
    val user_id = UserIdHelper.getUserId(context).toString()
    val firestore = FirebaseFirestore.getInstance()
    val peersList = remember { mutableStateListOf<CombineProfileAvatar>() }
    val colorScheme = MaterialTheme.colorScheme
    var sortSelected = remember { mutableStateOf("A to Z") }
    val sortOptions = listOf(
        "A to Z" to { peersList.sortBy { it.user.name } },
        "Z to A" to { peersList.sortByDescending { it.user.name } },
        "Followers first" to { peersList.sortByDescending { it.user.followers.contains(user_id)} },
        "Followers last" to { peersList.sortBy { it.user.followers.contains(user_id)} }
    )
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true )
    var showSheet by remember { mutableStateOf(false) }
    var selectedFaculties by remember { mutableStateOf(listOf<String>()) }
    var selectedCampuses by remember { mutableStateOf(listOf<String>()) }
    var currentlyWorking by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            //Fetch user profile of all the users
            val userDocs = firestore.collection("user_profile")
                .whereNotEqualTo("user_id", user_id)
                .get()
                .await()

            userDocs.forEach { doc ->
                val user = doc.toObject(UserProfile::class.java)
                val avatarDocs = firestore.collection("avatar")
                    .whereEqualTo("avatar_id", user.avatar_id)
                    .get()
                    .await()

                val avatar = avatarDocs.firstOrNull()?.toObject(Avatar::class.java)
                peersList.add(CombineProfileAvatar(user, avatar))
            }

            peersList.sortBy { it.user.name }

        } catch (e: Exception) {
            Log.d("exception", "Exception in loading the data: $e")
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
                    .padding(horizontal = 24.dp).background(colorScheme.primaryBg)
            ){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 0.dp, horizontal = 0.dp).background(colorScheme.primaryBg),
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    Text(
                        text = "Peers",
                        style = TextStyle(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primaryText
                        )
                    )

                    if(!peersList.isEmpty()) {
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
                                PeersBottomFilterContent(
                                    selectedFaculties,
                                    selectedCampuses,
                                    currentlyWorking
                                ) { newFaculties, newCampuses, newCurrentlyWorking ->
                                    selectedFaculties = newFaculties
                                    selectedCampuses = newCampuses
                                    currentlyWorking = newCurrentlyWorking
                                    showSheet = false
                                }
                            }
                        }

                        SortByFunction(sortOptions, sortSelected)
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    thickness = 2.dp,
                    color = colorScheme.primaryText
                )


                if(peersList.isEmpty()) {
                    noDataAvailablePrompt("No peers exists")
                }
                else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                    )
                    {
                        //Search peer by name
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            placeholder = {
                                Text(
                                    "Search by peer's name",
                                    color = inactiveGrey,
                                    fontSize = 18.sp
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth().padding(bottom = 10.dp),
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
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        items(peersList.size) { index ->
                            if(peersList[index].user.name.contains(name, ignoreCase = true)) {
                                if ((selectedFaculties.size > 0 && peersList[index].user.faculty in selectedFaculties) ||
                                    (selectedCampuses.size > 0 && peersList[index].user.primary_campus in selectedCampuses) ||
                                    (currentlyWorking && (peersList[index].user.company_name.trim().isNullOrEmpty() == false && peersList[index].user.company_name.trim() != "\"\"")) ||
                                    (selectedCampuses.size == 0 && selectedFaculties.size == 0 && !currentlyWorking)
                                ) {
                                    val userId = peersList[index].user.user_id
                                    Card(
                                        onClick = { navController.navigate("peerProfile/$userId") },
                                        shape = RectangleShape,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .shadow(6.dp),
                                        elevation = CardDefaults.cardElevation(
                                            defaultElevation = 10.dp
                                        ),
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
                                                        peersList[index].avatar?.avatar_file_path
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
                                                        text = peersList[index].user.name,
                                                        color = colorScheme.primaryText,
                                                        style = TextStyle(
                                                            fontSize = 18.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    )

                                                    val course =
                                                        peersList[index].user.course_name
                                                    val truncatedCourse =
                                                        if (course.length > 20) course.take(
                                                            20
                                                        ) + "..." else course

                                                    Text(
                                                        text = "$truncatedCourse\n${peersList[index].user.primary_campus}",
                                                        color = colorScheme.primaryText,
                                                        style = TextStyle(fontSize = 16.sp)
                                                    )
                                                }

                                                Spacer(modifier = Modifier.weight(1f))

                                                IconButton(
                                                    onClick = {
                                                        val updated_follower =
                                                            peersList[index].user.followers.toMutableList()

                                                        if (updated_follower.contains(
                                                                user_id
                                                            )
                                                        ) {
                                                            updated_follower.remove(user_id)
                                                        } else {
                                                            updated_follower.add(user_id)
                                                        }

                                                        updateFollower(
                                                            index,
                                                            updated_follower,
                                                            firestore,
                                                            peersList
                                                        )
                                                    }
                                                ) {
                                                    Icon(
                                                        painter = painterResource(
                                                            id = if (peersList[index].user.followers.contains(
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
                        }
                        item {
                            if(
                                peersList.any {
                                    it.user.name.contains(name, ignoreCase = true)
                                })
                            {
                                if(
                                    !peersList.any {
                                        (selectedFaculties.size > 0 && it.user.faculty in selectedFaculties) ||
                                                (selectedCampuses.size > 0 && it.user.primary_campus in selectedCampuses) ||
                                                (currentlyWorking && (!it.user.company_name.trim().isEmpty() && it.user.company_name.trim() != "\"\"")) ||
                                                (selectedCampuses.size == 0 && selectedFaculties.size==0 && !currentlyWorking)
                                    })
                                {
                                    noDataAvailablePrompt("No peer found for your preferences")
                                }
                            }
                            else{
                                noDataAvailablePrompt("No peer found for your preferences")
                            }
                        }

                        item { Spacer(Modifier.height(26.dp)) }
                    }
                }
            }
        }
    }
}

//Update follower of the peer
fun updateFollower(
    index: Int,
    updated_followers: List<String>,
    firestore: FirebaseFirestore,
    peersList: MutableList<CombineProfileAvatar>
) {
    firestore.collection("user_profile")
        .whereEqualTo("user_id", peersList[index].user.user_id)
        .get()
        .addOnSuccessListener { documents ->
            val document = documents.firstOrNull()
            if (document != null) {
                val docId = document.id
                firestore.collection("user_profile")
                    .document(docId)
                    .update("followers", updated_followers)
                    .addOnSuccessListener {
                        val current = peersList[index]
                        val updatedUser = current.user.copy(
                            followers = updated_followers
                        )
                        val updatedCombinedUser = current.copy(user = updatedUser)
                        peersList[index] = updatedCombinedUser
                    }
            }
        }
}