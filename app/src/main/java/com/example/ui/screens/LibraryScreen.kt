package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.vector.ImageVector
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
fun LibraryScreen(
    viewModel: EduViewModel
) {
    val scrollState = rememberScrollState()

    var activeSubTab by remember { mutableStateOf("textbooks") } // textbooks, pdfs, video
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All", "Mathematics", "Biology", "Chemistry", "Physics", "English", "ICT")
    
    val textbooks: List<Triple<String, String, ImageVector>> = listOf(
        Triple("Grade 10 Mathematics", "Mathematics", Icons.Default.Calculate),
        Triple("Grade 10 Biology", "Biology", Icons.Default.Coronavirus),
        Triple("Grade 10 Physics", "Physics", Icons.Default.Bolt),
        Triple("Grade 10 Chemistry", "Chemistry", Icons.Default.Science),
        Triple("Grade 10 English Literature", "English", Icons.Default.Translate),
        Triple("Grade 10 ICT Essentials", "ICT", Icons.Default.Computer)
    )

    val uploadedPdfs by viewModel.uploadedPdfs.collectAsState()
    val pdfSummary by viewModel.pdfSummary.collectAsState()
    val pdfConcepts by viewModel.pdfConcepts.collectAsState()
    val isPdfProcessing by viewModel.isPdfProcessing.collectAsState()

    // Video states
    var isVideoPlaying by remember { mutableStateOf(false) }
    var videoSpeed by remember { mutableStateOf("1.0x") }
    var videoSearchText by remember { mutableStateOf("") }

    val transcripts = listOf(
        Pair("00:15", "Let's first define Newton's Second Law of Motion."),
        Pair("01:30", "Force equals mass times acceleration (F=ma)."),
        Pair("03:10", "Now we look at some real examples of mechanics."),
        Pair("05:45", "In summary, acceleration depends on the net force.")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Sub-navigation: Textbooks, PDFs, Video
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val tabs = listOf("textbooks" to "Textbooks", "pdfs" to "Smart PDFs", "video" to "Video Hub")
            tabs.forEach { (key, label) ->
                val isSelected = activeSubTab == key
                Button(
                    onClick = { activeSubTab = key },
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

        if (activeSubTab == "textbooks") {
            // --- TEXTBOOKS BROWSER ---
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search textbooks, highlights, bookmarks...", color = TextGray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedBorderColor = ElectricBlue,
                    unfocusedBorderColor = Color(0x334F8CFF)
                ),
                leadingIcon = { Icon(Icons.Default.Search, "Search", tint = TextGray) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Category scrolling row
            Text("Categories", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextGray)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth().height(36.dp)) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                categories.forEach { cat ->
                                    val isSelected = selectedCategory == cat
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) PurpleAccent else Color(0x1F7C4DFF))
                                            .clickable { selectedCategory = cat }
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(cat, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Textbooks Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val filtered = textbooks.filter {
                    (selectedCategory == "All" || it.second == selectedCategory) &&
                    (searchQuery.isBlank() || it.first.contains(searchQuery, ignoreCase = true))
                }
                items(filtered) { item ->
                    val title = item.first
                    val subject = item.second
                    val icon = item.third
                    TextbookCard(title = title, subject = subject, icon = icon)
                }
            }

        } else if (activeSubTab == "pdfs") {
            // --- SMART PDF KNOWLEDGE BASE ---
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Smart PDF Knowledge System", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                    Text("Upload chapters. Our AI automatically extracts summaries, key concepts, creates custom study cards and quizzes.", fontSize = 12.sp, color = TextGray)

                    Button(
                        onClick = { viewModel.uploadMockPdf("Chemistry Unit 4 - Chemical Bonding.pdf") },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CloudUpload, "Upload")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Simulate Teacher PDF Upload", fontWeight = FontWeight.Bold)
                    }

                    HorizontalDivider(color = Color(0x1F8C9BAE))

                    Text("Active Knowledge Bases", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PurpleAccent)

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uploadedPdfs) { pdf ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0x0F4F8CFF))
                                    .clickable { viewModel.uploadMockPdf(pdf) }
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.PictureAsPdf, "PDF", tint = DangerRed)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(pdf, color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                                Icon(Icons.Default.CheckCircle, "Active", tint = EmeraldGreen, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    if (isPdfProcessing) {
                        Box(modifier = Modifier.fillMaxWidth().padding(8.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = EmeraldGreen, modifier = Modifier.size(24.dp))
                        }
                    } else if (pdfSummary.isNotBlank()) {
                        // Display generated content
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x0F00D084))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("AI EXTRACED SUMMARY", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = EmeraldGreen)
                            Text(pdfSummary, fontSize = 12.sp, color = TextWhite)
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("CORE CONCEPTS IDENTIFIED", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = PurpleAccent)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                pdfConcepts.forEach { concept ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0x1A7C4DFF))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(concept, fontSize = 10.sp, color = TextWhite)
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } else {
            // --- VIDEO LEARNING CENTER ---
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Video Lessons & Transcripts", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextWhite)

                    // Video Player Simulator Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(
                                onClick = { isVideoPlaying = !isVideoPlaying },
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(ElectricBlue)
                            ) {
                                Icon(
                                    imageVector = if (isVideoPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play",
                                    tint = TextWhite,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (isVideoPlaying) "Playing: Mechanics Unit 1 (02:15 / 15:40)" else "Video Paused",
                                fontSize = 11.sp,
                                color = TextGray
                            )
                        }
                    }

                    // Video Controls Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("1.0x", "1.25x", "1.5x", "2.0x").forEach { spd ->
                                val isSelected = videoSpeed == spd
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) ElectricBlue else Color(0x1F4F8CFF))
                                    .clickable { videoSpeed = spd }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(spd, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                                }
                            }
                        }
                        Text("1080p HD", fontSize = 10.sp, color = EmeraldGreen, fontWeight = FontWeight.Bold)
                    }

                    HorizontalDivider(color = Color(0x1F8C9BAE))

                    Text("AI Synced Notes & Transcript Search", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PurpleAccent)

                    OutlinedTextField(
                        value = videoSearchText,
                        onValueChange = { videoSearchText = it },
                        placeholder = { Text("Search words inside video...", color = TextGray, fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = PurpleAccent,
                            unfocusedBorderColor = Color(0x1F7C4DFF)
                        ),
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        singleLine = true
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val filtered = transcripts.filter {
                            videoSearchText.isBlank() || it.second.contains(videoSearchText, ignoreCase = true)
                        }
                        items(filtered) { (time, note) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0x334F8CFF))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(time, fontSize = 10.sp, color = ElectricBlue, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(note, fontSize = 12.sp, color = TextWhite)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TextbookCard(
    title: String,
    subject: String,
    icon: ImageVector
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp),
        cornerRadius = 12.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x1A4F8CFF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = ElectricBlue)
            }
            Column {
                Text(subject.uppercase(), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = PurpleAccent)
                Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
            }
        }
    }
}
