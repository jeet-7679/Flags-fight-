package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.BattleSpeed
import com.example.model.MatchStatus
import com.example.ui.components.ArenaCanvas
import com.example.ui.components.PodiumDialog
import com.example.ui.components.SessionLeaderboardDialog
import com.example.ui.components.SessionLeaderboardTicker
import com.example.viewmodel.BattleViewModel
import kotlin.math.roundToInt

@Composable
fun BattleScreen(
    viewModel: BattleViewModel,
    onNavigateSetup: () -> Unit,
    onNavigateLeaderboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val battleState by viewModel.battleState.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val sessionStats by viewModel.sessionLeaderboard.collectAsStateWithLifecycle()
    val totalSessionBattles by viewModel.totalSessionBattles.collectAsStateWithLifecycle()

    var joystickOffset by remember { mutableStateOf(Offset.Zero) }
    var showSessionLeaderboardDialog by remember { mutableStateOf(false) }
    var simFlagCount by remember { mutableIntStateOf(12) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF030712))
            .testTag("battle_screen")
    ) {
        // 1. Circular Arena Canvas
        ArenaCanvas(
            battleState = battleState,
            settings = settings,
            onArenaLayout = { center, radius ->
                viewModel.setArenaCenter(center, radius)
            },
            onPlayerInput = { input ->
                viewModel.setPlayerInput(input)
            },
            modifier = Modifier.fillMaxSize()
        )

        // 2. Top App Bar & Status HUD
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .align(Alignment.TopCenter)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Title and Arena badge
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "FLAG BATTLE",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = Color(0x3338BDF8),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0284C7))
                        ) {
                            Text(
                                text = settings.arenaType.displayName,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF38BDF8),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                    Text(
                        text = "Last Flag Standing in the Circle",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                // Top Right Action Buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Sound toggle
                    IconButton(
                        onClick = {
                            viewModel.updateSettings(settings.copy(soundEnabled = !settings.soundEnabled))
                        },
                        modifier = Modifier.testTag("sound_toggle")
                    ) {
                        Icon(
                            if (settings.soundEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                            contentDescription = "Toggle Sound",
                            tint = if (settings.soundEnabled) Color(0xFF38BDF8) else Color(0xFF64748B)
                        )
                    }

                    // Session Wins Leaderboard Button
                    IconButton(
                        onClick = { showSessionLeaderboardDialog = true },
                        modifier = Modifier.testTag("nav_session_leaderboard")
                    ) {
                        BadgedBox(
                            badge = {
                                if (totalSessionBattles > 0) {
                                    Badge(
                                        containerColor = Color(0xFFFBBF24),
                                        contentColor = Color.Black
                                    ) {
                                        Text("$totalSessionBattles", fontWeight = FontWeight.Bold, fontSize = 9.sp)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                Icons.Default.EmojiEvents,
                                contentDescription = "Session Leaderboard",
                                tint = Color(0xFFFBBF24)
                            )
                        }
                    }

                    // Tournament Customizer
                    IconButton(
                        onClick = onNavigateSetup,
                        modifier = Modifier.testTag("nav_setup")
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Battle Settings",
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Scoreboard Ticker: Alive / Total & Sudden Death Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xDD0F172A), RoundedCornerShape(14.dp))
                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Alive count
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "ALIVE: ", fontSize = 12.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                    Text(
                        text = "${battleState.aliveCount}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = if (battleState.aliveCount <= 3) Color(0xFFEF4444) else Color(0xFF22C55E)
                    )
                    Text(text = " / ${battleState.totalCount}", fontSize = 13.sp, color = Color(0xFF64748B))
                }

                // Match Timer
                Text(
                    text = "⏱️ ${battleState.elapsedTime.toInt()}s",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                // Sudden Death alert
                if (battleState.suddenDeathActive) {
                    Surface(
                        color = Color(0x44EF4444),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444))
                    ) {
                        Text(
                            text = "⚡ SUDDEN DEATH",
                            color = Color(0xFFF87171),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Live Kill Notification Banner
            val latestElim = battleState.eliminationEvents.lastOrNull()
            AnimatedVisibility(
                visible = latestElim != null && (battleState.elapsedTime - latestElim.timestampSec < 3.0f),
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                if (latestElim != null) {
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xEE1E1B4B)),
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(Color(0xFFEF4444), Color(0xFF8B5CF6)))),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "💥", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (latestElim.killer != null) {
                                    "${latestElim.killer.emoji} ${latestElim.killer.name} knocked out ${latestElim.victim.emoji} ${latestElim.victim.name}!"
                                } else {
                                    "${latestElim.victim.emoji} ${latestElim.victim.name} was ${latestElim.reason.lowercase()}!"
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // 3. Bottom Controls HUD
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .align(Alignment.BottomCenter)
        ) {
            // Live Session Wins Tracker Bar
            SessionLeaderboardTicker(
                sessionStats = sessionStats,
                totalBattles = totalSessionBattles,
                onExpand = { showSessionLeaderboardDialog = true },
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // 'Start Simulation' Hero Action Button Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { viewModel.startRandomizedSimulation(simFlagCount) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("start_simulation_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2563EB)
                    ),
                    shape = RoundedCornerShape(14.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Start Simulation",
                        modifier = Modifier.size(20.dp),
                        tint = Color(0xFFFDE047)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "START SIMULATION",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        letterSpacing = 0.5.sp
                    )
                }

                // Random flag count switcher chip (8, 16, 32, 64, All)
                val allCount = com.example.model.CountryCatalog.allCountries.size
                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                    modifier = Modifier
                        .height(48.dp)
                        .clickable {
                            simFlagCount = when {
                                simFlagCount == 8 -> 16
                                simFlagCount == 16 -> 32
                                simFlagCount == 32 -> 64
                                simFlagCount == 64 -> allCount
                                else -> 8
                            }
                            viewModel.startRandomizedSimulation(simFlagCount)
                        }
                        .testTag("random_count_chip")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (simFlagCount >= allCount) "🌍 ALL ($allCount)" else "🎲 $simFlagCount Flags",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF38BDF8)
                        )
                    }
                }
            }

            // Speed and Controls Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xDD0F172A), RoundedCornerShape(20.dp))
                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Play / Pause Button
                IconButton(
                    onClick = { viewModel.togglePause() },
                    modifier = Modifier
                        .size(42.dp)
                        .background(Color(0xFF1E293B), CircleShape)
                        .testTag("pause_toggle")
                ) {
                    Icon(
                        if (battleState.status == MatchStatus.PAUSED) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = "Play or Pause",
                        tint = Color(0xFF38BDF8)
                    )
                }

                // Restart / Rerun Button
                IconButton(
                    onClick = { viewModel.startBattle() },
                    modifier = Modifier
                        .size(42.dp)
                        .background(Color(0xFF1E293B), CircleShape)
                        .testTag("restart_battle")
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Restart Battle",
                        tint = Color(0xFFFACC15)
                    )
                }

                // Speed Selector Pills
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BattleSpeed.entries.forEach { spd ->
                        val isSelected = settings.speed == spd
                        Surface(
                            color = if (isSelected) Color(0xFF2563EB) else Color(0xFF1E293B),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .clickable { viewModel.setSpeed(spd) }
                                .testTag("speed_${spd.label}")
                        ) {
                            Text(
                                text = spd.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                                color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Virtual Joystick for Player Controlled Flag (if enabled)
            if (settings.playerCountryCode != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val joystickRadius = 55.dp
                    Box(
                        modifier = Modifier
                            .size(joystickRadius * 2)
                            .clip(CircleShape)
                            .background(Color(0x661E293B))
                            .border(2.dp, Color(0xFF38BDF8), CircleShape)
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragEnd = {
                                        joystickOffset = Offset.Zero
                                        viewModel.setPlayerInput(Offset.Zero)
                                    },
                                    onDragCancel = {
                                        joystickOffset = Offset.Zero
                                        viewModel.setPlayerInput(Offset.Zero)
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        val newOff = joystickOffset + dragAmount
                                        val dist = newOff.getDistance()
                                        val maxD = 65f
                                        val clamped = if (dist > maxD) newOff * (maxD / dist) else newOff
                                        joystickOffset = clamped
                                        viewModel.setPlayerInput(Offset(clamped.x / maxD, clamped.y / maxD))
                                    }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        // Joystick thumb
                        Box(
                            modifier = Modifier
                                .offset { IntOffset(joystickOffset.x.roundToInt(), joystickOffset.y.roundToInt()) }
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Brush.radialGradient(listOf(Color(0xFF38BDF8), Color(0xFF0284C7))))
                                .border(2.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🕹️", fontSize = 16.sp)
                        }
                    }
                }
            }
        }

        // 4. Winner Celebration Podium Dialog
        if (battleState.status == MatchStatus.VICTORY && battleState.winner != null) {
            val winStat = sessionStats.find { it.country.code == battleState.winner?.country?.code }
            PodiumDialog(
                battleState = battleState,
                winnerSessionWins = winStat?.wins,
                onRematch = { viewModel.startBattle() },
                onStartSimulation = { viewModel.startRandomizedSimulation(simFlagCount) },
                onOpenSetup = onNavigateSetup,
                onOpenLeaderboard = onNavigateLeaderboard
            )
        }

        // 5. Session Leaderboard Modal Dialog
        if (showSessionLeaderboardDialog) {
            SessionLeaderboardDialog(
                sessionStats = sessionStats,
                totalBattles = totalSessionBattles,
                onResetSession = { viewModel.resetSessionStats() },
                onDismiss = { showSessionLeaderboardDialog = false }
            )
        }
    }
}
