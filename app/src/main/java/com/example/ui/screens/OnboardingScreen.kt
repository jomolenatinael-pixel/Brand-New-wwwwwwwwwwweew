package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.School
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
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onComplete: (name: String, role: String, companion: String, language: String) -> Unit
) {
    var name by remember { mutableStateOf("Samuel Ayele") }
    var selectedRole by remember { mutableStateOf("Student") }
    var selectedCompanion by remember { mutableStateOf("Genius Owl") }
    var selectedLanguage by remember { mutableStateOf("English") }

    val roles = listOf("Student", "Teacher", "Moderator", "Admin")
    val companions = listOf(
        Pair("Genius Owl", "🦉 Sage guidance & critical Socratic thinking."),
        Pair("Robo Teacher", "🤖 Clear sequential step-by-step concepts."),
        Pair("Study Dragon", "🐉 High motivation, streaks & energy!"),
        Pair("Future Scientist", "🔬 Highlights high-yield formulas & career notes.")
    )
    val languages = listOf("English", "Amharic", "Afaan Oromo", "Tigrinya")

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Futuristic Header
        Icon(
            imageVector = Icons.Default.School,
            contentDescription = "Arekahub Logo",
            tint = ElectricBlue,
            modifier = Modifier.size(64.dp)
        )

        Text(
            text = "AREKAHUB",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextWhite,
            letterSpacing = 4.sp,
            textAlign = TextAlign.Center
        )
        
        Text(
            text = "EduVerse AI • Grade 10 Platform",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = PurpleAccent,
            letterSpacing = 1.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        GlassCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Personalize Your Interface",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )

                // Name Input
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Student Full Name", color = TextGray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = TextGray,
                        focusedLabelColor = ElectricBlue,
                        unfocusedLabelColor = TextGray
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("onboarding_name_input"),
                    singleLine = true
                )

                // Role Selector
                Text(
                    text = "Select Workspace Role",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextGray
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    roles.forEach { role ->
                        val isSelected = selectedRole == role
                        Button(
                            onClick = { selectedRole = role },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) ElectricBlue else Color(0x1F4F8CFF),
                                contentColor = TextWhite
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("role_btn_$role"),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(role, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // Companion Avatar Grid
        GlassCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Choose Your AI Companion",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                
                companions.forEach { (compName, desc) ->
                    val isSelected = selectedCompanion == compName
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedCompanion = compName }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { selectedCompanion = compName },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = PurpleAccent,
                                unselectedColor = TextGray
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = compName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) PurpleAccent else TextWhite
                            )
                            Text(
                                text = desc,
                                fontSize = 12.sp,
                                color = TextGray
                            )
                        }
                    }
                }
            }
        }

        // Language Selector
        GlassCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Portal Display Language",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    languages.forEach { lang ->
                        val isSelected = selectedLanguage == lang
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) EmeraldGreen else Color(0x1F00D084))
                                .clickable { selectedLanguage = lang }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = lang,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Complete Button
        Button(
            onClick = {
                if (name.isNotBlank()) {
                    onComplete(name, selectedRole, selectedCompanion, selectedLanguage)
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = ElectricBlue
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("onboarding_complete_button")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Launch Arekahub Portal",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Forward Icon",
                    tint = TextWhite
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
