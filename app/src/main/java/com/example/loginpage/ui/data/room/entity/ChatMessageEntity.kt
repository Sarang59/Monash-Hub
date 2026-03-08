package com.example.loginpage.ui.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "chats",
    primaryKeys = ["chat_table_name", "timestamp"] // This combination must be unique
)
data class ChatMessageEntity(
    val chat_table_name: String,
    val message: String,
    val sender_id: String,
    val receiver_id: String,
    val timestamp: Long // use epoch millis
)


