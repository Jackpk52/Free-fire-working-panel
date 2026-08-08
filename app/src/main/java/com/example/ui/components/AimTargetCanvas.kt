package com.example.ui.components

import android.view.MotionEvent
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInteropFilter
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
import kotlin.math.hypot

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AimTargetCanvas(
    sensitivityGeneral: Int,
    crosshairConfig: CrosshairConfig,
    onDragTestComplete: (speed: Float, durationMs: Long, accuracy: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var touchPath by remember { mutableStateOf(listOf<Offset>()) }
    var touchStartMs by remember { mutableStateOf(0L) }
    var lastOutcome by remember { mutableStateOf<String?>(null) }
    var lastAccuracy by remember { mutableStateOf(0) }
    var totalShots by remember { mutableStateOf(0) }
    var headshotCount by remember { mutableStateOf(0) }
    var rangeMode by remember { mutableStateOf("CLOSE_RANGE") } // CLOSE_RANGE, MID_RANGE, LONG_RANGE

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("aim_target_canvas_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.GpsFixed,
                    contentDescription = null,
                    tint = CyberRed,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "LIVE DRAG HEADSHOT SIMULATOR",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Text(
                        text = "Swipe UP on the target to test drag timing",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                // Range Selector Buttons
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    RangeChip("CLOSE") { rangeMode = "CLOSE_RANGE" }
                    RangeChip("MID") { rangeMode = "MID_RANGE" }
                    RangeChip("FAR") { rangeMode = "LONG_RANGE" }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Canvas Surface
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(DarkCanvas, RoundedCornerShape(14.dp))
                    .border(1.dp, Brush.verticalGradient(listOf(CyberRed.copy(alpha = 0.5f), ElectricCyan.copy(alpha = 0.5f))), RoundedCornerShape(14.dp))
                    .pointerInteropFilter { event ->
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                touchStartMs = System.currentTimeMillis()
                                touchPath = listOf(Offset(event.x, event.y))
                                true
                            }
                            MotionEvent.ACTION_MOVE -> {
                                touchPath = touchPath + Offset(event.x, event.y)
                                true
                            }
                            MotionEvent.ACTION_UP -> {
                                touchPath = touchPath + Offset(event.x, event.y)
                                val durationMs = (System.currentTimeMillis() - touchStartMs).coerceAtLeast(1L)
                                val start = touchPath.firstOrNull() ?: Offset.Zero
                                val end = touchPath.lastOrNull() ?: Offset.Zero

                                val dy = start.y - end.y // Upwards drag distance
                                val dx = end.x - start.x

                                val dragDist = hypot(dx.toDouble(), dy.toDouble()).toFloat()
                                val speedPxPerMs = dragDist / durationMs.toFloat()

                                // Calculate Headshot Accuracy based on drag vector and Sensitivity scaling
                                val sensFactor = sensitivityGeneral / 100f
                                val autoHeadBoost = if (crosshairConfig.isAutoHeadAlignEnabled) 1.25f else 1.0f
                                val adjustedSpeed = speedPxPerMs * sensFactor * autoHeadBoost

                                // Target center is top center of canvas
                                val isUpward = dy > 30f
                                val rawAcc = when {
                                    !isUpward -> 25
                                    adjustedSpeed in 1.1f..5.2f -> (88..100).random()
                                    adjustedSpeed in 0.5f..1.09f -> (55..80).random()
                                    else -> (20..50).random()
                                }
                                val accuracy = if (crosshairConfig.isAutoHeadAlignEnabled && isUpward) {
                                    (rawAcc + 15).coerceAtMost(100)
                                } else rawAcc

                                totalShots++
                                if (accuracy >= 80) headshotCount++

                                lastAccuracy = accuracy
                                lastOutcome = when {
                                    accuracy >= 85 -> "🔴 100% HEADSHOT! RED NUMBERS!"
                                    accuracy >= 60 -> "🟡 BODY DAMAGE - INCREASE SENSITIVITY"
                                    else -> "⚠️ SLOW DRAG / AIM MISSED"
                                }

                                onDragTestComplete(speedPxPerMs, durationMs, accuracy)
                                true
                            }
                            else -> false
                        }
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height

                    // Target Dummy coordinates based on Range
                    val targetY = when (rangeMode) {
                        "CLOSE_RANGE" -> canvasHeight * 0.28f
                        "MID_RANGE" -> canvasHeight * 0.22f
                        else -> canvasHeight * 0.16f
                    }

                    val targetHeadRadius = when (rangeMode) {
                        "CLOSE_RANGE" -> 28.dp.toPx()
                        "MID_RANGE" -> 22.dp.toPx()
                        else -> 16.dp.toPx()
                    }

                    val targetCenterX = canvasWidth / 2f

                    // Draw Background Grid
                    val gridStep = 40.dp.toPx()
                    var x = 0f
                    while (x < canvasWidth) {
                        drawLine(
                            color = DarkSurfaceBorder.copy(alpha = 0.3f),
                            start = Offset(x, 0f),
                            end = Offset(x, canvasHeight),
                            strokeWidth = 1f
                        )
                        x += gridStep
                    }
                    var y = 0f
                    while (y < canvasHeight) {
                        drawLine(
                            color = DarkSurfaceBorder.copy(alpha = 0.3f),
                            start = Offset(0f, y),
                            end = Offset(canvasWidth, y),
                            strokeWidth = 1f
                        )
                        y += gridStep
                    }

                    // Draw Target Dummy
                    // Head (Red Zone)
                    drawCircle(
                        color = CyberRed.copy(alpha = 0.25f),
                        radius = targetHeadRadius * 1.5f,
                        center = Offset(targetCenterX, targetY)
                    )
                    drawCircle(
                        color = CyberRed,
                        radius = targetHeadRadius,
                        center = Offset(targetCenterX, targetY)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = targetHeadRadius * 0.3f,
                        center = Offset(targetCenterX, targetY)
                    )

                    // Chest / Body (Yellow Zone)
                    val bodyWidth = targetHeadRadius * 2.2f
                    val bodyHeight = targetHeadRadius * 3.0f
                    val bodyTop = targetY + targetHeadRadius * 1.2f

                    drawRect(
                        color = CyberGold.copy(alpha = 0.8f),
                        topLeft = Offset(targetCenterX - bodyWidth / 2f, bodyTop),
                        size = androidx.compose.ui.geometry.Size(bodyWidth, bodyHeight)
                    )

                    // Draw Fire Button Indicator at Bottom
                    val fireBtnCenter = Offset(targetCenterX, canvasHeight * 0.82f)
                    drawCircle(
                        color = CyberRed.copy(alpha = 0.3f),
                        radius = 36.dp.toPx(),
                        center = fireBtnCenter
                    )
                    drawCircle(
                        color = CyberRed,
                        radius = 28.dp.toPx(),
                        center = fireBtnCenter,
                        style = Stroke(width = 4f)
                    )

                    // Draw Custom Crosshair Overlay on Target Canvas
                    if (crosshairConfig.isEnabled) {
                        val crosshairPos = if (crosshairConfig.isAutoHeadAlignEnabled) {
                            Offset(targetCenterX, targetY)
                        } else {
                            touchPath.lastOrNull() ?: Offset(targetCenterX, targetY)
                        }
                        val chColor = Color(crosshairConfig.colorHex)
                        val sizePx = crosshairConfig.sizeDp.dp.toPx()
                        val gapPx = crosshairConfig.gapDp.dp.toPx()
                        val strokePx = crosshairConfig.strokeWidthDp.dp.toPx()
                        val dotPx = crosshairConfig.dotSizeDp.dp.toPx()

                        when (crosshairConfig.style) {
                            CrosshairStyle.CROSS_DOT, CrosshairStyle.CLASSIC_CROSS -> {
                                if (crosshairConfig.style == CrosshairStyle.CROSS_DOT) {
                                    drawCircle(color = chColor, radius = dotPx, center = crosshairPos)
                                }
                                drawLine(color = chColor, start = Offset(crosshairPos.x, crosshairPos.y - gapPx), end = Offset(crosshairPos.x, crosshairPos.y - gapPx - sizePx), strokeWidth = strokePx)
                                drawLine(color = chColor, start = Offset(crosshairPos.x, crosshairPos.y + gapPx), end = Offset(crosshairPos.x, crosshairPos.y + gapPx + sizePx), strokeWidth = strokePx)
                                drawLine(color = chColor, start = Offset(crosshairPos.x - gapPx, crosshairPos.y), end = Offset(crosshairPos.x - gapPx - sizePx, crosshairPos.y), strokeWidth = strokePx)
                                drawLine(color = chColor, start = Offset(crosshairPos.x + gapPx, crosshairPos.y), end = Offset(crosshairPos.x + gapPx + sizePx, crosshairPos.y), strokeWidth = strokePx)
                            }
                            CrosshairStyle.CIRCLE_DOT -> {
                                drawCircle(color = chColor, radius = dotPx, center = crosshairPos)
                                drawCircle(color = chColor, radius = sizePx, center = crosshairPos, style = Stroke(width = strokePx))
                            }
                            CrosshairStyle.PURE_DOT -> {
                                drawCircle(color = chColor, radius = sizePx * 0.6f, center = crosshairPos)
                            }
                        }
                    }

                    // Draw Touch Drag Trail
                    if (touchPath.size > 1) {
                        val path = Path()
                        path.moveTo(touchPath.first().x, touchPath.first().y)
                        for (i in 1 until touchPath.size) {
                            path.lineTo(touchPath[i].x, touchPath[i].y)
                        }
                        drawPath(
                            path = path,
                            color = NeonGreen,
                            style = Stroke(width = 6f)
                        )
                    }
                }

                // Instructions Overlay if no shot yet
                if (lastOutcome == null) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.TouchApp,
                            contentDescription = null,
                            tint = CyberGold,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "SWIPE UPWARD FROM FIRE BUTTON",
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "Simulate your in-game drag headshot motion",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                }

                // Outcome Banner Overlay
                lastOutcome?.let { outcome ->
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(12.dp)
                            .background(DarkCanvas.copy(alpha = 0.9f), RoundedCornerShape(10.dp))
                            .border(1.dp, CyberGold, RoundedCornerShape(10.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = outcome,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = TextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Score & Stats Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "HEADSHOT RATE",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                    val rate = if (totalShots > 0) (headshotCount * 100 / totalShots) else 0
                    Text(
                        text = "$rate% ($headshotCount / $totalShots Hits)",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = if (rate >= 70) NeonGreen else CyberGold
                    )
                }

                Button(
                    onClick = {
                        touchPath = emptyList()
                        lastOutcome = null
                        totalShots = 0
                        headshotCount = 0
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = TextPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "RESET", fontSize = 11.sp, color = TextPrimary)
                }
            }
        }
    }
}

@Composable
private fun RangeChip(
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(DarkSurfaceVariant, RoundedCornerShape(6.dp))
            .border(0.8.dp, DarkSurfaceBorder, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .padding(0.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
    }
}
