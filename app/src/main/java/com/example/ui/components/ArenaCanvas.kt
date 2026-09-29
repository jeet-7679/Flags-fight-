package com.example.ui.components

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import com.example.flag.FlagRenderer
import com.example.model.ArenaType
import com.example.model.BattleSettings
import com.example.model.BattleState
import com.example.model.MatchStatus
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ArenaCanvas(
    battleState: BattleState,
    settings: BattleSettings,
    onArenaLayout: (center: Offset, radius: Float) -> Unit,
    onPlayerInput: (Offset) -> Unit,
    modifier: Modifier = Modifier
) {
    val textPaint = remember {
        Paint().apply {
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { },
                    onDragEnd = { onPlayerInput(Offset.Zero) },
                    onDragCancel = { onPlayerInput(Offset.Zero) },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val norm = dragAmount / 30f
                        onPlayerInput(Offset(norm.x.coerceIn(-1f, 1f), norm.y.coerceIn(-1f, 1f)))
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxAvailableRadius = (minOf(size.width, size.height) / 2f) * 0.92f

            onArenaLayout(center, maxAvailableRadius)

            val radius = battleState.arenaRadius.coerceAtMost(maxAvailableRadius)

            // 1. Dark Void Deep Background
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF0F172A), Color(0xFF030712)),
                    center = center,
                    radius = maxAvailableRadius * 1.5f
                )
            )

            // 2. Arena Floor Interior (Clipped to circular ring)
            val arenaPath = Path().apply {
                addOval(androidx.compose.ui.geometry.Rect(center.x - radius, center.y - radius, center.x + radius, center.y + radius))
            }

            clipPath(arenaPath) {
                // Circular metallic arena plate
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF1E293B), Color(0xFF0F172A), Color(0xFF090D16)),
                        center = center,
                        radius = radius
                    ),
                    radius = radius,
                    center = center
                )

                // Arena Grid Rings & Radar Lines
                val ringCount = 4
                for (i in 1..ringCount) {
                    val r = radius * (i.toFloat() / ringCount)
                    drawCircle(
                        color = Color(0x1838BDF8),
                        radius = r,
                        center = center,
                        style = Stroke(width = 1.2f)
                    )
                }

                // Rotating radar/hazard crosshairs
                rotate(degrees = battleState.arenaRotation, pivot = center) {
                    drawLine(
                        color = Color(0x1438BDF8),
                        start = Offset(center.x - radius, center.y),
                        end = Offset(center.x + radius, center.y),
                        strokeWidth = 1.5f
                    )
                    drawLine(
                        color = Color(0x1438BDF8),
                        start = Offset(center.x, center.y - radius),
                        end = Offset(center.x, center.y + radius),
                        strokeWidth = 1.5f
                    )
                }

                // Central Black Hole / Vortex if VORTEX_HOLE arena
                if (settings.arenaType == ArenaType.VORTEX_HOLE) {
                    val holeRadius = 38f
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color.Black, Color(0xFF581C87), Color(0x00581C87)),
                            center = center,
                            radius = holeRadius * 2.2f
                        ),
                        radius = holeRadius * 2.2f,
                        center = center
                    )
                    drawCircle(
                        color = Color.Black,
                        radius = holeRadius,
                        center = center
                    )
                    drawCircle(
                        color = Color(0xFFA855F7),
                        radius = holeRadius,
                        center = center,
                        style = Stroke(width = 2.5f)
                    )
                }

                // Central Sumo Bumper if SUMO_BUMPERS
                if (settings.arenaType == ArenaType.SUMO_BUMPERS) {
                    val bumperRadius = 32f
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFFF43F5E), Color(0xFF9F1239)),
                            center = center,
                            radius = bumperRadius
                        ),
                        radius = bumperRadius,
                        center = center
                    )
                    drawCircle(
                        color = Color(0xFFFFE4E6),
                        radius = bumperRadius,
                        center = center,
                        style = Stroke(width = 3f)
                    )
                }
            }

            // 3. Arena Perimeter Boundary Wall
            when (settings.arenaType) {
                ArenaType.VOID_GATES -> {
                    // Draw outer ring with rotating gate gaps
                    val gateWidth = battleState.gateWidthRad
                    val gate1 = battleState.gateAngle1
                    val gate2 = (battleState.gateAngle1 + (2f * PI.toFloat() / 3f)) % (2f * PI.toFloat())
                    val gate3 = (battleState.gateAngle1 + (4f * PI.toFloat() / 3f)) % (2f * PI.toFloat())

                    drawCircle(
                        color = Color(0x3338BDF8),
                        radius = radius + 6f,
                        center = center,
                        style = Stroke(width = 8f)
                    )

                    // Draw 3 arc sections
                    val arcSpanDeg = (360f / 3f) - Math.toDegrees(gateWidth.toDouble()).toFloat()
                    val g1Deg = Math.toDegrees(gate1.toDouble()).toFloat() + Math.toDegrees((gateWidth / 2f).toDouble()).toFloat()
                    val g2Deg = Math.toDegrees(gate2.toDouble()).toFloat() + Math.toDegrees((gateWidth / 2f).toDouble()).toFloat()
                    val g3Deg = Math.toDegrees(gate3.toDouble()).toFloat() + Math.toDegrees((gateWidth / 2f).toDouble()).toFloat()

                    drawArc(
                        color = Color(0xFF38BDF8),
                        startAngle = g1Deg,
                        sweepAngle = arcSpanDeg,
                        useCenter = false,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = 5.5f)
                    )
                    drawArc(
                        color = Color(0xFF38BDF8),
                        startAngle = g2Deg,
                        sweepAngle = arcSpanDeg,
                        useCenter = false,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = 5.5f)
                    )
                    drawArc(
                        color = Color(0xFF38BDF8),
                        startAngle = g3Deg,
                        sweepAngle = arcSpanDeg,
                        useCenter = false,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = 5.5f)
                    )

                    // Gate Hazard Exit Markers
                    val gateAngles = listOf(gate1, gate2, gate3)
                    for (gAngle in gateAngles) {
                        val gx = center.x + cos(gAngle) * radius
                        val gy = center.y + sin(gAngle) * radius
                        drawCircle(Color(0xFFEF4444), 6f, Offset(gx, gy))
                    }
                }

                ArenaType.SHRINKING_RING -> {
                    // Electric glowing perimeter ring
                    val ringColor = if (battleState.suddenDeathActive) Color(0xFFEF4444) else Color(0xFF38BDF8)
                    val glowColor = if (battleState.suddenDeathActive) Color(0x44EF4444) else Color(0x3338BDF8)

                    // Outer electric haze
                    drawCircle(
                        color = glowColor,
                        radius = radius + 5f,
                        center = center,
                        style = Stroke(width = 10f)
                    )
                    // Core electric laser ring
                    drawCircle(
                        color = ringColor,
                        radius = radius,
                        center = center,
                        style = Stroke(width = 4.5f)
                    )

                    // Hazard tick marks
                    rotate(degrees = battleState.arenaRotation * 1.5f, pivot = center) {
                        for (i in 0 until 12) {
                            val angle = (2f * PI.toFloat() * i / 12f)
                            val p1 = Offset(center.x + cos(angle) * (radius - 8f), center.y + sin(angle) * (radius - 8f))
                            val p2 = Offset(center.x + cos(angle) * radius, center.y + sin(angle) * radius)
                            drawLine(color = ringColor, start = p1, end = p2, strokeWidth = 2.5f)
                        }
                    }
                }

                else -> {
                    // Sumo Bumpers & Classic
                    drawCircle(
                        color = Color(0x44F59E0B),
                        radius = radius + 6f,
                        center = center,
                        style = Stroke(width = 10f)
                    )
                    drawCircle(
                        color = Color(0xFFF59E0B),
                        radius = radius,
                        center = center,
                        style = Stroke(width = 5f)
                    )
                }
            }

            // 4. Power-Up Orbs on Field
            val native = drawContext.canvas.nativeCanvas
            for (pu in battleState.powerUps) {
                // Pulsing glow
                drawCircle(
                    color = pu.type.color.copy(alpha = 0.35f),
                    radius = pu.radius * 1.5f,
                    center = pu.position
                )
                drawCircle(
                    color = pu.type.color,
                    radius = pu.radius,
                    center = pu.position
                )
                drawCircle(
                    color = Color.White,
                    radius = pu.radius,
                    center = pu.position,
                    style = Stroke(width = 2f)
                )

                // Icon
                textPaint.color = android.graphics.Color.WHITE
                textPaint.textSize = pu.radius * 1.3f
                textPaint.clearShadowLayer()
                native.drawText(pu.type.icon, pu.position.x, pu.position.y + pu.radius * 0.45f, textPaint)
            }

            // 5. Shockwaves
            for (sw in battleState.shockwaves) {
                drawCircle(
                    color = sw.color.copy(alpha = sw.alpha),
                    radius = sw.currentRadius,
                    center = sw.center,
                    style = Stroke(width = 4f)
                )
            }

            // 6. Particles (Sparks, Explosions, Confetti)
            for (p in battleState.particles) {
                drawCircle(
                    color = p.color.copy(alpha = p.life.coerceIn(0f, 1f)),
                    radius = p.size * p.life,
                    center = Offset(p.x, p.y)
                )
            }

            // 7. Active Country Flag Balls
            val aliveBalls = battleState.balls.filter { it.isAlive }
            val topKiller = aliveBalls.maxByOrNull { it.kills }

            for (ball in aliveBalls) {
                val isLeader = (ball == topKiller && (topKiller.kills > 0))
                FlagRenderer.drawFlagBall(
                    drawScope = this,
                    ball = ball,
                    showHpBar = settings.arenaType == ArenaType.SUMO_BUMPERS,
                    isLeader = isLeader
                )
            }

            // 8. Countdown Overlay (3, 2, 1, FIGHT!)
            if (battleState.status == MatchStatus.COUNTDOWN) {
                val cd = battleState.countdownTime
                val text = when {
                    cd > 2.0f -> "3"
                    cd > 1.0f -> "2"
                    cd > 0.3f -> "1"
                    else -> "FIGHT!"
                }
                textPaint.color = if (text == "FIGHT!") android.graphics.Color.parseColor("#FDE047") else android.graphics.Color.WHITE
                textPaint.textSize = 80f
                textPaint.setShadowLayer(16f, 0f, 0f, android.graphics.Color.BLACK)
                native.drawText(text, center.x, center.y + 25f, textPaint)
            }
        }
    }
}
