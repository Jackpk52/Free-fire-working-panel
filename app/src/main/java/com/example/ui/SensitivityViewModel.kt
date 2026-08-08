package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.PresetEntity
import com.example.data.SensitivityRepository
import com.example.model.CrosshairConfig
import com.example.model.CrosshairStyle
import com.example.model.DeviceSpecs
import com.example.model.Playstyle
import com.example.model.ProcessorTier
import com.example.model.ProPreset
import com.example.model.SensitivityData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SensitivityViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SensitivityRepository

    init {
        val database = AppDatabase.getInstance(application)
        repository = SensitivityRepository(database.sensitivityDao())
    }

    // Saved database presets
    val savedPresets: StateFlow<List<PresetEntity>> = repository.savedPresets
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Current Active Sensitivity Settings
    private val _currentSensitivity = MutableStateFlow(SensitivityData())
    val currentSensitivity: StateFlow<SensitivityData> = _currentSensitivity.asStateFlow()

    // Device Specs
    private val _deviceSpecs = MutableStateFlow(DeviceSpecs())
    val deviceSpecs: StateFlow<DeviceSpecs> = _deviceSpecs.asStateFlow()

    // Selected Playstyle
    private val _selectedPlaystyle = MutableStateFlow(Playstyle.ONE_TAP_DRAG)
    val selectedPlaystyle: StateFlow<Playstyle> = _selectedPlaystyle.asStateFlow()

    // Crosshair Settings
    private val _crosshairConfig = MutableStateFlow(CrosshairConfig())
    val crosshairConfig: StateFlow<CrosshairConfig> = _crosshairConfig.asStateFlow()

    // Built-in Pro Presets
    val proPresets: List<ProPreset> = repository.getBuiltInProPresets()

    // Aim Drag Test Analytics State
    private val _dragTestResult = MutableStateFlow<DragTestResult?>(null)
    val dragTestResult: StateFlow<DragTestResult?> = _dragTestResult.asStateFlow()

    // Toast / Message Notice
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    fun updateGeneral(valInt: Int) {
        _currentSensitivity.update { it.copy(general = valInt.coerceIn(0, 200)) }
    }

    fun updateRedDot(valInt: Int) {
        _currentSensitivity.update { it.copy(redDot = valInt.coerceIn(0, 200)) }
    }

    fun updateScope2x(valInt: Int) {
        _currentSensitivity.update { it.copy(scope2x = valInt.coerceIn(0, 200)) }
    }

    fun updateScope4x(valInt: Int) {
        _currentSensitivity.update { it.copy(scope4x = valInt.coerceIn(0, 200)) }
    }

    fun updateAwmScope(valInt: Int) {
        _currentSensitivity.update { it.copy(awmScope = valInt.coerceIn(0, 200)) }
    }

    fun updateFreeLook(valInt: Int) {
        _currentSensitivity.update { it.copy(freeLook = valInt.coerceIn(0, 200)) }
    }

    fun updateFireButtonSize(size: Int) {
        _currentSensitivity.update { it.copy(fireButtonSize = size.coerceIn(30, 80)) }
    }

    fun updateDpi(dpi: Int) {
        _currentSensitivity.update { it.copy(recommendedDpi = dpi.coerceIn(320, 960)) }
    }

    fun updateDeviceRam(ramGb: Int) {
        _deviceSpecs.update { it.copy(ramGb = ramGb) }
        recalculateAutoSensitivity()
    }

    fun updateDeviceRefreshRate(hz: Int) {
        _deviceSpecs.update { it.copy(refreshRateHz = hz) }
        recalculateAutoSensitivity()
    }

    fun updateProcessorTier(tier: ProcessorTier) {
        _deviceSpecs.update { it.copy(processorTier = tier) }
        recalculateAutoSensitivity()
    }

    fun updatePlaystyle(playstyle: Playstyle) {
        _selectedPlaystyle.value = playstyle
        recalculateAutoSensitivity()
    }

    fun recalculateAutoSensitivity() {
        val calculated = repository.calculateOptimalSensitivity(_deviceSpecs.value, _selectedPlaystyle.value)
        _currentSensitivity.value = calculated
        _toastMessage.value = "Calculated 100% Headshot Auto-Sensitivity for ${_deviceSpecs.value.ramGb}GB RAM!"
    }

    fun applyPreset(sensitivity: SensitivityData, presetTitle: String) {
        _currentSensitivity.value = sensitivity
        _toastMessage.value = "Applied Preset: $presetTitle"
    }

    fun saveCurrentAsPreset(name: String) {
        viewModelScope.launch {
            val curr = _currentSensitivity.value
            val entity = PresetEntity(
                name = name.ifBlank { "Custom Headshot ${curr.ramTierGbs}GB" },
                general = curr.general,
                redDot = curr.redDot,
                scope2x = curr.scope2x,
                scope4x = curr.scope4x,
                awmScope = curr.awmScope,
                freeLook = curr.freeLook,
                fireButtonSize = curr.fireButtonSize,
                dpiSetting = curr.recommendedDpi,
                ramGb = curr.ramTierGbs,
                playstyleName = curr.playstyle.label
            )
            repository.savePreset(entity)
            _toastMessage.value = "Saved Preset '$name' to Database!"
        }
    }

    fun deletePreset(id: Long) {
        viewModelScope.launch {
            repository.deletePreset(id)
            _toastMessage.value = "Preset Deleted"
        }
    }

    fun toggleFavoritePreset(id: Long, isFav: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(id, isFav)
        }
    }

    fun recordDragTest(verticalSpeedPxPerMs: Float, dragDurationMs: Long, headshotAccPercent: Int) {
        val feedback = when {
            headshotAccPercent >= 88 -> "PERFECT 100% HEADSHOT! Instant Red Numbers!"
            headshotAccPercent >= 70 -> "GREAT DRAG! Minor drag velocity adjustment needed."
            headshotAccPercent >= 45 -> "BODY HIT / TOO SLOW. Increase General sensitivity by +5."
            else -> "OVERDRAG / AIM ABOVE HEAD. Lower General sensitivity by -5."
        }
        _dragTestResult.value = DragTestResult(
            dragSpeed = verticalSpeedPxPerMs,
            dragTimeMs = dragDurationMs,
            headshotAccuracy = headshotAccPercent,
            feedbackMessage = feedback
        )
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }

    // Crosshair config methods
    fun toggleCrosshairEnabled(enabled: Boolean) {
        _crosshairConfig.update { it.copy(isEnabled = enabled) }
        _toastMessage.value = if (enabled) "Custom Crosshair Overlay ACTIVE" else "Custom Crosshair Overlay DISABLED"
    }

    fun toggleAutoHeadAlign(enabled: Boolean) {
        _crosshairConfig.update { it.copy(isAutoHeadAlignEnabled = enabled) }
        _toastMessage.value = if (enabled) "Auto-Headlock Alignment ASSIST ON" else "Auto-Headlock Alignment OFF"
    }

    fun updateCrosshairStyle(style: CrosshairStyle) {
        _crosshairConfig.update { it.copy(style = style) }
    }

    fun updateCrosshairColor(colorHex: Long) {
        _crosshairConfig.update { it.copy(colorHex = colorHex) }
    }

    fun updateCrosshairSize(size: Int) {
        _crosshairConfig.update { it.copy(sizeDp = size.coerceIn(10, 40)) }
    }

    fun updateCrosshairGap(gap: Int) {
        _crosshairConfig.update { it.copy(gapDp = gap.coerceIn(0, 20)) }
    }
}

data class DragTestResult(
    val dragSpeed: Float,
    val dragTimeMs: Long,
    val headshotAccuracy: Int,
    val feedbackMessage: String
)
