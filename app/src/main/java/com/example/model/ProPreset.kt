package com.example.model

data class ProPreset(
    val id: String,
    val title: String,
    val playerOrCategory: String,
    val badgeTag: String,
    val sensitivity: SensitivityData,
    val isVerified: Boolean = true,
    val description: String
)

data class CrosshairConfig(
    val style: CrosshairStyle = CrosshairStyle.CROSS_DOT,
    val colorHex: Long = 0xFF00E676, // Neon Green
    val sizeDp: Int = 18,
    val gapDp: Int = 4,
    val strokeWidthDp: Int = 2,
    val dotSizeDp: Int = 4,
    val isOpacityFull: Boolean = true
)

enum class CrosshairStyle(val title: String) {
    CROSS_DOT("Crosshair + Dot"),
    CIRCLE_DOT("Circle + Dot"),
    PURE_DOT("Precision Dot"),
    CLASSIC_CROSS("Classic Cross")
}
