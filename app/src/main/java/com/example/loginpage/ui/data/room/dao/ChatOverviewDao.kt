package com.example.loginpage.ui.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.loginpage.ui.data.room.entity.ChatOverviewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatOverviewDao {

    @Query("SELECT * FROM chat_overview")
    fun getAllChatOverviews(): Flow<List<ChatOverviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatOverview(chatOverview: ChatOverviewEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllChatOverviews(chatOverviews: List<ChatOverviewEntity>)
}
