package com.example.model

data class DeviceSpecs(
    val ramGb: Int = 6,
    val refreshRateHz: Int = 90,
    val processorTier: ProcessorTier = ProcessorTier.MID_RANGE,
    val screenDpi: Int = 420
)

enum class ProcessorTier(val displayName: String, val touchMultiplier: Float) {
    ENTRY_LEVEL("Entry Level (Helio P/G35, SD 4xx)", 1.15f),
    MID_RANGE("Mid Range (Helio G99, SD 7xx / Dimensity 7000)", 1.0f),
    FLAGSHIP("Flagship (SD 8 Gen / Dimensity 9000+)", 0.9f)
}
