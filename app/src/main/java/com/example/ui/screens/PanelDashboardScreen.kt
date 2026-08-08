package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.FilterCenterFocus
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DeviceSpecs
import com.example.model.Playstyle
import com.example.model.ProcessorTier
import com.example.model.SensitivityData
import com.example.ui.components.DeviceSpecSelector
import com.example.ui.components.HeaderBanner
import com.example.ui.components.SensitivitySliderCard
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
fun PanelDashboardScreen(
    sensitivity: SensitivityData,
    specs: DeviceSpecs,
    selectedPlaystyle: Playstyle,
    onGeneralChange: (Int) -> Unit,
    onRedDotChange: (Int) -> Unit,
    onScope2xChange: (Int) -> Unit,
    onScope4xChange: (Int) -> Unit,
    onAwmScopeChange: (Int) -> Unit,
    onFreeLookChange: (Int) -> Unit,
    onFireButtonSizeChange: (Int) -> Unit,
    onDpiChange: (Int) -> Unit,
    onRamSelected: (Int) -> Unit,
    onRefreshRateSelected: (Int) -> Unit,
    onProcessorSelected: (ProcessorTier) -> Unit,
    onPlaystyleSelected: (Playstyle) -> Unit,
    onAutoCalculateClick: () -> Unit,
    onSavePresetClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showSaveDialog by remember { mutableStateOf(false) }
    var presetNameInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(12.dp)) }

        // Top Esports Banner
        item {
            HeaderBanner()
        }

        // Action Toolbar (Copy to Clipboard & Save Preset)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        val textToCopy = """
                            🔥 FREE FIRE HEADSHOT PANEL SENSITIVITY 🔥
                            • General: ${sensitivity.general}
                            • Red Dot: ${sensitivity.redDot}
                            • 2X Scope: ${sensitivity.scope2x}
                            • 4X Scope: ${sensitivity.scope4x}
                            • AWM Scope: ${sensitivity.awmScope}
                            • Free Look: ${sensitivity.freeLook}
                            • Fire Button Size: ${sensitivity.fireButtonSize}%
                            • Recommended DPI: ${sensitivity.recommendedDpi}
                            • Device RAM: ${sensitivity.ramTierGbs}GB
                        """.trimIndent()

                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("FF Sensitivity", textToCopy)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Sensitivity Settings Copied to Clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("copy_settings_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "COPY ALL", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Button(
                    onClick = { showSaveDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("save_preset_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberGold),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = null, tint = CyberGold, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "SAVE PROFILE", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)
                }
            }
        }

        // Device Specs & Auto Engine
        item {
            DeviceSpecSelector(
                specs = specs,
                selectedPlaystyle = selectedPlaystyle,
                onRamSelected = onRamSelected,
                onRefreshRateSelected = onRefreshRateSelected,
                onProcessorSelected = onProcessorSelected,
                onPlaystyleSelected = onPlaystyleSelected,
                onAutoCalculateClick = onAutoCalculateClick
            )
        }

        // Section Title: Custom Sensitivity Panel
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Tune, contentDescription = null, tint = CyberRed, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "PRECISE SENSITIVITY CUSTOMIZER",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = TextPrimary
                )
            }
        }

        // 1. General Sensitivity
        item {
            SensitivitySliderCard(
                title = "General Sensitivity",
                subtitle = "Main camera speed & overall drag acceleration",
                value = sensitivity.general,
                icon = Icons.Default.Speed,
                accentColor = CyberRed,
                testTagPrefix = "general_sens",
                onValueChange = onGeneralChange,
                recommendedMin = 180,
                recommendedMax = 200
            )
        }

        // 2. Red Dot
        item {
            SensitivitySliderCard(
                title = "Red Dot Sensitivity",
                subtitle = "Aiming without scope / close-range red dot lock",
                value = sensitivity.redDot,
                icon = Icons.Default.RadioButtonChecked,
                accentColor = CyberGold,
                testTagPrefix = "red_dot_sens",
                onValueChange = onRedDotChange,
                recommendedMin = 165,
                recommendedMax = 190
            )
        }

        // 3. 2X Scope
        item {
            SensitivitySliderCard(
                title = "2X Scope Sensitivity",
                subtitle = "Mid-range assault rifle drag headshot lock",
                value = sensitivity.scope2x,
                icon = Icons.Default.FilterCenterFocus,
                accentColor = ElectricCyan,
                testTagPrefix = "scope_2x_sens",
                onValueChange = onScope2xChange,
                recommendedMin = 160,
                recommendedMax = 180
            )
        }

        // 4. 4X Scope
        item {
            SensitivitySliderCard(
                title = "4X Scope Sensitivity",
                subtitle = "Long-range precision tracking for Marksman rifles",
                value = sensitivity.scope4x,
                icon = Icons.Default.ZoomIn,
                accentColor = NeonGreen,
                testTagPrefix = "scope_4x_sens",
                onValueChange = onScope4xChange,
                recommendedMin = 150,
                recommendedMax = 175
            )
        }

        // 5. AWM / Sniper Scope
        item {
            SensitivitySliderCard(
                title = "AWM / Sniper Scope",
                subtitle = "Controlled tracking for heavy snipers (AWM, M82B)",
                value = sensitivity.awmScope,
                icon = Icons.Default.CropSquare,
                accentColor = CyberGold,
                testTagPrefix = "awm_scope_sens",
                onValueChange = onAwmScopeChange,
                recommendedMin = 90,
                recommendedMax = 140
            )
        }

        // 6. Free Look
        item {
            SensitivitySliderCard(
                title = "Free Look Sensitivity",
                subtitle = "360° situational eye button awareness",
                value = sensitivity.freeLook,
                icon = Icons.Default.Visibility,
                accentColor = ElectricCyan,
                testTagPrefix = "free_look_sens",
                onValueChange = onFreeLookChange,
                recommendedMin = 130,
                recommendedMax = 165
            )
        }

        // Fire Button Optimizer Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(CyberRed.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.RadioButtonChecked, contentDescription = null, tint = CyberRed, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "FIRE BUTTON SIZE OPTIMIZER", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                            Text(text = "Optimal size prevents thumb slips during fast upward drags", style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontSize = 11.sp)
                        }
                        Text(text = "${sensitivity.fireButtonSize}%", fontWeight = FontWeight.Black, fontSize = 18.sp, color = CyberRed)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Slider(
                        value = sensitivity.fireButtonSize.toFloat(),
                        onValueChange = { onFireButtonSizeChange(it.toInt()) },
                        valueRange = 30f..80f,
                        colors = SliderDefaults.colors(thumbColor = CyberRed, activeTrackColor = CyberRed)
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Small (Fast Drag)", fontSize = 10.sp, color = TextSecondary)
                        Text(text = "REC: 42% - 52%", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ElectricCyan)
                        Text(text = "Large (Easy Touch)", fontSize = 10.sp, color = TextSecondary)
                    }
                }
            }
        }

        // DPI Auto-Calculator Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(ElectricCyan.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Smartphone, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "DPI AUTO-CALCULATOR", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                            Text(text = "Smallest width developer setting for maximum drag speed", style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontSize = 11.sp)
                        }
                        Text(text = "${sensitivity.recommendedDpi} DPI", fontWeight = FontWeight.Black, fontSize = 16.sp, color = ElectricCyan)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Slider(
                        value = sensitivity.recommendedDpi.toFloat(),
                        onValueChange = { onDpiChange(it.toInt()) },
                        valueRange = 320f..800f,
                        colors = SliderDefaults.colors(thumbColor = ElectricCyan, activeTrackColor = ElectricCyan)
                    )

                    Text(
                        text = "💡 Developer Options Tip: Set 'Smallest Width' in Android Settings to ${sensitivity.recommendedDpi} dp for ultra smooth touch response.",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }

    // Save Preset Name Dialog
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = {
                Text(text = "Save Profile to Database", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(text = "Enter a name for your custom sensitivity configuration:", fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = presetNameInput,
                        onValueChange = { presetNameInput = it },
                        placeholder = { Text("e.g., My M1887 One-Tap") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberRed,
                            unfocusedBorderColor = DarkSurfaceBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSavePresetClick(presetNameInput)
                        presetNameInput = ""
                        showSaveDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberRed)
                ) {
                    Text(text = "SAVE", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text(text = "CANCEL", color = TextSecondary)
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
