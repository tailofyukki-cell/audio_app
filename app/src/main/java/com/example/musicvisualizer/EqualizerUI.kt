package com.example.musicvisualizer

import androidx.compose.foundation.Canvas // ← この行を追加
import androidx.compose.material3.ExperimentalMaterial3Api // このimport文がなければ追加
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EqualizerPanel(
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    currentPreset: EqualizerManager.EqualizerPreset,
    onPresetChange: (EqualizerManager.EqualizerPreset) -> Unit,
    bandGains: List<Int>,
    onBandGainChange: (Int, Int) -> Unit,
    isEnabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
    gainRange: Pair<Int, Int>,
    bandFrequencies: List<Int>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // ヘッダー
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "🎚️",
                        fontSize = 24.sp
                    )
                    Text(
                        text = "GEQ",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // ON/OFFスイッチ
                    Switch(
                        checked = isEnabled,
                        onCheckedChange = onEnabledChange,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF4CAF50),
                            checkedTrackColor = Color(0xFF81C784)
                        )
                    )
                    
                    // 展開/折りたたみボタン
                    FilledTonalButton(
                        onClick = { onExpandedChange(!isExpanded) },
                        modifier = Modifier.height(48.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(0xFF2E2E2E),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isExpanded) "折りたたむ" else "展開",
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isExpanded) "閉じる" else "開く",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            
            if (isExpanded && isEnabled) {
                Spacer(modifier = Modifier.height(16.dp))
                
                // プリセット選択
                PresetSelector(
                    currentPreset = currentPreset,
                    onPresetChange = onPresetChange
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 10バンドイコライザー（水平スクロール可能）
                EqualizerBands(
                    bandGains = bandGains,
                    onBandGainChange = onBandGainChange,
                    gainRange = gainRange,
                    bandFrequencies = bandFrequencies
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class) // この行を追加
@Composable
fun PresetSelector(
    currentPreset: EqualizerManager.EqualizerPreset,
    onPresetChange: (EqualizerManager.EqualizerPreset) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Column {
        Text(
            text = "プリセット",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = currentPreset.displayName,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF4CAF50),
                    unfocusedBorderColor = Color.Gray
                ),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color(0xFF2E2E2E))
            ) {
                EqualizerManager.EqualizerPreset.values().forEach { preset ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = preset.displayName,
                                color = Color.White
                            )
                        },
                        onClick = {
                            onPresetChange(preset)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EqualizerBands(
    bandGains: List<Int>,
    onBandGainChange: (Int, Int) -> Unit,
    gainRange: Pair<Int, Int>,
    bandFrequencies: List<Int>
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "周波数帯域（10バンド）",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Text(
                text = "◁ スワイプ ▷",
                fontSize = 11.sp,
                color = Color(0xFF4CAF50),
                fontWeight = FontWeight.Medium
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 水平スクロール可能なコンテナ
        val scrollState = rememberScrollState()
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            
            bandGains.forEachIndexed { index, gain ->
                VerticalEqualizerBand(
                    bandIndex = index,
                    frequency = bandFrequencies.getOrNull(index) ?: 0,
                    gain = gain,
                    onGainChange = { newGain ->
                        onBandGainChange(index, newGain)
                    },
                    gainRange = gainRange
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
        }
        
        // スクロールインジケーター
        if (scrollState.maxValue > 0) {
            Spacer(modifier = Modifier.height(8.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(Color(0xFF2E2E2E), RoundedCornerShape(2.dp))
            ) {
                val progress = scrollState.value.toFloat() / scrollState.maxValue.toFloat()
                val indicatorWidth = 0.3f // 30%の幅
                
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(indicatorWidth)
                        .offset(x = (progress * (1f - indicatorWidth) * 100).dp)
                        .background(Color(0xFF4CAF50), RoundedCornerShape(2.dp))
                )
            }
        }
    }
}

@Composable
fun VerticalEqualizerBand(
    bandIndex: Int,
    frequency: Int,
    gain: Int,
    onGainChange: (Int) -> Unit,
    gainRange: Pair<Int, Int>
) {
    val sliderHeight = 140.dp
    
    Column(
        modifier = Modifier
            .width(48.dp)
            .height(220.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // ゲイン表示
        Text(
            text = if (gain > 0) "+$gain" else "$gain",
            fontSize = 11.sp,
            color = when {
                gain > 0 -> Color(0xFF4CAF50)
                gain < 0 -> Color(0xFFF44336)
                else -> Color.Gray
            },
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.height(20.dp)
        )
        
        // 縦スライダー
        VerticalSlider(
            value = gain.toFloat(),
            onValueChange = { onGainChange(it.roundToInt()) },
            valueRange = gainRange.first.toFloat()..gainRange.second.toFloat(),
            modifier = Modifier
                .width(48.dp)
                .height(sliderHeight)
        )
        
        // 周波数表示
        Text(
            text = formatFrequency(frequency),
            fontSize = 10.sp,
            color = Color.White,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.height(24.dp),
            lineHeight = 11.sp
        )
    }
}

@Composable
fun VerticalSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier
) {
    var sliderValue by remember { mutableStateOf(value) }
    
    LaunchedEffect(value) {
        sliderValue = value
    }
    
    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val height = size.height
                        val newValue = valueRange.endInclusive - 
                            ((offset.y / height) * (valueRange.endInclusive - valueRange.start))
                        sliderValue = newValue.coerceIn(valueRange)
                        onValueChange(sliderValue)
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        val height = size.height
                        val newValue = valueRange.endInclusive - 
                            ((change.position.y / height) * (valueRange.endInclusive - valueRange.start))
                        sliderValue = newValue.coerceIn(valueRange)
                        onValueChange(sliderValue)
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val trackWidth = 5.dp.toPx()
            val thumbRadius = 12.dp.toPx()
            
            // トラック（背景）
            drawLine(
                color = Color.Gray,
                start = Offset(width / 2, thumbRadius),
                end = Offset(width / 2, height - thumbRadius),
                strokeWidth = trackWidth,
                cap = StrokeCap.Round
            )
            
            // 値に応じた位置を計算
            val normalizedValue = (sliderValue - valueRange.start) / (valueRange.endInclusive - valueRange.start)
            val thumbY = height - thumbRadius - (normalizedValue * (height - 2 * thumbRadius))
            
            // アクティブトラック（0dBから現在値まで）
            val zeroValue = 0f
            val normalizedZero = (zeroValue - valueRange.start) / (valueRange.endInclusive - valueRange.start)
            val zeroY = height - thumbRadius - (normalizedZero * (height - 2 * thumbRadius))
            
            val activeColor = when {
                sliderValue > 0 -> Color(0xFF4CAF50)
                sliderValue < 0 -> Color(0xFFF44336)
                else -> Color.Gray
            }
            
            drawLine(
                color = activeColor,
                start = Offset(width / 2, zeroY),
                end = Offset(width / 2, thumbY),
                strokeWidth = trackWidth,
                cap = StrokeCap.Round
            )
            
            // 0dBマーカー
            drawCircle(
                color = Color.White,
                radius = 3.dp.toPx(),
                center = Offset(width / 2, zeroY)
            )
            
            // つまみ
            drawCircle(
                color = activeColor,
                radius = thumbRadius,
                center = Offset(width / 2, thumbY)
            )
            
            // つまみの内側
            drawCircle(
                color = Color.White,
                radius = thumbRadius * 0.5f,
                center = Offset(width / 2, thumbY)
            )
        }
    }
}

fun formatFrequency(frequency: Int): String {
    return when {
        frequency >= 1000 -> "${frequency / 1000}k"
        else -> "$frequency"
    }
}
