package com.example.loginpage.ui.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_overview")

data class ChatOverviewEntity(
    @PrimaryKey
    val chat_table_name: String, // e.g., "user1_user2" as the chat document ID
    val peer_user_id: String,
    val name: String,
    val image: String
)