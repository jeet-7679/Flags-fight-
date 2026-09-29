package com.example.flag

import android.graphics.Paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import com.example.model.Country
import com.example.model.FlagBall
import com.example.model.FlagPattern
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

object FlagRenderer {

    private val textPaint = Paint().apply {
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        typeface = android.graphics.Typeface.DEFAULT_BOLD
    }

    fun drawFlagBall(
        drawScope: DrawScope,
        ball: FlagBall,
        showHpBar: Boolean = false,
        isLeader: Boolean = false
    ) {
        val center = Offset(ball.x, ball.y)
        val radius = ball.currentRadius
        val country = ball.country

        // 1. Trail effect if speed boosted or moving fast
        if (ball.trail.size >= 2) {
            for (i in 0 until ball.trail.size - 1) {
                val alpha = (i + 1).toFloat() / ball.trail.size * 0.35f
                val tRadius = radius * (0.4f + 0.6f * (i + 1).toFloat() / ball.trail.size)
                drawScope.drawCircle(
                    color = country.color1.copy(alpha = alpha),
                    radius = tRadius,
                    center = ball.trail[i]
                )
            }
        }

        // 2. Shield Glow Aura
        if (ball.hasShield) {
            drawScope.drawCircle(
                color = Color(0x6638BDF8),
                radius = radius + 9f,
                center = center
            )
            drawScope.drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x0038BDF8), Color(0xCC38BDF8), Color(0xFF67E8F9)),
                    center = center,
                    radius = radius + 11f
                ),
                radius = radius + 10f,
                center = center
            )
        }

        // 3. Speed Boost Fire Aura
        if (ball.hasSpeedBoost) {
            drawScope.drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x00FACC15), Color(0x99FACC15), Color(0xFFEF4444)),
                    center = center,
                    radius = radius + 8f
                ),
                radius = radius + 7f,
                center = center
            )
        }

        // 4. Mega Titan Aura
        if (ball.hasMegaSize) {
            drawScope.drawCircle(
                color = Color(0x66A855F7),
                radius = radius + 10f,
                center = center
            )
        }

        // 5. Clip canvas to circular ball for clean flag geometry
        val clipCirclePath = Path().apply {
            addOval(Rect(center.x - radius, center.y - radius, center.x + radius, center.y + radius))
        }

        drawScope.clipPath(clipCirclePath) {
            drawCountryFlagGeometry(this, country, center, radius)

            // Sphere 3D shading & glossy specular highlight
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x66FFFFFF), Color(0x15FFFFFF), Color(0x00000000), Color(0x66000000)),
                    center = Offset(center.x - radius * 0.32f, center.y - radius * 0.32f),
                    radius = radius * 1.35f
                ),
                radius = radius,
                center = center
            )
        }

        // 6. Outer Border Ring
        val borderColor = when {
            ball.isPlayer -> Color(0xFFFBBF24) // Gold for player
            ball.hasShield -> Color(0xFF38BDF8)
            ball.hasMegaSize -> Color(0xFFC084FC)
            isLeader -> Color(0xFFF59E0B) // Champion Gold
            else -> Color(0xDDFFFFFF)
        }
        val borderWidth = if (ball.isPlayer || isLeader) 3.5f else 1.8f

        drawScope.drawCircle(
            color = borderColor,
            radius = radius,
            center = center,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = borderWidth)
        )

        // 7. Country Code Text Badge on center/bottom of ball
        val nativeCanvas = drawScope.drawContext.canvas.nativeCanvas
        textPaint.color = android.graphics.Color.WHITE
        textPaint.textSize = (radius * 0.72f).coerceIn(11f, 22f)
        textPaint.setShadowLayer(5f, 0f, 0f, android.graphics.Color.BLACK)

        // Draw country ISO-3 code
        val textY = center.y + (textPaint.textSize * 0.35f)
        nativeCanvas.drawText(country.code, center.x, textY, textPaint)

        // 8. Crown icon for current kill leader / champion
        if (isLeader && ball.kills > 0) {
            val crownY = center.y - radius - 10f
            textPaint.textSize = (radius * 0.8f).coerceIn(14f, 24f)
            nativeCanvas.drawText("👑", center.x, crownY, textPaint)
        }

        // 9. "YOU" tag for player controlled ball
        if (ball.isPlayer) {
            textPaint.color = android.graphics.Color.parseColor("#FDE047")
            textPaint.textSize = (radius * 0.55f).coerceIn(10f, 15f)
            nativeCanvas.drawText("YOU", center.x, center.y - radius - 6f, textPaint)
        }

        // 10. Health bar (for Sumo Bumpers mode or damaged balls)
        if (showHpBar && ball.hp < ball.maxHp) {
            val barWidth = radius * 1.8f
            val barHeight = 4.5f
            val barX = center.x - (barWidth / 2f)
            val barY = center.y + radius + 5f

            // Background
            drawScope.drawRect(
                color = Color(0xCC000000),
                topLeft = Offset(barX, barY),
                size = Size(barWidth, barHeight)
            )

            // Health Fill
            val hpPct = (ball.hp / ball.maxHp).coerceIn(0f, 1f)
            val hpColor = when {
                hpPct > 0.5f -> Color(0xFF22C55E)
                hpPct > 0.25f -> Color(0xFFEAB308)
                else -> Color(0xFFEF4444)
            }
            drawScope.drawRect(
                color = hpColor,
                topLeft = Offset(barX, barY),
                size = Size(barWidth * hpPct, barHeight)
            )
        }
    }

    private fun drawCountryFlagGeometry(
        drawScope: DrawScope,
        country: Country,
        center: Offset,
        radius: Float
    ) {
        val left = center.x - radius
        val top = center.y - radius
        val size = radius * 2f

        when (country.pattern) {
            FlagPattern.HORIZ_TRICOLOR -> {
                val stripeH = size / 3f
                drawScope.drawRect(country.color1, Offset(left, top), Size(size, stripeH))
                drawScope.drawRect(country.color2, Offset(left, top + stripeH), Size(size, stripeH))
                drawScope.drawRect(country.color3, Offset(left, top + stripeH * 2), Size(size, stripeH))

                if (country.emblemType == "sun") {
                    drawScope.drawCircle(Color(0xFFFDB913), radius * 0.22f, center)
                } else if (country.emblemType == "eagle" || country.emblemType == "shield") {
                    drawScope.drawCircle(Color(0xFFC0A020), radius * 0.18f, center)
                }
            }

            FlagPattern.VERT_TRICOLOR -> {
                val stripeW = size / 3f
                drawScope.drawRect(country.color1, Offset(left, top), Size(stripeW, size))
                drawScope.drawRect(country.color2, Offset(left + stripeW, top), Size(stripeW, size))
                drawScope.drawRect(country.color3, Offset(left + stripeW * 2, top), Size(stripeW, size))

                if (country.emblemType == "eagle") {
                    drawScope.drawCircle(Color(0xFF8B5A2B), radius * 0.2f, center)
                } else if (country.emblemType == "sphere") {
                    drawScope.drawCircle(Color(0xFFFFCC00), radius * 0.22f, Offset(left + stripeW, center.y))
                }
            }

            FlagPattern.HORIZ_BICOLOR -> {
                val stripeH = size / 2f
                drawScope.drawRect(country.color1, Offset(left, top), Size(size, stripeH))
                drawScope.drawRect(country.color2, Offset(left, top + stripeH), Size(size, stripeH))

                if (country.emblemType == "star") {
                    drawStar(drawScope, Offset(left + radius * 0.6f, top + stripeH * 0.5f), radius * 0.25f, Color.White)
                } else if (country.emblemType == "crescent") {
                    drawScope.drawCircle(Color.White, radius * 0.28f, Offset(center.x - radius * 0.3f, top + stripeH * 0.5f))
                    drawScope.drawCircle(country.color1, radius * 0.24f, Offset(center.x - radius * 0.22f, top + stripeH * 0.5f))
                } else if (country.emblemType == "sun") {
                    drawScope.drawCircle(Color(0xFFFDB913), radius * 0.25f, Offset(left + radius * 0.5f, center.y))
                }
            }

            FlagPattern.NORDIC_CROSS -> {
                // Background
                drawScope.drawRect(country.color1, Offset(left, top), Size(size, size))
                val crossW = size * 0.22f
                val crossX = left + size * 0.38f
                val crossY = top + size * 0.5f

                // Outer border stripe if exists (e.g. Norway/Iceland)
                if (country.color3 != Color.Transparent) {
                    val outerW = size * 0.34f
                    drawScope.drawRect(country.color3, Offset(crossX - outerW / 2, top), Size(outerW, size))
                    drawScope.drawRect(country.color3, Offset(left, crossY - outerW / 2), Size(size, outerW))
                }

                // Main cross
                drawScope.drawRect(country.color2, Offset(crossX - crossW / 2, top), Size(crossW, size))
                drawScope.drawRect(country.color2, Offset(left, crossY - crossW / 2), Size(size, crossW))
            }

            FlagPattern.CENTER_CIRCLE -> {
                drawScope.drawRect(country.color1, Offset(left, top), Size(size, size))
                drawScope.drawCircle(country.color2, radius * 0.58f, center)
            }

            FlagPattern.BRAZIL_RHOMBUS -> {
                // Green background
                drawScope.drawRect(country.color1, Offset(left, top), Size(size, size))

                // Yellow Rhombus diamond
                val diamondPath = Path().apply {
                    moveTo(center.x, top + radius * 0.2f)
                    lineTo(left + size * 0.9f, center.y)
                    lineTo(center.x, top + size * 0.9f)
                    lineTo(left + size * 0.1f, center.y)
                    close()
                }
                drawScope.drawPath(diamondPath, country.color2)

                // Blue celestial circle
                drawScope.drawCircle(country.color3, radius * 0.44f, center)
                // White curved band
                drawScope.drawArc(
                    color = Color.White,
                    startAngle = 160f,
                    sweepAngle = 90f,
                    useCenter = false,
                    topLeft = Offset(center.x - radius * 0.4f, center.y - radius * 0.4f),
                    size = Size(radius * 0.8f, radius * 0.8f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5f)
                )
            }

            FlagPattern.CANADIAN_PALE -> {
                val sideW = size * 0.25f
                drawScope.drawRect(country.color1, Offset(left, top), Size(sideW, size))
                drawScope.drawRect(country.color2, Offset(left + sideW, top), Size(size * 0.5f, size))
                drawScope.drawRect(country.color1, Offset(left + size * 0.75f, top), Size(sideW, size))
                // Stylized maple leaf diamond/star in center
                drawStar(drawScope, center, radius * 0.42f, country.color1)
            }

            FlagPattern.STARS_AND_STRIPES -> {
                // 7 red/white horizontal stripes
                val stripeCount = 7
                val sHeight = size / stripeCount
                for (i in 0 until stripeCount) {
                    val sColor = if (i % 2 == 0) country.color1 else country.color2
                    drawScope.drawRect(sColor, Offset(left, top + i * sHeight), Size(size, sHeight))
                }
                // Blue Canton
                drawScope.drawRect(country.color3, Offset(left, top), Size(radius * 0.95f, radius * 0.95f))
                // White star in canton
                drawStar(drawScope, Offset(left + radius * 0.48f, top + radius * 0.48f), radius * 0.32f, Color.White)
            }

            FlagPattern.CROSS_AND_SALTIRE -> {
                // UK Union Jack
                drawScope.drawRect(country.color1, Offset(left, top), Size(size, size))
                // White saltire
                val saltirePath = Path().apply {
                    moveTo(left, top)
                    lineTo(left + size, top + size)
                    moveTo(left + size, top)
                    lineTo(left, top + size)
                }
                drawScope.drawPath(
                    saltirePath,
                    Color.White,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = size * 0.26f)
                )
                // Red saltire
                drawScope.drawPath(
                    saltirePath,
                    country.color3,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = size * 0.12f)
                )
                // White cross
                val crossW = size * 0.28f
                drawScope.drawRect(Color.White, Offset(center.x - crossW / 2, top), Size(crossW, size))
                drawScope.drawRect(Color.White, Offset(left, center.y - crossW / 2), Size(size, crossW))
                // Red cross
                val redCrossW = size * 0.16f
                drawScope.drawRect(country.color3, Offset(center.x - redCrossW / 2, top), Size(redCrossW, size))
                drawScope.drawRect(country.color3, Offset(left, center.y - redCrossW / 2), Size(size, redCrossW))
            }

            FlagPattern.KOREA_TAEGEUK -> {
                // White base
                drawScope.drawRect(country.color1, Offset(left, top), Size(size, size))
                // Red top semicircle & Blue bottom semicircle (Taegeuk)
                drawScope.drawArc(
                    color = country.color2, // Red
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = true,
                    topLeft = Offset(center.x - radius * 0.5f, center.y - radius * 0.5f),
                    size = Size(radius, radius)
                )
                drawScope.drawArc(
                    color = country.color3, // Blue
                    startAngle = 0f,
                    sweepAngle = 180f,
                    useCenter = true,
                    topLeft = Offset(center.x - radius * 0.5f, center.y - radius * 0.5f),
                    size = Size(radius, radius)
                )
                drawScope.drawCircle(country.color2, radius * 0.25f, Offset(center.x - radius * 0.25f, center.y))
                drawScope.drawCircle(country.color3, radius * 0.25f, Offset(center.x + radius * 0.25f, center.y))
            }

            FlagPattern.DIAGONAL_SALTIRE -> {
                // E.g. Jamaica
                drawScope.drawRect(country.color1, Offset(left, top), Size(size, size))
                // Black side triangles
                val leftTri = Path().apply {
                    moveTo(left, top)
                    lineTo(center.x, center.y)
                    lineTo(left, top + size)
                    close()
                }
                drawScope.drawPath(leftTri, country.color3)
                val rightTri = Path().apply {
                    moveTo(left + size, top)
                    lineTo(center.x, center.y)
                    lineTo(left + size, top + size)
                    close()
                }
                drawScope.drawPath(rightTri, country.color3)

                // Gold saltire cross
                val sPath = Path().apply {
                    moveTo(left, top); lineTo(left + size, top + size)
                    moveTo(left + size, top); lineTo(left, top + size)
                }
                drawScope.drawPath(sPath, country.color2, style = androidx.compose.ui.graphics.drawscope.Stroke(width = size * 0.16f))
            }

            FlagPattern.INDIA_CHAKRA -> {
                val stripeH = size / 3f
                drawScope.drawRect(country.color1, Offset(left, top), Size(size, stripeH))
                drawScope.drawRect(country.color2, Offset(left, top + stripeH), Size(size, stripeH))
                drawScope.drawRect(country.color3, Offset(left, top + stripeH * 2), Size(size, stripeH))

                // Navy Blue Ashoka Chakra wheel in center
                drawScope.drawCircle(
                    color = country.color4,
                    radius = radius * 0.24f,
                    center = center,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
                )
                drawScope.drawCircle(country.color4, radius * 0.06f, center)
            }

            FlagPattern.SPAIN_CREST -> {
                val redH = size * 0.25f
                val yellowH = size * 0.5f
                drawScope.drawRect(country.color1, Offset(left, top), Size(size, redH))
                drawScope.drawRect(country.color2, Offset(left, top + redH), Size(size, yellowH))
                drawScope.drawRect(country.color3, Offset(left, top + redH + yellowH), Size(size, redH))
                drawScope.drawCircle(Color(0xFF8B0000), radius * 0.2f, Offset(center.x - radius * 0.28f, center.y))
            }

            FlagPattern.GREECE_STRIPES -> {
                val sCount = 5
                val sH = size / sCount
                for (i in 0 until sCount) {
                    val col = if (i % 2 == 0) country.color1 else country.color2
                    drawScope.drawRect(col, Offset(left, top + i * sH), Size(size, sH))
                }
                // Blue canton with white cross
                val cantonSize = radius * 0.9f
                drawScope.drawRect(country.color1, Offset(left, top), Size(cantonSize, cantonSize))
                val cW = cantonSize * 0.3f
                drawScope.drawRect(Color.White, Offset(left + cantonSize / 2 - cW / 2, top), Size(cW, cantonSize))
                drawScope.drawRect(Color.White, Offset(left, top + cantonSize / 2 - cW / 2), Size(cantonSize, cW))
            }

            FlagPattern.SOUTH_AFRICA_Y -> {
                // Red top, blue bottom
                drawScope.drawRect(country.color3, Offset(left, top), Size(size, size / 2))
                drawScope.drawRect(country.color2, Offset(left, top + size / 2), Size(size, size / 2))
                // Black triangle
                val tri = Path().apply {
                    moveTo(left, top)
                    lineTo(left + size * 0.42f, center.y)
                    lineTo(left, top + size)
                    close()
                }
                drawScope.drawPath(tri, Color.Black)
                // Green horizontal Y band
                drawScope.drawRect(country.color1, Offset(left, center.y - size * 0.12f), Size(size, size * 0.24f))
            }

            FlagPattern.SOLID_EMBLEM -> {
                drawScope.drawRect(country.color1, Offset(left, top), Size(size, size))

                when (country.emblemType) {
                    "star" -> {
                        drawStar(drawScope, center, radius * 0.48f, country.color2)
                    }
                    "stars5" -> { // China 1 big star + 4 small
                        drawStar(drawScope, Offset(left + radius * 0.6f, top + radius * 0.6f), radius * 0.36f, country.color2)
                    }
                    "crescent" -> {
                        // Moon & Star (Turkey)
                        drawScope.drawCircle(country.color2, radius * 0.38f, Offset(center.x - radius * 0.15f, center.y))
                        drawScope.drawCircle(country.color1, radius * 0.32f, Offset(center.x - radius * 0.05f, center.y))
                        drawStar(drawScope, Offset(center.x + radius * 0.32f, center.y), radius * 0.18f, country.color2)
                    }
                    "swiss_cross" -> {
                        val armL = radius * 0.8f
                        val armW = radius * 0.26f
                        drawScope.drawRect(country.color2, Offset(center.x - armW / 2, center.y - armL / 2), Size(armW, armL))
                        drawScope.drawRect(country.color2, Offset(center.x - armL / 2, center.y - armW / 2), Size(armL, armW))
                    }
                    "southern_cross" -> {
                        // Australia / NZ stars
                        drawStar(drawScope, Offset(center.x + radius * 0.35f, top + radius * 0.5f), radius * 0.18f, country.color2)
                        drawStar(drawScope, Offset(center.x + radius * 0.35f, top + radius * 1.4f), radius * 0.18f, country.color2)
                    }
                    else -> {
                        drawStar(drawScope, center, radius * 0.35f, country.color2)
                    }
                }
            }
        }
    }

    private fun drawStar(
        drawScope: DrawScope,
        center: Offset,
        outerRadius: Float,
        color: Color,
        points: Int = 5
    ) {
        val innerRadius = outerRadius * 0.42f
        val starPath = Path()
        val angleStep = (PI / points).toFloat()
        var currentAngle = -PI.toFloat() / 2f

        for (i in 0 until points * 2) {
            val r = if (i % 2 == 0) outerRadius else innerRadius
            val x = center.x + r * cos(currentAngle)
            val y = center.y + r * sin(currentAngle)
            if (i == 0) starPath.moveTo(x, y) else starPath.lineTo(x, y)
            currentAngle += angleStep
        }
        starPath.close()
        drawScope.drawPath(starPath, color)
    }
}
