package com.example.loginpage.ui.commonFunction

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object RememberMeHelper {
    // Variables
    private const val fileName = "RememberMeConf"
    private const val rememberMeKey = "rememberMe"
    private const val storedEmail = "savedEmail"
    private const val storedPassword = "savedPassword"

    /**
     * A function which is used to save the credentials in the RememberMeConf file
     */
    fun saveCredentials(context: Context, email: String, password: String) {
        val preference: SharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        preference.edit()
            .putBoolean(rememberMeKey, true)
            .putString(storedEmail, email)
            .putString(storedPassword, password)
            .apply()
    }

    /**
     * A function which is used to clear the credentials
     */
    fun clearCredentials(context: Context) {
        val preference: SharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        preference.edit()
            .clear()
            .apply()
    }

    /**
     * A function which is used to check if the user has its data in config file or not
     */
    fun isRemembered(context: Context): Boolean {
        val preference: SharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        return preference.getBoolean(rememberMeKey, false)
    }

    /**
     * A function which is used to save the email in the config file
     */
    fun getSavedEmail(context: Context): String? {
        val preference: SharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        return preference.getString(storedEmail, "")
    }

    /**
     * A function which is used to save the password in the config file
     */
    fun getSavedPassword(context: Context): String? {
        val preference: SharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        return preference.getString(storedPassword, "")
    }
}