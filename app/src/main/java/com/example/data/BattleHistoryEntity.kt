package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "battle_history")
data class BattleHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val winnerCode: String,
    val winnerName: String,
    val runnerUpCode: String,
    val runnerUpName: String,
    val thirdPlaceCode: String = "",
    val totalParticipants: Int,
    val durationSeconds: Int,
    val arenaType: String,
    val winnerKills: Int,
    val timestamp: Long = System.currentTimeMillis()
)
