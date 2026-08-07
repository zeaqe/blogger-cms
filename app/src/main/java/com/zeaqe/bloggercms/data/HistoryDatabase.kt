// HistoryDatabase.kt
package com.zeaqe.bloggercms.data

import androidx.room.*

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val action: String, // e.g., "Created Post", "Updated Draft"
    val postId: String?,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY timestamp DESC")
    fun getAllHistory(): kotlinx.coroutines.flow.Flow<List<HistoryEntity>>

    @Insert
    suspend fun insertHistory(history: HistoryEntity)
}

@Database(entities = [HistoryEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}