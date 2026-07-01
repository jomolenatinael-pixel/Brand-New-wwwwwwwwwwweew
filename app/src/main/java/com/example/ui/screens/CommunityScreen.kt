package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.EduViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    viewModel: EduViewModel
) {
    val activeChannel by viewModel.activeChannel.collectAsState()
    val messages by viewModel.chatMessages.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    val channels = listOf("#general", "#math-prep", "#physics-lab", "#exam-study")

    var inputMessage by remember { mutableStateOf("") }

    // Custom Poll states
    var voteCountA by remember { mutableStateOf(14) }
    var voteCountB by remember { mutableStateOf(6) }
    var hasVoted by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Left Channel Bar (Discord style)
        Column(
            modifier = Modifier
                .width(100.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "CHANNELS",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextGray,
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
            )

            channels.forEach { chan ->
                val isActive = activeChannel == chan
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isActive) PurpleAccent else Color(0x0F4F8CFF))
                        .clickable { viewModel.selectChannel(chan) }
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = chan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isActive) TextWhite else TextGray
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Community Guidelines tip
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(DangerRed.copy(alpha = 0.15f))
                    .padding(8.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Icon(Icons.Default.Shield, "Shield", tint = EmeraldGreen, modifier = Modifier.size(16.dp))
                    Text("AI SAFE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                    Text("Moderation active", fontSize = 8.sp, color = TextGray)
                }
            }
        }

        // Right Main Content Pane (Chats & Active Study Poll)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Interactive Study Poll Card
            GlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Poll, "Poll", tint = PurpleAccent, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Active Group Study Poll", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                    }
                    Text("Which topic should we request the AI Tutor to host a Live Duel on tonight?", fontSize = 11.sp, color = TextGray)
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (!hasVoted) {
                                    voteCountA += 1
                                    hasVoted = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0x1F4F8CFF)),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Quadratic roots ($voteCountA)", fontSize = 10.sp, color = ElectricBlue)
                        }

                        Button(
                            onClick = {
                                if (!hasVoted) {
                                    voteCountB += 1
                                    hasVoted = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0x1F7C4DFF)),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Covalent bonding ($voteCountB)", fontSize = 10.sp, color = PurpleAccent)
                        }
                    }
                }
            }

            // Chat Messages Log Box
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "Viewing $activeChannel",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricBlue
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(messages) { msg ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Avatar Circle
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(if (msg.senderRole == "Teacher") EmeraldGreen else PurpleAccent),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = msg.senderName.take(1),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = msg.senderName,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextWhite
                                        )
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(if (msg.senderRole == "Teacher") Color(0x3300D084) else Color(0x337C4DFF))
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = msg.senderRole,
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (msg.senderRole == "Teacher") EmeraldGreen else PurpleAccent
                                            )
                                        }
                                    }

                                    Text(
                                        text = msg.messageText,
                                        fontSize = 12.sp,
                                        color = if (msg.isFlagged) DangerRed else TextWhite,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Chat input row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedTextField(
                    value = inputMessage,
                    onValueChange = { inputMessage = it },
                    placeholder = { Text("Chat in $activeChannel...", color = TextGray, fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = Color(0x334F8CFF)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("community_chat_input"),
                    singleLine = true
                )

                IconButton(
                    onClick = {
                        if (inputMessage.isNotBlank()) {
                            viewModel.sendCommunityMessage(
                                text = inputMessage,
                                senderName = userProfile?.name ?: "Samuel Ayele",
                                senderRole = userProfile?.role ?: "Student",
                                senderAvatar = userProfile?.companionType ?: "Genius Owl"
                            )
                            inputMessage = ""
                        }
                    },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(ElectricBlue)
                        .size(40.dp)
                        .testTag("community_send_btn")
                ) {
                    Icon(Icons.Default.Send, "Send", tint = TextWhite, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
