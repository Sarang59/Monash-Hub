package com.example.loginpage.ui.data.model

data class ApiJobItem(
    val id: String?,
    val title: String?,
    val contract_type: String?,
    val description: String?,
    val redirect_url: String?,
    val salary_min: String?,
    val location: ApiJobLocation?,
    val category: ApiCategory?
)