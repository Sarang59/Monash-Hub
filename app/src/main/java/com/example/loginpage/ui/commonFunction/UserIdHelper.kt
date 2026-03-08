package com.example.loginpage.ui.commonFunction

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object UserIdHelper {
    // Variables
    private const val fileName = "UserIdConf"
    private const val keyUserId = "savedUserId"

    /**
     * A function which is used to get the information about the user from the user Id config file
     */
    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
    }

    /**
     * A function which is used to save the user id in the config file
     */
    fun saveUserId(context: Context, userId: String) : Boolean {
        return getPreferences(context).edit().putString(keyUserId, userId).commit()
    }

    /**
     * A function which is used to get the user Id from the config file
     */
    fun getUserId(context: Context): String? {
        return getPreferences(context).getString(keyUserId, null)
    }

    /**
     * A function which is used to check if the user Id is present in the config file or not
     */
    fun hasUserId(context: Context): Boolean {
        return getPreferences(context).contains(keyUserId)
    }

    /**
     * A function which is used to clear a specific user Id from the file
     */
    fun clearUserId(context: Context) : Boolean {
        return getPreferences(context).edit().remove(keyUserId).commit()
    }

    /**
     * A function which is used to clear the data present in the config file
     */
    fun clearAll(context: Context): Boolean {
        return getPreferences(context).edit().clear().commit()
    }
}
