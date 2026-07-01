package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.FrostedGlass
import com.example.ui.theme.FrostedGlassBorder

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    shadowElevation: Dp = 8.dp,
    borderWidth: Dp = 1.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val glassShape = RoundedCornerShape(cornerRadius)
    
    Box(
        modifier = modifier
            .shadow(
                elevation = shadowElevation,
                shape = glassShape,
                clip = false,
                ambientColor = Color(0x3D4F8CFF),
                spotColor = Color(0x3D7C4DFF)
            )
            .clip(glassShape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        FrostedGlass,
                        Color(0x0A07111F)
                    )
                )
            )
            .border(
                width = borderWidth,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        FrostedGlassBorder,
                        Color(0x1A7C4DFF)
                    )
                ),
                shape = glassShape
            )
            .padding(16.dp)
    ) {
        content()
    }
}
