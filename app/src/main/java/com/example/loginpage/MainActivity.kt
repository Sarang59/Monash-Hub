package com.example.loginpage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.loginpage.ui.navigation.AppNavGraph
import com.example.loginpage.ui.theme.LoginPageTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.loginpage.ui.commonFunction.ThemePrefHelper
import com.example.loginpage.ui.commonFunction.ThemePrefHelper.isDark
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.example.loginpage.ui.commonFunction.GoogleSignInHelper
import com.example.loginpage.ui.data.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.loginpage.ui.commonFunction.UserIdHelper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {

    lateinit var globalNavController: NavController
    lateinit var signInLauncher: ActivityResultLauncher<Intent>
    private var onLoginSuccess: () -> Unit = {}

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Register an Activity Result Launcher for handling result of Google Sign-In intent
        signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            // Check if the result returned from the sign-in activity is successful
            if (result.resultCode == RESULT_OK) {
                val data = result.data

                // Handle the Google sign-in result and authenticate with Firebase
                GoogleSignInHelper.handleSignInResult(
                    data,
                    FirebaseAuth.getInstance(),

                    // Get the information if the sign in is successful
                    onSuccess = { name, email ->

                        // Create a new user from fetched data
                        val newUser = Users(
                            dob = null,
                            email = email,
                            name = name,
                            onboarded = false,
                            user_id = "",
                            password = "",
                            google_login = true
                        )

                        // Check if the user is already present in the database
                        com.google.firebase.Firebase.firestore.collection("users")
                            .whereEqualTo("email", email)
                            .get()
                            .addOnSuccessListener { userDocuments ->
                                // if the document is not empty then it is present in database
                                if (!userDocuments.isEmpty) {
                                    // Check if user is google login or not
                                    if (userDocuments.documents[0].get("google_login") as? Boolean == true) {
                                        // Check if the user has completed their formalities or not
                                        if (userDocuments.documents[0].get("onboarded") as? Boolean == true) {
                                            // Save the user Id
                                            val userId = userDocuments.documents[0].id

                                            UserIdHelper.saveUserId(this@MainActivity, userId)
                                            onLoginSuccess()
                                            globalNavController.navigate("home")
                                        } else {
                                            // If onboarding process is incomplete then redirect the user to avatar Selection page
                                            globalNavController.navigate("avatarSelection/$email/$name")
                                        }
                                    } else {
                                        Toast.makeText(this, "Sign-in through normal login", Toast.LENGTH_LONG).show()
                                    }
                                }
                                // If the document is empty then add it in the database
                                else {
                                    com.google.firebase.Firebase.firestore.collection("users")
                                        .add(newUser)
                                        .addOnSuccessListener { referenceDocument ->
                                            // Generate the id and update the user Id variable
                                            // in the document
                                            val generateId = referenceDocument.id
                                            referenceDocument.update("user_id", generateId)
                                            Log.d("Success", "User ADDED")
                                            globalNavController.navigate("avatarSelection/$email/$name")
                                        }
                                        .addOnFailureListener {
                                            Log.e("Failed", "User not ADDED: ${it.message}")
                                            Toast.makeText(this, "User not ADDED", Toast.LENGTH_LONG).show()
                                        }
                                }
                            }
                            .addOnFailureListener {
                                Log.e("Error", "Failed to check email: ${it.message}")
                                Toast.makeText(this, "Failed to connect to the database", Toast.LENGTH_LONG).show()
                            }
                    },
                    onError = { errorMsg ->
                        Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        setContent {
            var darkMode by remember { mutableStateOf(false) }
            var userId by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(Unit) {
                val stored_uid = UserIdHelper.getUserId(this@MainActivity)
                userId = stored_uid
                ThemePrefHelper.loadFromFirestore(this@MainActivity) { loaded ->
                    darkMode = loaded
                }
            }

            LoginPageTheme(darkTheme = darkMode) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = androidx.navigation.compose.rememberNavController()
                    globalNavController = navController
                    onLoginSuccess   = {
                        val stored_uid = UserIdHelper.getUserId(this@MainActivity)
                        userId = stored_uid
                        ThemePrefHelper.loadFromFirestore(this@MainActivity) { loaded ->
                            darkMode = loaded
                        }
                    }

                    AppNavGraph(
                        navController = navController,
                        isDark = darkMode,
                        userId = userId,
                        signInLauncher = signInLauncher,
                        onToggleDark = { newVal ->
                            darkMode = newVal
                            ThemePrefHelper.toggle(this@MainActivity, newVal)
                        },
                        onLoginSuccess = onLoginSuccess
                    )

                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainApp() {
    val navController = androidx.navigation.compose.rememberNavController()
//    AppNavGraph(navController = navController, isDark = isDark,
//        onToggleDark = { newValue ->
//            isDark = newValue
//            ThemePrefHelper.toggle(this@MainActivity, newValue)
//        })
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LoginPageTheme {
//        MainApp()
    }
}
