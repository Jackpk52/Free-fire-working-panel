package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.model.DeviceSpecs
import com.example.model.Playstyle
import com.example.model.ProcessorTier
import com.example.ui.theme.CyberGold
import com.example.ui.theme.CyberRed
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DeviceSpecSelector(
    specs: DeviceSpecs,
    selectedPlaystyle: Playstyle,
    onRamSelected: (Int) -> Unit,
    onRefreshRateSelected: (Int) -> Unit,
    onProcessorSelected: (ProcessorTier) -> Unit,
    onPlaystyleSelected: (Playstyle) -> Unit,
    onAutoCalculateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("device_spec_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Memory,
                    contentDescription = null,
                    tint = CyberGold,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "DEVICE & PLAYSTYLE PROFILE",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // RAM Selection
            Text(
                text = "Device RAM Size",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(2, 3, 4, 6, 8, 12).forEach { ram ->
                    ChipButton(
                        label = "${ram}GB RAM",
                        isSelected = specs.ramGb == ram,
                        testTag = "ram_chip_$ram",
                        onClick = { onRamSelected(ram) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Refresh Rate Selection
            Text(
                text = "Screen Refresh Rate",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(60, 90, 120, 144).forEach { hz ->
                    ChipButton(
                        label = "${hz}Hz",
                        isSelected = specs.refreshRateHz == hz,
                        testTag = "hz_chip_$hz",
                        onClick = { onRefreshRateSelected(hz) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Processor Tier Selection
            Text(
                text = "Processor Class",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            ProcessorTier.entries.forEach { tier ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                        .background(
                            if (specs.processorTier == tier) DarkSurfaceVariant else Color.Transparent,
                            RoundedCornerShape(8.dp)
                        )
                        .border(
                            1.dp,
                            if (specs.processorTier == tier) ElectricCyan else DarkSurfaceBorder,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { onProcessorSelected(tier) }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = tier.displayName,
                        fontSize = 12.sp,
                        fontWeight = if (specs.processorTier == tier) FontWeight.Bold else FontWeight.Normal,
                        color = if (specs.processorTier == tier) ElectricCyan else TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Playstyle Selection
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.SportsEsports,
                    contentDescription = null,
                    tint = CyberRed,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Preferred Gaming Style",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(6.dp))

            Playstyle.entries.forEach { style ->
                val isSelected = selectedPlaystyle == style
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .background(
                            if (isSelected) CyberRed.copy(alpha = 0.15f) else DarkSurfaceVariant,
                            RoundedCornerShape(10.dp)
                        )
                        .border(
                            1.dp,
                            if (isSelected) CyberRed else DarkSurfaceBorder,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { onPlaystyleSelected(style) }
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = style.label,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) CyberRed else TextPrimary
                        )
                        Text(
                            text = style.description,
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Auto-Calculator Trigger
            Button(
                onClick = onAutoCalculateClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auto_calculate_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyberRed,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AUTO-CALCULATE 100% HEADSHOT SETTINGS",
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun ChipButton(
    label: String,
    isSelected: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .testTag(testTag)
            .background(
                if (isSelected) CyberRed else DarkSurfaceVariant,
                RoundedCornerShape(8.dp)
            )
            .border(
                1.dp,
                if (isSelected) CyberGold else DarkSurfaceBorder,
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.White else TextPrimary
        )
    }
}
