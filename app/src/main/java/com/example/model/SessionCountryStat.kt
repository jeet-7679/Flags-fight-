package com.example.model

data class SessionCountryStat(
    val country: Country,
    val wins: Int = 0,
    val matchesPlayed: Int = 0,
    val runnerUps: Int = 0,
    val totalKills: Int = 0,
    val currentStreak: Int = 0,
    val isRecentWinner: Boolean = false
) {
    val winRatePercent: Float
        get() = if (matchesPlayed > 0) (wins.toFloat() / matchesPlayed.toFloat()) * 100f else 0f
}
