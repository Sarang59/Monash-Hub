package com.example.loginpage.ui.data.model

//Data class for storing the post object
import com.google.firebase.Timestamp

data class Post(
    val post_id: String = "",
    val post_content: String = "",
    val post_created_at: Timestamp? = null,
    val post_created_by: String = "",
    val post_image: String ?= "",
    val post_liked: List<String> = emptyList(),
    val post_saved: List<String> = emptyList(),
    val post_category: String = ""
)