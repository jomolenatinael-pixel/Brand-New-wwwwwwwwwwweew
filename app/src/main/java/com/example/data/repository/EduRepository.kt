package com.example.data.repository

import android.content.Context
import com.example.data.database.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.*

class EduRepository(private val db: AppDatabase) {

    val userProfile: Flow<UserProfile?> = db.userProfileDao().getUserProfile()
    val allFlashcards: Flow<List<Flashcard>> = db.flashcardDao().getAllFlashcards()
    val allTasks: Flow<List<StudyTask>> = db.studyTaskDao().getAllTasks()
    val quizResults: Flow<List<QuizResult>> = db.quizResultDao().getAllQuizResults()

    fun getFlashcardsBySubject(subject: String): Flow<List<Flashcard>> =
        db.flashcardDao().getFlashcardsBySubject(subject)

    fun getTasksByDate(date: String): Flow<List<StudyTask>> =
        db.studyTaskDao().getTasksByDate(date)

    fun getMessagesByChannel(channel: String): Flow<List<ChatMessage>> =
        db.chatMessageDao().getMessagesByChannel(channel)

    suspend fun saveProfile(profile: UserProfile) {
        db.userProfileDao().insertProfile(profile)
    }

    suspend fun updateProfile(profile: UserProfile) {
        db.userProfileDao().updateProfile(profile)
    }

    suspend fun addXP(amount: Int) {
        val currentProfile = db.userProfileDao().getUserProfileSync()
        if (currentProfile != null) {
            val newXp = currentProfile.xp + amount
            // Every 500 XP is a level up
            val newLevel = (newXp / 500) + 1
            val levelUp = newLevel > currentProfile.level
            
            db.userProfileDao().updateProfile(
                currentProfile.copy(
                    xp = newXp,
                    level = newLevel,
                    streak = if (amount == 5) currentProfile.streak + 1 else currentProfile.streak // Daily login streak
                )
            )
        }
    }

    suspend fun addFlashcard(subject: String, question: String, answer: String) {
        db.flashcardDao().insertFlashcard(
            Flashcard(subject = subject, question = question, answer = answer)
        )
    }

    suspend fun updateFlashcard(flashcard: Flashcard) {
        db.flashcardDao().updateFlashcard(flashcard)
    }

    suspend fun deleteFlashcard(id: Int) {
        db.flashcardDao().deleteFlashcard(id)
    }

    suspend fun addTask(subject: String, title: String, date: String, duration: Int) {
        db.studyTaskDao().insertTask(
            StudyTask(subject = subject, title = title, date = date, durationMinutes = duration)
        )
    }

    suspend fun setTaskCompleted(id: Int, completed: Boolean) {
        db.studyTaskDao().setTaskCompleted(id, completed)
    }

    suspend fun deleteTask(id: Int) {
        db.studyTaskDao().deleteTask(id)
    }

    suspend fun addQuizResult(subject: String, score: Int, total: Int, mode: String) {
        db.quizResultDao().insertQuizResult(
            QuizResult(subject = subject, score = score, total = total, mode = mode)
        )
        // Correct answer +10 XP, Quiz complete +20 XP
        val correctAnswers = score
        val xpBonus = (correctAnswers * 10) + 20
        addXP(xpBonus)
    }

    suspend fun sendMessage(channel: String, senderName: String, senderRole: String, senderAvatar: String, messageText: String, isFlagged: Boolean = false) {
        db.chatMessageDao().insertMessage(
            ChatMessage(
                channel = channel,
                senderName = senderName,
                senderRole = senderRole,
                senderAvatar = senderAvatar,
                messageText = messageText,
                isFlagged = isFlagged
            )
        )
    }

    suspend fun flagMessage(id: Int) {
        db.chatMessageDao().flagMessage(id)
    }

