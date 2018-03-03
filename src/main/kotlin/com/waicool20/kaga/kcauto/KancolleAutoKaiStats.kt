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
import kotlin.reflect.KMutableProperty1

object KancolleAutoKaiStatsTracker {
    var startingTime: LocalDateTime? = null
    var crashes = 0
    var atPort = true
    val history = mutableListOf<KancolleAutoKaiStats>()

    init {
        with(LoggingEventBus) {
            // Track sorties conducted
            subscribe(Regex(".*Combat done: (\\d+) / attempted: (\\d+).*")) {
                currentStats().apply {
                    sortiesDone = it.groupValues[1].toInt()
                    sortiesAttempted = it.groupValues[2].toInt()
                }
            }

            // Track expeditions conducted
            subscribe(Regex(".*Expeditions sent: (\\d+) / received: (\\d+).*")) {
                currentStats().apply {
                    expeditionsSent = it.groupValues[1].toInt()
                    expeditionsReceived = it.groupValues[2].toInt()
                }
            }

            subscribe(Regex(".*Expeditions received: (\\d+).*")) {
                currentStats().expeditionsReceived = it.groupValues[1].toInt()
            }

            // Track quests conducted
            subscribe(Regex(".*Quests started: (\\d+) / finished: (\\d+).*")) {
                currentStats().apply {
                    questsStarted = it.groupValues[1].toInt()
                    questsDone = it.groupValues[2].toInt()
                }
            }

            // Track pvp conducted
            subscribe(Regex(".*PvPs done: (\\d+).*")) {
                currentStats().pvpsDone = it.groupValues[1].toInt()
            }

            // Track buckets used
            subscribe(Regex(".*Resupplies: (\\d+) \\|\\| Repairs: (\\d+) \\|\\| Buckets: (\\d+).*")) {
                currentStats().apply {
                    resupplies = it.groupValues[1].toInt()
                    repairs = it.groupValues[2].toInt()
                    bucketsUsed = it.groupValues[3].toInt()
                }
            }

            // Track submarines switched
            subscribe(Regex(".*Ships switched: (\\d+).*")) {
                currentStats().shipsSwitched = it.groupValues[1].toInt()
            }

            // Track crashes occurred
            subscribe(Regex(".*KCAuto-Kai didn't terminate gracefully.*")) {
                crashes++
            }

            // Track recoveries
            subscribe(Regex(".*Recoveries done: (\\d+).*")) {
                currentStats().recoveries = it.groupValues[1].toInt()
            }

            // Track home
            subscribe(Regex(".*Beginning PvP.*")) { atPort = false }
            subscribe(Regex(".*Navigating to combat screen.*")) { atPort = false }
            subscribe(Regex(".*Finished PvP sortie.*")) { atPort = true }
            subscribe(Regex(".*Sortie complete.*")) { atPort = true }
            subscribe(Regex(".*At Home.*")) { atPort = true }
        }
    }

    fun startNewSession() {
        history.clear()
        startingTime = LocalDateTime.now()
        crashes = 0
        atPort = true
        trackNewChild()
    }

    fun trackNewChild() = history.add(KancolleAutoKaiStats())

    operator fun get(stat: KMutableProperty1<KancolleAutoKaiStats, Int>) = history.sumBy { stat.get(it) }

    private fun currentStats() = history.last()
}

data class KancolleAutoKaiStats(
        var sortiesDone: Int = 0,
        var sortiesAttempted: Int = 0,
        var expeditionsSent: Int = 0,
        var expeditionsReceived: Int = 0,
        var pvpsDone: Int = 0,
        var questsDone: Int = 0,
        var questsStarted: Int = 0,
        var resupplies: Int = 0,
        var repairs: Int = 0,
        var bucketsUsed: Int = 0,
        var shipsSwitched: Int = 0,
        var recoveries: Int = 0
)
