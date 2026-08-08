package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberGold
import com.example.ui.theme.CyberRed
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SensitivitySliderCard(
    title: String,
    subtitle: String,
    value: Int,
    range: IntRange = 0..200,
    icon: ImageVector,
    accentColor: Color = CyberRed,
    testTagPrefix: String,
    onValueChange: (Int) -> Unit,
    recommendedMin: Int = 160,
    recommendedMax: Int = 195,
    modifier: Modifier = Modifier
) {
    val isOptimal = value in recommendedMin..recommendedMax

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("${testTagPrefix}_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                // Numeric readout badge
                Box(
                    modifier = Modifier
                        .background(
                            if (isOptimal) accentColor else DarkSurfaceVariant,
                            RoundedCornerShape(8.dp)
                        )
                        .border(
                            1.dp,
                            if (isOptimal) CyberGold else DarkSurfaceBorder,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "$value",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isOptimal) Color.White else TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Slider
            Slider(
                value = value.toFloat(),
                onValueChange = { onValueChange(it.toInt()) },
                valueRange = range.first.toFloat()..range.last.toFloat(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("${testTagPrefix}_slider"),
                colors = SliderDefaults.colors(
                    thumbColor = accentColor,
                    activeTrackColor = accentColor,
                    inactiveTrackColor = DarkSurfaceVariant
                )
            )

            // Fine tuning step buttons (-5, -1, +1, +5)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    StepButton(label = "-5") { onValueChange((value - 5).coerceIn(range.first, range.last)) }
                    StepButton(label = "-1") { onValueChange((value - 1).coerceIn(range.first, range.last)) }
                }

                Text(
                    text = if (isOptimal) "PERFECT LOCK RANGE" else "REC: $recommendedMin - $recommendedMax",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isOptimal) ElectricCyan else TextSecondary
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    StepButton(label = "+1") { onValueChange((value + 1).coerceIn(range.first, range.last)) }
                    StepButton(label = "+5") { onValueChange((value + 5).coerceIn(range.first, range.last)) }
                }
            }
        }
    }
}

@Composable
private fun StepButton(
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(DarkSurfaceVariant, RoundedCornerShape(6.dp))
            .border(0.8.dp, DarkSurfaceBorder, RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
    }
}
