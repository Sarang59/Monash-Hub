package com.example.loginpage.ui.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.loginpage.ui.data.room.entity.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {

    @Query("SELECT * FROM chats WHERE chat_table_name = :chatTableName ORDER BY timestamp ASC")
    fun getMessagesForChat(chatTableName: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllMessages(messages: List<ChatMessageEntity>)


}
