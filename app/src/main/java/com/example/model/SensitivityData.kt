package com.example.model

data class SensitivityData(
    val general: Int = 185,      // 0 to 200 range
    val redDot: Int = 170,       // 0 to 200
    val scope2x: Int = 165,      // 0 to 200
    val scope4x: Int = 155,      // 0 to 200
    val awmScope: Int = 120,     // 0 to 200
    val freeLook: Int = 140,     // 0 to 200
    val fireButtonSize: Int = 48, // 30% to 80%
    val recommendedDpi: Int = 520,
    val ramTierGbs: Int = 6,
    val playstyle: Playstyle = Playstyle.ONE_TAP_DRAG
)

enum class Playstyle(val label: String, val description: String) {
    ONE_TAP_DRAG("One-Tap Drag", "High general sensitivity for fast upward swipe & instant headshots"),
    RUSH_AGGRO("Aggressive Rush", "Maximum agility & fast camera rotation for close range 1v4"),
    SNIPER_PRECISION("Sniper Precision", "Controlled scope drag with lower AWM sensitivity for steady hits"),
    BALANCED_CONTROL("Balanced All-Rounder", "Smooth camera control for both long & close distance fights")
}
