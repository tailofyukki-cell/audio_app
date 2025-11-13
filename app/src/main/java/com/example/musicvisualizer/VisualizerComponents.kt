package com.example.musicvisualizer

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun BarVisualizer(
    modifier: Modifier = Modifier,
    data: IntArray,
    barColor: Color = Color(0xFF2196F3)
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEach { value ->
            val normalizedValue = (value.toFloat() / 128f).coerceIn(0f, 1f)
            val targetHeight = (normalizedValue * 200).dp
            
            val height by animateDpAsState(
                targetValue = targetHeight,
                animationSpec = tween(durationMillis = 100),
                label = "barHeight"
            )
            
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(height.coerceAtLeast(2.dp))
                    .padding(horizontal = 1.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(color = barColor)
                }
            }
        }
    }
}

@Composable
fun WaveformVisualizer(
    modifier: Modifier = Modifier,
    data: IntArray,
    waveColor: Color = Color(0xFF4CAF50)
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val centerY = height / 2
        val step = width / data.size
        
        for (i in 0 until data.size - 1) {
            val x1 = i * step
            val x2 = (i + 1) * step
            
            val normalizedValue1 = (data[i].toFloat() / 128f).coerceIn(-1f, 1f)
            val normalizedValue2 = (data[i + 1].toFloat() / 128f).coerceIn(-1f, 1f)
            
            val y1 = centerY + (normalizedValue1 * height * 0.4f)
            val y2 = centerY + (normalizedValue2 * height * 0.4f)
            
            drawLine(
                color = waveColor,
                start = Offset(x1, y1),
                end = Offset(x2, y2),
                strokeWidth = 3f,
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun CircularVisualizer(
    modifier: Modifier = Modifier,
    data: IntArray
) {
    Canvas(modifier = modifier) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = min(size.width, size.height) / 3
        val angleStep = 360f / data.size
        
        data.forEachIndexed { index, value ->
            val angle = Math.toRadians((index * angleStep).toDouble())
            val normalizedValue = (value.toFloat() / 128f).coerceIn(0f, 1f)
            val barLength = normalizedValue * radius * 0.8f
            
            val startX = centerX + (radius * cos(angle)).toFloat()
            val startY = centerY + (radius * sin(angle)).toFloat()
            val endX = centerX + ((radius + barLength) * cos(angle)).toFloat()
            val endY = centerY + ((radius + barLength) * sin(angle)).toFloat()
            
            val hue = (index.toFloat() / data.size) * 360f
            val color = Color.hsv(hue, 0.8f, 1f)
            
            drawLine(
                color = color,
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = 4f,
                cap = StrokeCap.Round
            )
        }
        
        // 中心の円
        drawCircle(
            color = Color.White,
            radius = radius * 0.1f,
            center = Offset(centerX, centerY)
        )
    }
}

@Composable
fun SpectrumVisualizer(
    modifier: Modifier = Modifier,
    data: IntArray
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val barWidth = width / data.size
        
        val gradientColors = listOf(
            Color(0xFF00BCD4),
            Color(0xFF2196F3),
            Color(0xFF9C27B0),
            Color(0xFFE91E63),
            Color(0xFFFF5722)
        )
        
        data.forEachIndexed { index, value ->
            val normalizedValue = (value.toFloat() / 128f).coerceIn(0f, 1f)
            val barHeight = normalizedValue * height
            
            val x = index * barWidth
            val colorIndex = (index.toFloat() / data.size * (gradientColors.size - 1)).toInt()
            val nextColorIndex = min(colorIndex + 1, gradientColors.size - 1)
            val colorProgress = (index.toFloat() / data.size * (gradientColors.size - 1)) - colorIndex
            
            val startColor = gradientColors[colorIndex]
            val endColor = gradientColors[nextColorIndex]
            
            val brush = Brush.verticalGradient(
                colors = listOf(endColor, startColor),
                startY = height - barHeight,
                endY = height
            )
            
            drawRect(
                brush = brush,
                topLeft = Offset(x, height - barHeight),
                size = androidx.compose.ui.geometry.Size(barWidth - 2f, barHeight)
            )
        }
    }
}
