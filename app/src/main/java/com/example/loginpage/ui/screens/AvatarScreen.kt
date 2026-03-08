package com.example.loginpage.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.loginpage.ui.theme.primaryBg
import com.example.loginpage.ui.theme.primaryText
import com.example.loginpage.R
import com.example.loginpage.ui.commonFunction.loadDrawableByName
import com.example.loginpage.ui.data.model.Avatar
import com.example.loginpage.ui.data.model.Post
import com.example.loginpage.ui.data.model.UserProfile
import com.example.loginpage.ui.theme.errorText
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import com.google.firebase.firestore.toObjects
import com.google.gson.Gson
import java.net.URLEncoder

@Composable
fun AvatarScreen(navController: NavController,
                 email: String,
                 name: String) {

    // Variables
    var selectedIndex by remember { mutableIntStateOf(-1) }
    var avatarNotSelected by remember { mutableStateOf( false ) }
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme
    var avatarList = remember { mutableStateListOf<Avatar>() }
    var selectedAvatar by remember { mutableStateOf(Avatar()) }

    // Fetch the Avatar data from the firebase database
    LaunchedEffect(Unit) {
        Firebase.firestore.collection("avatar")
            .get()
            .addOnSuccessListener { result ->
                for(avatar in result) {
                    val fetchedAvatar = avatar.toObject(Avatar::class.java)
                    Log.d("Fetched", "Successfully" + fetchedAvatar.avatar_id)
                    avatarList.add(fetchedAvatar)
                }
            }
            .addOnFailureListener {
                Log.e("Firestore", "Failed to fetch avatars: ${it.message}")
            }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorScheme.primaryBg
    )
    {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            // Heading
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome to MonashHub",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primaryText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Secure, social and student-centered",
                    fontSize = 16.sp,
                    color = colorScheme.primaryText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    thickness = 2.dp,
                    color = colorScheme.primaryText
                )
            }

            // Display the Avatar to be selected
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Text(
                    text = "Please select an Avatar for your profile",
                    fontSize = 16.sp,
                    color = colorScheme.primaryText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
                )

                //Avatar
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colorScheme.primaryText
                            )
                        ) {
                            append("Select your profile avatar: ")
                        }

                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp,
                                color = colorScheme.errorText
                            )
                        ) {
                            append("*")
                        }
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                if(avatarNotSelected)
                {
                    Text(
                        text = "Please select an avatar",
                        color = colorScheme.errorText,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(5.dp))

                // Used Lazy Vertical Grid to have specific number of elements in a row
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(15.dp),
                    verticalArrangement = Arrangement.spacedBy(30.dp)
                )
                {
                    // Check if list is empty or not
                    if(avatarList.isEmpty())
                    {
                        return@LazyVerticalGrid
                    }
                    else {
                        val sortedAvatarList = avatarList.sortedBy { it.avatar_id.toInt() }
                        items(sortedAvatarList.size) { index ->
                            val isSelected = index == selectedIndex

                            Image(
                                painter = loadDrawableByName(context, sortedAvatarList[index].avatar_file_path),
                                contentDescription = "Avatar $index",
                                colorFilter = if (!isSelected) {
                                    ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
                                } else null,
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        avatarNotSelected = false
                                        selectedIndex = index // updates to newly selected avatar
                                        selectedAvatar = sortedAvatarList[selectedIndex]
                                    }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Button(
                    onClick = {
                        if(selectedIndex == -1) {
                            avatarNotSelected = true
                            return@Button
                        }
                        else {
                            // Update the avatar Id and redirect to setup page
                            val avatarId = selectedAvatar.avatar_id
                            navController.navigate("setup/${email}/${name}/${avatarId}")
                        }
                    },
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .align(Alignment.CenterHorizontally)
                        .width(100.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primaryText)
                ) {
                    Text(
                        "Next",
                        color = colorScheme.primaryBg,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}
