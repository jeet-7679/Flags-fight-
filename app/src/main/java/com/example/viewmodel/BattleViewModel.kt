package com.example.viewmodel

import android.app.Application
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundManager
import com.example.data.AppDatabase
import com.example.data.BattleHistoryEntity
import com.example.data.CountryWinStat
import com.example.model.ArenaType
import com.example.model.BattleSettings
import com.example.model.BattleSpeed
import com.example.model.BattleState
import com.example.model.CountryCatalog
import com.example.model.FlagBall
import com.example.model.MatchStatus
import com.example.model.SessionCountryStat
import com.example.physics.PhysicsEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class BattleViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val dao = db.battleHistoryDao()
    val soundManager = SoundManager(application)

    private val _settings = MutableStateFlow(BattleSettings())
    val settings: StateFlow<BattleSettings> = _settings.asStateFlow()

    private val _battleState = MutableStateFlow(BattleState())
    val battleState: StateFlow<BattleState> = _battleState.asStateFlow()

    val leaderboard: StateFlow<List<CountryWinStat>> = dao.getLeaderboard()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentBattles: StateFlow<List<BattleHistoryEntity>> = dao.getAllBattles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // In-session country flag wins and statistics
    private val _sessionStats = MutableStateFlow<Map<String, SessionCountryStat>>(emptyMap())
    val sessionLeaderboard: StateFlow<List<SessionCountryStat>> = _sessionStats
        .map { map ->
            map.values.sortedWith(
                compareByDescending<SessionCountryStat> { it.wins }
                    .thenByDescending { it.winRatePercent }
                    .thenByDescending { it.totalKills }
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _totalSessionBattles = MutableStateFlow(0)
    val totalSessionBattles: StateFlow<Int> = _totalSessionBattles.asStateFlow()

    fun resetSessionStats() {
        _sessionStats.value = emptyMap()
        _totalSessionBattles.value = 0
    }

    private var simulationJob: Job? = null
    private var physicsEngine = PhysicsEngine(_settings.value, soundManager)
    private var playerInput = Offset.Zero
    private var currentArenaCenter = Offset(500f, 500f)

    init {
        soundManager.soundEnabled = _settings.value.soundEnabled
        soundManager.hapticsEnabled = _settings.value.hapticsEnabled
    }

    fun updateSettings(newSettings: BattleSettings) {
        _settings.value = newSettings
        soundManager.soundEnabled = newSettings.soundEnabled
        soundManager.hapticsEnabled = newSettings.hapticsEnabled
        physicsEngine = PhysicsEngine(newSettings, soundManager)
    }

    fun setArenaCenter(center: Offset, arenaRadius: Float) {
        currentArenaCenter = center
        if (_battleState.value.status == MatchStatus.COUNTDOWN && _battleState.value.balls.isEmpty()) {
            setupBattle(arenaRadius)
        }
    }

    fun setPlayerInput(input: Offset) {
        playerInput = input
    }

    fun startBattle() {
        setupBattle(_battleState.value.initialArenaRadius)
    }

    /**
     * Initializes the arena with a randomized set of country flag circles and starts the simulation.
     */
    fun startRandomizedSimulation(flagCount: Int = 12) {
        val all = CountryCatalog.allCountries.shuffled(Random(System.currentTimeMillis()))
        val count = flagCount.coerceIn(4, all.size)
        val randomSelection = all.take(count).map { it.code }
        val newSettings = _settings.value.copy(
            selectedCountryCodes = randomSelection,
            playerCountryCode = null
        )
        updateSettings(newSettings)
        setupBattle(_battleState.value.initialArenaRadius)
    }

    fun togglePause() {
        val current = _battleState.value
        if (current.status == MatchStatus.RUNNING) {
            _battleState.value = current.copy(status = MatchStatus.PAUSED)
        } else if (current.status == MatchStatus.PAUSED) {
            _battleState.value = current.copy(status = MatchStatus.RUNNING)
        }
    }

    fun setSpeed(speed: BattleSpeed) {
        val newSet = _settings.value.copy(speed = speed)
        updateSettings(newSet)
    }

    fun setupBattle(baseRadius: Float = 320f) {
        simulationJob?.cancel()

        val selectedCodes = _settings.value.selectedCountryCodes.ifEmpty {
            listOf("BRA", "ARG", "FRA", "GER", "ESP", "ITA", "GBR", "NED")
        }
        val countries = selectedCodes.mapNotNull { CountryCatalog.getByCode(it) }

        // Ball size adapts gracefully to count (from 4 up to 150+ all countries)
        val ballRadius = when {
            countries.size <= 8 -> 26f
            countries.size <= 16 -> 22f
            countries.size <= 32 -> 17f
            countries.size <= 64 -> 13f
            countries.size <= 100 -> 10.5f
            else -> 8.5f
        }

        val center = currentArenaCenter
        val spawnRadius = (baseRadius - ballRadius - 30f).coerceAtLeast(80f)
        val balls = mutableListOf<FlagBall>()
        val rnd = Random(System.currentTimeMillis())

        val ringCount = when {
            countries.size <= 12 -> 2
            countries.size <= 30 -> 3
            countries.size <= 60 -> 4
            countries.size <= 100 -> 5
            else -> 6
        }

        for (i in countries.indices) {
            val country = countries[i]
            val ring = i % ringCount
            val ringFraction = (ring + 1).toFloat() / (ringCount + 0.5f)
            val dist = spawnRadius * (0.22f + 0.74f * ringFraction)
            val itemsInRing = (countries.size + ringCount - 1) / ringCount
            val ringItemIndex = i / ringCount
            val angle = (2f * PI.toFloat() * ringItemIndex / itemsInRing.toFloat()) + (ring * 0.45f)
            val x = center.x + cos(angle) * dist
            val y = center.y + sin(angle) * dist

            // Initial inward / tangential velocity
            val speed = 120f + rnd.nextFloat() * 60f
            val launchAngle = angle + PI.toFloat() * 0.75f + (rnd.nextFloat() - 0.5f) * 0.4f
            val vx = cos(launchAngle) * speed
            val vy = sin(launchAngle) * speed

            val isPlayer = country.code == _settings.value.playerCountryCode

            balls.add(
                FlagBall(
                    id = i,
                    country = country,
                    x = x,
                    y = y,
                    vx = vx,
                    vy = vy,
                    baseRadius = ballRadius,
                    currentRadius = ballRadius,
                    isPlayer = isPlayer
                )
            )
        }

        _battleState.value = BattleState(
            status = MatchStatus.COUNTDOWN,
            countdownTime = 3.0f,
            elapsedTime = 0f,
            arenaRadius = baseRadius,
            initialArenaRadius = baseRadius,
            balls = balls
        )

        physicsEngine = PhysicsEngine(_settings.value, soundManager)

        startSimulationLoop()
    }

    private fun startSimulationLoop() {
        simulationJob = viewModelScope.launch {
            // Countdown loop
            while (isActive && _battleState.value.status == MatchStatus.COUNTDOWN) {
                delay(50)
                val newCd = _battleState.value.countdownTime - 0.05f
                if (newCd <= 0f) {
                    _battleState.value = _battleState.value.copy(
                        status = MatchStatus.RUNNING,
                        countdownTime = 0f
                    )
                } else {
                    _battleState.value = _battleState.value.copy(countdownTime = newCd)
                }
            }

            var lastTimeNanos = System.nanoTime()

            // Main physics battle loop
            while (isActive && (_battleState.value.status == MatchStatus.RUNNING || _battleState.value.status == MatchStatus.PAUSED)) {
                val currentTimeNanos = System.nanoTime()
                val deltaSec = ((currentTimeNanos - lastTimeNanos) / 1_000_000_000f).coerceIn(0.005f, 0.05f)
                lastTimeNanos = currentTimeNanos

                if (_battleState.value.status == MatchStatus.RUNNING) {
                    val updated = physicsEngine.update(
                        state = _battleState.value,
                        dtSec = deltaSec,
                        arenaCenter = currentArenaCenter,
                        playerInput = playerInput
                    )
                    _battleState.value = updated

                    if (updated.status == MatchStatus.VICTORY) {
                        onBattleFinished(updated)
                        break
                    }
                }

                delay(16) // ~60fps step
            }
        }
    }

    private fun onBattleFinished(finalState: BattleState) {
        val winner = finalState.winner ?: return
        val runnerUp = finalState.runnerUp

        // Update in-session statistics
        _totalSessionBattles.value += 1
        val currentMap = _sessionStats.value.toMutableMap()
        for (ball in finalState.balls) {
            val code = ball.country.code
            val prev = currentMap[code] ?: com.example.model.SessionCountryStat(country = ball.country)
            val isWin = ball.id == winner.id
            val isRunnerUp = runnerUp != null && ball.id == runnerUp.id
            val newWins = if (isWin) prev.wins + 1 else prev.wins
            val newRunnerUps = if (isRunnerUp) prev.runnerUps + 1 else prev.runnerUps
            val newStreak = if (isWin) prev.currentStreak + 1 else 0

            currentMap[code] = prev.copy(
                wins = newWins,
                matchesPlayed = prev.matchesPlayed + 1,
                runnerUps = newRunnerUps,
                totalKills = prev.totalKills + ball.kills,
                currentStreak = newStreak,
                isRecentWinner = isWin
            )
        }
        _sessionStats.value = currentMap

        viewModelScope.launch {
            try {
                dao.insertBattle(
                    BattleHistoryEntity(
                        winnerCode = winner.country.code,
                        winnerName = winner.country.name,
                        runnerUpCode = runnerUp?.country?.code ?: "Unknown",
                        runnerUpName = runnerUp?.country?.name ?: "Unknown",
                        thirdPlaceCode = finalState.thirdPlace?.country?.code ?: "",
                        totalParticipants = finalState.totalCount,
                        durationSeconds = finalState.elapsedTime.toInt(),
                        arenaType = _settings.value.arenaType.displayName,
                        winnerKills = winner.kills
                    )
                )
            } catch (_: Exception) {
                // Ignore DB error
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            dao.clearHistory()
        }
    }

    override fun onCleared() {
        super.onCleared()
        simulationJob?.cancel()
        soundManager.release()
    }
}
