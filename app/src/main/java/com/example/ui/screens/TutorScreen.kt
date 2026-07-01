package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.EduViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorScreen(
    viewModel: EduViewModel
) {
    val currentMode by viewModel.selectedTutorMode.collectAsState()
    val currentSubject by viewModel.selectedTutorSubject.collectAsState()
    val chatHistory by viewModel.tutorChatHistory.collectAsState()
    val isLoading by viewModel.tutorLoading.collectAsState()

    // Mock exam states
    var isExamPrepMode by remember { mutableStateOf(false) }
    val activeQuestions by viewModel.activeQuizQuestions.collectAsState()
    val currentIndex by viewModel.currentQuestionIndex.collectAsState()
    val selectedOption by viewModel.selectedOptionIndex.collectAsState()
    val quizCompleted by viewModel.quizCompleted.collectAsState()
    val quizScore by viewModel.quizScore.collectAsState()
    val isQuizLoading by viewModel.isQuizLoading.collectAsState()

    val subjects = listOf("General", "Mathematics", "Physics", "Chemistry", "Biology")
    val modes = listOf("Teach Me", "Quiz Me Mode", "Exam Mode", "Revision Mode", "Socratic Mode", "Homework Helper")

    var userMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Toggle Top Tabs between AI Tutor Chat & Exam Prep Center
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { isExamPrepMode = false },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!isExamPrepMode) ElectricBlue else Color(0x1F4F8CFF)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Psychology, "Tutor", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("AI Super Tutor", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { isExamPrepMode = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isExamPrepMode) PurpleAccent else Color(0x1F7C4DFF)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Assignment, "Exam Prep", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("AI Exam Prep", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (!isExamPrepMode) {
            // --- TUTOR MODE LAYOUT ---
            
            // Mode Select Row
            Text("Select AI Tutor Interaction Mode", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextGray)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                modes.forEach { mode ->
                                    val isSelected = currentMode == mode
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) PurpleAccent else Color(0x1F7C4DFF))
                                            .clickable { viewModel.selectTutorMode(mode) }
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(mode, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Subject Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                subjects.forEach { subj ->
                    val isSelected = currentSubject == subj
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) ElectricBlue else Color(0x1F4F8CFF))
                            .clickable { viewModel.selectTutorSubject(subj) }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(subj, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                    }
                }
            }

            // Main Chat Log Container
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$currentSubject • $currentMode",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PurpleAccent
                        )
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear Chat",
                            tint = TextGray,
                            modifier = Modifier
                                .size(18.dp)
                                .clickable { viewModel.clearTutorChat() }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(chatHistory) { (message, isUser) ->
                            ChatBubble(text = message, isUser = isUser)
                        }
                        if (isLoading) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = ElectricBlue,
                                        strokeWidth = 2.dp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Message Input bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = userMessage,
                    onValueChange = { userMessage = it },
                    placeholder = { Text("Ask anything... (e.g., 'Simplify Kepler's Laws')", color = TextGray, fontSize = 13.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = Color(0x334F8CFF)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("tutor_chat_input"),
                    singleLine = true
                )

                IconButton(
                    onClick = {
                        if (userMessage.isNotBlank()) {
                            viewModel.sendTutorMessage(userMessage)
                            userMessage = ""
                        }
                    },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(ElectricBlue)
                        .size(48.dp)
                        .testTag("tutor_send_button")
                ) {
                    Icon(Icons.Default.Send, "Send", tint = TextWhite, modifier = Modifier.size(18.dp))
                }
            }

        } else {
            // --- EXAM PREP / MOCK EXAM CENTER LAYOUT ---
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (activeQuestions.isEmpty() && !isQuizLoading) {
                    // Welcome & Start Center
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = "Metrics",
                            tint = PurpleAccent,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "AI Exam Prep Center",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = "Test your readiness under realistic time pressure. Our AI predicts your exam readiness based on correct responses, intervals, and speeds.",
                            fontSize = 13.sp,
                            color = TextGray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Predicted Exam Readiness Score: 84%",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldGreen
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { viewModel.generateAIQuiz("Mathematics") },
                            colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("Launch Timed Math Mock Exam", fontWeight = FontWeight.Bold)
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { viewModel.generateAIQuiz("Physics") },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("Launch Timed Physics Mock Exam", fontWeight = FontWeight.Bold)
                        }
                    }
                } else if (isQuizLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            CircularProgressIndicator(color = PurpleAccent)
                            Text("AI is extracting exam chapters and building custom quiz...", color = TextGray, fontSize = 12.sp)
                        }
                    }
                } else if (!quizCompleted) {
                    // Active Timed Exam wizard
                    val q = activeQuestions[currentIndex]
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Question ${currentIndex + 1} of ${activeQuestions.size}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextGray
                            )
                            Text(
                                text = "⏱️ TIMED MODE",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = DangerRed
                            )
                        }

                        // Linear progress bar
                        LinearProgressIndicator(
                            progress = { (currentIndex + 1).toFloat() / activeQuestions.size },
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)),
                            color = PurpleAccent,
                            trackColor = Color(0x337C4DFF)
                        )

                        Text(
                            text = q.question,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            q.options.forEachIndexed { index, option ->
                                val isSelected = selectedOption == index
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) PurpleAccent.copy(alpha = 0.2f) else Color(0x0F4F8CFF))
                                        .clickable { viewModel.selectOption(index) }
                                        .padding(12.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { viewModel.selectOption(index) },
                                            colors = RadioButtonDefaults.colors(selectedColor = PurpleAccent)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(option, color = TextWhite, fontSize = 13.sp)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Button(
                            onClick = { viewModel.nextQuestion() },
                            enabled = selectedOption != null,
                            colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().height(44.dp)
                        ) {
                            Text(if (currentIndex == activeQuestions.size - 1) "Submit Exam" else "Next Question", fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    // Exam Finished / Score Results
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Cup",
                            tint = WarningOrange,
                            modifier = Modifier.size(72.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Mock Exam Complete!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = "You scored $quizScore correct out of ${activeQuestions.size} total!",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EmeraldGreen
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))

                        // Dynamic calculation of readiness
                        val readiness = 60 + (quizScore * 8)
                        Text(
                            text = "Updated Exam Readiness Score: $readiness%",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = PurpleAccent
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { viewModel.generateAIQuiz("Mathematics") }, // resets
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Start New Mock Exam", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(text: String, isUser: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (!isUser) {
                // Owl avatar placeholder for AI
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(PurpleAccent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Psychology, "Owl", tint = TextWhite, modifier = Modifier.size(16.dp))
                }
            }

            Box(
                modifier = Modifier
                    .widthIn(max = 260.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 12.dp,
                            topEnd = 12.dp,
                            bottomStart = if (isUser) 12.dp else 0.dp,
                            bottomEnd = if (isUser) 0.dp else 12.dp
                        )
                    )
                    .background(if (isUser) ElectricBlue else Color(0x1A4F8CFF))
                    .padding(12.dp)
            ) {
                Text(
                    text = text,
                    fontSize = 13.sp,
                    color = TextWhite,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
