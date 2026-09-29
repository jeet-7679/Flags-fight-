package com.example.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

data class Particle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var color: Color,
    var size: Float,
    var life: Float, // 1.0 down to 0.0
    val decay: Float = 0.035f
)

data class Shockwave(
    val center: Offset,
    var currentRadius: Float,
    val maxRadius: Float,
    val color: Color,
    var alpha: Float = 0.8f
)

data class FlagBall(
    val id: Int,
    val country: Country,
    var x: Float = 0f,
    var y: Float = 0f,
    var vx: Float = 0f,
    var vy: Float = 0f,
    var baseRadius: Float = 24f,
    var currentRadius: Float = 24f,
    var mass: Float = 1.0f,
    var hp: Float = 100f,
    val maxHp: Float = 100f,
    var isAlive: Boolean = true,
    var kills: Int = 0,
    var bounces: Int = 0,
    var damageDealt: Float = 0f,
    var eliminationRank: Int = 0,
    var eliminatedByName: String? = null,
    var eliminationTimeSec: Float = 0f,
    // Power-up buffs
    var shieldTime: Float = 0f,
    var speedBoostTime: Float = 0f,
    var megaSizeTime: Float = 0f,
    // Visual indicators
    var flashTimer: Float = 0f,
    var isPlayer: Boolean = false,
    var lastHitBy: FlagBall? = null,
    // Recent trail points for motion blur
    val trail: MutableList<Offset> = mutableListOf()
) {
    val hasShield: Boolean get() = shieldTime > 0f
    val hasSpeedBoost: Boolean get() = speedBoostTime > 0f
    val hasMegaSize: Boolean get() = megaSizeTime > 0f
}

data class EliminationEvent(
    val victim: Country,
    val killer: Country?,
    val rank: Int,
    val reason: String,
    val timestampSec: Float
)

enum class MatchStatus {
    COUNTDOWN,
    RUNNING,
    PAUSED,
    VICTORY
}

data class BattleState(
    val status: MatchStatus = MatchStatus.COUNTDOWN,
    val countdownTime: Float = 3.0f,
    val elapsedTime: Float = 0f,
    val arenaRadius: Float = 320f,
    val initialArenaRadius: Float = 320f,
    val minArenaRadius: Float = 95f,
    val arenaRotation: Float = 0f,
    val balls: List<FlagBall> = emptyList(),
    val powerUps: List<PowerUp> = emptyList(),
    val particles: List<Particle> = emptyList(),
    val shockwaves: List<Shockwave> = emptyList(),
    val eliminationEvents: List<EliminationEvent> = emptyList(),
    val winner: FlagBall? = null,
    val runnerUp: FlagBall? = null,
    val thirdPlace: FlagBall? = null,
    val gateAngle1: Float = 0f,
    val gateWidthRad: Float = 0.5f,
    val suddenDeathActive: Boolean = false
) {
    val aliveCount: Int get() = balls.count { it.isAlive }
    val totalCount: Int get() = balls.size
}
