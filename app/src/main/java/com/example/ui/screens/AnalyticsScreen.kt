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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.EduViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: EduViewModel
) {
    var subTab by remember { mutableStateOf("analytics") } // analytics, battles, leaderboard, badges

    val results by viewModel.quizResults.collectAsState()
    val activeQuestions by viewModel.activeQuizQuestions.collectAsState()
    val currentIndex by viewModel.currentQuestionIndex.collectAsState()
    val selectedOption by viewModel.selectedOptionIndex.collectAsState()
    val quizCompleted by viewModel.quizCompleted.collectAsState()
    val quizScore by viewModel.quizScore.collectAsState()
    val isQuizLoading by viewModel.isQuizLoading.collectAsState()

    val opponentScore by viewModel.battleOpponentScore.collectAsState()
    val timerCount by viewModel.battleTimer.collectAsState()
    val isBattleActive by viewModel.isBattleActive.collectAsState()

    // Mock leaderboards
    val leaderboard: List<Triple<Int, String, Int>> = listOf(
        Triple(1, "Samuel Ayele (You)", 1280),
        Triple(2, "Hana Girma", 1150),
        Triple(3, "Aster Mengistu", 1020),
        Triple(4, "Yared Kebede", 950),
        Triple(5, "Ruth Tolossa", 880)
    )

    // Badges
    val badges: List<Pair<String, ImageVector>> = listOf(
        Pair("Fast Learner", Icons.Default.Bolt),
        Pair("Math Master", Icons.Default.Calculate),
        Pair("Science Explorer", Icons.Default.Science),
        Pair("Consistency King", Icons.Default.WorkspacePremium),
        Pair("Quiz Warrior", Icons.Default.EmojiEvents),
        Pair("AI Scholar", Icons.Default.Psychology)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Tab navigator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val tabs = listOf("analytics" to "Stats", "battles" to "Quiz Duel", "leaderboard" to "Ranks", "badges" to "Badges")
            tabs.forEach { (key, label) ->
                val isSelected = subTab == key
                Button(
                    onClick = { subTab = key },
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

        if (subTab == "analytics") {
            // --- STATISTICS CENTER ---
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
                    Text("SaaS Learning Analytics", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextWhite)

                    // Strength/Weakness Analytics List
                    Text("Subject Analysis Matrix", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PurpleAccent)

                    TopicIndicator(subject = "Mathematics", accuracy = 88, isStrong = true)
                    TopicIndicator(subject = "Biology", accuracy = 91, isStrong = true)
                    TopicIndicator(subject = "Physics", accuracy = 65, isStrong = false)
                    TopicIndicator(subject = "Chemistry", accuracy = 55, isStrong = false)

                    HorizontalDivider(color = Color(0x1F8C9BAE))

                    Text("SaaS System Metrics", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PurpleAccent)
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x0F4F8CFF))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text("Student Engagement", fontSize = 10.sp, color = TextGray)
                                Text("94.2% Active", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x0F7C4DFF))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text("Retention Rate", fontSize = 10.sp, color = TextGray)
                                Text("98.5% SaaS Monthly", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ElectricBlue)
                            }
                        }
                    }

                    // Historic list
                    Text("Syllabus Completion Rates", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PurpleAccent)
                    LinearProgressBarWithLabel("Mathematics Syllabus", 0.75f, EmeraldGreen)
                    LinearProgressBarWithLabel("Physics Mechanics Unit", 0.40f, ElectricBlue)
                    LinearProgressBarWithLabel("Chemistry Formulas Module", 0.30f, PurpleAccent)
                }
            }

        } else if (subTab == "battles") {
            // --- TIMED LEARNING BATTLES (QUIZ DUEL) ---
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (!isBattleActive && activeQuestions.isEmpty()) {
                    // Matchmaker Start Layout
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.FlashOn, "Duel", tint = WarningOrange, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Live Grade 10 Learning Battles", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        Text("Challenge classmates in real-time speed battles. Fast and correct answers secure high placement on school-wide rankings.", fontSize = 12.sp, color = TextGray, textAlign = TextAlign.Center)

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { viewModel.startQuizBattle("Physics") },
                            colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().height(46.dp)
                        ) {
                            Text("Queue Up for Physics Battle Duel", fontWeight = FontWeight.Bold)
                        }
                    }
                } else if (isQuizLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            CircularProgressIndicator(color = PurpleAccent)
                            Text("Finding online opponent... launching quiz core...", color = TextGray, fontSize = 12.sp)
                        }
                    }
                } else if (!quizCompleted) {
                    // Active Timed Battle Duel Layout
                    val q = activeQuestions[currentIndex]
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Live Score indicators
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("You (Samuel)", fontSize = 11.sp, color = ElectricBlue, fontWeight = FontWeight.Bold)
                                Text("$quizScore / ${activeQuestions.size}", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = TextWhite)
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("⏱️ $timerCount s", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DangerRed)
                                Text("BATTLE DUEL", fontSize = 8.sp, fontWeight = FontWeight.ExtraBold, color = WarningOrange)
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("Hana Girma (Opponent)", fontSize = 11.sp, color = PurpleAccent, fontWeight = FontWeight.Bold)
                                Text("$opponentScore / ${activeQuestions.size}", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = TextWhite)
                            }
                        }

                        LinearProgressIndicator(
                            progress = { timerCount.toFloat() / 30f },
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)),
                            color = WarningOrange,
                            trackColor = Color(0x33FF9800)
                        )

                        Text(q.question, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextWhite)

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            q.options.forEachIndexed { idx, opt ->
                                val isSelected = selectedOption == idx
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) PurpleAccent.copy(alpha = 0.2f) else Color(0x0F4F8CFF))
                                        .clickable { viewModel.selectOption(idx) }
                                        .padding(10.dp)
                                ) {
                                    Text(opt, color = TextWhite, fontSize = 13.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Button(
                            onClick = { viewModel.nextQuestion() },
                            enabled = selectedOption != null,
                            colors = ButtonDefaults.buttonColors(containerColor = WarningOrange),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Lock Answer", fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    // Battle Duel Finished
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val playerWon = quizScore >= opponentScore
                        Icon(
                            imageVector = if (playerWon) Icons.Default.EmojiEvents else Icons.Default.MoodBad,
                            contentDescription = "Outcome",
                            tint = if (playerWon) EmeraldGreen else DangerRed,
                            modifier = Modifier.size(72.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (playerWon) "VICTORY! YOU WON!" else "DEFEAT! Hana Won!",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (playerWon) EmeraldGreen else DangerRed
                        )
                        Text(
                            text = "Final score: You $quizScore - $opponentScore Hana",
                            fontSize = 15.sp,
                            color = TextWhite
                        )
                        Text(
                            text = if (playerWon) "+100 XP Challenge Completion Bonus Unlocked!" else "+20 XP quiz participation complete.",
                            fontSize = 12.sp,
                            color = TextGray,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { viewModel.startQuizBattle("Physics") },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Rematch Duel Challenge", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

        } else if (subTab == "leaderboard") {
            // --- WEEKLY RANKS ---
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Weekly School-Wide Leaderboard", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextWhite)

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(leaderboard) { item ->
                            val rank = item.first
                            val name = item.second
                            val xp = item.third
                            val isMe = name.contains("You")
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isMe) PurpleAccent.copy(alpha = 0.2f) else Color(0x0F4F8CFF))
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(if (rank == 1) WarningOrange else ElectricBlue),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("$rank", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(name, color = TextWhite, fontSize = 13.sp, fontWeight = if (isMe) FontWeight.Bold else FontWeight.Medium)
                                }
                                Text("$xp XP", color = EmeraldGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

        } else {
            // --- BADGES SHOWCASE ---
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Unlocked Badges & Achievements", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                    
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(badges) { item ->
                            val name = item.first
                            val icon = item.second
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0x0F00D084))
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color(0x1F00D084)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(icon, contentDescription = name, tint = EmeraldGreen)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(name, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text("Unlocked • 100 XP level bonus granted", color = TextGray, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TopicIndicator(subject: String, accuracy: Int, isStrong: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(subject, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
            Text(if (isStrong) "Strong Topic" else "Needs Spaced Repetition Review", fontSize = 11.sp, color = if (isStrong) EmeraldGreen else DangerRed)
        }
        Text("$accuracy% accuracy", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
    }
}

@Composable
fun LinearProgressBarWithLabel(label: String, progress: Float, color: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, fontSize = 11.sp, color = TextWhite)
            Text("${(progress * 100).toInt()}% completed", fontSize = 11.sp, color = color, fontWeight = FontWeight.Bold)
        }
        LinearProgressIndicator(
            progress = { progress },
            color = color,
            trackColor = Color(0x1F8C9BAE),
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp))
        )
    }
}
