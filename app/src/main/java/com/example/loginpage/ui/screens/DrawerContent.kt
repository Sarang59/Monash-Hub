package com.example.loginpage.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.loginpage.R
import com.example.loginpage.ui.theme.primaryBg
import com.example.loginpage.ui.theme.primaryText
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.loginpage.ui.commonFunction.UserIdHelper
import com.example.loginpage.ui.commonFunction.loadDrawableByName
import com.example.loginpage.ui.data.model.Avatar
import com.example.loginpage.ui.data.model.CombineProfileAvatar
import com.example.loginpage.ui.data.model.DrawerMenu
import com.example.loginpage.ui.data.model.UserProfile
import com.example.loginpage.ui.theme.Transparent
import com.example.loginpage.ui.theme.cardBg
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun DrawerContent(navController: NavController, drawerState: DrawerState, userId: String?)
{
    val context = LocalContext.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    var activeMenu = navBackStackEntry?.destination?.route
    val scope = rememberCoroutineScope()
    val colorScheme = MaterialTheme.colorScheme

    val menus = arrayOf(
        DrawerMenu("home", Icons.Filled.Home, "Home"),
        DrawerMenu("postcreation", Icons.Filled.Create, "Create a Post"),
        DrawerMenu("chat", Icons.Filled.Chat, "Chat"),
        DrawerMenu("peer", Icons.Filled.Person, "Peers"),
        DrawerMenu("jobListing", Icons.Filled.Work, "Job Listing"),
        DrawerMenu("mapScreen", Icons.Filled.Map, "Campus Map"),
        DrawerMenu("profile", Icons.Filled.AccountCircle, "Profile"),
        DrawerMenu("settings", Icons.Filled.Settings, "Settings")
    )
    val firestore = FirebaseFirestore.getInstance()
    var user_profile by remember { mutableStateOf<CombineProfileAvatar?>(null) }

    LaunchedEffect(userId) {
        //Fetch current user data
        if (!userId.isNullOrBlank()) {
            try {
                val userDocs = firestore.collection("user_profile")
                    .whereEqualTo("user_id", userId)
                    .get()
                    .await()

                val user = userDocs.firstOrNull()?.toObject(UserProfile::class.java)

                if (user != null) {
                    val avatarDocs = firestore.collection("avatar")
                        .whereEqualTo("avatar_id", user.avatar_id)
                        .get()
                        .await()

                    val avatar = avatarDocs.firstOrNull()?.toObject(Avatar::class.java)

                    user_profile = CombineProfileAvatar(user,avatar)
                }
            } catch (e: Exception) {
                Log.e("Firestore error", "Failed to fetch user or avatar", e)
            }
        }
    }

    Column(modifier = Modifier.padding(vertical = 20.dp, horizontal = 10.dp)
    ) {
        Column(){
            IconButton(onClick = {
                scope.launch {
                    drawerState.close()
                }
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Return Icon",
                    tint = colorScheme.primaryText,
                    modifier = Modifier
                        .size(30.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column (modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp))
        {
            user_profile?.let {
                Row(
                    modifier = Modifier
                        .fillMaxWidth().padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    Image(
                        painter = loadDrawableByName(
                            context,
                            user_profile!!.avatar?.avatar_file_path
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .size(65.dp)
                            .clip(CircleShape)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colorScheme.cardBg)
                    ) {
                        Text(
                            text = user_profile!!.user.name,
                            color = colorScheme.primaryText,
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        Text(
                            text = user_profile!!.user.course_name,
                            color = colorScheme.primaryText,
                            style = TextStyle(fontSize = 16.sp)
                        )
                    }
                }
            }

            HorizontalDivider(
                thickness = 2.dp,
                color = colorScheme.primaryText,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            )

            menus.forEach {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (activeMenu?.startsWith(it.route) == true) colorScheme.primaryText else colorScheme.Transparent)
                        .clickable {
                            activeMenu = it.route
                            navController.navigate(it.route)
                            scope.launch { drawerState.close() }
                        }
                        .padding(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Icon(
                        imageVector = it.icon,
                        contentDescription = null,
                        tint = if (activeMenu?.startsWith(it.route) == true) colorScheme.primaryBg else colorScheme.primaryText,
                        modifier = Modifier.size(30.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(it.label,
                        modifier = Modifier
                            .fillMaxWidth(),
                        color = if (activeMenu?.startsWith(it.route) == true) colorScheme.primaryBg else colorScheme.primaryText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}