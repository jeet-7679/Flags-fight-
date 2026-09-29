package com.example.physics

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.audio.SoundManager
import com.example.model.ArenaType
import com.example.model.BattleSettings
import com.example.model.BattleState
import com.example.model.EliminationEvent
import com.example.model.FlagBall
import com.example.model.MatchStatus
import com.example.model.Particle
import com.example.model.PowerUp
import com.example.model.PowerUpType
import com.example.model.Shockwave
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

class PhysicsEngine(
    private val settings: BattleSettings,
    private val soundManager: SoundManager? = null
) {

    private val random = Random(System.currentTimeMillis())
    private var nextPowerUpId = 1L
    private var lastPowerUpSpawnTime = 0f

    fun update(
        state: BattleState,
        dtSec: Float,
        arenaCenter: Offset,
        playerInput: Offset = Offset.Zero
    ): BattleState {
        if (state.status != MatchStatus.RUNNING) {
            return state
        }

        val effectiveDt = dtSec * settings.speed.multiplier
        val newElapsedTime = state.elapsedTime + effectiveDt

        // 1. Calculate Arena Radius & Rotation
        var newArenaRadius = state.arenaRadius
        if (settings.arenaType == ArenaType.SHRINKING_RING) {
            // Shrink rate accelerates gradually
            val shrinkSpeed = when {
                newElapsedTime < 10f -> 3.2f
                newElapsedTime < 25f -> 5.5f
                else -> 8.0f
            }
            newArenaRadius = (state.initialArenaRadius - (newElapsedTime * shrinkSpeed))
                .coerceAtLeast(state.minArenaRadius)
        }

        val newArenaRotation = (state.arenaRotation + effectiveDt * 35f) % 360f
        val newGateAngle = (state.gateAngle1 + effectiveDt * 0.8f) % (2f * PI.toFloat())
        val suddenDeath = state.aliveCount <= 3 || (settings.arenaType == ArenaType.SHRINKING_RING && newArenaRadius <= state.minArenaRadius + 30f)

        // 2. Power-Up Spawning
        val currentPowerUps = state.powerUps.toMutableList()
        if (settings.powerUpsEnabled && newElapsedTime - lastPowerUpSpawnTime > 6.5f && currentPowerUps.size < 3) {
            lastPowerUpSpawnTime = newElapsedTime
            val angle = random.nextFloat() * 2f * PI.toFloat()
            val dist = random.nextFloat() * (newArenaRadius * 0.65f)
            val pX = arenaCenter.x + cos(angle) * dist
            val pY = arenaCenter.y + sin(angle) * dist
            val type = PowerUpType.entries.random()
            currentPowerUps.add(
                PowerUp(
                    id = nextPowerUpId++,
                    type = type,
                    position = Offset(pX, pY),
                    spawnTimeSec = newElapsedTime
                )
            )
        }

        // Update power-up life
        currentPowerUps.removeAll { newElapsedTime - it.spawnTimeSec > it.durationLifeSec }

        // 3. Shockwaves and Particles
        val newParticles = state.particles.toMutableList()
        val newShockwaves = state.shockwaves.toMutableList()
        val newEliminations = state.eliminationEvents.toMutableList()

        // Update existing particles
        val particleIterator = newParticles.iterator()
        while (particleIterator.hasNext()) {
            val p = particleIterator.next()
            p.x += p.vx * effectiveDt * 60f
            p.y += p.vy * effectiveDt * 60f
            p.life -= p.decay * (effectiveDt * 60f)
            if (p.life <= 0f) particleIterator.remove()
        }

        // Update shockwaves
        val shockwaveIterator = newShockwaves.iterator()
        while (shockwaveIterator.hasNext()) {
            val sw = shockwaveIterator.next()
            sw.currentRadius += effectiveDt * 400f
            sw.alpha = (1f - (sw.currentRadius / sw.maxRadius)).coerceAtLeast(0f)
            if (sw.currentRadius >= sw.maxRadius) shockwaveIterator.remove()
        }

        // 4. Update Balls
        val balls = state.balls
        val activeBalls = balls.filter { it.isAlive }

        // Buff timers & Player inputs
        for (ball in activeBalls) {
            if (ball.shieldTime > 0f) ball.shieldTime = (ball.shieldTime - effectiveDt).coerceAtLeast(0f)
            if (ball.speedBoostTime > 0f) ball.speedBoostTime = (ball.speedBoostTime - effectiveDt).coerceAtLeast(0f)
            if (ball.megaSizeTime > 0f) {
                ball.megaSizeTime = (ball.megaSizeTime - effectiveDt).coerceAtLeast(0f)
                ball.currentRadius = ball.baseRadius * 1.5f
                ball.mass = 2.4f
            } else {
                ball.currentRadius = ball.baseRadius
                ball.mass = 1.0f
            }

            // Record trail
            ball.trail.add(Offset(ball.x, ball.y))
            if (ball.trail.size > 5) ball.trail.removeAt(0)

            // Player control or AI subtle drift
            if (ball.isPlayer && playerInput != Offset.Zero) {
                val accel = 450f
                ball.vx += playerInput.x * accel * effectiveDt
                ball.vy += playerInput.y * accel * effectiveDt
            } else {
                // Autonomous AI Behavior:
                // Gentle wander + attraction toward power-ups + push away from hazard perimeter
                val dX = ball.x - arenaCenter.x
                val dY = ball.y - arenaCenter.y
                val distFromCenter = sqrt(dX * dX + dY * dY)

                // Steer away from edges
                if (distFromCenter > newArenaRadius * 0.78f) {
                    val steerInward = 120f
                    ball.vx -= (dX / distFromCenter) * steerInward * effectiveDt
                    ball.vy -= (dY / distFromCenter) * steerInward * effectiveDt
                }

                // Seek nearest power-up
                var nearestPU: PowerUp? = null
                var nearestDist = Float.MAX_VALUE
                for (pu in currentPowerUps) {
                    val pDist = (pu.position - Offset(ball.x, ball.y)).getDistance()
                    if (pDist < nearestDist) {
                        nearestDist = pDist
                        nearestPU = pu
                    }
                }
                if (nearestPU != null && nearestDist < 250f) {
                    val dir = (nearestPU.position - Offset(ball.x, ball.y)) / nearestDist
                    ball.vx += dir.x * 60f * effectiveDt
                    ball.vy += dir.y * 60f * effectiveDt
                }

                // Random slight impulse to keep motion alive
                if (random.nextFloat() < 0.05f) {
                    val randAngle = random.nextFloat() * 2f * PI.toFloat()
                    val impulse = random.nextFloat() * 40f
                    ball.vx += cos(randAngle) * impulse
                    ball.vy += sin(randAngle) * impulse
                }
            }

            // Speed multiplier buff
            val maxSpeed = if (ball.hasSpeedBoost) 680f else 460f
            val currentSpeed = sqrt(ball.vx * ball.vx + ball.vy * ball.vy)
            if (currentSpeed > maxSpeed) {
                val ratio = maxSpeed / currentSpeed
                ball.vx *= ratio
                ball.vy *= ratio
            }

            // Minimum velocity maintenance so simulation doesn't stall
            if (currentSpeed < 50f) {
                val minAngle = random.nextFloat() * 2f * PI.toFloat()
                ball.vx += cos(minAngle) * 70f
                ball.vy += sin(minAngle) * 70f
            }

            // Gravity Vortex Mode Attraction
            if (settings.arenaType == ArenaType.VORTEX_HOLE) {
                val vDx = arenaCenter.x - ball.x
                val vDy = arenaCenter.y - ball.y
                val vDist = sqrt(vDx * vDx + vDy * vDy).coerceAtLeast(20f)
                val gravityPull = 12000f / (vDist + 40f)
                ball.vx += (vDx / vDist) * gravityPull * effectiveDt
                ball.vy += (vDy / vDist) * gravityPull * effectiveDt

                // Check center hole elimination (black hole event horizon ~35f)
                if (vDist < 35f && !ball.hasShield) {
                    eliminateBall(
                        ball = ball,
                        reason = "Sucked into Vortex",
                        killer = ball.lastHitBy,
                        aliveCount = state.aliveCount,
                        timestamp = newElapsedTime,
                        eliminations = newEliminations,
                        particles = newParticles
                    )
                }
            }

            // Update Position
            ball.x += ball.vx * effectiveDt
            ball.y += ball.vy * effectiveDt

            // Apply slight environmental drag
            ball.vx *= settings.friction
            ball.vy *= settings.friction
        }

        // 5. Ball-to-PowerUp Collisions
        val puIterator = currentPowerUps.iterator()
        while (puIterator.hasNext()) {
            val pu = puIterator.next()
            for (ball in activeBalls) {
                val dist = (Offset(ball.x, ball.y) - pu.position).getDistance()
                if (dist < ball.currentRadius + pu.radius) {
                    soundManager?.playPowerUp()
                    // Apply power-up
                    when (pu.type) {
                        PowerUpType.SPEED_BOOST -> {
                            ball.speedBoostTime = pu.type.durationSec
                            ball.vx *= 1.6f
                            ball.vy *= 1.6f
                        }
                        PowerUpType.SHIELD -> {
                            ball.shieldTime = pu.type.durationSec
                        }
                        PowerUpType.BLAST_BOMB -> {
                            newShockwaves.add(
                                Shockwave(
                                    center = Offset(ball.x, ball.y),
                                    currentRadius = 10f,
                                    maxRadius = 220f,
                                    color = Color(0xFFEF4444)
                                )
                            )
                            // Push other balls away
                            for (other in activeBalls) {
                                if (other.id != ball.id) {
                                    val pushDir = Offset(other.x - ball.x, other.y - ball.y)
                                    val pushDist = pushDir.getDistance().coerceAtLeast(10f)
                                    if (pushDist < 220f) {
                                        val factor = (1f - (pushDist / 220f)) * 420f
                                        other.vx += (pushDir.x / pushDist) * factor
                                        other.vy += (pushDir.y / pushDist) * factor
                                        other.lastHitBy = ball
                                    }
                                }
                            }
                        }
                        PowerUpType.MEGA_SIZE -> {
                            ball.megaSizeTime = pu.type.durationSec
                        }
                        PowerUpType.HEAL_HEART -> {
                            ball.hp = (ball.hp + 40f).coerceAtMost(ball.maxHp)
                        }
                    }
                    // Emit sparkle particles
                    emitSparkles(pu.position, pu.type.color, newParticles)
                    puIterator.remove()
                    break
                }
            }
        }

        // 6. Ball-to-Ball Collisions
        val aliveList = activeBalls.filter { it.isAlive }
        for (i in aliveList.indices) {
            val b1 = aliveList[i]
            if (!b1.isAlive) continue

            for (j in i + 1 until aliveList.size) {
                val b2 = aliveList[j]
                if (!b2.isAlive) continue

                val dx = b2.x - b1.x
                val dy = b2.y - b1.y
                val dist = sqrt(dx * dx + dy * dy)
                val minDist = b1.currentRadius + b2.currentRadius

                if (dist < minDist && dist > 0.001f) {
                    val nx = dx / dist
                    val ny = dy / dist
                    val overlap = minDist - dist

                    // Positional separation based on mass
                    val totalMass = b1.mass + b2.mass
                    b1.x -= nx * overlap * (b2.mass / totalMass)
                    b1.y -= ny * overlap * (b2.mass / totalMass)
                    b2.x += nx * overlap * (b1.mass / totalMass)
                    b2.y += ny * overlap * (b1.mass / totalMass)

                    // Relative velocity along normal
                    val rvx = b2.vx - b1.vx
                    val rvy = b2.vy - b1.vy
                    val velAlongNormal = rvx * nx + rvy * ny

                    if (velAlongNormal < 0f) {
                        val restitution = settings.bounciness
                        val impulse = -(1f + restitution) * velAlongNormal / (1f / b1.mass + 1f / b2.mass)

                        b1.vx -= (impulse / b1.mass) * nx
                        b1.vy -= (impulse / b1.mass) * ny
                        b2.vx += (impulse / b2.mass) * nx
                        b2.vy += (impulse / b2.mass) * ny

                        b1.bounces++
                        b2.bounces++
                        b1.lastHitBy = b2
                        b2.lastHitBy = b1

                        val impact = -velAlongNormal
                        soundManager?.playBounce(impact / 400f)

                        // Emit collision sparks
                        val midX = (b1.x + b2.x) / 2f
                        val midY = (b1.y + b2.y) / 2f
                        emitSparks(midX, midY, b1.country.color1, b2.country.color1, newParticles, (impact / 40f).toInt())

                        // Sumo Bumpers Mode Health Damage
                        if (settings.arenaType == ArenaType.SUMO_BUMPERS) {
                            val dmg1 = if (b1.hasShield) 0f else (impact * 0.16f * b2.mass)
                            val dmg2 = if (b2.hasShield) 0f else (impact * 0.16f * b1.mass)

                            b1.hp -= dmg1
                            b2.hp -= dmg2
                            b1.damageDealt += dmg2
                            b2.damageDealt += dmg1

                            if (b1.hp <= 0f) {
                                eliminateBall(
                                    ball = b1,
                                    reason = "Knocked Out by ${b2.country.name}",
                                    killer = b2,
                                    aliveCount = state.aliveCount,
                                    timestamp = newElapsedTime,
                                    eliminations = newEliminations,
                                    particles = newParticles
                                )
                            }
                            if (b2.hp <= 0f) {
                                eliminateBall(
                                    ball = b2,
                                    reason = "Knocked Out by ${b1.country.name}",
                                    killer = b1,
                                    aliveCount = state.aliveCount,
                                    timestamp = newElapsedTime,
                                    eliminations = newEliminations,
                                    particles = newParticles
                                )
                            }
                        }
                    }
                }
            }
        }

        // 7. Ball-to-Arena Boundary Collisions & Ring Outs
        for (ball in activeBalls) {
            if (!ball.isAlive) continue

            val dX = ball.x - arenaCenter.x
            val dY = ball.y - arenaCenter.y
            val dist = sqrt(dX * dX + dY * dY)

            // Check VOID_GATES open slots
            if (settings.arenaType == ArenaType.VOID_GATES) {
                val ballAngle = (atan2(dY, dX) + 2f * PI.toFloat()) % (2f * PI.toFloat())
                // 3 rotating gates spaced evenly at 0, 120, 240 degrees
                val gate1 = newGateAngle
                val gate2 = (newGateAngle + (2f * PI.toFloat() / 3f)) % (2f * PI.toFloat())
                val gate3 = (newGateAngle + (4f * PI.toFloat() / 3f)) % (2f * PI.toFloat())
                val halfWidth = state.gateWidthRad / 2f

                val inGate = isAngleInRange(ballAngle, gate1, halfWidth) ||
                        isAngleInRange(ballAngle, gate2, halfWidth) ||
                        isAngleInRange(ballAngle, gate3, halfWidth)

                if (inGate && dist > newArenaRadius - ball.currentRadius) {
                    // Ball passed through gate!
                    if (dist > newArenaRadius + ball.currentRadius + 30f) {
                        eliminateBall(
                            ball = ball,
                            reason = "Hurled into the Void",
                            killer = ball.lastHitBy,
                            aliveCount = state.aliveCount,
                            timestamp = newElapsedTime,
                            eliminations = newEliminations,
                            particles = newParticles
                        )
                        continue
                    }
                }
            }

            // Normal Boundary Bounce or Ring Shrink Hazard
            if (dist + ball.currentRadius >= newArenaRadius) {
                if (settings.arenaType == ArenaType.SHRINKING_RING && suddenDeath && dist > newArenaRadius + 15f) {
                    // Out of bounds in shrinking ring mode
                    eliminateBall(
                        ball = ball,
                        reason = "Eliminated by Electric Ring",
                        killer = ball.lastHitBy,
                        aliveCount = state.aliveCount,
                        timestamp = newElapsedTime,
                        eliminations = newEliminations,
                        particles = newParticles
                    )
                    continue
                }

                // Bounce against circular arena wall
                val nx = -dX / dist
                val ny = -dY / dist

                // Clamp position
                ball.x = arenaCenter.x - nx * (newArenaRadius - ball.currentRadius)
                ball.y = arenaCenter.y - ny * (newArenaRadius - ball.currentRadius)

                val vn = ball.vx * nx + ball.vy * ny
                if (vn < 0f) {
                    val restitution = settings.bounciness
                    ball.vx -= (1f + restitution) * vn * nx
                    ball.vy -= (1f + restitution) * vn * ny
                    ball.bounces++

                    val speedRatio = (sqrt(ball.vx * ball.vx + ball.vy * ball.vy) / 450f).coerceIn(0.1f, 1f)
                    soundManager?.playBounce(speedRatio)

                    // Sparks at wall hit point
                    emitSparks(ball.x, ball.y, Color(0xFF38BDF8), Color.White, newParticles, 4)

                    // Shrinking ring hazard damage
                    if (settings.arenaType == ArenaType.SHRINKING_RING && suddenDeath) {
                        ball.hp -= 15f
                        if (ball.hp <= 0f) {
                            eliminateBall(
                                ball = ball,
                                reason = "Zapped by Electric Fence",
                                killer = ball.lastHitBy,
                                aliveCount = state.aliveCount,
                                timestamp = newElapsedTime,
                                eliminations = newEliminations,
                                particles = newParticles
                            )
                        }
                    }
                }
            }
        }

        // 8. Determine Winner / Last Flag Standing!
        val remainingAlive = balls.filter { it.isAlive }
        var matchStatus = state.status
        var winner = state.winner
        var runnerUp = state.runnerUp
        var thirdPlace = state.thirdPlace

        if (remainingAlive.size <= 1 && state.status == MatchStatus.RUNNING) {
            matchStatus = MatchStatus.VICTORY
            winner = remainingAlive.firstOrNull() ?: state.eliminationEvents.lastOrNull()?.victim?.let { v ->
                balls.find { it.country.code == v.code }
            }
            winner?.eliminationRank = 1

            // Runner-up is the last eliminated
            runnerUp = newEliminations.lastOrNull()?.victim?.let { v ->
                balls.find { it.country.code == v.code }
            }
            thirdPlace = if (newEliminations.size >= 2) {
                val v = newEliminations[newEliminations.size - 2].victim
                balls.find { it.country.code == v.code }
            } else null

            soundManager?.playVictory()

            // Confetti explosion around winner
            if (winner != null) {
                emitConfetti(winner.x, winner.y, newParticles)
            }
        }

        return state.copy(
            status = matchStatus,
            elapsedTime = newElapsedTime,
            arenaRadius = newArenaRadius,
            arenaRotation = newArenaRotation,
            gateAngle1 = newGateAngle,
            suddenDeathActive = suddenDeath,
            balls = balls,
            powerUps = currentPowerUps,
            particles = newParticles,
            shockwaves = newShockwaves,
            eliminationEvents = newEliminations,
            winner = winner,
            runnerUp = runnerUp,
            thirdPlace = thirdPlace
        )
    }

    private fun eliminateBall(
        ball: FlagBall,
        reason: String,
        killer: FlagBall?,
        aliveCount: Int,
        timestamp: Float,
        eliminations: MutableList<EliminationEvent>,
        particles: MutableList<Particle>
    ) {
        ball.isAlive = false
        ball.eliminationRank = aliveCount
        ball.eliminatedByName = killer?.country?.name
        ball.eliminationTimeSec = timestamp

        if (killer != null && killer.id != ball.id) {
            killer.kills++
        }

        soundManager?.playElimination()

        // Elimination event record
        eliminations.add(
            EliminationEvent(
                victim = ball.country,
                killer = killer?.country,
                rank = aliveCount,
                reason = reason,
                timestampSec = timestamp
            )
        )

        // Explode into country colored particle burst
        emitExplosion(ball.x, ball.y, ball.country.color1, ball.country.color2, particles)
    }

    private fun isAngleInRange(angle: Float, gateCenter: Float, halfWidth: Float): Boolean {
        var diff = (angle - gateCenter) % (2f * PI.toFloat())
        if (diff < -PI.toFloat()) diff += 2f * PI.toFloat()
        if (diff > PI.toFloat()) diff -= 2f * PI.toFloat()
        return kotlin.math.abs(diff) <= halfWidth
    }

    private fun emitSparks(x: Float, y: Float, c1: Color, c2: Color, list: MutableList<Particle>, count: Int) {
        val n = count.coerceIn(3, 14)
        for (i in 0 until n) {
            val angle = random.nextFloat() * 2f * PI.toFloat()
            val speed = 60f + random.nextFloat() * 180f
            list.add(
                Particle(
                    x = x,
                    y = y,
                    vx = cos(angle) * speed,
                    vy = sin(angle) * speed,
                    color = if (i % 2 == 0) c1 else c2,
                    size = 3.5f + random.nextFloat() * 3f,
                    life = 1.0f,
                    decay = 0.045f
                )
            )
        }
    }

    private fun emitSparkles(pos: Offset, color: Color, list: MutableList<Particle>) {
        for (i in 0 until 18) {
            val angle = random.nextFloat() * 2f * PI.toFloat()
            val speed = 80f + random.nextFloat() * 140f
            list.add(
                Particle(
                    x = pos.x,
                    y = pos.y,
                    vx = cos(angle) * speed,
                    vy = sin(angle) * speed,
                    color = color,
                    size = 4f + random.nextFloat() * 4f,
                    life = 1.0f,
                    decay = 0.035f
                )
            )
        }
    }

    private fun emitExplosion(x: Float, y: Float, c1: Color, c2: Color, list: MutableList<Particle>) {
        for (i in 0 until 40) {
            val angle = random.nextFloat() * 2f * PI.toFloat()
            val speed = 100f + random.nextFloat() * 320f
            list.add(
                Particle(
                    x = x,
                    y = y,
                    vx = cos(angle) * speed,
                    vy = sin(angle) * speed,
                    color = when (i % 3) {
                        0 -> c1
                        1 -> c2
                        else -> Color(0xFFFDE047)
                    },
                    size = 5f + random.nextFloat() * 5f,
                    life = 1.0f,
                    decay = 0.022f
                )
            )
        }
    }

    private fun emitConfetti(x: Float, y: Float, list: MutableList<Particle>) {
        val colors = listOf(
            Color(0xFFEF4444), Color(0xFF3B82F6), Color(0xFF10B981),
            Color(0xFFF59E0B), Color(0xFF8B5CF6), Color(0xFFEC4899), Color.White
        )
        for (i in 0 until 90) {
            val angle = random.nextFloat() * 2f * PI.toFloat()
            val speed = 80f + random.nextFloat() * 420f
            list.add(
                Particle(
                    x = x,
                    y = y,
                    vx = cos(angle) * speed,
                    vy = sin(angle) * speed,
                    color = colors.random(),
                    size = 6f + random.nextFloat() * 6f,
                    life = 1.0f,
                    decay = 0.012f
                )
            )
        }
    }
}
