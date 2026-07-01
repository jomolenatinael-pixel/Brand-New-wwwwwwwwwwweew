package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiRepository
import com.example.data.database.AppDatabase
import com.example.data.database.ChatMessage
import com.example.data.database.Flashcard
import com.example.data.database.QuizResult
import com.example.data.database.StudyTask
import com.example.data.database.UserProfile
import com.example.data.repository.EduRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class EduViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: EduRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = EduRepository(database)
        viewModelScope.launch {
            repository.prepopulateData()
        }
    }

    // --- State Observables ---
    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val flashcards: StateFlow<List<Flashcard>> = repository.allFlashcards
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val studyTasks: StateFlow<List<StudyTask>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val quizResults: StateFlow<List<QuizResult>> = repository.quizResults
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Active Tab State ---
    private val _currentTab = MutableStateFlow("dashboard")
    val currentTab: StateFlow<String> = _currentTab.asStateFlow()

    fun setCurrentTab(tab: String) {
        _currentTab.value = tab
    }

    // --- AI Tutor State ---
    private val _tutorChatHistory = MutableStateFlow<List<Pair<String, Boolean>>>(
        listOf(
            "Hello Samuel! I am your AI Super Tutor. I can assist you in Mathematics, Physics, Chemistry, Biology, and more. Choose a mode below and let's start studying! 🎓" to false
        )
    )
    val tutorChatHistory: StateFlow<List<Pair<String, Boolean>>> = _tutorChatHistory.asStateFlow()

    private val _tutorLoading = MutableStateFlow(false)
    val tutorLoading: StateFlow<Boolean> = _tutorLoading.asStateFlow()

    private val _selectedTutorMode = MutableStateFlow("Teach Me")
    val selectedTutorMode: StateFlow<String> = _selectedTutorMode.asStateFlow()

    private val _selectedTutorSubject = MutableStateFlow("General")
    val selectedTutorSubject: StateFlow<String> = _selectedTutorSubject.asStateFlow()

    fun selectTutorMode(mode: String) {
        _selectedTutorMode.value = mode
    }

    fun selectTutorSubject(subject: String) {
        _selectedTutorSubject.value = subject
    }

    fun sendTutorMessage(message: String) {
        if (message.isBlank()) return
        val currentHistory = _tutorChatHistory.value.toMutableList()
        currentHistory.add(message to true)
        _tutorChatHistory.value = currentHistory
        _tutorLoading.value = true

        viewModelScope.launch {
            val systemPrompt = when (_selectedTutorMode.value) {
                "Teach Me" -> "You are a Grade 10 teacher. Explain concepts step-by-step using extremely clear, simple analogies and rich examples. If the user asks about a formula, break down each symbol clearly."
                "Quiz Me Mode" -> "You are a quiz master. Ask the student one challenging Grade 10 level question on ${_selectedTutorSubject.value} with 4 multiple-choice options (A, B, C, D). Wait for their answer before revealing if it is correct and explaining why."
                "Exam Mode" -> "You are an examiner. Explain concepts strictly with high-yield exam patterns, definitions, expected mark breakups, and quick study mnemonics designed to score 100%."
                "Revision Mode" -> "Create highly condensed revision cards or summaries. Use bullet points, bold key terms, and summary tables to help the student memorize content quickly."
                "Socratic Mode" -> "You are Socrates. Do NOT give the direct answer to any questions. Instead, reply with a helpful guiding question that leads the student to think critically and arrive at the solution step-by-step."
                "Homework Helper" -> "Explain the steps and rules behind the user's homework question. Guide them through the logic without directly giving the solution, prompting them to try the next step."
                else -> "You are a supportive, genius Grade 10 educational AI assistant."
            }

            val response = GeminiRepository.getAIResponse(
                prompt = "The student asks about ${_selectedTutorSubject.value}: $message",
                systemInstruction = systemPrompt
            )
            
            val updatedHistory = _tutorChatHistory.value.toMutableList()
            updatedHistory.add(response to false)
            _tutorChatHistory.value = updatedHistory
            _tutorLoading.value = false
            
            // Add XP for learning interaction
            repository.addXP(15)
        }
    }

    fun clearTutorChat() {
        _tutorChatHistory.value = listOf(
            "Chat log cleared! Let's start fresh. How can I help you learn today? 🚀" to false
        )
    }

    // --- Quiz & Learning Battles Engine ---
    data class QuizQuestion(
        val question: String,
        val options: List<String>,
        val correctIndex: Int,
        val explanation: String
    )

    private val _activeQuizQuestions = MutableStateFlow<List<QuizQuestion>>(emptyList())
    val activeQuizQuestions: StateFlow<List<QuizQuestion>> = _activeQuizQuestions.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _selectedOptionIndex = MutableStateFlow<Int?>(null)
    val selectedOptionIndex: StateFlow<Int?> = _selectedOptionIndex.asStateFlow()

    private val _quizCompleted = MutableStateFlow(false)
    val quizCompleted: StateFlow<Boolean> = _quizCompleted.asStateFlow()

    private val _quizScore = MutableStateFlow(0)
    val quizScore: StateFlow<Int> = _quizScore.asStateFlow()

    private val _battleOpponentScore = MutableStateFlow(0)
    val battleOpponentScore: StateFlow<Int> = _battleOpponentScore.asStateFlow()

    private val _battleTimer = MutableStateFlow(30)
    val battleTimer: StateFlow<Int> = _battleTimer.asStateFlow()

    private val _isBattleActive = MutableStateFlow(false)
    val isBattleActive: StateFlow<Boolean> = _isBattleActive.asStateFlow()

    private val _isQuizLoading = MutableStateFlow(false)
    val isQuizLoading: StateFlow<Boolean> = _isQuizLoading.asStateFlow()

    fun generateAIQuiz(subject: String) {
        _isQuizLoading.value = true
        _quizCompleted.value = false
        _currentQuestionIndex.value = 0
        _selectedOptionIndex.value = null
        _quizScore.value = 0
        _activeQuizQuestions.value = emptyList()

        viewModelScope.launch {
            val prompt = """
                Generate a quiz with exactly 5 Grade 10 multiple choice questions on the subject: $subject.
                Return ONLY a JSON array matching this exact schema:
                [
                  {
                    "question": "The question text",
                    "options": ["Option A", "Option B", "Option C", "Option D"],
                    "correctIndex": 0,
                    "explanation": "Why this option is correct"
                  }
                ]
                Do not include markdown blocks or any text other than the JSON itself.
            """.trimIndent()

            val aiResponse = GeminiRepository.getAIResponse(prompt)
            // Parse response safely, fallback to curated local questions if API is missing/unstable
            val questions = parseQuizJson(aiResponse, subject)
            _activeQuizQuestions.value = questions
            _isQuizLoading.value = false
        }
    }

    private fun parseQuizJson(jsonText: String, subject: String): List<QuizQuestion> {
        try {
            // Clean markdown wraps if any
            val cleanJson = jsonText.replace("```json", "").replace("```", "").trim()
            val moshi = com.squareup.moshi.Moshi.Builder()
                .addLast(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
                .build()
            val type = com.squareup.moshi.Types.newParameterizedType(List::class.java, QuizQuestion::class.java)
            val adapter = moshi.adapter<List<QuizQuestion>>(type)
            val parsed = adapter.fromJson(cleanJson)
            if (!parsed.isNullOrEmpty()) {
                return parsed
            }
        } catch (e: Exception) {
            // Fallback
        }
        return getCuratedMockQuestions(subject)
    }

    private fun getCuratedMockQuestions(subject: String): List<QuizQuestion> {
        return when (subject) {
            "Physics" -> listOf(
                QuizQuestion("What does the slope of a velocity-time graph represent?", listOf("Displacement", "Acceleration", "Force", "Speed"), 1, "The slope of velocity-time graph gives acceleration (dv/dt)."),
                QuizQuestion("A car travels 100 meters in 5 seconds. What is its speed?", listOf("10 m/s", "20 m/s", "50 m/s", "500 m/s"), 1, "Speed = Distance / Time = 100 / 5 = 20 m/s."),
                QuizQuestion("Which Newton's law explains that action and reaction are equal and opposite?", listOf("First Law", "Second Law", "Third Law", "Law of Gravitation"), 2, "Newton's Third Law states that for every action there is an equal and opposite reaction."),
                QuizQuestion("What is the SI unit of Force?", listOf("Joule", "Watt", "Pascal", "Newton"), 3, "The Newton (N) is the SI unit of Force."),
                QuizQuestion("Which energy conversion takes place in a solar cell?", listOf("Chemical to Electrical", "Light to Electrical", "Heat to Chemical", "Kinetic to Electrical"), 1, "Solar cells convert solar energy (light) directly into electrical energy.")
            )
            "Biology" -> listOf(
                QuizQuestion("Which organelle is responsible for cellular respiration?", listOf("Ribosome", "Chloroplast", "Mitochondria", "Lysosome"), 2, "Mitochondria synthesize ATP through cellular respiration."),
                QuizQuestion("What is the process of cell division that results in four daughter cells?", listOf("Mitosis", "Meiosis", "Binary Fission", "Budding"), 1, "Meiosis is reduction division producing 4 genetically diverse haploid gametes."),
                QuizQuestion("Which pigment captures light energy in plants?", listOf("Carotene", "Xanthophyll", "Chlorophyll", "Hemoglobin"), 2, "Chlorophyll captures red/blue light wavelengths for photosynthesis."),
                QuizQuestion("What does DNA stand for?", listOf("Deoxyribonucleic Acid", "Deoxyribose Nitrogen Acid", "Di-nitric Acetate", "Duplex Nucleolus Acid"), 0, "Deoxyribonucleic Acid is the hereditary material in organisms."),
                QuizQuestion("Which blood cells carry oxygen?", listOf("White Blood Cells", "Red Blood Cells", "Platelets", "Plasma Cells"), 1, "Red Blood Cells (erythrocytes) carry oxygen using hemoglobin.")
            )
            else -> listOf( // Mathematics default
                QuizQuestion("What are the roots of the equation x² - 5x + 6 = 0?", listOf("x = 1, 6", "x = 2, 3", "x = -2, -3", "x = 5, 6"), 1, "Factorizing gives (x-2)(x-3) = 0, so x = 2 and x = 3."),
                QuizQuestion("What is the value of log₁₀(1000)?", listOf("1", "2", "3", "4"), 2, "log₁₀(10³) = 3."),
                QuizQuestion("Solve for x: 2x + 7 = 15.", listOf("x = 4", "x = 8", "x = 11", "x = 15"), 0, "2x = 15 - 7 = 8, so x = 4."),
                QuizQuestion("If a triangle has sides 3, 4, and 5, what is its area?", listOf("6", "12", "15", "20"), 0, "It is a right triangle with base=3, height=4. Area = 0.5 * 3 * 4 = 6."),
                QuizQuestion("What is the sum of angles in a regular hexagon?", listOf("360°", "540°", "720°", "1080°"), 2, "Sum of angles = (n - 2) * 180 = (6 - 2) * 180 = 720°.")
            )
        }
    }

    fun startQuizBattle(subject: String) {
        _isBattleActive.value = true
        _battleOpponentScore.value = 0
        _battleTimer.value = 30
        generateAIQuiz(subject)

        // Launch simulated live timer & peer score responses
        viewModelScope.launch {
            while (_battleTimer.value > 0 && !_quizCompleted.value) {
                delay(1000)
                _battleTimer.value -= 1
                // Randomly increment opponent score to simulate competition
                if (Math.random() > 0.65 && _battleOpponentScore.value < 5) {
                    _battleOpponentScore.value += 1
                }
            }
            if (_battleTimer.value == 0) {
                submitQuiz()
            }
        }
    }

    fun selectOption(index: Int) {
        _selectedOptionIndex.value = index
    }

    fun nextQuestion() {
        val current = _selectedOptionIndex.value
        val correct = _activeQuizQuestions.value.getOrNull(_currentQuestionIndex.value)?.correctIndex
        if (current == correct) {
            _quizScore.value += 1
        }

        _selectedOptionIndex.value = null
        if (_currentQuestionIndex.value < _activeQuizQuestions.value.size - 1) {
            _currentQuestionIndex.value += 1
        } else {
            submitQuiz()
        }
    }

    private fun submitQuiz() {
        _quizCompleted.value = true
        _isBattleActive.value = false
        viewModelScope.launch {
            repository.addQuizResult(
                subject = _selectedTutorSubject.value,
                score = _quizScore.value,
                total = _activeQuizQuestions.value.size,
                mode = if (_isBattleActive.value) "Duel" else "Practice"
            )
        }
    }

    // --- Smart PDF & Knowledge Base System ---
    private val _uploadedPdfs = MutableStateFlow<List<String>>(listOf("Biology Unit 3 - Cell Division.pdf", "Mathematics Unit 2 - Quad Equations.pdf"))
    val uploadedPdfs: StateFlow<List<String>> = _uploadedPdfs.asStateFlow()

    private val _pdfSummary = MutableStateFlow("")
    val pdfSummary: StateFlow<String> = _pdfSummary.asStateFlow()

    private val _pdfConcepts = MutableStateFlow<List<String>>(emptyList())
    val pdfConcepts: StateFlow<List<String>> = _pdfConcepts.asStateFlow()

    private val _isPdfProcessing = MutableStateFlow(false)
    val isPdfProcessing: StateFlow<Boolean> = _isPdfProcessing.asStateFlow()

    fun uploadMockPdf(fileName: String) {
        val list = _uploadedPdfs.value.toMutableList()
        list.add(fileName)
        _uploadedPdfs.value = list
        _isPdfProcessing.value = true

        viewModelScope.launch {
            // Ask Gemini to extract details and generate automated content based on Mock chapters
            val prompt = """
                Summarize Grade 10 chapter details for: $fileName.
                Provide:
                1. A rich 3-sentence summary.
                2. Exactly 4 core concepts separated by commas.
                Ensure it looks high educational standard.
            """.trimIndent()
            
            val response = GeminiRepository.getAIResponse(prompt)
            val parts = response.split("\n")
            _pdfSummary.value = parts.firstOrNull() ?: "Summary extracted successfully."
            _pdfConcepts.value = listOf("Chapter Structure", "Analytical Formulas", "High-Yield Facts", "Example Computations")
            _isPdfProcessing.value = false
            
            // Add rewards
            repository.addXP(50)
        }
    }

    // --- AI Study Plan Generator ---
    private val _generatedPlan = MutableStateFlow<List<StudyTask>>(emptyList())
    val generatedPlan: StateFlow<List<StudyTask>> = _generatedPlan.asStateFlow()

    private val _isPlanGenerating = MutableStateFlow(false)
    val isPlanGenerating: StateFlow<Boolean> = _isPlanGenerating.asStateFlow()

    fun generateStudyPlan(examDate: String, subjects: List<String>, strengths: String, weaknesses: String) {
        _isPlanGenerating.value = true
        viewModelScope.launch {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val today = sdf.format(Date())

            val prompt = """
                Generate a 3-day high-performance personalized study roadmap for a Grade 10 student.
                The exam date is $examDate.
                Subjects: ${subjects.joinToString()}.
                Strengths: $strengths.
                Weaknesses: $weaknesses.
                Return exactly 3 tasks in simple plain format (Day: Task Title | Subject | DurationMinutes).
            """.trimIndent()

            val response = GeminiRepository.getAIResponse(prompt)
            // Save to database as well
            val calendar = Calendar.getInstance()
            val parsedTasks = response.split("\n").mapIndexedNotNull { index, line ->
                val dateStr = sdf.format(calendar.time)
                calendar.add(Calendar.DAY_OF_YEAR, 1)
                
                if (line.isNotBlank() && line.contains("|")) {
                    val parts = line.split("|")
                    val title = parts.getOrNull(0)?.replace(Regex("^Day \\d+:"), "")?.trim() ?: "Study Topic"
                    val subject = parts.getOrNull(1)?.trim() ?: subjects.firstOrNull() ?: "Mathematics"
                    val duration = parts.getOrNull(2)?.replace(Regex("[^0-9]"), "")?.trim()?.toIntOrNull() ?: 45
                    StudyTask(date = dateStr, subject = subject, title = title, durationMinutes = duration)
                } else {
                    StudyTask(date = dateStr, subject = "Mathematics", title = "Revise foundational equations", durationMinutes = 45)
                }
            }

            for (task in parsedTasks) {
                repository.addTask(task.subject, task.title, task.date, task.durationMinutes)
            }
            _isPlanGenerating.value = _isPlanGenerating.value // trigger flow updates
            _isPlanGenerating.value = false
            repository.addXP(100) // Badge Unlock bonus
        }
    }

    fun toggleTask(id: Int, completed: Boolean) {
        viewModelScope.launch {
            repository.setTaskCompleted(id, completed)
            if (completed) {
                repository.addXP(10) // Correct check
            }
        }
    }

    fun addCustomTask(subject: String, title: String, date: String, duration: Int) {
        viewModelScope.launch {
            repository.addTask(subject, title, date, duration)
        }
    }

    fun deleteTask(id: Int) {
        viewModelScope.launch {
            repository.deleteTask(id)
        }
    }

    // --- Community Discord-Style Hub ---
    private val _activeChannel = MutableStateFlow("#general")
    val activeChannel: StateFlow<String> = _activeChannel.asStateFlow()

    val chatMessages: StateFlow<List<ChatMessage>> = _activeChannel
        .flatMapLatest { channel -> repository.getMessagesByChannel(channel) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectChannel(channel: String) {
        _activeChannel.value = channel
    }

    fun sendCommunityMessage(text: String, senderName: String, senderRole: String, senderAvatar: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            // Real-time AI Moderation Sim: toxicity/spam check
            val isToxic = text.contains("abuse", ignoreCase = true) || text.contains("cheat", ignoreCase = true) || text.length > 500
            repository.sendMessage(_activeChannel.value, senderName, senderRole, senderAvatar, text, isFlagged = isToxic)
            
            // Auto reply simulator from peers or AI Tutor inside channel
            delay(1500)
            if (!isToxic) {
                val autoReplyText = getAutoReplyMessage(text, _activeChannel.value)
                repository.sendMessage(
                    _activeChannel.value,
                    senderName = "Hana Grade-10",
                    senderRole = "Student",
                    senderAvatar = "Genius Owl",
                    messageText = autoReplyText
                )
            } else {
                repository.sendMessage(
                    _activeChannel.value,
                    senderName = "AI Moderator",
                    senderRole = "Moderator",
                    senderAvatar = "Robo Teacher",
                    messageText = "⚠️ Message flagged and hidden. Please maintain standard educational integrity guidelines."
                )
            }
        }
    }

    private fun getAutoReplyMessage(prompt: String, channel: String): String {
        return when {
            prompt.contains("help", ignoreCase = true) -> "Yes, let's group up! I am studying for Physics unit 2 right now too."
            prompt.contains("when", ignoreCase = true) -> "I think the exam is scheduled for next Monday. We should build a roadmap."
            channel == "#math-prep" -> "That makes sense. Can you explain question 5 as well? It looked tricky."
            else -> "Agreed! Let's keep working hard and level up our Streaks! 💪🔥"
        }
    }

    // --- Spaced Repetition Flashcards ---
    fun addFlashcard(subject: String, question: String, answer: String) {
        viewModelScope.launch {
            repository.addFlashcard(subject, question, answer)
        }
    }

    fun reviewFlashcard(card: Flashcard, isCorrect: Boolean) {
        viewModelScope.launch {
            val updated = if (isCorrect) {
                val rep = card.repetitions + 1
                val interval = if (rep == 1) 1 else if (rep == 2) 6 else (card.interval * card.easiness).toInt()
                card.copy(
                    repetitions = rep,
                    interval = interval,
                    nextReviewDate = System.currentTimeMillis() + (interval * 86400000L),
                    easiness = Math.max(1.3f, card.easiness + 0.1f)
                )
            } else {
                card.copy(
                    repetitions = 0,
                    interval = 1,
                    nextReviewDate = System.currentTimeMillis() + 86400000L,
                    easiness = Math.max(1.3f, card.easiness - 0.2f)
                )
            }
            repository.updateFlashcard(updated)
            repository.addXP(10)
        }
    }

    // --- Onboarding / Role Selection ---
    fun updateProfileNameAndAvatar(name: String, role: String, companion: String, language: String) {
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfile(name = name, role = role, companionType = companion, language = language)
            repository.saveProfile(
                current.copy(
                    name = name,
                    role = role,
                    companionType = companion,
                    language = language
                )
            )
        }
    }
}
