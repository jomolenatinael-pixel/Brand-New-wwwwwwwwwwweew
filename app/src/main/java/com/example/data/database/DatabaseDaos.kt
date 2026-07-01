package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getUserProfileSync(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: UserProfile)

    @Update
    suspend fun updateProfile(profile: UserProfile)
}

@Dao
interface FlashcardDao {
    @Query("SELECT * FROM flashcards WHERE subject = :subject")
    fun getFlashcardsBySubject(subject: String): Flow<List<Flashcard>>

    @Query("SELECT * FROM flashcards")
    fun getAllFlashcards(): Flow<List<Flashcard>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcard(flashcard: Flashcard)

    @Update
    suspend fun updateFlashcard(flashcard: Flashcard)

    @Query("DELETE FROM flashcards WHERE id = :id")
    suspend fun deleteFlashcard(id: Int)
}

@Dao
interface StudyTaskDao {
    @Query("SELECT * FROM study_tasks ORDER BY date ASC")
    fun getAllTasks(): Flow<List<StudyTask>>

    @Query("SELECT * FROM study_tasks WHERE date = :date")
    fun getTasksByDate(date: String): Flow<List<StudyTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: StudyTask)

    @Query("UPDATE study_tasks SET isCompleted = :completed WHERE id = :id")
    suspend fun setTaskCompleted(id: Int, completed: Boolean)

    @Query("DELETE FROM study_tasks WHERE id = :id")
    suspend fun deleteTask(id: Int)
}

@Dao
interface QuizResultDao {
    @Query("SELECT * FROM quiz_results ORDER BY date DESC")
    fun getAllQuizResults(): Flow<List<QuizResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizResult(result: QuizResult)
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM community_messages WHERE channel = :channel ORDER BY timestamp ASC")
    fun getMessagesByChannel(channel: String): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage)

    @Query("UPDATE community_messages SET isFlagged = 1 WHERE id = :id")
    suspend fun flagMessage(id: Int)
}
