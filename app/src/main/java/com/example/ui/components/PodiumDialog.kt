package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.BattleState

@Composable
fun PodiumDialog(
    battleState: BattleState,
    winnerSessionWins: Int? = null,
    onRematch: () -> Unit,
    onStartSimulation: () -> Unit = onRematch,
    onOpenSetup: () -> Unit,
    onOpenLeaderboard: () -> Unit
) {
    val winner = battleState.winner ?: return
    val runnerUp = battleState.runnerUp
    val thirdPlace = battleState.thirdPlace

    var animationStarted by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        animationStarted = true
    }

    val trophyScale by animateFloatAsState(
        targetValue = if (animationStarted) 1f else 0.4f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "trophyScale"
    )

    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFFBBF24), Color(0xFF3B82F6))
                    ),
                    width = 2.dp
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("podium_dialog")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Tag
                    Surface(
                        color = Color(0x33FBBF24),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "👑 LAST FLAG STANDING 👑",
                            color = Color(0xFFFDE047),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Winner Display with scaling animation
                    Box(
                        modifier = Modifier
                            .scale(trophyScale)
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(Color(0xFFFBBF24), Color(0xFFB45309))
                                )
                            )
                            .border(3.dp, Color(0xFFFEF08A), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = winner.country.emoji,
                            fontSize = 48.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = winner.country.name,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "CHAMPION OF THE CIRCLE ARENA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF94A3B8)
                    )

                    if (winnerSessionWins != null && winnerSessionWins > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            color = Color(0x33FBBF24),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFBBF24))
                        ) {
                            Text(
                                text = "🏆 $winnerSessionWins Session ${if (winnerSessionWins == 1) "Win" else "Wins"}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFDE047),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Podium Row: 2nd Place, 1st Place, 3rd Place
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        // 2nd Place
                        PodiumColumn(
                            rank = "🥈 2nd",
                            countryEmoji = runnerUp?.country?.emoji ?: "🏳️",
                            countryName = runnerUp?.country?.code ?: "None",
                            barHeight = 55.dp,
                            color = Color(0xFF94A3B8)
                        )

                        // 1st Place
                        PodiumColumn(
                            rank = "🥇 1st",
                            countryEmoji = winner.country.emoji,
                            countryName = winner.country.code,
                            barHeight = 85.dp,
                            color = Color(0xFFF59E0B)
                        )

                        // 3rd Place
                        PodiumColumn(
                            rank = "🥉 3rd",
                            countryEmoji = thirdPlace?.country?.emoji ?: "🏳️",
                            countryName = thirdPlace?.country?.code ?: "None",
                            barHeight = 42.dp,
                            color = Color(0xFFB45309)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Match Stats Mini Grid
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F172A), RoundedCornerShape(16.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        StatItem(label = "Winner Kills", value = "${winner.kills} 💥")
                        StatItem(label = "Bounces", value = "${winner.bounces} 🔄")
                        StatItem(label = "Duration", value = "${battleState.elapsedTime.toInt()}s ⏱️")
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Action Buttons
                    Button(
                        onClick = onStartSimulation,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("podium_start_simulation_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Start Simulation", tint = Color(0xFFFDE047))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("START NEW SIMULATION 🎲", fontWeight = FontWeight.Black, fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onRematch,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("rematch_button"),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.Replay, contentDescription = "Rematch", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Rematch", fontSize = 13.sp)
                        }

                        OutlinedButton(
                            onClick = onOpenSetup,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("setup_button"),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.Tune, contentDescription = "Setup", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Customize", fontSize = 13.sp)
                        }

                        OutlinedButton(
                            onClick = onOpenLeaderboard,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("leaderboard_button"),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.EmojiEvents, contentDescription = "Hall of Fame", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Records", fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PodiumColumn(
    rank: String,
    countryEmoji: String,
    countryName: String,
    barHeight: androidx.compose.ui.unit.Dp,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        Text(text = countryEmoji, fontSize = 28.sp)
        Text(
            text = countryName,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .width(64.dp)
                .height(barHeight)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = rank,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 11.sp, color = Color(0xFF64748B))
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}
