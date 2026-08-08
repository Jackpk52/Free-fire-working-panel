package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.CyberGold
import com.example.ui.theme.CyberRed
import com.example.ui.theme.DarkCanvas
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CrosshairPreviewCanvas(
    config: CrosshairConfig,
    onStyleSelected: (CrosshairStyle) -> Unit,
    onColorSelected: (Long) -> Unit,
    onSizeChange: (Int) -> Unit,
    onGapChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("crosshair_preview_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CenterFocusStrong,
                    contentDescription = null,
                    tint = ElectricCyan,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "CUSTOM CROSSHAIR OVERLAY PREVIEW",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Crosshair Live Canvas Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(DarkCanvas, RoundedCornerShape(14.dp))
                    .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val crosshairColor = Color(config.colorHex)
                    val sizePx = config.sizeDp.dp.toPx()
                    val gapPx = config.gapDp.dp.toPx()
                    val strokePx = config.strokeWidthDp.dp.toPx()
                    val dotPx = config.dotSizeDp.dp.toPx()

                    when (config.style) {
                        CrosshairStyle.CROSS_DOT, CrosshairStyle.CLASSIC_CROSS -> {
                            // Draw Center Dot
                            if (config.style == CrosshairStyle.CROSS_DOT) {
                                drawCircle(color = crosshairColor, radius = dotPx, center = center)
                            }
                            // Top line
                            drawLine(
                                color = crosshairColor,
                                start = Offset(center.x, center.y - gapPx),
                                end = Offset(center.x, center.y - gapPx - sizePx),
                                strokeWidth = strokePx
                            )
                            // Bottom line
                            drawLine(
                                color = crosshairColor,
                                start = Offset(center.x, center.y + gapPx),
                                end = Offset(center.x, center.y + gapPx + sizePx),
                                strokeWidth = strokePx
                            )
                            // Left line
                            drawLine(
                                color = crosshairColor,
                                start = Offset(center.x - gapPx, center.y),
                                end = Offset(center.x - gapPx - sizePx, center.y),
                                strokeWidth = strokePx
                            )
                            // Right line
                            drawLine(
                                color = crosshairColor,
                                start = Offset(center.x + gapPx, center.y),
                                end = Offset(center.x + gapPx + sizePx, center.y),
                                strokeWidth = strokePx
                            )
                        }
                        CrosshairStyle.CIRCLE_DOT -> {
                            // Center Dot
                            drawCircle(color = crosshairColor, radius = dotPx, center = center)
                            // Circle Outer Ring
                            drawCircle(
                                color = crosshairColor,
                                radius = sizePx,
                                center = center,
                                style = Stroke(width = strokePx)
                            )
                        }
                        CrosshairStyle.PURE_DOT -> {
                            drawCircle(color = crosshairColor, radius = sizePx * 0.6f, center = center)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Crosshair Styles
            Text(
                text = "Crosshair Style",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CrosshairStyle.entries.forEach { style ->
                    val isSel = config.style == style
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                if (isSel) ElectricCyan.copy(alpha = 0.2f) else DarkSurfaceVariant,
                                RoundedCornerShape(8.dp)
                            )
                            .border(
                                1.dp,
                                if (isSel) ElectricCyan else DarkSurfaceBorder,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { onStyleSelected(style) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = style.title,
                            fontSize = 10.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSel) ElectricCyan else TextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Crosshair Color Palette
            Text(
                text = "Crosshair Color",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf(
                    0xFF00E676 to "Neon Green",
                    0xFFFF2A42 to "Cyber Red",
                    0xFF00E5FF to "Electric Cyan",
                    0xFFFFB800 to "Fire Gold",
                    0xFFFFFFFF to "Pure White",
                    0xFFFF4081 to "Vibrant Pink"
                ).forEach { (colorLong, name) ->
                    val isSel = config.colorHex == colorLong
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(colorLong), CircleShape)
                            .border(
                                if (isSel) 3.dp else 1.dp,
                                if (isSel) Color.White else DarkSurfaceBorder,
                                CircleShape
                            )
                            .clickable { onColorSelected(colorLong) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Size Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Crosshair Scale", fontSize = 12.sp, color = TextPrimary)
                Text(text = "${config.sizeDp} dp", fontSize = 12.sp, color = ElectricCyan, fontWeight = FontWeight.Bold)
            }
            Slider(
                value = config.sizeDp.toFloat(),
                onValueChange = { onSizeChange(it.toInt()) },
                valueRange = 10f..40f,
                colors = SliderDefaults.colors(
                    thumbColor = ElectricCyan,
                    activeTrackColor = ElectricCyan
                )
            )

            // Gap Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Gap Offset", fontSize = 12.sp, color = TextPrimary)
                Text(text = "${config.gapDp} dp", fontSize = 12.sp, color = ElectricCyan, fontWeight = FontWeight.Bold)
            }
            Slider(
                value = config.gapDp.toFloat(),
                onValueChange = { onGapChange(it.toInt()) },
                valueRange = 0f..20f,
                colors = SliderDefaults.colors(
                    thumbColor = ElectricCyan,
                    activeTrackColor = ElectricCyan
                )
            )
        }
    }
}
