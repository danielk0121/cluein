package dev.danielk.cluein.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey val id: String,
    val date: String,
    val originalText: String,
    val correctedText: String,
    val explanation: String,
    val probability: Int,
    val inputSummary: String,
    val correctedSummary: String,
    val sourcesJson: String // List<SourceItem>을 JSON 문자열로 저장
)

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY date DESC, id DESC")
    fun getAllHistory(): Flow<List<HistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: HistoryEntity)

    @Delete
    suspend fun deleteHistory(history: HistoryEntity)
}

@Database(entities = [HistoryEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cluein_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
