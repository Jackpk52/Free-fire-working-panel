package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CrosshairConfig
import com.example.model.SensitivityData
import com.example.ui.DragTestResult
import com.example.ui.components.AimTargetCanvas
import com.example.ui.theme.CyberGold
import com.example.ui.theme.CyberRed
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AimPracticeScreen(
    sensitivity: SensitivityData,
    crosshairConfig: CrosshairConfig,
    dragTestResult: DragTestResult?,
    onDragTestComplete: (speed: Float, durationMs: Long, accuracy: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(12.dp)) }

        // Live Aim Drag Simulator Canvas
        item {
            AimTargetCanvas(
                sensitivityGeneral = sensitivity.general,
                crosshairConfig = crosshairConfig,
                onDragTestComplete = onDragTestComplete
            )
        }

        // Real-time Gesture Analytics Dashboard
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("aim_analytics_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = null,
                            tint = ElectricCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "TOUCH DRAG TELEMETRY ANALYTICS",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (dragTestResult == null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(DarkSurfaceVariant, RoundedCornerShape(10.dp))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Swipe on the target above to calculate drag acceleration...",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    } else {
                        // Metric 1: Drag Velocity
                        MetricRow(
                            icon = Icons.Default.Speed,
                            label = "Drag Upward Speed",
                            value = "%.2f px/ms".format(dragTestResult.dragSpeed),
                            statusColor = ElectricCyan
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Metric 2: Gesture Duration
                        MetricRow(
                            icon = Icons.Default.Timer,
                            label = "Swipe Execution Time",
                            value = "${dragTestResult.dragTimeMs} ms",
                            statusColor = CyberGold
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Metric 3: Calculated Accuracy
                        MetricRow(
                            icon = Icons.Default.SportsScore,
                            label = "Headshot Collision Rating",
                            value = "${dragTestResult.headshotAccuracy}%",
                            statusColor = if (dragTestResult.headshotAccuracy >= 75) NeonGreen else CyberRed
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Diagnostic Feedback Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CyberRed.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                                .border(1.dp, CyberRed, RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = CyberRed,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = dragTestResult.feedbackMessage,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Training Guide Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🎯 PRO DRAG HEADSHOT TECHNIQUE TIPS",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = CyberGold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    BulletPoint("• Straight Upward Drag: For enemies running straight toward or away from you.")
                    BulletPoint("• J-Shape Curve Drag: For enemies moving laterally across your field of view.")
                    BulletPoint("• Fire Button Placement: Place lower on your screen so thumb has room to drag upward.")
                    BulletPoint("• Touch Powder / Screen Clean: Ensure screen glass is smooth to prevent drag friction.")
                }
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }
}

@Composable
private fun MetricRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    statusColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(statusColor.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = statusColor, modifier = Modifier.size(14.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = label, fontSize = 12.sp, color = TextSecondary)
        }
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Black, color = statusColor)
    }
}

@Composable
private fun BulletPoint(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        color = TextPrimary,
        modifier = Modifier.padding(vertical = 3.dp)
    )
}
