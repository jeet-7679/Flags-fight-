package com.example.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

enum class PowerUpType(
    val title: String,
    val icon: String,
    val color: Color,
    val durationSec: Float
) {
    SPEED_BOOST("Speed Surge", "⚡", Color(0xFFFACC15), 5.0f),
    SHIELD("Energy Shield", "🛡️", Color(0xFF38BDF8), 6.0f),
    BLAST_BOMB("Shockwave Bomb", "💥", Color(0xFFEF4444), 0.0f),
    MEGA_SIZE("Titan Size", "🍄", Color(0xFFA855F7), 6.5f),
    HEAL_HEART("Health Boost", "💖", Color(0xFFEC4899), 0.0f)
}

data class PowerUp(
    val id: Long,
    val type: PowerUpType,
    val position: Offset,
    val radius: Float = 18f,
    val spawnTimeSec: Float,
    val durationLifeSec: Float = 12.0f,
    var pulsePhase: Float = 0f
)
