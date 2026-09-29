package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.ArenaType
import com.example.model.BattleSettings
import com.example.model.Country
import com.example.model.CountryCatalog
import com.example.viewmodel.BattleViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SetupScreen(
    viewModel: BattleViewModel,
    onStartBattle: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentSettings by viewModel.settings.collectAsStateWithLifecycle()
    var localSettings by remember(currentSettings) { mutableStateOf(currentSettings) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedContinent by remember { mutableStateOf("All") }

    val filteredCountries = remember(searchQuery, selectedContinent) {
        CountryCatalog.allCountries.filter { country ->
            val matchesSearch = searchQuery.isBlank() ||
                country.name.contains(searchQuery, ignoreCase = true) ||
                country.code.contains(searchQuery, ignoreCase = true)
            val matchesContinent = selectedContinent == "All" || country.continent.equals(selectedContinent, ignoreCase = true)
            matchesSearch && matchesContinent
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Tournament Setup",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("setup_back")) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A)
                )
            )
        },
        bottomBar = {
            Surface(
                color = Color(0xFF0F172A),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        viewModel.updateSettings(localSettings)
                        viewModel.setupBattle()
                        onStartBattle()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(54.dp)
                        .testTag("start_tournament_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "START CIRCLE BATTLE (${localSettings.selectedCountryCodes.size} COUNTRIES)",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )
                }
            }
        },
        containerColor = Color(0xFF030712),
        modifier = modifier.testTag("setup_screen")
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // 1. Preset Tournaments
            item {
                Text(
                    text = "🏆 TOURNAMENT PRESETS",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF38BDF8)
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CountryCatalog.presets.forEach { (presetName, codes) ->
                        val isSelected = localSettings.selectedCountryCodes == codes
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Color(0xFF2563EB) else Color(0xFF1E293B),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) Color(0xFF60A5FA) else Color(0xFF334155)
                            ),
                            modifier = Modifier
                                .clickable {
                                    localSettings = localSettings.copy(selectedCountryCodes = codes)
                                }
                                .testTag("preset_$presetName")
                        ) {
                            Text(
                                text = "$presetName (${codes.size})",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else Color(0xFFCBD5E1),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }

                    // Quick Randomize Presets
                    listOf(8, 12, 16, 24).forEach { count ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF1E1B4B),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF8B5CF6)),
                            modifier = Modifier
                                .clickable {
                                    val randomized = CountryCatalog.allCountries.shuffled().take(count).map { it.code }
                                    localSettings = localSettings.copy(selectedCountryCodes = randomized)
                                }
                                .testTag("randomize_${count}_button")
                        ) {
                            Text(
                                text = "🎲 Random $count",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFC084FC),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // 2. Arena Type Selection
            item {
                Text(
                    text = "🏟️ CIRCLE ARENA TYPE",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF38BDF8)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ArenaType.entries.forEach { arena ->
                        val isSelected = localSettings.arenaType == arena
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color(0xFF1E293B) else Color(0xFF0F172A)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (isSelected) Color(0xFF38BDF8) else Color(0xFF1E293B)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { localSettings = localSettings.copy(arenaType = arena) }
                                .testTag("arena_${arena.name}")
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = arena.icon, fontSize = 26.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = arena.displayName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = arena.description,
                                        fontSize = 11.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color(0xFF38BDF8)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Play Mode: Watch AI vs Control Country
            item {
                Text(
                    text = "🕹️ YOUR PARTICIPATION",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF38BDF8)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Interactive Player Control",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = if (localSettings.playerCountryCode != null) "Steer your country with joystick!" else "Spectator / Simulation Mode (AI vs AI)",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                            Switch(
                                checked = localSettings.playerCountryCode != null,
                                onCheckedChange = { checked ->
                                    val code = if (checked) localSettings.selectedCountryCodes.firstOrNull() ?: "USA" else null
                                    localSettings = localSettings.copy(playerCountryCode = code)
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF38BDF8))
                            )
                        }

                        // Pick player's country
                        if (localSettings.playerCountryCode != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Select Your Flag to Control:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFFBBF24)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                localSettings.selectedCountryCodes.forEach { code ->
                                    val country = CountryCatalog.getByCode(code) ?: return@forEach
                                    val isMe = localSettings.playerCountryCode == code
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isMe) Color(0xFFF59E0B) else Color(0xFF1E293B),
                                        modifier = Modifier.clickable {
                                            localSettings = localSettings.copy(playerCountryCode = code)
                                        }
                                    ) {
                                        Text(
                                            text = "${country.emoji} ${country.code}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isMe) Color.Black else Color.White,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. Power-ups & Audio Options
            item {
                Text(
                    text = "⚙️ GAMEPLAY OPTIONS",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF38BDF8)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Arena Power-Ups", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                                Text("Speed Surge, Shields, Blast Bombs, Titans", fontSize = 11.sp, color = Color(0xFF94A3B8))
                            }
                            Switch(
                                checked = localSettings.powerUpsEnabled,
                                onCheckedChange = { localSettings = localSettings.copy(powerUpsEnabled = it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF38BDF8))
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Sound Effects & Chimes", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                                Text("Bounces, explosions, victory melody", fontSize = 11.sp, color = Color(0xFF94A3B8))
                            }
                            Switch(
                                checked = localSettings.soundEnabled,
                                onCheckedChange = { localSettings = localSettings.copy(soundEnabled = it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF38BDF8))
                            )
                        }
                    }
                }
            }

            // 5. Country Roster Customizer
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🌍 COUNTRIES IN TOURNAMENT (${localSettings.selectedCountryCodes.size} / ${CountryCatalog.allCountries.size})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF38BDF8)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF1E293B),
                            modifier = Modifier.clickable {
                                localSettings = localSettings.copy(
                                    selectedCountryCodes = CountryCatalog.allCountries.map { it.code }
                                )
                            }
                        ) {
                            Text(
                                "All (${CountryCatalog.allCountries.size})",
                                fontSize = 11.sp,
                                color = Color(0xFF60A5FA),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF1E293B),
                            modifier = Modifier.clickable {
                                val current = localSettings.selectedCountryCodes.toMutableList()
                                filteredCountries.forEach { c ->
                                    if (!current.contains(c.code)) current.add(c.code)
                                }
                                localSettings = localSettings.copy(selectedCountryCodes = current)
                            }
                        ) {
                            Text(
                                "+ Shown",
                                fontSize = 11.sp,
                                color = Color(0xFF34D399),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search country name or code...", fontSize = 12.sp, color = Color(0xFF64748B)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear search", tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF38BDF8),
                        unfocusedBorderColor = Color(0xFF1E293B),
                        focusedContainerColor = Color(0xFF0F172A),
                        unfocusedContainerColor = Color(0xFF0F172A),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("country_search_field")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Continent Filter Chips
                val continents = listOf("All", "Americas", "Europe", "Asia", "Africa", "Oceania")
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    continents.forEach { cont ->
                        val isSelected = selectedContinent == cont
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) Color(0xFF2563EB) else Color(0xFF1E293B),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) Color(0xFF60A5FA) else Color(0xFF334155)
                            ),
                            modifier = Modifier.clickable { selectedContinent = cont }
                        ) {
                            Text(
                                text = cont,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Country Grid
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    filteredCountries.forEach { country ->
                        val isSelected = localSettings.selectedCountryCodes.contains(country.code)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) Color(0xFF1E293B) else Color(0xFF090D16),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) Color(0xFF38BDF8) else Color(0xFF1E293B)
                            ),
                            modifier = Modifier
                                .clickable {
                                    val currentList = localSettings.selectedCountryCodes.toMutableList()
                                    if (isSelected) {
                                        if (currentList.size > 2) currentList.remove(country.code)
                                    } else {
                                        currentList.add(country.code)
                                    }
                                    localSettings = localSettings.copy(selectedCountryCodes = currentList)
                                }
                                .testTag("country_${country.code}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = country.emoji, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = country.code,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(30.dp)) }
        }
    }
}
