package com.example.loginpage.ui.data.room.repository

import android.app.Application
import com.example.loginpage.ui.data.room.dao.ChatMessageDao
import com.example.loginpage.ui.data.room.entity.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

class ChatMessageRepository(application: Application) {
    private val chatMessageDao: ChatMessageDao =
        ChatDatabase.getDatabase(application).chatMessageDao()

    fun getMessagesByChatTableId(chatTableName: String): Flow<List<ChatMessageEntity>> {
        return chatMessageDao.getMessagesForChat(chatTableName)
    }

    suspend fun insert(message: ChatMessageEntity) {
        chatMessageDao.insertMessage(message)
    }

    suspend fun insertAll(messages: List<ChatMessageEntity>) {
        chatMessageDao.insertAllMessages(messages)
    }
}
