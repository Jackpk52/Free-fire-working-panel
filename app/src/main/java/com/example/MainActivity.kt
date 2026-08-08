package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.SensitivityViewModel
import com.example.ui.screens.AimPracticeScreen
import com.example.ui.screens.BoosterGuideScreen
import com.example.ui.screens.HudCustomizerScreen
import com.example.ui.screens.PanelDashboardScreen
import com.example.ui.screens.ProPresetsScreen
import com.example.ui.theme.CyberGold
import com.example.ui.theme.CyberRed
import com.example.ui.theme.DarkCanvas
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

class MainActivity : ComponentActivity() {

    private val viewModel: SensitivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                SensitivityApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun SensitivityApp(viewModel: SensitivityViewModel) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }

    val currentSensitivity by viewModel.currentSensitivity.collectAsStateWithLifecycle()
    val deviceSpecs by viewModel.deviceSpecs.collectAsStateWithLifecycle()
    val selectedPlaystyle by viewModel.selectedPlaystyle.collectAsStateWithLifecycle()
    val savedPresets by viewModel.savedPresets.collectAsStateWithLifecycle()
    val crosshairConfig by viewModel.crosshairConfig.collectAsStateWithLifecycle()
    val dragTestResult by viewModel.dragTestResult.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    // Show Toast messages when triggered
    LaunchedEffect(toastMessage) {
        toastMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearToastMessage()
        }
    }

    val navigationItems = listOf(
        NavItem("Panel", Icons.Default.Tune, "nav_panel"),
        NavItem("Aim Drag", Icons.Default.GpsFixed, "nav_aim"),
        NavItem("Pro Presets", Icons.Default.MilitaryTech, "nav_presets"),
        NavItem("HUD & Sight", Icons.Default.CenterFocusStrong, "nav_hud"),
        NavItem("Booster", Icons.Default.Speed, "nav_booster")
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DarkCanvas,
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurface,
                tonalElevation = androidx.compose.ui.unit.Dp.Unspecified,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                navigationItems.forEachIndexed { index, item ->
                    val isSelected = selectedTab == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = index },
                        modifier = Modifier.testTag(item.testTag),
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = if (isSelected) CyberRed else TextSecondary
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) CyberRed else TextSecondary
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = CyberRed.copy(alpha = 0.2f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> PanelDashboardScreen(
                    sensitivity = currentSensitivity,
                    specs = deviceSpecs,
                    selectedPlaystyle = selectedPlaystyle,
                    onGeneralChange = { viewModel.updateGeneral(it) },
                    onRedDotChange = { viewModel.updateRedDot(it) },
                    onScope2xChange = { viewModel.updateScope2x(it) },
                    onScope4xChange = { viewModel.updateScope4x(it) },
                    onAwmScopeChange = { viewModel.updateAwmScope(it) },
                    onFreeLookChange = { viewModel.updateFreeLook(it) },
                    onFireButtonSizeChange = { viewModel.updateFireButtonSize(it) },
                    onDpiChange = { viewModel.updateDpi(it) },
                    onRamSelected = { viewModel.updateDeviceRam(it) },
                    onRefreshRateSelected = { viewModel.updateDeviceRefreshRate(it) },
                    onProcessorSelected = { viewModel.updateProcessorTier(it) },
                    onPlaystyleSelected = { viewModel.updatePlaystyle(it) },
                    onAutoCalculateClick = { viewModel.recalculateAutoSensitivity() },
                    onSavePresetClick = { name -> viewModel.saveCurrentAsPreset(name) }
                )
                1 -> AimPracticeScreen(
                    sensitivity = currentSensitivity,
                    crosshairConfig = crosshairConfig,
                    dragTestResult = dragTestResult,
                    onDragTestComplete = { speed, durationMs, accuracy ->
                        viewModel.recordDragTest(speed, durationMs, accuracy)
                    }
                )
                2 -> ProPresetsScreen(
                    proPresets = viewModel.proPresets,
                    savedPresets = savedPresets,
                    onApplySensitivity = { sens, title -> viewModel.applyPreset(sens, title) },
                    onDeletePreset = { id -> viewModel.deletePreset(id) },
                    onToggleFavorite = { id, isFav -> viewModel.toggleFavoritePreset(id, isFav) }
                )
                3 -> HudCustomizerScreen(
                    crosshairConfig = crosshairConfig,
                    fireButtonSize = currentSensitivity.fireButtonSize,
                    onCrosshairToggleEnabled = { viewModel.toggleCrosshairEnabled(it) },
                    onCrosshairToggleAutoHeadAlign = { viewModel.toggleAutoHeadAlign(it) },
                    onCrosshairStyleSelected = { viewModel.updateCrosshairStyle(it) },
                    onCrosshairColorSelected = { viewModel.updateCrosshairColor(it) },
                    onCrosshairSizeChange = { viewModel.updateCrosshairSize(it) },
                    onCrosshairGapChange = { viewModel.updateCrosshairGap(it) },
                    onFireButtonSizeChange = { viewModel.updateFireButtonSize(it) }
                )
                4 -> BoosterGuideScreen(
                    specs = deviceSpecs,
                    recommendedDpi = currentSensitivity.recommendedDpi
                )
            }
        }
    }
}

private data class NavItem(
    val label: String,
    val icon: ImageVector,
    val testTag: String
)
