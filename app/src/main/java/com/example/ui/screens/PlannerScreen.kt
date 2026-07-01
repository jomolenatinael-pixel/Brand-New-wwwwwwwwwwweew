package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun PlannerScreen(
    viewModel: EduViewModel
) {
    var plannerTab by remember { mutableStateOf("schedule") } // schedule, ai_generator, flashcards

    val tasks by viewModel.studyTasks.collectAsState()
    val flashcards by viewModel.flashcards.collectAsState()
    val isPlanGenerating by viewModel.isPlanGenerating.collectAsState()

    // Planner inputs
    var strengths by remember { mutableStateOf("Calculus foundation, biology concepts") }
    var weaknesses by remember { mutableStateOf("Quadratic equation roots, chemistry bonding symbols") }
    var selectedDate by remember { mutableStateOf("2026-07-15") }

    // Flashcard states
    var currentCardIndex by remember { mutableStateOf(0) }
    var isCardFlipped by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Top sub-tab navigator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val tabs = listOf("schedule" to "Daily Tasks", "ai_generator" to "AI Plan Gen", "flashcards" to "Flashcards")
            tabs.forEach { (key, label) ->
                val isSelected = plannerTab == key
                Button(
                    onClick = { plannerTab = key },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) ElectricBlue else Color(0x1F4F8CFF)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (plannerTab == "schedule") {
            // --- DAILY TASKS & CALENDAR ---
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Daily Schedule & Roadmap", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextWhite)

                    // Compact horizontal calendar representation
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val days = listOf("Mon 1" to true, "Tue 2" to false, "Wed 3" to false, "Thu 4" to false, "Fri 5" to false)
                        days.forEach { (day, active) ->
                            Column(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (active) PurpleAccent else Color(0x0F4F8CFF))
                                    .clickable { }
                                    .padding(vertical = 8.dp, horizontal = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(day.split(" ")[0], fontSize = 11.sp, color = TextWhite)
                                Text(day.split(" ")[1], fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                            }
                        }
                    }

                    HorizontalDivider(color = Color(0x1F8C9BAE))

                    Text("Scheduled Tasks (+10 XP on completion)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PurpleAccent)

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(tasks) { task ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0x0F4F8CFF))
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = task.isCompleted,
                                        onCheckedChange = { viewModel.toggleTask(task.id, it) },
                                        colors = CheckboxDefaults.colors(checkedColor = EmeraldGreen)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(
                                            text = task.title,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextWhite
                                        )
                                        Text(
                                            text = "${task.subject} • ${task.durationMinutes} mins",
                                            fontSize = 11.sp,
                                            color = TextGray
                                        )
                                    }
                                }
                                IconButton(onClick = { viewModel.deleteTask(task.id) }) {
                                    Icon(Icons.Default.Delete, "Delete", tint = DangerRed, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }

        } else if (plannerTab == "ai_generator") {
            // --- AI PLAN GENERATOR ---
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("AI Study Plan Generator", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                    Text("Input your attributes below. Our AI computes customized topic roadmaps based on weakness optimization theories.", fontSize = 12.sp, color = TextGray)

                    OutlinedTextField(
                        value = strengths,
                        onValueChange = { strengths = it },
                        label = { Text("What are your Strengths?", color = TextGray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = Color(0x1F4F8CFF)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = weaknesses,
                        onValueChange = { weaknesses = it },
                        label = { Text("What are your Weaknesses?", color = TextGray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = Color(0x1F4F8CFF)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = selectedDate,
                        onValueChange = { selectedDate = it },
                        label = { Text("Target Exam Date (YYYY-MM-DD)", color = TextGray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = Color(0x1F4F8CFF)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = { viewModel.generateStudyPlan(selectedDate, listOf("Mathematics", "Physics", "Biology"), strengths, weaknesses) },
                        colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isPlanGenerating
                    ) {
                        if (isPlanGenerating) {
                            CircularProgressIndicator(color = TextWhite, modifier = Modifier.size(20.dp))
                        } else {
                            Icon(Icons.Default.AutoAwesome, "Gen")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Compute High-Yield Study Roadmap", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

        } else {
            // --- SPACED REPETITION FLASHCARDS ---
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (flashcards.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No flashcards compiled. Create some in your Socratic Tutor!", color = TextGray)
                    }
                } else {
                    val currentCard = flashcards[currentCardIndex % flashcards.size]
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Flashcard Review (${currentCardIndex % flashcards.size + 1} of ${flashcards.size})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextGray
                        )

                        Text(
                            text = currentCard.subject.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PurpleAccent
                        )

                        // Interactive Card Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isCardFlipped) Color(0xFF14243C) else Color(0x1F4F8CFF))
                                .clickable { isCardFlipped = !isCardFlipped }
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (isCardFlipped) "ANSWER:" else "QUESTION:",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isCardFlipped) EmeraldGreen else ElectricBlue,
                                    letterSpacing = 2.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = if (isCardFlipped) currentCard.answer else currentCard.question,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 22.sp
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Text(
                                    text = "Tap Card to Flip",
                                    fontSize = 11.sp,
                                    color = TextGray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Review verification buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.reviewFlashcard(currentCard, false)
                                    isCardFlipped = false
                                    currentCardIndex += 1
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Close, "Wrong")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Review Again", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    viewModel.reviewFlashcard(currentCard, true)
                                    isCardFlipped = false
                                    currentCardIndex += 1
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Check, "Correct")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("I Know This", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
