package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.ui.screens.BattleScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.SetupScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.BattleViewModel

enum class AppScreen {
    BATTLE,
    SETUP,
    LEADERBOARD
}

class MainActivity : ComponentActivity() {

    private val viewModel: BattleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme(darkTheme = true) {
                MainContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainContent(viewModel: BattleViewModel) {
    var currentScreen by remember { mutableStateOf(AppScreen.BATTLE) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        when (currentScreen) {
            AppScreen.BATTLE -> {
                BattleScreen(
                    viewModel = viewModel,
                    onNavigateSetup = { currentScreen = AppScreen.SETUP },
                    onNavigateLeaderboard = { currentScreen = AppScreen.LEADERBOARD },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            AppScreen.SETUP -> {
                BackHandler { currentScreen = AppScreen.BATTLE }
                SetupScreen(
                    viewModel = viewModel,
                    onStartBattle = { currentScreen = AppScreen.BATTLE },
                    onBack = { currentScreen = AppScreen.BATTLE },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            AppScreen.LEADERBOARD -> {
                BackHandler { currentScreen = AppScreen.BATTLE }
                LeaderboardScreen(
                    viewModel = viewModel,
                    onBack = { currentScreen = AppScreen.BATTLE },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}
