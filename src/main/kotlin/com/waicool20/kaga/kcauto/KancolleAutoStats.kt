/*
 * GPLv3 License
 *
 *  Copyright (c) KAGA by waicool20
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.waicool20.kaga.kcauto

import com.waicool20.kaga.util.LoggingEventBus
import java.time.LocalDateTime

class KancolleAutoStatsTracker {
    var startingTime: LocalDateTime? = null
    var crashes = 0
    var atPort = true
    val history = mutableListOf<KancolleAutoStats>()

    init {
        with(LoggingEventBus) {
            // Track sorties conducted
            subscribe(".*~(\\d+) sorties conducted.*".toRegex()) {
                currentStats().sortiesConducted = it.groupValues[1].toInt()
            }

            // Track expeditions conducted
            subscribe(".*~(\\d+) expeditions conducted.*".toRegex()) {
                currentStats().expeditionsConducted = it.groupValues[1].toInt()
            }

            // Track pvp conducted
            subscribe(".*~(\\d+) PvPs conducted.*".toRegex()) {
                currentStats().pvpsConducted = it.groupValues[1].toInt()
            }

            // Track buckets used
            subscribe(".*[uU]sing bucket.*".toRegex()) {
                currentStats().bucketsUsed++
            }

            // Track submarines switched
            subscribe(".*Swapping submarines!.*".toRegex()) {
                currentStats().submarinesSwitched++
            }

            // Track crashes occurred
            subscribe(".*Kancolle Auto didn't terminate gracefully.*".toRegex()) {
                crashes++
            }

            // Track home
            subscribe(".*Beginning PvP sortie!.*".toRegex()) { atPort = false }
            subscribe(".*Commencing sortie!.*".toRegex()) { atPort = false }
            subscribe(".*PvP complete!.*".toRegex()) { atPort = true }
            subscribe(".*Sortie complete!.*".toRegex()) { atPort = true }
            subscribe(".*At Home!.*".toRegex()) { atPort = true }
        }
    }

    fun startNewSession() {
        history.clear()
        startingTime = LocalDateTime.now()
        crashes = 0
        atPort = true
        trackNewChild()
    }

    fun trackNewChild() = history.add(KancolleAutoStats())

    fun sortiesConductedTotal() = history.map { it.sortiesConducted }.sum()

    fun expeditionsConductedTotal() = history.map { it.expeditionsConducted }.sum()

    fun pvpsConductedTotal() = history.map { it.pvpsConducted }.sum()

    fun bucketsUsedTotal() = history.map { it.bucketsUsed }.sum()

    fun submarinesSwitchedTotal() = history.map { it.submarinesSwitched }.sum()

    private fun currentStats() = history.last()
}

data class KancolleAutoStats(
        var sortiesConducted: Int = 0,
        var expeditionsConducted: Int = 0,
        var pvpsConducted: Int = 0,
        var bucketsUsed: Int = 0,
        var submarinesSwitched: Int = 0
)
