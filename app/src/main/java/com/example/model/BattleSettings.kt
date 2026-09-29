package com.example.model

enum class ArenaType(
    val displayName: String,
    val description: String,
    val icon: String
) {
    SHRINKING_RING(
        displayName = "Shrinking Zone",
        description = "The electric ring shrinks over time. Anyone caught outside is eliminated!",
        icon = "⭕"
    ),
    VOID_GATES(
        displayName = "Void Gates",
        description = "Rotating openings in the ring boundary. Knock opponents into the abyss!",
        icon = "🌀"
    ),
    SUMO_BUMPERS(
        displayName = "Sumo Bumpers",
        description = "Violent bounce bumpers! Deplete enemy HP on collision until they explode.",
        icon = "💥"
    ),
    VORTEX_HOLE(
        displayName = "Gravity Vortex",
        description = "A swirling black hole at the center. Fight the gravitational pull to survive!",
        icon = "🪐"
    )
}

enum class BattleSpeed(val multiplier: Float, val label: String) {
    SLOW(0.6f, "0.6x"),
    NORMAL(1.0f, "1.0x"),
    FAST(1.8f, "1.8x"),
    TURBO(2.8f, "2.8x")
}

data class BattleSettings(
    val arenaType: ArenaType = ArenaType.SHRINKING_RING,
    val speed: BattleSpeed = BattleSpeed.NORMAL,
    val powerUpsEnabled: Boolean = true,
    val bounciness: Float = 0.95f, // restitution
    val friction: Float = 0.994f,
    val selectedCountryCodes: List<String> = CountryCatalog.presets["World Cup Titans"] ?: listOf("BRA", "ARG", "FRA", "GER", "ESP", "ITA", "GBR", "NED"),
    val playerCountryCode: String? = null, // if non-null, user controls this country
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true
)
