package com.example.loginpage.ui.navigation

import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.loginpage.ui.screens.*
import com.example.loginpage.ui.theme.cardBg
import androidx.navigation.navArgument
import com.example.loginpage.ui.screens.AvatarScreen
import com.example.loginpage.ui.screens.Dashboard
import com.example.loginpage.ui.screens.DrawerContent
import com.example.loginpage.ui.screens.Followers
import com.example.loginpage.ui.screens.ForgotPasswordOTPVerificationScreen
import com.example.loginpage.ui.screens.ForgotPasswordScreen
import com.example.loginpage.ui.screens.LikedPost
import com.example.loginpage.ui.screens.LoginScreen
import com.example.loginpage.ui.screens.MapScreen
import com.example.loginpage.ui.screens.PeerProfileScreen
import com.example.loginpage.ui.screens.PostCreationScreen
import com.example.loginpage.ui.screens.Profile
import com.example.loginpage.ui.screens.ProfileStatistics
import com.example.loginpage.ui.screens.SavedPost
import com.example.loginpage.ui.screens.Settings
import com.example.loginpage.ui.screens.SetupProfilePage1
import com.example.loginpage.ui.screens.SetupProfilePage2
import com.example.loginpage.ui.screens.SetupProfilePage3
import com.example.loginpage.ui.screens.SetupProfilePage4
import com.example.loginpage.ui.screens.SignUp
import com.example.loginpage.ui.screens.SignUpOtpVerificationScreen
import com.example.loginpage.ui.screens.SuccessPage
import com.example.loginpage.ui.screens.UpdatePassword
import androidx.compose.runtime.getValue

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavGraph(navController: NavHostController, isDark: Boolean, userId: String?,
                signInLauncher: ActivityResultLauncher<Intent>,
                onToggleDark: (Boolean) -> Unit, onLoginSuccess: () -> Unit) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val routesWithGesturesPrefix = listOf("home", "settings", "profile", "editProfile", "chat", "chatDetailed", "peer", "peerProfile",
        "postcreation", "profileStatistics", "profileListedProduct", "mapScreen", "profileLikedPost",
        "profileSavedPost", "profileFollowers", "jobListing", "terms", "privacypolicy", "notification")
    val gesturesEnabled = routesWithGesturesPrefix.any { prefix ->
        currentRoute?.startsWith(prefix) == true
    }


        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = gesturesEnabled,
            drawerContent = {
                Surface(
                    modifier = Modifier
                        .width(320.dp)
                        .fillMaxHeight(),
                    color = MaterialTheme.colorScheme.cardBg,
                    tonalElevation = 8.dp
                )
                {
                    DrawerContent(
                        navController = navController,
                        drawerState = drawerState,
                        userId = userId
                    )
                }
            }
        ) {
            NavHost(navController = navController, startDestination = "login") {
                composable("login") {
                    LoginScreen(navController = navController, onLoginSuccess = onLoginSuccess,
                        signInLauncher = signInLauncher)
                }

                /*composable("OtpVerification") {
                OtpVerificationScreen(navController = navController,
                    onVerify = { otpCode ->
                        // call your API here to verify `otpCode` with backend
                        println("Verifying: $otpCode")
                    },
                    recipientEmail = "test@example.com"
                )
            }*/

                composable(
                    route = "SignUpOtpVerification/{email}/{name}/{birthday}/{password}",
                    arguments = listOf(
                        navArgument("email") { type = NavType.StringType },
                        navArgument("name") { type = NavType.StringType },
                        navArgument("birthday") { type = NavType.StringType },
                        navArgument("password") { type = NavType.StringType })
                ) { backStackEntry ->
                    val email = backStackEntry.arguments?.getString("email") ?: ""
                    val name = backStackEntry.arguments?.getString("name") ?: ""
                    val birthday = backStackEntry.arguments?.getString("birthday") ?: ""
                    val password = backStackEntry.arguments?.getString("password") ?: ""

                    SignUpOtpVerificationScreen(
                        navController = navController,
                        onVerify = { otpCode ->
                            println("Verifying: $otpCode")
                        },
                        recipientEmail = email,
                        name = name,
                        birthday = birthday,
                        password = password
                    )
                }

                composable(
                    route = "ForgotPasswordOtpVerification/{email}",
                    arguments = listOf(navArgument("email") { type = NavType.StringType })
                ) { backStackEntry ->
                    val email = backStackEntry.arguments?.getString("email") ?: ""

                    ForgotPasswordOTPVerificationScreen(
                        navController = navController,
                        onVerify = { otpCode ->
                            println("Verifying: $otpCode")
                        },
                        recipientEmail = email
                    )
                }

                composable("ForgotPasswordScreen") {
                    ForgotPasswordScreen(
                        navController = navController,
                        onResetPassword = {
                            val hardcodedEmail = "testuser@example.com"
                            println("Reset link sent to: $hardcodedEmail")
                        }
                    )
                }

                composable(
                    route = "avatarSelection/{email}/{name}",
                    arguments = listOf(
                        navArgument("email") { type = NavType.StringType },
                        navArgument("name") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val email = backStackEntry.arguments?.getString("email") ?: ""
                    val name = backStackEntry.arguments?.getString("name") ?: ""

                    AvatarScreen(
                        navController = navController,
                        email = email,
                        name = name
                    )
                }

                composable("signup") {
                    SignUp(navController = navController)
                }

                composable(
                    route = "setup/{email}/{name}/{avatarId}",
                    arguments = listOf(
                        navArgument("email") { type = NavType.StringType },
                        navArgument("name") { type = NavType.StringType },
                        navArgument("avatarId") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val email = backStackEntry.arguments?.getString("email") ?: ""
                    val name = backStackEntry.arguments?.getString("name") ?: ""
                    val avatarId = backStackEntry.arguments?.getString("avatarId") ?: ""

                    SetupProfilePage1(
                        navController = navController,
                        email = email,
                        name = name,
                        avatarId = avatarId
                    )
                }

                composable(
                    route = "setupPage2/{email}/{name}/{avatarId}/{faculty}" +
                            "/{course}/{campus}/{graduationYear}",
                    arguments = listOf(
                        navArgument("email") { type = NavType.StringType },
                        navArgument("name") { type = NavType.StringType },
                        navArgument("avatarId") { type = NavType.StringType },
                        navArgument("faculty") { type = NavType.StringType },
                        navArgument("course") { type = NavType.StringType },
                        navArgument("campus") { type = NavType.StringType },
                        navArgument("graduationYear") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val email = backStackEntry.arguments?.getString("email") ?: ""
                    val name = backStackEntry.arguments?.getString("name") ?: ""
                    val avatarId = backStackEntry.arguments?.getString("avatarId") ?: ""
                    val faculty = backStackEntry.arguments?.getString("faculty") ?: ""
                    val course = backStackEntry.arguments?.getString("course") ?: ""
                    val campus = backStackEntry.arguments?.getString("campus") ?: ""
                    val graduationYear = backStackEntry.arguments?.getString("graduationYear") ?: ""

                    SetupProfilePage2(
                        navController = navController,
                        email = email,
                        name = name,
                        avatarId = avatarId,
                        faculty = faculty,
                        course = course,
                        campus = campus,
                        graduationYear = graduationYear
                    )
                }

                composable(
                    route = "setupPage3/{email}/{name}/{avatarId}/{faculty}" +
                            "/{course}/{campus}/{graduationYear}"
                            + "?company={company}&role={role}&doj={doj}",
                    arguments = listOf(
                        navArgument("email") { type = NavType.StringType },
                        navArgument("name") { type = NavType.StringType },
                        navArgument("avatarId") { type = NavType.StringType },
                        navArgument("faculty") { type = NavType.StringType },
                        navArgument("course") { type = NavType.StringType },
                        navArgument("campus") { type = NavType.StringType },
                        navArgument("graduationYear") { type = NavType.StringType },
                        navArgument("company") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = ""
                        },
                        navArgument("role") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = ""
                        },
                        navArgument("doj") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = ""
                        }
                    )
                ) { backStackEntry ->
                    val email = backStackEntry.arguments?.getString("email") ?: ""
                    val name = backStackEntry.arguments?.getString("name") ?: ""
                    val avatarId = backStackEntry.arguments?.getString("avatarId") ?: ""
                    val faculty = backStackEntry.arguments?.getString("faculty") ?: ""
                    val course = backStackEntry.arguments?.getString("course") ?: ""
                    val campus = backStackEntry.arguments?.getString("campus") ?: ""
                    val graduationYear = backStackEntry.arguments?.getString("graduationYear") ?: ""

                    val company = backStackEntry.arguments?.getString("company") ?: ""
                    val role = backStackEntry.arguments?.getString("role") ?: ""
                    val doj = backStackEntry.arguments?.getString("doj") ?: ""

                    SetupProfilePage3(
                        navController = navController,
                        email = email,
                        name = name,
                        avatarId = avatarId,
                        faculty = faculty,
                        course = course,
                        campus = campus,
                        graduationYear = graduationYear,
                        company = company,
                        role = role,
                        doj = doj
                    )
                }

                composable(
                    route = "setupPage4/{email}/{name}/{avatarId}/{faculty}" +
                            "/{course}/{campus}/{graduationYear}/"
                            + "{summary}/{selectedTopics}"
                            + "?company={company}&role={role}&doj={doj}",
                    arguments = listOf(
                        navArgument("email") { type = NavType.StringType },
                        navArgument("name") { type = NavType.StringType },
                        navArgument("avatarId") { type = NavType.StringType },
                        navArgument("faculty") { type = NavType.StringType },
                        navArgument("course") { type = NavType.StringType },
                        navArgument("campus") { type = NavType.StringType },
                        navArgument("graduationYear") { type = NavType.StringType },
                        navArgument("summary") { type = NavType.StringType },
                        navArgument("selectedTopics") { type = NavType.StringType },

                        navArgument("company") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = ""
                        },
                        navArgument("role") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = ""
                        },
                        navArgument("doj") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = ""
                        }
                    )
                ) { backStackEntry ->
                    val email = backStackEntry.arguments?.getString("email") ?: ""
                    val name = backStackEntry.arguments?.getString("name") ?: ""
                    val avatarId = backStackEntry.arguments?.getString("avatarId") ?: ""
                    val faculty = backStackEntry.arguments?.getString("faculty") ?: ""
                    val course = backStackEntry.arguments?.getString("course") ?: ""
                    val campus = backStackEntry.arguments?.getString("campus") ?: ""
                    val graduationYear = backStackEntry.arguments?.getString("graduationYear") ?: ""
                    val summary = backStackEntry.arguments?.getString("summary") ?: ""
                    val selectedTopics = backStackEntry.arguments?.getString("selectedTopics") ?: ""

                    val company = backStackEntry.arguments?.getString("company") ?: ""
                    val role = backStackEntry.arguments?.getString("role") ?: ""
                    val doj = backStackEntry.arguments?.getString("doj") ?: ""

                    SetupProfilePage4(
                        navController = navController,
                        email = email,
                        name = name,
                        avatarId = avatarId,
                        faculty = faculty,
                        course = course,
                        campus = campus,
                        graduationYear = graduationYear,
                        company = company,
                        role = role,
                        doj = doj,
                        summary = summary,
                        encodedSelectedTopics = selectedTopics,
                        onLoginSuccess = onLoginSuccess
                    )
                }

                composable(
                    route = "updatePassword/{email}",
                    arguments = listOf(navArgument("email") { type = NavType.StringType })
                ) { backStackEntry ->
                    val email = backStackEntry.arguments?.getString("email") ?: ""

                    UpdatePassword(
                        navController = navController,
                        email = email
                    )
                }

                composable("home") {
                    Dashboard(navController = navController, drawerState = drawerState)
                }

                composable("settings") {
                    Settings(navController = navController, drawerState = drawerState, isDark = isDark,
                        onToggleDark = onToggleDark)
                }

                composable(route = "postcreation") {
                    PostCreationScreen(navController = navController, drawerState = drawerState)
                }

                composable("peer"
                ) {
                    PeersListScreen(navController = navController, drawerState = drawerState)
                }

                composable(
                    route = "peerProfile/{userId}",
                    arguments = listOf(navArgument("userId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val userId = backStackEntry.arguments?.getString("userId") ?: ""
                    PeerProfileScreen(navController = navController, drawerState = drawerState, peerUserId = userId)
                }

                composable("chat") {
                    ChatScreen(navController = navController, drawerState = drawerState)
                }

                composable("notification") {
                    NotificationScreen(navController = navController, drawerState = drawerState)
                }

                composable(
                    route = "chatDetailed/{peer_user_id}/{name}/{image}",
                    arguments = listOf(
                        navArgument("peer_user_id") { type = NavType.StringType },
                        navArgument("name") { type = NavType.StringType },
                        navArgument("image") { type = NavType.StringType }
                    )
                ) {
                    val peerUserId = it.arguments?.getString("peer_user_id") ?: ""
                    val userName = it.arguments?.getString("name") ?: ""
                    val imageResId = it.arguments?.getString("image") ?: "1"

                    ChatDetailedScreen(
                        navController = navController,
                        drawerState = drawerState,
                        peer_user_id = peerUserId,
                        peer_name = userName,
                        image = imageResId
                    )
                }


                composable("profile") {
                    Profile(navController = navController, drawerState = drawerState)
                }

                composable("editProfile") {
                    EditProfile(navController = navController, drawerState = drawerState)
                }

                composable("profileStatistics") {
                    ProfileStatistics(navController = navController, drawerState = drawerState)
                }


                composable("profileLikedPost") {
                    LikedPost(navController = navController, drawerState = drawerState)
                }

                composable("profileSavedPost") {
                    SavedPost(navController = navController, drawerState = drawerState)
                }

                composable("profileFollowers") {
                    Followers(navController = navController, drawerState = drawerState)
                }

                composable("MapScreen") {
                    MapScreen(navController = navController, drawerState = drawerState)
                }

                composable("jobListing") {
                    JobListingScreen(navController = navController, drawerState = drawerState)
                }

                composable("terms") {
                    TermsAndConditionsScreen(navController, drawerState)
                }

                composable("PrivacyPolicy") {
                    PrivacyPolicyScreen(navController, drawerState)
                }

                composable(
                    route = "successPage/{lastPageVisited}",
                    arguments = listOf(navArgument("lastPageVisited") { type = NavType.StringType })
                ) { backStackEntry ->
                    val lastVisitedPage =
                        backStackEntry.arguments?.getString("lastPageVisited") ?: ""

                    SuccessPage(
                        navController = navController,
                        lastPageVisited = lastVisitedPage
                    )
                }
            }
        }
    }

