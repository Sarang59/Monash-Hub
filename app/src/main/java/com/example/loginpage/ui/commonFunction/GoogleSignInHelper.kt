package com.example.loginpage.ui.commonFunction

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.example.loginpage.R

object GoogleSignInHelper {

    // Constant which is used to identify the Google Sign-In request
    private const val RC_SIGN_IN = 9001

    // GoogleSignInClient used to launch sign-in intent
    private lateinit var googleSignInClient: GoogleSignInClient

    /**
     * Initializes Google Sign-In by configuring the required options.
     * This must be called before starting the sign-in intent.
     */
    fun initGoogleSignIn(activity: Activity) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            // This ID should match the web client ID from Firebase console
            .requestIdToken(activity.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(activity, gso)
    }

    /**
     * Starts the Google Sign-In flow by launching sign-in intent.
     */
    fun startSignInIntent(launcher: ActivityResultLauncher<Intent>) {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    /**
     * Handles result returned from sign-in activity.
     * Tries to extract Google account and authenticate with Firebase.
     */
    fun handleSignInResult(data: Intent?, auth: FirebaseAuth, onSuccess: (String, String) -> Unit, onError: (String) -> Unit) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            // If Google sign-in is successful then fetch account
            val account = task.getResult(ApiException::class.java)!!
            // Authenticate with Firebase using Google account
            firebaseAuthWithGoogle(account, auth, onSuccess, onError)
        }
        // Throw an exception
        catch (e: ApiException) {
            onError("Google sign-in failed: ${e.message}")
        }
    }

    /**
     * Authenticates the Google account with Firebase.
     */
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount, auth: FirebaseAuth, onSuccess: (String, String) -> Unit, onError: (String) -> Unit) {
        // Create a credential using the Google ID token
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        // Sign in to Firebase with the credential
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val email = account.email ?: "No Email"
                    val name = account.displayName ?: "No Name"
                    onSuccess(name, email)
                } else {
                    onError("Firebase authentication failed: ${task.exception?.message}")
                }
            }
    }

    // Expose request code for use in activities
    const val SIGN_IN_REQUEST_CODE = RC_SIGN_IN
}
