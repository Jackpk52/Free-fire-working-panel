package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_presets")
data class PresetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val general: Int,
    val redDot: Int,
    val scope2x: Int,
    val scope4x: Int,
    val awmScope: Int,
    val freeLook: Int,
    val fireButtonSize: Int,
    val dpiSetting: Int,
    val ramGb: Int,
    val playstyleName: String,
    val isFavorite: Boolean = false,
    val createdAtTimestamp: Long = System.currentTimeMillis()
)
