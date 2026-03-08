package com.example.loginpage.ui.screens

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.loginpage.R
import com.example.loginpage.ui.commonFunction.UserIdHelper
import com.example.loginpage.ui.data.model.Post
import com.example.loginpage.ui.theme.Transparent
import com.example.loginpage.ui.theme.errorText
import com.example.loginpage.ui.theme.inactiveGrey
import com.example.loginpage.ui.theme.primaryBg
import com.example.loginpage.ui.theme.primaryText
import com.example.loginpage.utils.DriveUploader
import com.example.loginpage.utils.TextClassifier
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostCreationScreen(navController: NavController, drawerState: DrawerState) {
    // Context and coroutine scope for background operations
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // State variables for post description, validation, image URI, and click status
    val postDescription = remember { mutableStateOf("") }
    val isPostDescriptionEmpty = remember { mutableStateOf(false) }
    val imageUri = remember { mutableStateOf<Uri?>(null) }

    // Launcher to pick an image from the device
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        imageUri.value = it
    }
    val isActive = remember { mutableStateOf(false) }

    // Initialize ML text classifier
    TextClassifier.initialize(context)
    TextClassifier.initialize(LocalContext.current)
    val colorScheme = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorScheme.primaryBg
    ) {
        Column {

            // App header with navigation drawer
            Header(navController = navController,drawerState = drawerState)
            Spacer(modifier = Modifier.height(26.dp))

            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    "Create a post",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primaryText
                )

                HorizontalDivider(
                    thickness = 2.dp,
                    color = colorScheme.primaryText,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(buildAnnotatedString {
                    withStyle(SpanStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = colorScheme.primaryText)) {
                        append("Add Description: ")
                    }
                    withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = colorScheme.error)) {
                        append("*")
                    }
                })

                // Validation error if description is empty
                if(isPostDescriptionEmpty.value)
                {
                    Text(
                        text = "Description cannot be blank",
                        color = colorScheme.errorText,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                OutlinedTextField(
                    value = postDescription.value,
                    onValueChange = {
                        if(it.length != 0){
                            postDescription.value = it
                            isPostDescriptionEmpty.value = false
                        }
                        else{
                            postDescription.value = it
                            isPostDescriptionEmpty.value = true
                        }
                                    },
                    placeholder = {
                        Text("Enter Description", color = colorScheme.inactiveGrey)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(vertical = 6.dp),
                    shape = RectangleShape,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = colorScheme.Transparent,
                        unfocusedContainerColor = colorScheme.Transparent,
                        focusedIndicatorColor = colorScheme.primaryText,
                        unfocusedIndicatorColor = colorScheme.inactiveGrey,
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    "Add Image:",
                    fontSize = 18.sp,
                    color = colorScheme.primaryText,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(colorScheme.Transparent)
                        .border(
                            1.dp,
                            color = if (isActive.value) colorScheme.primaryText else colorScheme.inactiveGrey
                        )
                        .clickable {
                            isActive.value = true
                            launcher.launch("image/*")
                        }
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri.value != null) {
                        // Show selected image
                        Image(
                            painter = rememberAsyncImagePainter(imageUri.value),
                            contentDescription = "Selected Image",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Upload",
                                modifier = Modifier.size(40.dp),
                                tint = if (isActive.value) colorScheme.primaryText else colorScheme.inactiveGrey
                            )
                            Text(
                                "Upload Image",
                                color = if (isActive.value) colorScheme.primaryText else colorScheme.inactiveGrey
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if(isPostDescriptionEmpty.value || postDescription.value.length ==0) {
                            isPostDescriptionEmpty.value = true
                            return@Button
                        }
                        else
                        {
                            // Use ML to predict post category
                            val postCategory = TextClassifier.predict(postDescription.value)

                            // Background process to upload and save post
                            scope.launch {
                                val driveUploader = DriveUploader(context)
                                var imageUrl: String? = ""

                                if (imageUri.value != null) {
                                    withContext(Dispatchers.IO) {
                                        kotlinx.coroutines.suspendCancellableCoroutine<Unit> { continuation ->
                                            driveUploader.uploadFileFromUri(imageUri.value!!) { url ->
                                                imageUrl = url ?: ""
                                                continuation.resume(Unit, null)
                                            }
                                        }
                                    }
                                }

                                val savedId = UserIdHelper.getUserId(context) ?: ""


                                val post = Post(
                                    post_id = "",
                                    post_content = postDescription.value,
                                    post_created_at = Timestamp.now(),
                                    post_created_by = savedId,
                                    post_image = imageUrl?: "",
                                    post_liked = emptyList(),
                                    post_saved = emptyList(),
                                    post_category = postCategory
                                )

                                // Save post to Firestore
                                FirebaseFirestore.getInstance()
                                    .collection("post")
                                    .add(post)
                                    .addOnSuccessListener { documentRef ->
                                        val generatedId = documentRef.id
                                        documentRef.update("post_id", generatedId)
                                            .addOnSuccessListener {
                                                navController.navigate("successPage/postcreation")
                                            }
                                            .addOnFailureListener {
                                                Toast.makeText(context, "Failed to save post", Toast.LENGTH_SHORT).show()
                                            }
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(context, "Failed to save post", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(150.dp)
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primaryText)
                ) {
                    Text(
                        "Create",
                        color = colorScheme.primaryBg,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
