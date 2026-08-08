package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CrosshairConfig
import com.example.model.CrosshairStyle
import com.example.ui.components.CrosshairPreviewCanvas
import com.example.ui.theme.CyberGold
import com.example.ui.theme.CyberRed
import com.example.ui.theme.DarkCanvas
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HudCustomizerScreen(
    crosshairConfig: CrosshairConfig,
    fireButtonSize: Int,
    onCrosshairToggleEnabled: (Boolean) -> Unit,
    onCrosshairToggleAutoHeadAlign: (Boolean) -> Unit,
    onCrosshairStyleSelected: (CrosshairStyle) -> Unit,
    onCrosshairColorSelected: (Long) -> Unit,
    onCrosshairSizeChange: (Int) -> Unit,
    onCrosshairGapChange: (Int) -> Unit,
    onFireButtonSizeChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var buttonYPercent by remember { mutableStateOf(75) } // 50% to 90% screen height

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(12.dp)) }

        // Custom Crosshair Generator
        item {
            CrosshairPreviewCanvas(
                config = crosshairConfig,
                onToggleEnabled = onCrosshairToggleEnabled,
                onToggleAutoHeadAlign = onCrosshairToggleAutoHeadAlign,
                onStyleSelected = onCrosshairStyleSelected,
                onColorSelected = onCrosshairColorSelected,
                onSizeChange = onCrosshairSizeChange,
                onGapChange = onCrosshairGapChange
            )
        }

        // Fire Button Position & Arc Simulator
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hud_placement_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Dashboard,
                            contentDescription = null,
                            tint = CyberGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "FIRE BUTTON HUD POSITION SIMULATOR",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Screen HUD Mockup Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(DarkCanvas, RoundedCornerShape(12.dp))
                            .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp))
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height

                            // Draw HUD Screen Boundary
                            drawRect(
                                color = DarkSurfaceVariant,
                                topLeft = Offset(0f, 0f),
                                size = size,
                                style = Stroke(width = 2f)
                            )

                            // Fire button placement
                            val btnY = h * (buttonYPercent / 100f)
                            val btnX = w * 0.72f
                            val radiusPx = (fireButtonSize * 0.8f).dp.toPx()

                            // Thumb Upward Drag Arc Guidelines
                            val arcStart = Offset(btnX, btnY)
                            val arcEnd = Offset(btnX, btnY - (h * 0.4f))

                            drawLine(
                                color = CyberRed,
                                start = arcStart,
                                end = arcEnd,
                                strokeWidth = 4f
                            )

                            // Draw Fire Button
                            drawCircle(
                                color = CyberRed.copy(alpha = 0.35f),
                                radius = radiusPx * 1.3f,
                                center = arcStart
                            )
                            drawCircle(
                                color = CyberRed,
                                radius = radiusPx,
                                center = arcStart,
                                style = Stroke(width = 4f)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(10.dp)
                                .background(DarkSurfaceVariant, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "SCREEN MOCKUP: $fireButtonSize% SIZE • $buttonYPercent% HEIGHT",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElectricCyan
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Position Height Slider
                    Text(text = "Vertical Button Height position ($buttonYPercent%)", fontSize = 12.sp, color = TextPrimary)
                    Slider(
                        value = buttonYPercent.toFloat(),
                        onValueChange = { buttonYPercent = it.toInt() },
                        valueRange = 55f..88f,
                        colors = SliderDefaults.colors(
                            thumbColor = CyberGold,
                            activeTrackColor = CyberGold
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Size Slider
                    Text(text = "Fire Button Scale Size ($fireButtonSize%)", fontSize = 12.sp, color = TextPrimary)
                    Slider(
                        value = fireButtonSize.toFloat(),
                        onValueChange = { onFireButtonSizeChange(it.toInt()) },
                        valueRange = 30f..80f,
                        colors = SliderDefaults.colors(
                            thumbColor = CyberRed,
                            activeTrackColor = CyberRed
                        )
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }
}
