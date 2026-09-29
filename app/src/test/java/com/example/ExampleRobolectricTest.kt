package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.CountryCatalog
import com.example.model.FlagPattern
import com.example.model.SessionCountryStat
import com.example.viewmodel.BattleViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Flag Battle", appName)
  }

  @Test
  fun `verify country catalog loading`() {
    assertTrue(CountryCatalog.allCountries.size >= 130)
    val continents = CountryCatalog.allCountries.map { it.continent }.toSet()
    assertTrue(continents.contains("Americas"))
    assertTrue(continents.contains("Europe"))
    assertTrue(continents.contains("Asia"))
    assertTrue(continents.contains("Africa"))
    assertTrue(continents.contains("Oceania"))

    val usa = CountryCatalog.getByCode("USA")
    assertNotNull(usa)
    assertEquals("United States", usa?.name)
    assertEquals(FlagPattern.STARS_AND_STRIPES, usa?.pattern)

    val brazil = CountryCatalog.getByCode("BRA")
    assertNotNull(brazil)
    assertEquals("Brazil", brazil?.name)

    val presets = CountryCatalog.presets
    assertTrue(presets.containsKey("All Countries Mega"))
    assertTrue(presets.containsKey("World Cup Titans"))
    assertTrue(presets.containsKey("Euro Championship"))
  }

  @Test
  fun `verify session country stat win rate calculation`() {
    val country = CountryCatalog.getByCode("BRA")!!
    val stat = SessionCountryStat(
      country = country,
      wins = 3,
      matchesPlayed = 4,
      runnerUps = 1,
      totalKills = 7
    )
    assertEquals(75.0f, stat.winRatePercent, 0.01f)
    assertEquals(3, stat.wins)
    assertEquals(4, stat.matchesPlayed)
  }

  @Test
  fun `verify battle viewmodel session stats reset`() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val vm = BattleViewModel(app)
    assertEquals(0, vm.totalSessionBattles.value)
    vm.resetSessionStats()
    assertEquals(0, vm.totalSessionBattles.value)
    assertTrue(vm.sessionLeaderboard.value.isEmpty())
  }

  @Test
  fun `verify start randomized simulation initializes arena balls`() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val vm = BattleViewModel(app)
    vm.startRandomizedSimulation(12)
    val state = vm.battleState.value
    assertEquals(12, state.totalCount)
    assertEquals(12, state.balls.size)
    assertTrue(state.balls.all { it.isAlive })
    // Ensure all flags are distinct valid countries
    val codes = state.balls.map { it.country.code }.toSet()
    assertEquals(12, codes.size)
  }
}
