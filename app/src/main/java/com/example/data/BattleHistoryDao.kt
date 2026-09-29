package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

data class CountryWinStat(
    val winnerCode: String,
    val winnerName: String,
    val totalWins: Int,
    val totalKills: Int
)

@Dao
interface BattleHistoryDao {
    @Insert
    suspend fun insertBattle(battle: BattleHistoryEntity): Long

    @Query("SELECT * FROM battle_history ORDER BY timestamp DESC LIMIT 50")
    fun getAllBattles(): Flow<List<BattleHistoryEntity>>

    @Query("""
        SELECT winnerCode, winnerName, COUNT(*) as totalWins, SUM(winnerKills) as totalKills
        FROM battle_history
        GROUP BY winnerCode
        ORDER BY totalWins DESC, totalKills DESC
    """)
    fun getLeaderboard(): Flow<List<CountryWinStat>>

    @Query("DELETE FROM battle_history")
    suspend fun clearHistory()
}
