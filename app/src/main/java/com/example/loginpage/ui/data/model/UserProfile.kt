package com.example.loginpage.ui.data.model

import com.google.firebase.Timestamp

data class UserProfile(
    var avatar_id: String = "",
    var company_name: String = "",
    var course_name: String = "",
    var current_role: String = "",
    var dark_mode: Boolean = false,
    var doj: Timestamp? = null,
    var faculty: String = "",
    var followers: List<String> = emptyList(),
    var graduation_year: String = "",
    var interest: List<String> = emptyList(),
    var name: String = "",
    var primary_campus: String = "",
    var profile_statement: String = "",
    var purpose_of_joining: List<String> = emptyList(),
    var user_id: String = ""
)