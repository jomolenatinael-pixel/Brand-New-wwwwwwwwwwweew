package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.database.UserProfile
import com.example.ui.components.GlassCard
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    profile: UserProfile,
    onQuickActionClick: (tab: String) -> Unit,
    onAskTutorClick: () -> Unit,
    onGenerateQuizClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Section & Custom Sci-Fi Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Black)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_hero_banner),
                contentDescription = "Futuristic educational space banner",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color(0xCC07111F))
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Welcome Back, ${profile.name} 👋",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Text(
                    text = "You're on a ${profile.streak}-day learning streak! Keep growing.",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = EmeraldGreen
                )
                Text(
                    text = "AI Companion (${profile.companionType}): 'Excellent progress, Samuel! Try the Mathematics challenge today!'",
                    fontSize = 11.sp,
                    color = TextGray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Quick Actions Grid
        Text(
            text = "Quick Actions",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextWhite,
            modifier = Modifier.padding(top = 4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionBtn(
                title = "Ask AI Tutor",
                icon = Icons.Default.AutoAwesome,
                color = ElectricBlue,
                modifier = Modifier.weight(1f),
                onClick = onAskTutorClick
            )
            QuickActionBtn(
                title = "Generate Quiz",
                icon = Icons.Default.Quiz,
                color = PurpleAccent,
                modifier = Modifier.weight(1f),
                onClick = onGenerateQuizClick
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionBtn(
                title = "Open Library",
                icon = Icons.Default.Book,
                color = EmeraldGreen,
                modifier = Modifier.weight(1f),
                onClick = { onQuickActionClick("library") }
            )
            QuickActionBtn(
                title = "Study Planner",
                icon = Icons.Default.CalendarToday,
                color = WarningOrange,
                modifier = Modifier.weight(1f),
                onClick = { onQuickActionClick("planner") }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionBtn(
                title = "Join Community",
                icon = Icons.Default.Forum,
                color = ElectricBlue,
                modifier = Modifier.weight(1f),
                onClick = { onQuickActionClick("community") }
            )
            QuickActionBtn(
                title = "Analytics",
                icon = Icons.Default.BarChart,
                color = PurpleAccent,
                modifier = Modifier.weight(1f),
                onClick = { onQuickActionClick("analytics") }
            )
        }

        // Learning Metrics / Dashboard Stats
        Text(
            text = "Your Metrics",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextWhite,
            modifier = Modifier.padding(top = 4.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Total XP",
                    value = "${profile.xp}",
                    icon = Icons.Default.WorkspacePremium,
                    color = ElectricBlue,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Current Level",
                    value = "Lvl ${profile.level}",
                    icon = Icons.Default.TrendingUp,
                    color = PurpleAccent,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Study Hours",
                    value = "${profile.studyHours} hrs",
                    icon = Icons.Default.AccessTime,
                    color = WarningOrange,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Avg. Quiz Score",
                    value = "${profile.averageScore}%",
                    icon = Icons.Default.DoneAll,
                    color = EmeraldGreen,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "AI Mastery Score",
                    value = "${profile.learningScore}/100",
                    icon = Icons.Default.Psychology,
                    color = PurpleAccent,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Completed Quizzes",
                    value = "14",
                    icon = Icons.Default.School,
                    color = ElectricBlue,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Custom Analytics Canvas Drawing (Saves memory, extremely high-fidelity)
        GlassCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Weekly Learning consistency",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = "+18% this week",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EmeraldGreen
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val width = size.width
                        val height = size.height
                        
                        // Draw grid lines
                        val numGridLines = 4
                        for (i in 0..numGridLines) {
                            val y = height * i / numGridLines
                            drawLine(
                                color = Color(0x1F8C9BAEL),
                                start = Offset(0f, y),
                                end = Offset(width, y),
                                strokeWidth = 2f
                            )
                        }

                        // Draw line graph
                        val points = listOf(0.2f, 0.4f, 0.35f, 0.7f, 0.6f, 0.85f, 0.9f)
                        val stepX = width / (points.size - 1)
                        val drawPoints = points.mapIndexed { idx, value ->
                            Offset(stepX * idx, height * (1 - value))
                        }

                        // Draw path area
                        val fillPath = androidx.compose.ui.graphics.Path().apply {
                            moveTo(0f, height)
                            drawPoints.forEach { lineTo(it.x, it.y) }
                            lineTo(width, height)
                            close()
                        }
                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0x3D4F8CFFL), Color.Transparent)
                            )
                        )

                        // Draw line connection
                        for (i in 0 until drawPoints.size - 1) {
                            drawLine(
                                color = ElectricBlue,
                                start = drawPoints[i],
                                end = drawPoints[i + 1],
                                strokeWidth = 6f,
                                cap = StrokeCap.Round
                            )
                        }

                        // Draw nodes
                        drawPoints.forEach { point ->
                            drawCircle(
                                color = TextWhite,
                                radius = 8f,
                                center = point
                            )
                            drawCircle(
                                color = ElectricBlue,
                                radius = 4f,
                                center = point
                            )
                        }
                    }
                }

                // Days labels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                    days.forEach { day ->
                        Text(
                            text = day,
                            fontSize = 11.sp,
                            color = TextGray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(36.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun QuickActionBtn(
    title: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x0F4F8CFF))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier,
        cornerRadius = 12.dp,
        shadowElevation = 4.dp
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontSize = 11.sp, color = TextGray, fontWeight = FontWeight.Bold)
                Icon(icon, contentDescription = title, tint = color, modifier = Modifier.size(16.dp))
            }
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextWhite
            )
        }
    }
}
