package com.example.loginpage.ui.data.room.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginpage.ui.data.room.entity.ChatOverviewEntity
import com.example.loginpage.ui.data.room.repository.ChatOverviewRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ChatOverviewViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ChatOverviewRepository

    init {
        repository = ChatOverviewRepository(application)
    }

    val allChatOverviews: Flow<List<ChatOverviewEntity>> = repository.allChatOverviews

    fun insertChatOverview(chatOverview: ChatOverviewEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(chatOverview)
    }

}