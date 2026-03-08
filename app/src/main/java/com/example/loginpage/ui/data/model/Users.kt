package com.example.loginpage.ui.data.model

import com.google.firebase.Timestamp

data class Users(
    val user_id: String = "",
    val name: String = "",
    val email: String = "",
    val dob: Timestamp? = null,
    val password: String = "",
    val onboarded: Boolean = false,
    val google_login: Boolean = false
)