package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.data.PresetEntity
import com.example.model.ProPreset
import com.example.model.SensitivityData
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
fun ProPresetsScreen(
    proPresets: List<ProPreset>,
    savedPresets: List<PresetEntity>,
    onApplySensitivity: (SensitivityData, String) -> Unit,
    onDeletePreset: (Long) -> Unit,
    onToggleFavorite: (Long, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(12.dp)) }

        // Section 1: User Saved Database Presets
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = CyberGold,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "YOUR SAVED DATABASE PRESETS (${savedPresets.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = TextPrimary
                )
            }
        }

        if (savedPresets.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "No custom saved presets yet.",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Use 'SAVE PROFILE' on the main panel to save your favorite setups!",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        } else {
            items(savedPresets, key = { it.id }) { preset ->
                SavedPresetCard(
                    preset = preset,
                    onApply = {
                        val sens = SensitivityData(
                            general = preset.general,
                            redDot = preset.redDot,
                            scope2x = preset.scope2x,
                            scope4x = preset.scope4x,
                            awmScope = preset.awmScope,
                            freeLook = preset.freeLook,
                            fireButtonSize = preset.fireButtonSize,
                            recommendedDpi = preset.dpiSetting,
                            ramTierGbs = preset.ramGb
                        )
                        onApplySensitivity(sens, preset.name)
                    },
                    onDelete = { onDeletePreset(preset.id) },
                    onFavoriteToggle = { onToggleFavorite(preset.id, preset.isFavorite) }
                )
            }
        }

        // Section 2: Verified Global Pro Presets
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.MilitaryTech,
                    contentDescription = null,
                    tint = CyberRed,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "VERIFIED GLOBAL PRO PRESETS",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = TextPrimary
                )
            }
        }

        items(proPresets, key = { it.id }) { pro ->
            ProPresetCard(
                pro = pro,
                onApply = { onApplySensitivity(pro.sensitivity, pro.title) }
            )
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }
}

@Composable
private fun SavedPresetCard(
    preset: PresetEntity,
    onApply: () -> Unit,
    onDelete: () -> Unit,
    onFavoriteToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("saved_preset_${preset.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (preset.isFavorite) CyberGold else DarkSurfaceBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onFavoriteToggle) {
                    Icon(
                        imageVector = if (preset.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Favorite",
                        tint = if (preset.isFavorite) CyberGold else TextSecondary
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = preset.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                    Text(text = "${preset.ramGb}GB RAM • ${preset.playstyleName}", fontSize = 11.sp, color = TextSecondary)
                }

                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = CyberRed.copy(alpha = 0.8f))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Values pill preview
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurfaceVariant, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                ValueMiniPill("GEN", preset.general)
                ValueMiniPill("RED", preset.redDot)
                ValueMiniPill("2X", preset.scope2x)
                ValueMiniPill("4X", preset.scope4x)
                ValueMiniPill("AWM", preset.awmScope)
                ValueMiniPill("BTN", preset.fireButtonSize, suffix = "%")
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onApply,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = CyberRed),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "APPLY TO PANEL", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun ProPresetCard(
    pro: ProPreset,
    onApply: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("pro_preset_${pro.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = pro.title, fontWeight = FontWeight.Black, fontSize = 14.sp, color = TextPrimary, modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .background(CyberRed.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                        .border(0.8.dp, CyberRed, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(text = pro.badgeTag, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = CyberRed)
                }
            }

            Text(text = pro.playerOrCategory, fontSize = 11.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = pro.description, fontSize = 11.sp, color = TextPrimary)

            Spacer(modifier = Modifier.height(10.dp))

            // Values preview row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurfaceVariant, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                ValueMiniPill("GEN", pro.sensitivity.general)
                ValueMiniPill("RED", pro.sensitivity.redDot)
                ValueMiniPill("2X", pro.sensitivity.scope2x)
                ValueMiniPill("4X", pro.sensitivity.scope4x)
                ValueMiniPill("AWM", pro.sensitivity.awmScope)
                ValueMiniPill("DPI", pro.sensitivity.recommendedDpi)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onApply,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = DarkSurface),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "APPLY PRO PRESET", fontWeight = FontWeight.Black, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun ValueMiniPill(label: String, value: Int, suffix: String = "") {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 9.sp, color = TextSecondary)
        Text(text = "$value$suffix", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}
