package com.example.loginpage.ui.data.model

import com.google.firebase.Timestamp

data class Chat (
    val message: String = "",
    val sender_id: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val receiver_id: String = ""
)
