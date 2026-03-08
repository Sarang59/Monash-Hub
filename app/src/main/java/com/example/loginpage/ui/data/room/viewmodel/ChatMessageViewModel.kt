package com.example.loginpage.ui.data.room.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginpage.ui.data.room.entity.ChatMessageEntity
import com.example.loginpage.ui.data.room.repository.ChatMessageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ChatMessageViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ChatMessageRepository

    init {
        repository = ChatMessageRepository(application)
    }

    fun getMessagesByChatTableId(chatTableName: String): Flow<List<ChatMessageEntity>> {
        return repository.getMessagesByChatTableId(chatTableName)
    }

    fun insertMessage(message: ChatMessageEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(message)
    }

    fun insertAllChats(chats: List<ChatMessageEntity>) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertAll(chats)
    }
}