    // Prepopulate some interactive high-fidelity dummy content on clean boot
    suspend fun prepopulateData() {
        val currentProfile = db.userProfileDao().getUserProfileSync()
        if (currentProfile == null) {
            // Setup default profile
            db.userProfileDao().insertProfile(
                UserProfile(
                    name = "Samuel Ayele",
                    role = "Student",
                    companionType = "Genius Owl",
                    language = "English",
                    xp = 780,
                    level = 2,
                    streak = 12,
                    studyHours = 18.2f,
                    averageScore = 86,
                    learningScore = 91
                )
            )

            // Setup default flashcards for Grade 10 Students
            val defaultFlashcards = listOf(
                Flashcard(subject = "Mathematics", question = "What is the formula for the roots of a quadratic equation ax² + bx + c = 0?", answer = "x = [-b ± √(b² - 4ac)] / (2a)"),
                Flashcard(subject = "Mathematics", question = "State the Pythagoras theorem.", answer = "In a right-angled triangle, the square of the hypotenuse is equal to the sum of the squares of the other two sides: a² + b² = c²"),
                Flashcard(subject = "Physics", question = "What is Newton's Second Law of Motion?", answer = "The acceleration of an object is directly proportional to the net force acting on it and inversely proportional to its mass (F = ma)."),
                Flashcard(subject = "Physics", question = "What is the speed of light in vacuum?", answer = "Approximately 3.00 × 10⁸ meters per second (m/s)."),
                Flashcard(subject = "Chemistry", question = "What is the atomic number of Carbon and its electron configuration?", answer = "Atomic number is 6. Electron configuration is 1s² 2s² 2p² (or 2, 4)."),
                Flashcard(subject = "Biology", question = "What is the powerhouse of the cell?", answer = "The Mitochondria, responsible for producing cellular energy in the form of ATP."),
                Flashcard(subject = "Biology", question = "What is the primary function of DNA?", answer = "To store genetic information that guides the development, functioning, and reproduction of living organisms."),
                Flashcard(subject = "English", question = "What is an active voice vs passive voice?", answer = "Active: The subject performs the action (e.g., 'The teacher explained the lesson'). Passive: The subject receives the action (e.g., 'The lesson was explained by the teacher').")
            )
            for (card in defaultFlashcards) {
                db.flashcardDao().insertFlashcard(card)
            }

            // Setup daily study planner tasks
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val today = sdf.format(Date())
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            val tomorrow = sdf.format(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            val dayAfter = sdf.format(calendar.time)

            db.studyTaskDao().insertTask(StudyTask(date = today, subject = "Mathematics", title = "Solve quadratic equation exercises", durationMinutes = 45, isCompleted = true))
            db.studyTaskDao().insertTask(StudyTask(date = today, subject = "Biology", title = "Revise cell structure & organelles", durationMinutes = 30, isCompleted = false))
            db.studyTaskDao().insertTask(StudyTask(date = tomorrow, subject = "Physics", title = "Solve mechanics problems on F = ma", durationMinutes = 50, isCompleted = false))
            db.studyTaskDao().insertTask(StudyTask(date = dayAfter, subject = "Chemistry", title = "Draw covalent bonding diagrams", durationMinutes = 40, isCompleted = false))

            // Prepopulate some quiz results
            db.quizResultDao().insertQuizResult(QuizResult(subject = "Mathematics", score = 8, total = 10, mode = "Practice"))
            db.quizResultDao().insertQuizResult(QuizResult(subject = "Physics", score = 5, total = 5, mode = "Duel"))
            db.quizResultDao().insertQuizResult(QuizResult(subject = "Biology", score = 7, total = 10, mode = "Practice"))

            // Prepopulate community hub chat channels
            db.chatMessageDao().insertMessage(ChatMessage(channel = "#general", senderName = "Aster", senderRole = "Student", senderAvatar = "Study Dragon", messageText = "Welcome to Arekahub everyone! This Grade 10 platform is amazing 🚀"))
            db.chatMessageDao().insertMessage(ChatMessage(channel = "#general", senderName = "Dr. Tadesse", senderRole = "Teacher", senderAvatar = "Robo Teacher", messageText = "Greetings students! I'll be uploading the Physics revision PDF shortly. Get ready for tomorrow's quiz!"))
            db.chatMessageDao().insertMessage(ChatMessage(channel = "#general", senderName = "Yared", senderRole = "Student", senderAvatar = "Future Scientist", messageText = "Who is up for a Physics Quiz Duel battle in 10 minutes? Let's go! 🔥"))
            
            db.chatMessageDao().insertMessage(ChatMessage(channel = "#math-prep", senderName = "Aster", senderRole = "Student", senderAvatar = "Study Dragon", messageText = "Can anyone explain the difference between linear and quadratic equations?"))
            db.chatMessageDao().insertMessage(ChatMessage(channel = "#math-prep", senderName = "AI Tutor", senderRole = "Moderator", senderAvatar = "Genius Owl", messageText = "A linear equation forms a straight line and has degree 1 (e.g. y = mx + c). A quadratic equation forms a parabola and has degree 2 (e.g. y = ax² + bx + c)."))
        }
    }
}
