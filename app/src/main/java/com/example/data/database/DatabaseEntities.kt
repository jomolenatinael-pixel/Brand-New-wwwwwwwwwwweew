package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1, // Single user profile
    val name: String,
    val role: String, // Student, Teacher, Moderator, Admin
    val companionType: String, // Genius Owl, Robo Teacher, Study Dragon, Future Scientist
    val language: String, // English, Amharic, Afaan Oromo, Tigrinya
    val xp: Int = 100,
    val level: Int = 1,
    val streak: Int = 12,
    val studyHours: Float = 14.5f,
    val averageScore: Int = 88,
    val learningScore: Int = 92
)

@Entity(tableName = "flashcards")
data class Flashcard(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subject: String,
    val question: String,
    val answer: String,
    val level: Int = 1, // Spaced repetition index
    val repetitions: Int = 0,
    val interval: Int = 1,
    val easiness: Float = 2.5f,
    val nextReviewDate: Long = System.currentTimeMillis()
)

@Entity(tableName = "study_tasks")
data class StudyTask(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String, // YYYY-MM-DD
    val subject: String,
    val title: String,
    val durationMinutes: Int = 45,
    val isCompleted: Boolean = false
)

@Entity(tableName = "quiz_results")
data class QuizResult(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subject: String,
    val score: Int,
    val total: Int,
    val mode: String, // Duel, Mock, Speed, Practice
    val date: Long = System.currentTimeMillis()
)

@Entity(tableName = "community_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val channel: String, // #math, #physics, #general
    val senderName: String,
    val senderRole: String, // Student, Teacher, Moderator
    val senderAvatar: String, // Owl, Robo, Dragon, Scientist
    val messageText: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFlagged: Boolean = false // AI Moderation flag
)
