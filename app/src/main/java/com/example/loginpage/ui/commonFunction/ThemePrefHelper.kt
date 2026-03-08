package com.example.loginpage.ui.commonFunction

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

// Singleton object to manage theme preference (dark mode) locally and in Firestore
object ThemePrefHelper {
    // Constants for shared preferences file name and key
    private const val PREFS = "theme_prefs"
    private const val KEY = "dark_mode"
    private const val USER_COL = "user_profile"     // Firestore collection name

    // Helper function to get SharedPreferences instance
    private fun prefs(ctx: Context): SharedPreferences =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    /**
     * Loads the theme preference from Firestore for the current user
     * and saves it locally in SharedPreferences.
     * Calls callback with the result.
     */
    fun loadFromFirestore(context: Context, onLoaded: (Boolean) -> Unit) {
        val uid = UserIdHelper.getUserId(context)

        // Fetch user's profile from Firestore
        FirebaseFirestore.getInstance()
            .collection(USER_COL)
            .whereEqualTo("user_id", uid)   // Query for matching user_id
            .get()
            .addOnSuccessListener { snap ->
                // Read the dark mode value, defaulting to false if missing
                val dark = snap.documents
                    .firstOrNull()
                    ?.getBoolean(KEY) ?: false

                // Save the preference locally
                prefs(context).edit().putBoolean(KEY, dark).apply()
                // Trigger callback with fetched value
                onLoaded(dark)

            }
            .addOnFailureListener {
                // If Firestore fetch fails, log the error
                Log.e("ThemePref", "load failed", it)
                // Fallback to the locally stored value
                onLoaded(prefs(context).getBoolean(KEY, false))
            }
    }


    // Returns whether dark mode is currently enabled from SharedPreferences
    fun isDark(context: Context): Boolean =
        prefs(context).getBoolean(KEY, false)

    /**
     * Toggles and updates the theme preference both locally and in Firestore is the new dark mode preference.
     */
    fun toggle(context: Context, newValue: Boolean) {
        // Save the new value to SharedPreferences
        prefs(context).edit().putBoolean(KEY, newValue).apply()

        // Get the user ID for Firestore update
        val uid = UserIdHelper.getUserId(context) ?: return

        // Find the user's Firestore document
        FirebaseFirestore.getInstance()
            .collection(USER_COL)
            .whereEqualTo("user_id", uid)
            .get()
            .addOnSuccessListener { snap ->
                snap.documents.firstOrNull()?.let { doc ->
                    FirebaseFirestore.getInstance()
                        .collection(USER_COL)
                        .document(doc.id)
                        .update(KEY, newValue)
                }
            }
            .addOnFailureListener {
                // Log any update failure
                Log.e("ThemePref", "update failed", it) }
    }
}
