package com.example.loginpage.ui.data.model

data class ChatOverview (
    val peer_chat: List<ChatHelper> = emptyList(),
    val user_id: String = ""
)