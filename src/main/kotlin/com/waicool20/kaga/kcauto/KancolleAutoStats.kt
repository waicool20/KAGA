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
            subscribe(".*Combat done: (\\d+) / attempted: (\\d+).*".toRegex()) {
                currentStats().apply {
                    sortiesDone = it.groupValues[1].toInt()
                    sortiesAttempted = it.groupValues[2].toInt()
                }
            }

            // Track expeditions conducted
            subscribe(".*Expeditions sent: (\\d+) / received: (\\d+).*".toRegex()) {
                currentStats().apply {
                    expeditionsSent = it.groupValues[1].toInt()
                    expeditionsReceived = it.groupValues[2].toInt()
                }
            }

            // Track pvp conducted
            subscribe(".*PvPs done: (\\d+).*".toRegex()) {
                currentStats().pvpsDone = it.groupValues[1].toInt()
            }

            // Track buckets used
            subscribe(".*Resupplies: (\\d+) \\|\\| Repairs: (\\d+) \\|\\| Buckets: (\\d+).*".toRegex()) {
                currentStats().apply {
                    resupplies = it.groupValues[1].toInt()
                    repairs = it.groupValues[2].toInt()
                    bucketsUsed = it.groupValues[3].toInt()
                }
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
            subscribe(".*Beginning PvP.*".toRegex()) { atPort = false }
            subscribe(".*Navigating to combat screen.*".toRegex()) { atPort = false }
            subscribe(".*Finished PvP sortie.*".toRegex()) { atPort = true }
            subscribe(".*Sortie complete.*".toRegex()) { atPort = true }
            subscribe(".*At Home.*".toRegex()) { atPort = true }
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

    fun sortiesDoneTotal() = history.sumBy { it.sortiesDone }

    fun sortiesAttemptedTotal() = history.sumBy { it.sortiesAttempted }

    fun expeditionsSentTotal() = history.sumBy { it.expeditionsSent }

    fun expeditionsReceivedTotal() = history.sumBy { it.expeditionsReceived }

    fun pvpsDoneTotal() = history.sumBy { it.pvpsDone }

    fun resuppliesTotal() = history.sumBy { it.resupplies }

    fun repairsTotal() = history.sumBy { it.repairs }

    fun bucketsUsedTotal() = history.sumBy { it.bucketsUsed }

    fun submarinesSwitchedTotal() = history.sumBy { it.submarinesSwitched }

    private fun currentStats() = history.last()
}

data class KancolleAutoStats(
        var sortiesDone: Int = 0,
        var sortiesAttempted: Int = 0,
        var expeditionsSent: Int = 0,
        var expeditionsReceived: Int = 0,
        var pvpsDone: Int = 0,
        var resupplies: Int = 0,
        var repairs: Int = 0,
        var bucketsUsed: Int = 0,
        var submarinesSwitched: Int = 0
)
