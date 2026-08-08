package com.example.data

import com.example.model.DeviceSpecs
import com.example.model.Playstyle
import com.example.model.ProcessorTier
import com.example.model.ProPreset
import com.example.model.SensitivityData
import kotlinx.coroutines.flow.Flow

class SensitivityRepository(private val dao: SensitivityDao) {

    val savedPresets: Flow<List<PresetEntity>> = dao.getAllPresets()

    suspend fun savePreset(preset: PresetEntity): Long = dao.insertPreset(preset)

    suspend fun deletePreset(id: Long) = dao.deletePresetById(id)

    suspend fun toggleFavorite(id: Long, currentFav: Boolean) = dao.toggleFavorite(id, !currentFav)

    // Smart Auto-Calculator Engine based on device specs & desired playstyle
    fun calculateOptimalSensitivity(device: DeviceSpecs, playstyle: Playstyle): SensitivityData {
        // Base values range from 100 to 200 based on RAM tier (Lower RAM = Needs higher sensitivity for fast drag)
        val ramMultiplier = when {
            device.ramGb <= 3 -> 1.25f // Low RAM needs high drag speed
            device.ramGb <= 4 -> 1.15f
            device.ramGb <= 6 -> 1.05f
            device.ramGb <= 8 -> 1.0f
            else -> 0.95f // High RAM + smooth screen needs slightly more controlled sensitivity
        }

        val hzMultiplier = when {
            device.refreshRateHz >= 120 -> 0.92f
            device.refreshRateHz >= 90 -> 0.97f
            else -> 1.08f
        }

        val procMult = device.processorTier.touchMultiplier

        val combinedMult = ramMultiplier * hzMultiplier * procMult

        val (baseGen, baseRedDot, base2x, base4x, baseAwm, baseFreeLook, baseButton) = when (playstyle) {
            Playstyle.ONE_TAP_DRAG -> Tuple7(192, 178, 170, 162, 125, 145, 46)
            Playstyle.RUSH_AGGRO -> Tuple7(200, 188, 180, 172, 135, 160, 42)
            Playstyle.SNIPER_PRECISION -> Tuple7(172, 155, 150, 142, 98, 130, 52)
            Playstyle.BALANCED_CONTROL -> Tuple7(182, 168, 162, 154, 115, 140, 50)
        }

        val gen = (baseGen * combinedMult).toInt().coerceIn(100, 200)
        val redDot = (baseRedDot * combinedMult).toInt().coerceIn(90, 200)
        val scope2x = (base2x * combinedMult).toInt().coerceIn(80, 200)
        val scope4x = (base4x * combinedMult).toInt().coerceIn(75, 200)
        val awm = (baseAwm * combinedMult).toInt().coerceIn(50, 180)
        val freeLook = (baseFreeLook * combinedMult).toInt().coerceIn(80, 200)

        // Recommended DPI calculation: Default 360/400 -> Scaled based on screen
        val recommendedDpi = (device.screenDpi * 1.22f).toInt().coerceIn(420, 800)

        return SensitivityData(
            general = gen,
            redDot = redDot,
            scope2x = scope2x,
            scope4x = scope4x,
            awmScope = awm,
            freeLook = freeLook,
            fireButtonSize = baseButton,
            recommendedDpi = recommendedDpi,
            ramTierGbs = device.ramGb,
            playstyle = playstyle
        )
    }

    fun getBuiltInProPresets(): List<ProPreset> {
        return listOf(
            ProPreset(
                id = "pro_onetap_god",
                title = "One-Tap Headshot God 2026",
                playerOrCategory = "Global Tournament Presets",
                badgeTag = "100% HEADSHOT",
                sensitivity = SensitivityData(
                    general = 198,
                    redDot = 185,
                    scope2x = 175,
                    scope4x = 168,
                    awmScope = 125,
                    freeLook = 150,
                    fireButtonSize = 44,
                    recommendedDpi = 580,
                    ramTierGbs = 6,
                    playstyle = Playstyle.ONE_TAP_DRAG
                ),
                description = "Engineered for maximum drag acceleration on close-range shotguns (M1887, DESERT EAGLE) and instant head locking."
            ),
            ProPreset(
                id = "pro_smooth_low_ram",
                title = "Low RAM Smooth Aim (3GB/4GB)",
                playerOrCategory = "Device Anti-Lag Specialist",
                badgeTag = "ZERO LAG DRAG",
                sensitivity = SensitivityData(
                    general = 200,
                    redDot = 195,
                    scope2x = 188,
                    scope4x = 180,
                    awmScope = 140,
                    freeLook = 170,
                    fireButtonSize = 40,
                    recommendedDpi = 480,
                    ramTierGbs = 4,
                    playstyle = Playstyle.RUSH_AGGRO
                ),
                description = "Compensates for frame drops and screen touch latency on entry-level devices. High general sensitivity guarantees fast vertical drag."
            ),
            ProPreset(
                id = "pro_flagship_120hz",
                title = "Beast Mode 120Hz / High DPI",
                playerOrCategory = "Ultra FPS Gaming",
                badgeTag = "EXPERT CONTROL",
                sensitivity = SensitivityData(
                    general = 178,
                    redDot = 162,
                    scope2x = 155,
                    scope4x = 148,
                    awmScope = 105,
                    freeLook = 135,
                    fireButtonSize = 52,
                    recommendedDpi = 640,
                    ramTierGbs = 8,
                    playstyle = Playstyle.BALANCED_CONTROL
                ),
                description = "For high-end phones with 90Hz/120Hz screens. Provides pin-point precision, preventing overdrag above the enemy's head."
            ),
            ProPreset(
                id = "pro_awm_sniper_king",
                title = "AWM & Sniper Quick Scope",
                playerOrCategory = "Long Range Specialist",
                badgeTag = "QUICK SCOPE",
                sensitivity = SensitivityData(
                    general = 170,
                    redDot = 150,
                    scope2x = 145,
                    scope4x = 140,
                    awmScope = 90,
                    freeLook = 120,
                    fireButtonSize = 55,
                    recommendedDpi = 520,
                    ramTierGbs = 6,
                    playstyle = Playstyle.SNIPER_PRECISION
                ),
                description = "Low AWM scope sensitivity allows rock-solid tracking while retaining high General speed for quick weapon swapping."
            ),
            ProPreset(
                id = "pro_smg_mp40_rush",
                title = "MP40 & UMP Spray Headshot",
                playerOrCategory = "Ranked Rushers",
                badgeTag = "SMG SPRAY",
                sensitivity = SensitivityData(
                    general = 190,
                    redDot = 182,
                    scope2x = 172,
                    scope4x = 165,
                    awmScope = 115,
                    freeLook = 155,
                    fireButtonSize = 46,
                    recommendedDpi = 540,
                    ramTierGbs = 6,
                    playstyle = Playstyle.RUSH_AGGRO
                ),
                description = "Optimal drag resistance for continuous SMG recoil control and maintaining red-dot drag alignment."
            )
        )
    }
}

private data class Tuple7<A, B, C, D, E, F, G>(
    val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G
)
