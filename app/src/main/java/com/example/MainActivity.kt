package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.EduViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: EduViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val profile by viewModel.userProfile.collectAsState()
                val activeTab by viewModel.currentTab.collectAsState()

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DeepNavy),
                    containerColor = DeepNavy,
                    bottomBar = {
                        if (profile != null) {
                            EduBottomNav(
                                activeTab = activeTab,
                                onTabSelected = { viewModel.setCurrentTab(it) }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0x1F7C4DFF), // subtle background glow
                                        DeepNavy
                                    )
                                )
                            )
                    ) {
                        if (profile == null) {
                            OnboardingScreen(
                                onComplete = { name, role, companion, language ->
                                    viewModel.updateProfileNameAndAvatar(name, role, companion, language)
                                }
                            )
                        } else {
                            Column(modifier = Modifier.fillMaxSize()) {
                                // Unified Header
                                EduHeader(
                                    name = profile!!.name,
                                    xp = profile!!.xp,
                                    level = profile!!.level
                                )

                                Box(modifier = Modifier.weight(1f)) {
                                    when (activeTab) {
                                        "dashboard" -> DashboardScreen(
                                            profile = profile!!,
                                            onQuickActionClick = { viewModel.setCurrentTab(it) },
                                            onAskTutorClick = {
                                                viewModel.setCurrentTab("tutor")
                                                viewModel.selectTutorMode("Teach Me")
                                            },
                                            onGenerateQuizClick = {
                                                viewModel.setCurrentTab("tutor")
                                                viewModel.selectTutorMode("Quiz Me Mode")
                                                viewModel.generateAIQuiz("Mathematics")
                                            }
                                        )
                                        "tutor" -> TutorScreen(viewModel = viewModel)
                                        "library" -> LibraryScreen(viewModel = viewModel)
                                        "planner" -> PlannerScreen(viewModel = viewModel)
                                        "community" -> CommunityScreen(viewModel = viewModel)
                                        "analytics" -> AnalyticsScreen(viewModel = viewModel)
                                    }
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
fun EduHeader(
    name: String,
    xp: Int,
    level: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x0F4F8CFF))
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Arekahub AI",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextWhite,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Grade 10 Space",
                    fontSize = 11.sp,
                    color = TextGray
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // XP Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x1F00D084))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$xp XP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldGreen
                    )
                }

                // Level Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x1F7C4DFF))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Lvl $level",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PurpleAccent
                    )
                }
            }
        }
    }
}

@Composable
fun EduBottomNav(
    activeTab: String,
    onTabSelected: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars), // safe gestures area
        color = CardNavy,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val navItems = listOf(
                NavigationItem("dashboard", "Home", Icons.Default.Home),
                NavigationItem("tutor", "AI Tutor", Icons.Default.Psychology),
                NavigationItem("library", "Library", Icons.Default.Book),
                NavigationItem("planner", "Planner", Icons.Default.CalendarToday),
                NavigationItem("community", "Groups", Icons.Default.Forum),
                NavigationItem("analytics", "Duels", Icons.Default.BarChart)
            )

            navItems.forEach { item ->
                val isSelected = activeTab == item.key
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onTabSelected(item.key) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("nav_${item.key}"),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (isSelected) ElectricBlue else TextGray,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = item.label,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) ElectricBlue else TextGray
                    )
                }
            }
        }
    }
}

data class NavigationItem(
    val key: String,
    val label: String,
    val icon: ImageVector
)
