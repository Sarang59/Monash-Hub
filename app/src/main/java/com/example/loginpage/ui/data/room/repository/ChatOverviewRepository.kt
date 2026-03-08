package com.example.loginpage.ui.data.room.repository

import android.app.Application
import android.util.Log
import com.example.loginpage.ui.data.room.dao.ChatOverviewDao
import com.example.loginpage.ui.data.room.entity.ChatOverviewEntity
import kotlinx.coroutines.flow.Flow

class ChatOverviewRepository(application: Application) {
    private val chatOverviewDao: ChatOverviewDao =
        ChatDatabase.getDatabase(application).chatOverviewDao()

    val allChatOverviews: Flow<List<ChatOverviewEntity>> = chatOverviewDao.getAllChatOverviews()

    suspend fun insert(chatOverview: ChatOverviewEntity) {
        chatOverviewDao.insertChatOverview(chatOverview)
    }
}