import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.loginpage.ui.data.room.dao.ChatMessageDao
import com.example.loginpage.ui.data.room.dao.ChatOverviewDao
import com.example.loginpage.ui.data.room.entity.ChatMessageEntity
import com.example.loginpage.ui.data.room.entity.ChatOverviewEntity

@Database(
    entities = [ChatOverviewEntity::class, ChatMessageEntity::class],
    version = 4,
    exportSchema = false
)
abstract class ChatDatabase : RoomDatabase() {

    abstract fun chatOverviewDao(): ChatOverviewDao
    abstract fun chatMessageDao(): ChatMessageDao

    companion object {
        @Volatile
        private var INSTANCE: ChatDatabase? = null

        fun getDatabase(context: Context): ChatDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ChatDatabase::class.java,
                    "chat_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
